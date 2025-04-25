# Casarrubios AWS Infrastructure

This directory contains Terraform code for deploying the Casarrubios AWS infrastructure. The infrastructure includes:

- An EKS cluster
- S3 buckets for resumes and applications
- CloudFront distribution for web applications
- RDS PostgreSQL instance for database storage

## Directory Structure

- `modules/` - Contains reusable Terraform modules
  - `vpc/` - VPC and networking resources
  - `eks/` - EKS cluster and node groups
  - `s3/` - S3 buckets for various purposes
  - `cloudfront/` - CloudFront distribution for web applications
  - `rds/` - RDS PostgreSQL instance
- `tests/` - Contains tests for the Terraform code
- `backup/` - Contains backup of original component files
- `main.tf` - Main Terraform configuration
- `variables.tf` - Input variables
- `outputs.tf` - Output values
- `providers.tf` - Provider configuration

## Prerequisites

- Terraform 1.0.0 or later
- AWS CLI configured with appropriate credentials
- kubectl (for interacting with the EKS cluster)

## Usage

### Testing Without Creating Resources

Before applying the Terraform code, you can test it without creating any resources using the testing framework in the `tests/` directory. See the [Testing README](tests/README.md) for more information.

```bash
# Run validation tests
cd tests/validation
chmod +x validate.sh
./validate.sh

# Run plan tests
cd ../plan
chmod +x plan_test.sh
./plan_test.sh

# Run Terratest tests (requires Go)
cd ../terratest
go mod download
go test -v
```

### Deploying the Infrastructure

To deploy the infrastructure:

```bash
# Initialize Terraform
terraform init

# Plan the changes
terraform plan -out=tfplan

# Apply the changes
terraform apply tfplan
```

### Destroying the Infrastructure

To destroy the infrastructure:

```bash
terraform destroy
```

## Variables

See `variables.tf` for a list of input variables and their descriptions.

## Outputs

See `outputs.tf` for a list of output values and their descriptions.

## Testing

The Terraform code includes a comprehensive testing framework that allows you to validate and test your infrastructure code without creating any resources. See the [Testing README](tests/README.md) for more information.

## Best Practices

1. Always run tests before applying changes to production
2. Use a separate tfvars file for each environment (dev, staging, prod)
3. Store sensitive values in a secure location (e.g., AWS Secrets Manager)
4. Use remote state storage (e.g., S3 with DynamoDB locking)
5. Apply changes through a CI/CD pipeline
