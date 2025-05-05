#!/bin/bash
set -e

createCluster() {
  K3D_FIX_DNS=0 k3d cluster create casarrubioscluster -p "8081:80@loadbalancer"
}

installArgoCD() {
  kubectl create namespace argocd
  kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
  kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'
  kubectl patch configmap/argocd-cm -n argocd --type merge -p '{"data":{"application.resourceTrackingMethod": "annotation"}}'

  sleep 10
}

configureArgoCDApps() {
  kubectl apply -f platform/k8s/argocd -n argocd
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

setup() {
  createCluster
  installArgoCD
  showInfo
}

setup
