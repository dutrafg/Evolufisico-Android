#!/usr/bin/env bash
# Execute NO SEU computador. A chave NUNCA vai para um commit Git.
set -euo pipefail
REPO="dutrafg/Evolufisico-Android"
command -v keytool >/dev/null || { echo "Instale JDK 17+."; exit 1; }
command -v gh >/dev/null || { echo "Instale e autentique GitHub CLI: gh auth login"; exit 1; }
gh auth status >/dev/null
read -r -p "Diretório PRIVADO para guardar o keystore: " TARGET
mkdir -p "$TARGET"
KEYSTORE="$TARGET/evolufisico-release.p12"
if [ -e "$KEYSTORE" ]; then
  read -r -p "Keystore existente. Reutilizar SEM gerar outra chave? (digite SIM): " CONFIRM
  [ "$CONFIRM" = "SIM" ] || { echo "Operação cancelada."; exit 1; }
else
  umask 077
  keytool -genkeypair -storetype PKCS12 -keystore "$KEYSTORE" \
    -alias evolufisico -keyalg RSA -keysize 3072 -validity 10000 \
    -dname 'CN=EvoluFisico,OU=Android,O=EvoluFisico,C=BR'
fi
echo "A senha solicitada a seguir é a do keystore que você acabou de criar."
read -r -s -p "Senha do keystore (não aparecerá na tela): " PASSWORD
printf '\n'
printf '%s' "$PASSWORD" | gh secret set EF_KEYSTORE_PASSWORD -R "$REPO"
printf '%s' "$PASSWORD" | gh secret set EF_KEY_PASSWORD -R "$REPO"
printf '%s' 'evolufisico' | gh secret set EF_KEY_ALIAS -R "$REPO"
base64 < "$KEYSTORE" | tr -d '\n' | gh secret set EF_KEYSTORE_BASE64 -R "$REPO"
FINGERPRINT="$(keytool -list -v -storetype PKCS12 -keystore "$KEYSTORE" \
  -alias evolufisico |
  sed -n 's/^[[:space:]]*SHA256: //p' | head -1 | tr -d ':' | tr '[:lower:]' '[:upper:]')"
[[ "$FINGERPRINT" =~ ^[A-F0-9]{64}$ ]] || { echo "Impressão digital não reconhecida."; exit 1; }
printf '%s' "$FINGERPRINT" | gh variable set EF_SIGNING_CERT_SHA256 -R "$REPO"
unset PASSWORD
echo "Identidade permanente cadastrada no GitHub. SHA-256 público: $FINGERPRINT"
echo "GUARDE 2 cópias offline do arquivo $KEYSTORE e a senha fora do repositório."
echo "Agora acesse Actions > Publish signed Android APK e use a tag da versão atual."
