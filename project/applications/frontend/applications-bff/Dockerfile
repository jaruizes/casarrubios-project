## Use the official Node.js image as the base image
#FROM node:20
#
## Set the working directory inside the container
#WORKDIR /usr/src/app
#
## Copy package.json and package-lock.json to the working directory
#COPY package*.json ./
#
## Install the application dependencies
#RUN npm install
#
## Copy the rest of the application files
#COPY . .
#
## Build the NestJS application
#RUN npm run build
#
## Expose the application port
#EXPOSE 3000
#
## Command to run the application
#CMD ["node", "dist/main"]
#
#
#

FROM node:20 AS build

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:20-slim

WORKDIR /app
COPY --from=build /app/dist /app
EXPOSE 3000
CMD ["node", "dist/main"]