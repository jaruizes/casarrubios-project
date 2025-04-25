# Variables for CloudFront module

variable "name_prefix" {
  description = "Prefix to use for resource names"
  type        = string
  default     = "casarrubios"
}

variable "enabled" {
  description = "Whether the distribution is enabled to accept end user requests for content"
  type        = bool
  default     = true
}

variable "is_ipv6_enabled" {
  description = "Whether the IPv6 is enabled for the distribution"
  type        = bool
  default     = true
}

variable "comment" {
  description = "Any comments you want to include about the distribution"
  type        = string
  default     = "Casarrubios Applications Distribution"
}

variable "default_root_object" {
  description = "The object that you want CloudFront to return when an end user requests the root URL"
  type        = string
  default     = "index.html"
}

variable "price_class" {
  description = "The price class for this distribution"
  type        = string
  default     = "PriceClass_100" # Use only North America and Europe
}

variable "candidates_application_domain_name" {
  description = "The domain name for the candidates application S3 bucket"
  type        = string
}

variable "recruitment_application_domain_name" {
  description = "The domain name for the recruitment application S3 bucket"
  type        = string
}

variable "allowed_methods" {
  description = "Controls which HTTP methods CloudFront processes and forwards to your origin"
  type        = list(string)
  default     = ["GET", "HEAD", "OPTIONS"]
}

variable "cached_methods" {
  description = "Controls which HTTP methods CloudFront caches responses to"
  type        = list(string)
  default     = ["GET", "HEAD"]
}

variable "forward_query_string" {
  description = "Indicates whether you want CloudFront to forward query strings to the origin"
  type        = bool
  default     = false
}

variable "forward_cookies" {
  description = "Specifies which cookies to forward to the origin for this cache behavior"
  type        = string
  default     = "none"
}

variable "viewer_protocol_policy" {
  description = "The protocol that users can use to access the files in the origin"
  type        = string
  default     = "redirect-to-https"
}

variable "min_ttl" {
  description = "The minimum amount of time that you want objects to stay in CloudFront caches"
  type        = number
  default     = 0
}

variable "default_ttl" {
  description = "The default amount of time that you want objects to stay in CloudFront caches"
  type        = number
  default     = 3600
}

variable "max_ttl" {
  description = "The maximum amount of time that you want objects to stay in CloudFront caches"
  type        = number
  default     = 86400
}

variable "recruitment_path_pattern" {
  description = "The pattern that specifies which requests to apply the behavior to"
  type        = string
  default     = "/recruitment/*"
}

variable "geo_restriction_type" {
  description = "The method that you want to use to restrict distribution of your content by country"
  type        = string
  default     = "none"
}

variable "geo_restriction_locations" {
  description = "The ISO 3166-1-alpha-2 codes for which you want CloudFront either to distribute your content or not to distribute your content"
  type        = list(string)
  default     = []
}

variable "cloudfront_default_certificate" {
  description = "Whether to use the CloudFront default certificate"
  type        = bool
  default     = true
}

variable "acm_certificate_arn" {
  description = "The ARN of the AWS Certificate Manager certificate that you wish to use with this distribution"
  type        = string
  default     = null
}

variable "ssl_support_method" {
  description = "Specifies how you want CloudFront to serve HTTPS requests"
  type        = string
  default     = null
}

variable "minimum_protocol_version" {
  description = "The minimum version of the SSL protocol that you want CloudFront to use for HTTPS connections"
  type        = string
  default     = null
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {}
}
