#!/bin/bash

echo "=== Formatting all files in project ==="

# Format Java files with Google Java Format
echo "1. Running Google Java Format for Java files..."
./mvnw fmt:format

# Format other files with Prettier if npm and Prettier are installed
if command -v npm &> /dev/null; then
  echo "2. Running Prettier for other files..."
  npm run format
else
  echo "WARNING: npm not found, skipping Prettier formatting. To format non-Java files, install npm and run 'npm install'."
fi

echo "=== Formatting complete ==="

# Exit with the status of the last command
exit $? 