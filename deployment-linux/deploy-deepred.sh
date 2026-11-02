#!/bin/bash
# =====================================================================
# Deploiement de l'IA DeepRed (equipe groupe-2) - TP3 420-5DD
# Deploie SEULEMENT notre IA : deepred-gateway + deepred-engine
# Prerequis : le backend du tournoi tourne deja dans le cluster
# Usage : bash deploy-deepred.sh   (depuis le dossier deployment-linux)
# =====================================================================

cd "$(dirname "$0")"

echo "Suppression d'une ancienne version de DeepRed (si elle existe)..."
kubectl delete deployments deepred-gateway deepred-engine --ignore-not-found
kubectl delete services deepred-gateway deepred-engine --ignore-not-found

sleep 5

echo "Deploiement de DeepRed (images tirees de Docker Hub)..."
kubectl apply -f deepred.yaml

echo "Attente que les pods soient prets..."
kubectl rollout status deployment/deepred-engine --timeout=180s
kubectl rollout status deployment/deepred-gateway --timeout=180s

echo "DeepRed est deploye :"
kubectl get pods -l 'io.kompose.service in (deepred-gateway,deepred-engine)'