# Supprimer les déploiements 'backend' et 'frontend' s'ils existent
kubectl delete deployments backend --ignore-not-found
kubectl delete deployments frontend --ignore-not-found
kubectl delete deployments randomai --ignore-not-found
kubectl delete deployments lechonkai --ignore-not-found

# Supprimer les services 'backend' et 'frontend' s'ils existent
kubectl delete services backend --ignore-not-found
kubectl delete services frontend --ignore-not-found
kubectl delete services randomai --ignore-not-found
kubectl delete services lechonkai --ignore-not-found

sleep 15
kubectl create -f backend-deployment.yaml
kubectl create -f backend-service.yaml
sleep 30
kubectl create -f frontend-deployment.yaml
kubectl create -f frontend-service.yaml
kubectl create -f ia.yaml
kubectl create -f lechonk.yaml
