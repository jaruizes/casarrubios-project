#!/bin/bash
set -e

ROOT_FOLDER="$(pwd)"
AWS_ZONE="eu-west-1"

configureKubectl() {
  aws eks --region "$AWS_ZONE" update-kubeconfig --name casarrubios
}

removeNamespaces() {
  echo "-------------------------------------------"
  echo "Removing namespaces...."

  namespaces=$(kubectl get namespaces -o jsonpath='{.items[*].metadata.name}')

  for namespace in $namespaces; do
    echo ""
    echo ""
    echo "Removing namespaces: $namespace"
    if [ $namespace != "default" ] && [ $namespace != "kube-public" ] && [ $namespace != "kube-system" ] && [ $namespace != "kube-node-lease" ]; then
      kubectl delete namespace "$namespace"
      kubectl create namespace "$namespace"
    fi

  done

  ## Remove all in default namespace
  kubectl delete all --all

  echo "Removing services with type LoadBalancer to force AWS to remove ELBs objects.....OK!"
  echo "-------------------------------------------"
}

removeTerraform() {
  echo "CLUSTER_ZONE=$AWS_ZONE"

  cd "$ROOT_FOLDER/../cloud/aws/terraform"

  terraform destroy -auto-approve
}

destroy() {
  echo "-------------------------------------------"
  echo "Destroying environment...."
  configureKubectl
  removeNamespaces
  removeTerraform
  echo "-------------------------------------------"
}


destroy
