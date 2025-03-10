#!/bin/bash

echo "=== Checking code formatting ==="

# Check Java files with Google Java Format
echo "1. Checking Java files with Google Java Format..."
./mvnw fmt:check

JAVA_FORMAT_STATUS=$?

# Check other files with Prettier if installed
if command -v npm &> /dev/null; then
  echo "2. Checking other files with Prettier..."
  npm run format:check
  PRETTIER_STATUS=$?
else
  echo "WARNING: npm not found, skipping Prettier check. To check non-Java files, install npm and run 'npm install'."
  PRETTIER_STATUS=0
fi

# Exit with error if any check failed
if [ $JAVA_FORMAT_STATUS -ne 0 ] || [ $PRETTIER_STATUS -ne 0 ]; then
  echo "=== Format check failed! Please run './scripts/format.sh' to fix formatting issues. ==="
  exit 1
else
  echo "=== All formatting checks passed! ==="
  exit 0
fi 