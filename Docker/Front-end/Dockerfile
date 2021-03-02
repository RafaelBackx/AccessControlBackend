FROM node:lts-alpine
RUN npm install -g http-server
WORKDIR /app
COPY acsf/package*.json ./
RUN npm install
COPY acsf/. .
EXPOSE 8080
run npm run build
CMD ["http-server","dist"]
