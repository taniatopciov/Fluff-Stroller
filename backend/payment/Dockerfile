FROM node:16-alpine

WORKDIR /usr/src/app

COPY dist/ .
COPY .env .env
COPY package*.json ./
COPY scripts/ scripts/

RUN npm ci --only=production

ARG NODE_PORT=4242

ENV PORT=${NODE_PORT}

EXPOSE ${NODE_PORT}

CMD ["scripts/run.sh"]
