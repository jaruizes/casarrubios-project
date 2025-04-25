#!/bin/bash

# Plan test script for Terraform configurations
# This script runs terraform plan to check what changes would be made without creating any resources

set -e

# Change to the Terraform directory
cd "$(dirname "$0")/../../"

# Initialize Terraform without configuring a backend
echo "Running terraform init..."
terraform init -backend=false

# Run terraform plan with detailed exit code
# Exit code 0: No changes
# Exit code 1: Error
# Exit code 2: Changes present
echo "Running terraform plan..."
terraform plan -var-file="tests/plan/test.tfvars" -detailed-exitcode || {
  # If exit code is 2, that means changes are present, which is expected
  if [ $? -eq 2 ]; then
    echo "Changes detected as expected."
    exit 0
  else
    echo "Error running terraform plan."
    exit 1
  fi
}

# If we get here, exit code was 0, meaning no changes
echo "No changes detected."
