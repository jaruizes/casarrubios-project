# Terraform Testing Framework

This directory contains tests for the Terraform infrastructure code. These tests allow you to validate and test your Terraform code without actually creating any resources in AWS.

## Test Types

### 1. Validation Tests

Simple syntax and configuration validation using `terraform validate`.

**How to run:**
```bash
cd platform/cloud/aws/terraform/tests/validation
chmod +x validate.sh
./validate.sh
```

### 2. Plan Tests

Tests that run `terraform plan` to check what changes would be made without actually applying them.

**How to run:**
```bash
cd platform/cloud/aws/terraform/tests/plan
chmod +x plan_test.sh
./plan_test.sh
```

### 3. Terratest Tests

More comprehensive tests using [Terratest](https://terratest.gruntwork.io/), a Go library for testing Terraform code.

**Prerequisites:**
- Go 1.20 or later
- Terraform CLI

**How to run:**
```bash
cd platform/cloud/aws/terraform/tests/terratest
go mod download
go test -v
```

## Test Configuration

The tests use mock values defined in `plan/test.tfvars` to avoid using real credentials or sensitive information. You can modify these values as needed for your tests.

## Adding New Tests

### Adding a New Validation Test

Modify the `validation/validate.sh` script to add additional validation checks.

### Adding a New Plan Test

Modify the `plan/plan_test.sh` script and update the `plan/test.tfvars` file as needed.

### Adding a New Terratest Test

Create a new Go test file in the `terratest` directory. See the existing `terraform_test.go` for an example.

## Best Practices

1. Always run tests before applying changes to production
2. Keep test values separate from production values
3. Never include real credentials in test files
4. Update tests when you add new resources to your Terraform code
