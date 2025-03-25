echo "Registrando conector <applications-cdc-postgresql-connector>..."
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://cdc-service:8083/connectors/ -d @/connectors/applications-cdc-postgresql-connector.json



echo "Registrando conector <recruiters-positions-cdc-connector>..."
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://cdc-service:8083/connectors/ -d @/connectors/recruiters-positions-cdc-connector.json
