#!/bin/bash

: ${SECURE_RANDOM:=true}

echo "Starting the Periscope application..."

if [ -n "$CERT_URL" ]; then
  curl -O $CERT_URL && keytool -import -noprompt -trustcacerts -file sequenceiq.com.crt -alias "sequenceiq" -keystore /usr/lib/jvm/java-7-openjdk-amd64/jre/lib/security/cacerts -storepass changeit
fi

if [ "$SECURE_RANDOM" == "false" ]; then
  CB_PARAMS=-Djava.security.egd=file:/dev/urandom
fi

java $CB_PARAMS -jar /periscope.jar
