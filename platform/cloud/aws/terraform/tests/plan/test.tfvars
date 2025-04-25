# Test values for Terraform plan testing
# These values are used for testing only and do not create actual resources

environment = "test"
name_prefix = "test-casarrubios"
region      = "us-east-1"

# VPC Variables
vpc_cidr           = "10.0.0.0/16"
availability_zones = ["us-east-1a", "us-east-1b"]

# EKS Variables
eks_cluster_name              = "test-eks-cluster"
eks_cluster_version           = "1.28"
eks_node_group_instance_types = ["t3.micro"]
eks_node_group_desired_size   = 1
eks_node_group_min_size       = 1
eks_node_group_max_size       = 2

# RDS Variables
db_instance_class = "db.t3.micro"
db_name           = "testdb"
db_username       = "testuser"
db_password       = "testpassword"

# S3 and CloudFront Variables
s3_bucket_prefix = "test-"

# Database Initialization
db_init_script_path = "platform/local/postgresql/inventory.sql"
