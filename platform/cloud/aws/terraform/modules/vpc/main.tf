module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.8.1"

  name                 = "${var.vpc_name}"
  cidr                 = "10.0.0.0/16"
  azs                  = ["${var.aws_region}a", "${var.aws_region}b", "${var.aws_region}c"]
  private_subnets      = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets       = ["10.0.4.0/24", "10.0.5.0/24", "10.0.6.0/24"]

  enable_nat_gateway   = true
  single_nat_gateway   = true
  enable_dns_hostnames = true
  create_database_subnet_group = false

  tags = merge(var.tags, {
    Name = var.vpc_name
  })

  public_subnet_tags = merge(var.tags, {
    Name = "${var.vpc_name}-public-subnet"
  })

  private_subnet_tags = merge(var.tags, {
    Name = "${var.vpc_name}-private-subnet"
  })
}

resource "aws_security_group" "rds_sg" {
  name_prefix = "${var.sg_name}_rds_sg"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port = 5432
    to_port   = 5432
    protocol  = "tcp"
    description = "Postgresql RDS"

    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.sg_name}_rds_sg"
  })
}

resource "aws_security_group" "eks_cluster_sg" {
  name        = "${var.sg_name}-eks-cluster-sg"
  description = "Security group for EKS cluster"
  vpc_id      = module.vpc.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.sg_name}-eks-cluster-sg"
  })
}

