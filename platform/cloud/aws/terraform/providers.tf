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

# The Kubernetes provider configuration will be defined in the EKS module
# This is a placeholder and will be properly configured after the EKS module is created
# provider "kubernetes" {
#   host                   = module.eks.cluster_endpoint
#   cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
#   
#   exec {
#     api_version = "client.authentication.k8s.io/v1beta1"
#     command     = "aws"
#     args = [
#       "eks",
#       "get-token",
#       "--cluster-name",
#       module.eks.cluster_name
#     ]
#   }
# }
