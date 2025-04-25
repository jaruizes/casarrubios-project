# Outputs for Casarrubios AWS infrastructure

# VPC Outputs
output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "public_subnet_ids" {
  description = "The IDs of the public subnets"
  value       = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  description = "The IDs of the private subnets"
  value       = module.vpc.private_subnet_ids
}

# EKS Outputs
output "eks_cluster_endpoint" {
  description = "Endpoint for EKS control plane"
  value       = module.eks.cluster_endpoint
}

output "eks_cluster_certificate_authority_data" {
  description = "Certificate authority data for EKS cluster"
  value       = module.eks.cluster_certificate_authority_data
}

output "eks_cluster_name" {
  description = "Name of the EKS cluster"
  value       = module.eks.cluster_name
}

output "eks_node_group_id" {
  description = "The ID of the EKS node group"
  value       = module.eks.node_group_id
}

# S3 Outputs
output "s3_resumes_bucket" {
  description = "The name of the S3 bucket for resumes"
  value       = module.s3.resumes_bucket_id
}

output "s3_candidates_application_bucket" {
  description = "The name of the S3 bucket for candidates application"
  value       = module.s3.candidates_application_bucket_id
}

output "s3_recruitment_application_bucket" {
  description = "The name of the S3 bucket for recruitment application"
  value       = module.s3.recruitment_application_bucket_id
}

output "s3_candidates_application_website_endpoint" {
  description = "The website endpoint of the S3 bucket for candidates application"
  value       = module.s3.candidates_application_website_endpoint
}

output "s3_recruitment_application_website_endpoint" {
  description = "The website endpoint of the S3 bucket for recruitment application"
  value       = module.s3.recruitment_application_website_endpoint
}

# CloudFront Outputs
output "cloudfront_domain_name" {
  description = "The domain name of the CloudFront distribution"
  value       = module.cloudfront.domain_name
}

# RDS Outputs
output "rds_endpoint" {
  description = "The endpoint of the RDS instance"
  value       = module.rds.endpoint
}

output "rds_port" {
  description = "The port of the RDS instance"
  value       = module.rds.port
}

output "rds_database_name" {
  description = "The database name of the RDS instance"
  value       = module.rds.database_name
}

# Additional useful outputs
output "region" {
  description = "The AWS region used"
  value       = var.region
}

output "environment" {
  description = "The environment name"
  value       = var.environment
}
