FROM node:16-alpine

WORKDIR /usr/src/app

COPY dist/ .
COPY .env .env
COPY package*.json ./

RUN npm ci --only=production

EXPOSE 8080

CMD ["node", "./src"]
