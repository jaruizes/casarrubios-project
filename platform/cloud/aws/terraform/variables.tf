# Variables for Casarrubios AWS infrastructure

variable "region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "eu-west-1"
}

variable "eks_workers_instance_types" {
  description = "Workers instance types"
  type        = list(string)
  default     = ["m5.large"]
}

variable "eks_workers_desired_capacity" {
  description = "Number of workers (desired)"
  type        = number
  default     = 3
}


variable "environment" {
  description = "Environment name (e.g., dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "name_prefix" {
  description = "Prefix to use for resource names"
  type        = string
  default     = "casarrubios"
}

# EKS Variables
variable "eks_cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
  default     = "casarrubios-eks-cluster"
}

variable "eks_cluster_version" {
  description = "Kubernetes version for the EKS cluster"
  type        = string
  default     = "1.29"
}

variable "db_name" {
  description = "Name of the database"
  type        = string
  default     = "casarrubios"
}

# S3 and CloudFront Variables
variable "s3_bucket_prefix" {
  description = "Prefix for S3 bucket names"
  type        = string
  default     = ""  # Empty by default, will use the exact names specified in the issue
}
