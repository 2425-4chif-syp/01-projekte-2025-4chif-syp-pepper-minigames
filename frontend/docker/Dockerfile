# Stage 1: Build Angular app
FROM node:22.15.0-alpine AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:1.28-alpine-slim
COPY --from=build /app/dist/frontend/browser/ /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
