#!/bin/sh

# Exit immediately if any command fails
set -e

# Validate that exactly two arguments were provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <WIDOCO_VERSION> <OUTPUT_FILE_PATH>"
    echo "Example: $0 1.4.36 /ontopus/widoco.jar"
    exit 1
fi

WIDOCO_VERSION=$1
OUTPUT_FILE_PATH=$2

# Construct the URL based on the provided version
URL="https://github.com/dgarijo/Widoco/releases/download/v${WIDOCO_VERSION}/widoco-${WIDOCO_VERSION}-jar-with-dependencies_JDK-17.jar"

echo "Downloading Widoco v${WIDOCO_VERSION}..."
echo "Source: ${URL}"
echo "Destination: ${OUTPUT_FILE_PATH}"

# -nv: Non-verbose mode
# -O: Write output to the specified file path
wget -nv -O "${OUTPUT_FILE_PATH}" "${URL}"

echo "Download successful!"
