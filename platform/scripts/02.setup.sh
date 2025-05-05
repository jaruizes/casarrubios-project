#!/bin/bash
set -e

ROOT_FOLDER="$(pwd)"
AWS_ZONE="eu-west-1"

init() {
  echo "Initializing environment variables..."
  echo "ROOT_FOLDER=$ROOT_FOLDER"
  echo "AWS_ZONE=$AWS_ZONE"
}


executeTerraform() {
  cd "$ROOT_FOLDER/../cloud/aws/terraform"

  terraform init
  terraform plan
  terraform apply -auto-approve
}

configureKubectl() {
  aws eks --region "$AWS_ZONE" update-kubeconfig --name casarrubios
}


createCluster() {
  K3D_FIX_DNS=0 k3d cluster create casarrubioscluster -p "8081:80@loadbalancer"
}

installArgoCD() {
  kubectl create namespace argocd
  kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
  kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'
  kubectl patch configmap/argocd-cm -n argocd --type merge -p '{"data":{"application.resourceTrackingMethod": "annotation"}}'

  echo "ArgoCD installed. Waiting for it to be ready..."
  sleep 30
}

configureArgoCDApps() {
  kubectl apply -f ../argocd/settings -n argocd
  kubectl apply -f ../argocd/infra/infrastructure-app.yaml -n argocd
  sleep 40

  kubectl apply -f ../argocd/candidates/candidates-app.yaml -n argocd
  kubectl apply -f ../argocd/recruitment/recruitment-app.yaml -n argocd

  sleep 10
}

showInfo() {
    ARGOCD_URL=$(kubectl get service argocd-server -n argocd -o jsonpath='https://{.status.loadBalancer.ingress[0].ip}:{.spec.ports[0].port}/')
    ARGOCD_PASSWORD=$(kubectl get secret argocd-initial-admin-secret -n argocd --template={{.data.password}} | base64 -D)

    echo ""
    echo ""
    echo "---------------------------------------"

    echo "ARGOCD URL: http://localhost:8081"
    echo "ARGOCD Credentials: admin/$ARGOCD_PASSWORD"

    echo "---------------------------------------"
    echo ""
    echo ""
}

# ORACLE_RDS_ENDPOINT=$(terraform -chdir="$ROOT_FOLDER/aws/terraform" output -raw db_rds_oracle_endpoint)
#cloudfront_domain_name = "d1sar9koeqe2h6.cloudfront.net"
#environment = "dev"
#private_subnet_ids = [
#  "subnet-006e515f02161f224",
#  "subnet-08196e71ed87099dd",
#  "subnet-05ab422a12e931400",
#]
#public_subnet_ids = [
#  "subnet-06e04349255b55d29",
#  "subnet-00c77359024e0ce31",
#  "subnet-0ef14b0f309efd774",
#]
#rds_endpoint = "casarrubios.ca57ppuvhfbu.eu-west-1.rds.amazonaws.com:5432"
#region = "eu-west-1"
#s3_candidates_application_bucket = "casarrubios--candidates-application"
#s3_candidates_application_website_endpoint = "casarrubios--candidates-application.s3-website-eu-west-1.amazonaws.com"
#s3_recruitment_application_bucket = "casarrubios--recruitment-application"
#s3_recruitment_application_website_endpoint = "casarrubios--recruitment-application.s3-website-eu-west-1.amazonaws.com"
#s3_resumes_bucket = "casarrubios--resumes"
#vpc_id = "vpc-071d6f0c92f3d52a6"


setup() {
  init
  executeTerraform
  configureKubectl
#  createCluster
#  installArgoCD
#  configureArgoCDApps
#  showInfo
}

setup
