module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  cluster_name    = var.eks_cluster_name
  cluster_version = var.eks_cluster_version
  subnet_ids      = var.vpc_private_subnets
  vpc_id          = var.vpc_id

  cluster_endpoint_public_access           = true
  enable_cluster_creator_admin_permissions = true
  enable_irsa                             = true

  cluster_addons = {
    coredns = {
      most_recent = true
    }
    kube-proxy = {
      most_recent = true
    }
    vpc-cni = {
      most_recent = true
    }
    aws-ebs-csi-driver = {
      most_recent = true
    }
  }

  eks_managed_node_group_defaults = {
    instance_types = var.eks_workers_instance_types
    disk_size = 50
    additional_tags = {
      app = var.eks_cluster_name
    }
  }

  eks_managed_node_groups = {
    workers = {
      min_size     = sum([var.eks_workers_desired_capacity, -1])
      max_size     = sum([var.eks_workers_desired_capacity, 1])
      desired_size = var.eks_workers_desired_capacity

      instance_types = var.eks_workers_instance_types
      iam_role_additional_policies = {
        AmazonEBSCSIDriverPolicy  = "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy"
      }

      k8s_labels = {
        Environment = "dev"
        App = var.eks_cluster_name
      }
    }
  }
}
