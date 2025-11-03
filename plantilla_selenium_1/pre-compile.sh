#!/bin/bash

# Configuración base
DOMAIN="mapfre-qaspain-resources"
DOMAIN_OWNER="975049890204"
REGION="eu-west-1"

# Obtener versión del POM
VERSION=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='version']/text()" pom.xml 2>/dev/null)

if [[ "$VERSION" == *"SNAPSHOT"* ]]; then
  REPO="snapshot"
else
  REPO="release"
fi

echo "📦 Versión detectada: $VERSION"
echo "📁 Repositorio seleccionado: $REPO"

echo "🔐 Obteniendo token de AWS CodeArtifact..."
export CODEARTIFACT_AUTH_TOKEN=$(aws codeartifact get-authorization-token \
  --domain "$DOMAIN" \
  --domain-owner "$DOMAIN_OWNER" \
  --region "$REGION" \
  --query authorizationToken \
  --output text)

if [ -z "$CODEARTIFACT_AUTH_TOKEN" ]; then
  echo "❌ No se pudo obtener el token. Revisa tus credenciales de AWS."
  exit 1
else
  echo "✅ Token obtenido. Ya podés compilar con Maven."
fi

# Ejecutar Maven si se pasaron comandos
if [ "$#" -gt 0 ]; then
  echo "🚀 Ejecutando: mvn $@"
  mvn "$@"
else
  echo "ℹ️ Solo se renovó el token y se exportaron las variables."
fi