# Outputs for S3 module

# Resumes bucket outputs
output "resumes_bucket_id" {
  description = "The name of the resumes bucket"
  value       = aws_s3_bucket.resumes.id
}

output "resumes_bucket_arn" {
  description = "The ARN of the resumes bucket"
  value       = aws_s3_bucket.resumes.arn
}

output "resumes_bucket_domain_name" {
  description = "The domain name of the resumes bucket"
  value       = aws_s3_bucket.resumes.bucket_domain_name
}

output "resumes_bucket_regional_domain_name" {
  description = "The regional domain name of the resumes bucket"
  value       = aws_s3_bucket.resumes.bucket_regional_domain_name
}

# Candidates application bucket outputs
output "candidates_application_bucket_id" {
  description = "The name of the candidates application bucket"
  value       = aws_s3_bucket.candidates_application.id
}

output "candidates_application_bucket_arn" {
  description = "The ARN of the candidates application bucket"
  value       = aws_s3_bucket.candidates_application.arn
}

output "candidates_application_bucket_domain_name" {
  description = "The domain name of the candidates application bucket"
  value       = aws_s3_bucket.candidates_application.bucket_domain_name
}

output "candidates_application_bucket_regional_domain_name" {
  description = "The regional domain name of the candidates application bucket"
  value       = aws_s3_bucket.candidates_application.bucket_regional_domain_name
}

output "candidates_application_website_endpoint" {
  description = "The website endpoint of the candidates application bucket"
  value       = aws_s3_bucket_website_configuration.candidates_application.website_endpoint
}

output "candidates_application_website_domain" {
  description = "The website domain of the candidates application bucket"
  value       = aws_s3_bucket_website_configuration.candidates_application.website_domain
}

# Recruitment application bucket outputs
output "recruitment_application_bucket_id" {
  description = "The name of the recruitment application bucket"
  value       = aws_s3_bucket.recruitment_application.id
}

output "recruitment_application_bucket_arn" {
  description = "The ARN of the recruitment application bucket"
  value       = aws_s3_bucket.recruitment_application.arn
}

output "recruitment_application_bucket_domain_name" {
  description = "The domain name of the recruitment application bucket"
  value       = aws_s3_bucket.recruitment_application.bucket_domain_name
}

output "recruitment_application_bucket_regional_domain_name" {
  description = "The regional domain name of the recruitment application bucket"
  value       = aws_s3_bucket.recruitment_application.bucket_regional_domain_name
}

output "recruitment_application_website_endpoint" {
  description = "The website endpoint of the recruitment application bucket"
  value       = aws_s3_bucket_website_configuration.recruitment_application.website_endpoint
}

output "recruitment_application_website_domain" {
  description = "The website domain of the recruitment application bucket"
  value       = aws_s3_bucket_website_configuration.recruitment_application.website_domain
}
