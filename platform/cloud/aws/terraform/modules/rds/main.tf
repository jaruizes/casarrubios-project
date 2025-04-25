module "postgresql" {
  source  = "terraform-aws-modules/rds/aws"
  version = "6.12.0"

  identifier = var.db_name

  engine                   = "postgres"
  engine_version           = "14"
  engine_lifecycle_support = "open-source-rds-extended-support-disabled"
  family                   = "postgres14" # DB parameter group
  major_engine_version     = "14"         # DB option group
  instance_class           = "db.t3.large"

  allocated_storage     = 20
  max_allocated_storage = 100
  storage_encrypted     = false
  auto_minor_version_upgrade = false

  db_name  = var.db_name
  manage_master_user_password = false
  username = "postgres"
  password = "postgres"
  port     = 5432

  multi_az               = false
  # DB subnet group
  create_db_subnet_group = true
  subnet_ids             = var.vpc_public_subnets
  vpc_security_group_ids = [var.security_group_id]
  publicly_accessible    = true

  backup_retention_period         = 1  // Enable backups to enable ARCHIVELOG
  maintenance_window              = "Mon:00:00-Mon:03:00"
  backup_window                   = "03:00-06:00"
  delete_automated_backups        = true
  #enabled_cloudwatch_logs_exports = ["oracle", "upgrade"]

  skip_final_snapshot     = true
  deletion_protection     = false

  tags = merge(var.tags, {
    Name = "eks-cluster-sg"
  })

}
