FROM redis:6-alpine

RUN apk add npm

RUN apk add nodejs

WORKDIR /usr/src/app

COPY dist/ .
COPY .env .env
COPY package*.json ./
COPY scripts/ scripts/

RUN npm ci --only=production

ARG NODE_PORT=3006

ENV PORT=${NODE_PORT}

EXPOSE ${NODE_PORT}

CMD ["scripts/run.sh"]
