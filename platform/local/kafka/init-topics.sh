#!/bin/bash

TOPICS_FILE="/topics-to-create.txt"

echo "Creando topics desde $TOPICS_FILE"
while read -r topic; do
  if [[ ! -z "$topic" ]]; then
    echo "Creando topic $topic..."
    kafka-topics --create --if-not-exists \
      --bootstrap-server broker:29092 \
      --partitions 1 \
      --replication-factor 1 \
      --config retention.ms=604800000 \
      --topic "$topic"
  fi
done < "$TOPICS_FILE"

echo "Todos los topics fueron procesados."
