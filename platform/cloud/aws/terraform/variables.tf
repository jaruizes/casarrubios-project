# Variables for Casarrubios AWS infrastructure

variable "region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "eu-west-1"
}

variable "eks_workers_instance_types" {
  description = "Workers instance types"
  type        = list(string)
  default     = ["m5.xlarge"]
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



# VPC Variables
variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "List of availability zones to use"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
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
  default     = "1.28"
}

variable "eks_node_group_instance_types" {
  description = "Instance types for the EKS node group"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "eks_node_group_desired_size" {
  description = "Desired size of the EKS node group"
  type        = number
  default     = 2
}

variable "eks_node_group_min_size" {
  description = "Minimum size of the EKS node group"
  type        = number
  default     = 1
}

variable "eks_node_group_max_size" {
  description = "Maximum size of the EKS node group"
  type        = number
  default     = 3
}

# RDS Variables
variable "db_instance_class" {
  description = "Instance class for the RDS instance"
  type        = string
  default     = "db.t3.small"
}

variable "db_name" {
  description = "Name of the database"
  type        = string
  default     = "casarrubios"
}

variable "db_username" {
  description = "Username for the database"
  type        = string
  default     = "casarrubios"
  sensitive   = true
}

variable "db_password" {
  description = "Password for the database"
  type        = string
  default     = "changeme"  # This should be overridden in a secure way
  sensitive   = true
}

# S3 and CloudFront Variables
variable "s3_bucket_prefix" {
  description = "Prefix for S3 bucket names"
  type        = string
  default     = ""  # Empty by default, will use the exact names specified in the issue
}

# Database Initialization
variable "db_init_script_path" {
  description = "Path to the SQL script to initialize the database"
  type        = string
  default     = "platform/local/postgresql/inventory.sql"  # Path to the inventory.sql script
}
