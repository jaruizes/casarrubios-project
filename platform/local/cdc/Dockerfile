FROM alpine:3.18

RUN apk --no-cache add curl

RUN mkdir -p /connectors

COPY platform/local/cdc/register-connectors.sh /register-connectors.sh
COPY project/candidates/cdc/connectors/applications-cdc-postgresql-connector.json /connectors/
COPY project/recruitment/cdc/connectors/recruiters-new-applications-outbox-connector.json /connectors/
COPY project/recruitment/cdc/connectors/recruiters-positions-cdc-connector.json /connectors/

RUN chmod +x /register-connectors.sh

CMD ["register-connectors.sh"]
