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


locals {
  region = var.region

  tags = {
    Project     = "Casarrubios"
    ManagedBy   = "Terraform"
  }
}

# VPC Module
module "vpc" {
  source = "./modules/vpc"

  vpc_name   = "casarrubios"
  aws_region = local.region
  sg_name    = "casarrubios"
  tags       = local.tags
}

# EKS Module
module "eks" {
  source = "./modules/eks"

  eks_cluster_name = "casarrubios"
  eks_cluster_version = var.eks_cluster_version
  eks_workers_instance_types = var.eks_workers_instance_types
  eks_workers_desired_capacity = var.eks_workers_desired_capacity
  vpc_id = module.vpc.id
  vpc_private_subnets = module.vpc.private_subnets

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

  vpc_public_subnets = module.vpc.public_subnets
  security_group_id = module.vpc.rds_security_group_id
  db_name = "casarrubios"

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
