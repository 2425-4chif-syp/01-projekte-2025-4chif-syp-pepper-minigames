# Kubernetes Manifests

This directory adds a separate Kubernetes deployment path for LeoCloud without changing the existing VM deployment.

## Structure

- `base/mealplan`: reusable manifests for the `pep-mealplan` stack
- `overlays/leocloud/mealplan`: LeoCloud-specific image tags and resource sizing

## Before first deploy

1. Authenticate against LeoCloud and switch to your namespace context.
2. Edit `base/mealplan/postgres-secret.yaml` and replace the placeholder credentials.
3. Create the GHCR pull secret if the images are private:

```sh
kubectl create secret docker-registry ghcr-pull-secret \
  --docker-server=ghcr.io \
  --docker-username=<github-username> \
  --docker-password=<github-pat-with-read-packages>
```

4. Verify the LeoCloud host in `overlays/leocloud/mealplan/patch-ingress.yaml`. It should match the URL from `leocloud get home`.
5. Push the LeoCloud frontend image with the dedicated workflow in `.github/workflows/build-mealplan-frontend-leocloud.yaml`.

## Deploy

```sh
kubectl apply -k k8s/overlays/leocloud/mealplan
kubectl rollout status statefulset/mealplan-postgres
kubectl rollout status deployment/mealplan-backend
kubectl rollout status deployment/mealplan-frontend
kubectl get ingress mealplan
```

The LeoCloud namespace URL from `leocloud get home` should then serve:

- `/` -> mealplan frontend
- `/api` -> mealplan backend

## Notes

- The backend image is reused from the existing GitHub Actions workflow.
- The LeoCloud frontend uses a separate image name so the VM build can keep serving `/mealplan/`.
- The frontend now derives its API base from the deployed `<base href>`, so the VM build still uses `/mealplan/api` while LeoCloud uses `/api`.
- If you later make the GHCR packages public, you can remove `patch-image-pull-secret.yaml` from the LeoCloud overlay.
