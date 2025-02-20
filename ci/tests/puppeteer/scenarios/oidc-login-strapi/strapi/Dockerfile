FROM node:22
RUN apt-get update > /dev/null && apt-get install iproute2 iputils-ping -y > /dev/null

RUN mkdir /srv/app && chown 1000:1000 -R /srv/app
WORKDIR /srv/app

RUN npx create-strapi@5.6.0 . \
            --no-run \
            --use-yarn \
            --javascript \
            --install \
            --git-init \
            --example \
            --skip-cloud \
            --dbclient=sqlite \
            --dbfile=data.db

ENV ADMIN_JWT_SECRET example-token
COPY config.json .
COPY server.js ./config

ENV PATH $PATH:/srv/app/node_modules/.bin

COPY entrypoint.sh /usr/local/bin/
RUN chmod 777 /usr/local/bin/entrypoint.sh
ENTRYPOINT ["entrypoint.sh"]
