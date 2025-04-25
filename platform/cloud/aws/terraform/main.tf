terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.20"
    }
  }
}

# Use data sources to get AWS account information
data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  region = data.aws_region.current.name

  tags = {
    Project     = "Casarrubios"
    ManagedBy   = "Terraform"
  }
}

# VPC Module
module "vpc" {
  source = "./modules/vpc"

  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
  name_prefix        = var.name_prefix
  eks_cluster_name   = var.eks_cluster_name
  tags               = local.tags
}

# EKS Module
module "eks" {
  source = "./modules/eks"

  name_prefix        = var.name_prefix
  cluster_name       = var.eks_cluster_name
  cluster_version    = var.eks_cluster_version
  subnet_ids         = concat(module.vpc.public_subnets, module.vpc.private_subnets)
  private_subnet_ids = module.vpc.private_subnets
  security_group_id  = module.vpc.

  node_group_instance_types = var.eks_node_group_instance_types
  node_group_desired_size   = var.eks_node_group_desired_size
  node_group_min_size       = var.eks_node_group_min_size
  node_group_max_size       = var.eks_node_group_max_size

  tags = local.tags
}

# S3 Module
module "s3" {
  source = "./modules/s3"

  bucket_prefix = var.s3_bucket_prefix == "" ? "${var.name_prefix}-" : "${var.s3_bucket_prefix}-"
  tags          = local.tags
}

# RDS Module
module "rds" {
  source = "./modules/rds"

  name_prefix     = var.name_prefix
  subnet_ids      = module.vpc.private_subnet_ids
  security_group_id = module.vpc.rds_security_group_id

  db_instance_class = var.db_instance_class
  db_name           = var.db_name
  db_username       = var.db_username
  db_password       = var.db_password
  db_init_script_path = var.db_init_script_path

  tags = local.tags
}

# CloudFront Module
module "cloudfront" {
  source = "./modules/cloudfront"

  name_prefix                       = var.name_prefix
  candidates_application_domain_name = module.s3.candidates_application_bucket_regional_domain_name
  recruitment_application_domain_name = module.s3.recruitment_application_bucket_regional_domain_name

  tags = local.tags
}
