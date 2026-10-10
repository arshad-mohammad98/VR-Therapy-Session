#!/usr/bin/env bash
set -e

echo "=============================================================================="
echo "  Generating Self-Signed PKCS12 Keystore for VR Therapy WebXR (HTTPS)"
echo "=============================================================================="

# Detect primary LAN IPv4
LAN_IP=$(ip -4 route get 1.1.1.1 2>/dev/null | awk '{print $7}' || hostname -I 2>/dev/null | awk '{print $1}' || echo "127.0.0.1")

if [ -z "$LAN_IP" ]; then
    LAN_IP="127.0.0.1"
fi

echo "[INFO] Detected Local Network IP: ${LAN_IP}"
SAN="dns:localhost,ip:127.0.0.1,ip:${LAN_IP}"
echo "[INFO] Subject Alternative Names (SAN): ${SAN}"

KEYSTORE_NAME="vrtherapy.p12"
KEYSTORE_PASS="vrtherapy123"
ALIAS="vrtherapy"

mkdir -p "src/main/resources"
rm -f "src/main/resources/${KEYSTORE_NAME}" "${KEYSTORE_NAME}"

echo "[INFO] Running keytool to generate RSA 2048-bit certificate valid for 365 days..."
keytool -genkeypair \
    -alias "${ALIAS}" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 365 \
    -keystore "src/main/resources/${KEYSTORE_NAME}" \
    -storetype PKCS12 \
    -storepass "${KEYSTORE_PASS}" \
    -keypass "${KEYSTORE_PASS}" \
    -dname "CN=VR Therapy Platform, OU=Telehealth Rehabilitation, O=Connected VR, L=Academic, ST=Virtual, C=US" \
    -ext "SAN=${SAN}"

cp -f "src/main/resources/${KEYSTORE_NAME}" "${KEYSTORE_NAME}"
if [ -d "target/classes" ]; then
    cp -f "src/main/resources/${KEYSTORE_NAME}" "target/classes/${KEYSTORE_NAME}"
fi

echo "=============================================================================="
echo "[SUCCESS] Keystore generated successfully at src/main/resources/${KEYSTORE_NAME}"
echo "  HTTPS Local URL: https://localhost:8443/"
echo "  HTTPS Phone URL: https://${LAN_IP}:8443/"
echo "=============================================================================="
