#!/bin/sh -l

echo "Running ent command $1 in folder $(pwd)"
ls -al
docker info
ent which
local command="$1"
eval "$command"
time=$(date)
echo "time=$time" >> $GITHUB_OUTPUT