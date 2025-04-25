variable "db_name" {
  description = "Database Name"
  type        = string
}

variable "vpc_public_subnets" {
  description = "The list of the public subnets"
  type        = list(string)
}

variable "security_group_id" {
  description = "Security Group"
  type        = string
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {}
}
