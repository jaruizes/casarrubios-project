# Outputs for CloudFront module

output "distribution_id" {
  description = "The identifier for the distribution"
  value       = aws_cloudfront_distribution.main.id
}

output "distribution_arn" {
  description = "The ARN for the distribution"
  value       = aws_cloudfront_distribution.main.arn
}

output "domain_name" {
  description = "The domain name corresponding to the distribution"
  value       = aws_cloudfront_distribution.main.domain_name
}

output "hosted_zone_id" {
  description = "The CloudFront Route 53 zone ID that can be used to route an Alias Resource Record Set to"
  value       = aws_cloudfront_distribution.main.hosted_zone_id
}

output "status" {
  description = "The current status of the distribution"
  value       = aws_cloudfront_distribution.main.status
}

output "last_modified_time" {
  description = "The date and time the distribution was last modified"
  value       = aws_cloudfront_distribution.main.last_modified_time
}

output "in_progress_validation_batches" {
  description = "The number of invalidation batches currently in progress"
  value       = aws_cloudfront_distribution.main.in_progress_validation_batches
}

output "etag" {
  description = "The current version of the distribution's information"
  value       = aws_cloudfront_distribution.main.etag
}

output "candidates_application_origin_id" {
  description = "The ID of the candidates application origin"
  value       = "S3-${var.name_prefix}-candidates-application"
}

output "recruitment_application_origin_id" {
  description = "The ID of the recruitment application origin"
  value       = "S3-${var.name_prefix}-recruitment-application"
}
