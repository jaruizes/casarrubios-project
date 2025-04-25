#!/bin/bash

# Simple validation script for Terraform configurations
# This script validates the Terraform configuration without creating any resources

set -e

# Change to the Terraform directory
cd "$(dirname "$0")/../../"

echo "Running terraform init..."
terraform init -backend=false

echo "Running terraform validate..."
terraform validate

echo "Validation completed successfully!"
