function getTicketNumber() {
    local str=$1
    echo "input: $str"
    result=$(echo "$str" | egrep -o "^EHUB-\\d*")
    echo "$result"
}

function setBundleVersion() {
    local versionPrefix=$1
    version=$(ent bundle info | grep "Version:" | awk '{print $2}')
    value=".version=\"$versionPrefix$version\""
    jq $value entando.json > temp.json && mv temp.json entando.json
}

function setComponentVersions() {
    local versionPrefix=$1

    components=$(ent bundle list | tail -n +$((n + 3)))
    while IFS= read -r line
    do
        name=$(echo $line | awk '{print $1}')
        type=$(echo $line | awk '{print $2}')
        version=$(echo $line | awk '{print $3}')
        stack=$(echo $line | awk '{print $4}')

        # Enter component folder
        cd "$type"s/"$name"

        ver="$versionPrefix$version"
        echo "Setting version=$ver for component $name"

        if [ "$stack" = "spring-boot" ]; then
            mvn versions:set -DnewVersion="$ver" > /dev/null
        fi

        if [ "$stack" = "react" ]; then
            value=".version=\"$ver\""
            jq $value package.json > temp.json && mv temp.json package.json
        fi

        # Return back to bundle folder
        cd ../../
        
    done <<< "$components"
}

"$@"