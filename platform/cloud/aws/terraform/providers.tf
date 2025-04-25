# Provider configuration for Casarrubios AWS infrastructure

provider "aws" {
  region = var.region

  default_tags {
    tags = {
      Environment = var.environment
      Project     = "Casarrubios"
      ManagedBy   = "Terraform"
    }
  }
}
