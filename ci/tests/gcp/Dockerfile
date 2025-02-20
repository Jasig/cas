FROM gcr.io/google.com/cloudsdktool/google-cloud-cli:511.0.0-alpine
ARG GCLOUD_COMPONENT

RUN apk add --update --no-cache openjdk11
RUN gcloud components install cloud-firestore-emulator beta
RUN gcloud components install pubsub-emulator beta
RUN gcloud components update

COPY entrypoint.sh .

ENV FIRESTORE_PORT 9980
#EXPOSE "$FIRESTORE_PORT"

ENV PUBSUB_PORT 8085
#EXPOSE "$PUBSUB_PORT"

ENV GCP_PROJECT_ID "apereo-cas-gcp"
ENV GCLOUD_COMPONENT $GCLOUD_COMPONENT

ENTRYPOINT ["./entrypoint.sh"]
