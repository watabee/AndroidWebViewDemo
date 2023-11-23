#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <output_directory>"
    exit 1
fi

output_dir=$1

npm ci
npx rollup --file "$output_dir/bundle.js" -c
