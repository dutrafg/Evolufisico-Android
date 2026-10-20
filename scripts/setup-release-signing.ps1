# Execute no PowerShell do SEU PC, com JDK 17+ e GitHub CLI autenticado.
# NUNCA suba .p12 ou senhas ao repositório.
$ErrorActionPreference = 'Stop'
$Repo = 'dutrafg/Evolufisico-Android'
if (-not (Get-Command keytool -ErrorAction SilentlyContinue)) { throw 'Instale JDK 17+ e coloque keytool no PATH.' }
if (-not (Get-Command gh -ErrorAction SilentlyContinue)) { throw 'Instale GitHub CLI e execute gh auth login.' }
gh auth status | Out-Null
if ($LASTEXITCODE -ne 0) { throw 'GitHub CLI não autenticado.' }
$Dir = Read-Host 'Pasta PRIVADA (fora de seu repositório) para guardar o keystore'
New-Item -ItemType Directory -Force -Path $Dir | Out-Null
$Key = Join-Path (Resolve-Path $Dir).Path 'evolufisico-release.p12'
if (Test-Path $Key) {
  $Confirm = Read-Host 'Chave já existe. Reutilizar SEM gerar outra? Digite SIM'
  if ($Confirm -cne 'SIM') { throw 'Operação cancelada. A chave antiga permanece.' }
} else {
  & keytool -genkeypair -storetype PKCS12 -keystore $Key -alias evolufisico -keyalg RSA -keysize 3072 -validity 10000 -dname 'CN=EvoluFisico,OU=Android,O=EvoluFisico,C=BR'
  if ($LASTEXITCODE -ne 0) { throw 'Falha ao criar keystore.' }
}
$SecurePassword = Read-Host 'Digite a mesma senha do keystore (será ocultada)' -AsSecureString
$Password = [System.Net.NetworkCredential]::new('', $SecurePassword).Password
try {
  $Password | gh secret set EF_KEYSTORE_PASSWORD -R $Repo
  if ($LASTEXITCODE -ne 0) { throw 'Falha ao enviar EF_KEYSTORE_PASSWORD' }
  $Password | gh secret set EF_KEY_PASSWORD -R $Repo
  if ($LASTEXITCODE -ne 0) { throw 'Falha ao enviar EF_KEY_PASSWORD' }
  'evolufisico' | gh secret set EF_KEY_ALIAS -R $Repo
  if ($LASTEXITCODE -ne 0) { throw 'Falha ao enviar alias' }
  [Convert]::ToBase64String([IO.File]::ReadAllBytes($Key)) | gh secret set EF_KEYSTORE_BASE64 -R $Repo
  if ($LASTEXITCODE -ne 0) { throw 'Falha ao enviar chave criptográfica' }
  $KeyInfo = & keytool -list -v -storetype PKCS12 -keystore $Key -alias evolufisico
  if ($LASTEXITCODE -ne 0) { throw 'Não foi possível consultar o keystore.' }
  $Fingerprint = ($KeyInfo | Select-String 'SHA256:\s*([0-9A-Fa-f:]{95})' | Select-Object -First 1).Matches.Groups[1].Value.Replace(':','').ToUpperInvariant()
  if ($Fingerprint -notmatch '^[0-9A-F]{64}$') { throw 'Impressão digital inválida.' }
  $Fingerprint | gh variable set EF_SIGNING_CERT_SHA256 -R $Repo
  if ($LASTEXITCODE -ne 0) { throw 'Falha ao cadastrar fingerprint.' }
  Write-Host "Certificado SHA-256 (público): $Fingerprint"
  Write-Host "Pronto. SALVE duas cópias privadas do keystore em locais separados."
  Write-Host 'Execute Actions > Publish signed Android APK usando a tag igual à versão do app.'
} finally {
  $Password = $null
  $SecurePassword.Dispose()
}
