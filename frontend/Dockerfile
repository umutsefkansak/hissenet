FROM node:18-alpine AS builder

WORKDIR /usr/src/app

COPY package.json package-lock.json ./

RUN npm ci

COPY public ./public
COPY src ./src

RUN npm run build

FROM nginx:stable-alpine

COPY --from=builder /usr/src/app/build /usr/share/nginx/html

EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]