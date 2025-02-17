FROM node:18 AS build

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

FROM nginx:1.23

COPY --from=build /app/dist/candidates-app/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8081
CMD ["nginx", "-g", "daemon off;"]
