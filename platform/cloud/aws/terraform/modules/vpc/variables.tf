variable "vpc_name" {
  description = "VPC Name"
  type        = string
}

variable "aws_region" {
  description = "AWS Region"
  type        = string
}

variable "sg_name" {
  description = "VPC name"
  type        = string
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {}
}
