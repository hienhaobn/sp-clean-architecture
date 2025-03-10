#!/bin/bash

echo "Running Google Java Format..."
./mvnw fmt:format

# Exit with the status of the last command
exit $? 