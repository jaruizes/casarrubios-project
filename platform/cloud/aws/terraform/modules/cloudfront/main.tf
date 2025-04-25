# CloudFront module for Casarrubios AWS infrastructure

# Create CloudFront origin access control for S3
resource "aws_cloudfront_origin_access_control" "candidates_application" {
  name                              = "${var.name_prefix}-candidates-application-oac"
  description                       = "Candidates Application OAC"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_origin_access_control" "recruitment_application" {
  name                              = "${var.name_prefix}-recruitment-application-oac"
  description                       = "Recruitment Application OAC"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# Create CloudFront distribution
resource "aws_cloudfront_distribution" "main" {
  enabled             = var.enabled
  is_ipv6_enabled     = var.is_ipv6_enabled
  comment             = var.comment
  default_root_object = var.default_root_object
  price_class         = var.price_class

  # Candidates Application Origin
  origin {
    domain_name              = var.candidates_application_domain_name
    origin_id                = "S3-${var.name_prefix}-candidates-application"
    origin_access_control_id = aws_cloudfront_origin_access_control.candidates_application.id
  }

  # Recruitment Application Origin
  origin {
    domain_name              = var.recruitment_application_domain_name
    origin_id                = "S3-${var.name_prefix}-recruitment-application"
    origin_access_control_id = aws_cloudfront_origin_access_control.recruitment_application.id
  }

  # Default cache behavior (for candidates application)
  default_cache_behavior {
    allowed_methods  = var.allowed_methods
    cached_methods   = var.cached_methods
    target_origin_id = "S3-${var.name_prefix}-candidates-application"

    forwarded_values {
      query_string = var.forward_query_string
      cookies {
        forward = var.forward_cookies
      }
    }

    viewer_protocol_policy = var.viewer_protocol_policy
    min_ttl                = var.min_ttl
    default_ttl            = var.default_ttl
    max_ttl                = var.max_ttl
  }

  # Cache behavior for recruitment application
  ordered_cache_behavior {
    path_pattern     = var.recruitment_path_pattern
    allowed_methods  = var.allowed_methods
    cached_methods   = var.cached_methods
    target_origin_id = "S3-${var.name_prefix}-recruitment-application"

    forwarded_values {
      query_string = var.forward_query_string
      cookies {
        forward = var.forward_cookies
      }
    }

    viewer_protocol_policy = var.viewer_protocol_policy
    min_ttl                = var.min_ttl
    default_ttl            = var.default_ttl
    max_ttl                = var.max_ttl
  }

  # Geo restrictions
  restrictions {
    geo_restriction {
      restriction_type = var.geo_restriction_type
      locations        = var.geo_restriction_locations
    }
  }

  # SSL certificate
  viewer_certificate {
    cloudfront_default_certificate = var.cloudfront_default_certificate
    acm_certificate_arn            = var.acm_certificate_arn
    ssl_support_method             = var.ssl_support_method
    minimum_protocol_version       = var.minimum_protocol_version
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-cloudfront-distribution"
  })
}
