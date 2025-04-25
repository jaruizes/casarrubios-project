variable "eks_cluster_name" {
  description = "The name of the EKS cluster"
  type        = string
}

variable "eks_cluster_version" {
  description = "The version of the EKS cluster"
  type        = string
}

variable "eks_workers_instance_types" {
  description = "Workers instance types"
  type        = list(string)
}

variable "eks_workers_desired_capacity" {
  description = "Number of workers (desired)"
  type        = number
}

variable "vpc_private_subnets" {
  description = "The list of the private subnets"
  type        = list(string)
}

variable "vpc_id" {
  description = "VPC id"
  type        = string
}
