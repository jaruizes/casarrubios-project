output "private_subnets" {
  description = "Private Subnets"
  value       = module.vpc.private_subnets
}

output "public_subnets" {
  description = "Public Subnets"
  value       = module.vpc.public_subnets
}


output "vpc_cidr_block" {
  description = "CIDR Block"
  value       = module.vpc.vpc_cidr_block
}

output "id" {
  description = "VPC ID"
  value = module.vpc.vpc_id
}
