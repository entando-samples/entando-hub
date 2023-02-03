#!/bin/sh -l

echo "Running ent command $1 in folder $(pwd)"
ls -al
docker info
ent which
$("$1")
time=$(date)
echo "time=$time" >> $GITHUB_OUTPUT