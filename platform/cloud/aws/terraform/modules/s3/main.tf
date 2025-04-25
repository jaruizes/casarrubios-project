# S3 bucket for resumes
resource "aws_s3_bucket" "resumes" {
  bucket = "${var.bucket_prefix}-resumes"

  tags = merge(var.tags, {
    Name = "${var.bucket_prefix}-resumes"
  })
}

# Block public access for resumes bucket
resource "aws_s3_bucket_public_access_block" "resumes" {
  bucket = aws_s3_bucket.resumes.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Create S3 bucket for candidates application
resource "aws_s3_bucket" "candidates_application" {
  bucket = "${var.bucket_prefix}-candidates-application"

  tags = merge(var.tags, {
    Name = "${var.bucket_prefix}-candidates-application"
  })
}

# Enable static website hosting for candidates application bucket
resource "aws_s3_bucket_website_configuration" "candidates_application" {
  bucket = aws_s3_bucket.candidates_application.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "error.html"
  }
}

# Create S3 bucket for recruitment application
resource "aws_s3_bucket" "recruitment_application" {
  bucket = "${var.bucket_prefix}-recruitment-application"

  tags = merge(var.tags, {
    Name = "${var.bucket_prefix}-recruitment-application"
  })
}

# Enable static website hosting for recruitment application bucket
resource "aws_s3_bucket_website_configuration" "recruitment_application" {
  bucket = aws_s3_bucket.recruitment_application.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "error.html"
  }
}

# Create bucket policy for candidates application to allow CloudFront access
resource "aws_s3_bucket_policy" "candidates_application" {
  bucket = aws_s3_bucket.candidates_application.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipal"
        Effect    = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.candidates_application.arn}/*"
      }
    ]
  })
}

# Create bucket policy for recruitment application to allow CloudFront access
resource "aws_s3_bucket_policy" "recruitment_application" {
  bucket = aws_s3_bucket.recruitment_application.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipal"
        Effect    = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.recruitment_application.arn}/*"
      }
    ]
  })
}

# Create CORS configuration for candidates application bucket
resource "aws_s3_bucket_cors_configuration" "candidates_application" {
  bucket = aws_s3_bucket.candidates_application.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "HEAD"]
    allowed_origins = ["*"]
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}

# Create CORS configuration for recruitment application bucket
resource "aws_s3_bucket_cors_configuration" "recruitment_application" {
  bucket = aws_s3_bucket.recruitment_application.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "HEAD"]
    allowed_origins = ["*"]
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}
