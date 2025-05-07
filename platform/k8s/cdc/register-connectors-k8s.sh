#!/bin/sh

echo "Updating connector configurations..."
echo "Using database host: $DATABASE_HOST"
echo "Using database name: $DATABASE_NAME"
echo "Using database name: $CDC_SERVICE"

connector_files="/connectors/recruiters-new-applications-outbox-connector.json /connectors/recruiters-positions-cdc-connector.json /connectors/applications-cdc-postgresql-connector.json"

# Update the database hostname and name in each connector file
for file in $connector_files; do
  if [ -f "$file" ]; then
    echo "Updating configuration in $file"

    # Create a temporary file for processing
    tmp_file="${file}.tmp"

    # Replace database.hostname and database.dbname values
    sed -e "s/\"database.hostname\"[[:space:]]*:[[:space:]]*\"[^\"]*\"/\"database.hostname\" : \"$DATABASE_HOST\"/g" \
        -e "s/\"database.dbname\"[[:space:]]*:[[:space:]]*\"[^\"]*\"/\"database.dbname\" : \"$DATABASE_NAME\"/g" \
        "$file" > "$tmp_file"

    # Move the temporary file back to the original
    mv "$tmp_file" "$file"
  else
    echo "Warning: $file not found"
  fi
done

# Wait for CDC service to be available
echo "Waiting for CDC service to be available..."
until curl -s http://${CDC_SERVICE}:8083/connectors/ > /dev/null; do
  echo "CDC service is unavailable - sleeping 5s"
  sleep 5
done
echo "CDC service is up!"

# Register connectors
echo "Registrando conector <applications-cdc-postgresql-connector>..."
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://${CDC_SERVICE}:8083/connectors/ -d @/connectors/applications-cdc-postgresql-connector.json
sleep 20

echo "Registrando conector <recruiters-positions-cdc-connector>..."
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://${CDC_SERVICE}:8083/connectors/ -d @/connectors/recruiters-positions-cdc-connector.json
sleep 20

echo "Registrando conector <recruiters-new-applications-outbox-connector>..."
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://${CDC_SERVICE}:8083/connectors/ -d @/connectors/recruiters-new-applications-outbox-connector.json

echo "All connectors registered successfully!"

