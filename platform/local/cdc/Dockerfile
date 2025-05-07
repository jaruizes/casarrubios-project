FROM alpine:3.18

# Install curl
RUN apk --no-cache add curl

# Create connectors directory
RUN mkdir -p /connectors

# Copy the connector files and script
COPY project/candidates/cdc/connectors/applications-cdc-postgresql-connector.json /connectors/
COPY project/recruitment/cdc/connectors/recruiters-new-applications-outbox-connector.json /connectors/
COPY project/recruitment/cdc/connectors/recruiters-positions-cdc-connector.json /connectors/

# Create register-connectors.sh script
COPY platform/local/cdc/register-connectors-k8s.sh /register-connectors.sh

# Make the script executable
RUN chmod +x /register-connectors.sh

# Run the script
CMD ["/register-connectors.sh"]
