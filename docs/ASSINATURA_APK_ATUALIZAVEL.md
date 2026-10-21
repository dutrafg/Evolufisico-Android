# Assinatura permanente: atualização sem reinstalar

## Regras obrigatórias

1. O app instalado e o APK novo devem ter o **mesmo applicationId**. O APK principal usa `br.com.evolufisico.android`. Os APKs DEBUG com sufixo `.alpha88` ou `.beta2` são instalações diferentes.
2. Os APKs devem ser assinados pelo **mesmo certificado original**. Chaves aleatórias geradas a cada CI não servem para atualização.
3. O novo `versionCode` precisa ser maior. A atualização normal do Android conserva os dados do aplicativo quando estes critérios são satisfeitos.
4. Mesmo atendendo aos itens 1–3, faça backup antes do primeiro teste real. Não é possível substituir uma assinatura divergente sem desinstalar, salvo mecanismos de rotação previamente configurados e compatíveis.

## Workflow do GitHub

Existe `.github/workflows/android-publish-signed-release.yml`, disparado manualmente em `main`. Usa os GitHub Actions Secrets `EF_KEYSTORE_BASE64`, `EF_KEYSTORE_PASSWORD`, `EF_KEY_ALIAS`, `EF_KEY_PASSWORD`, e a variável `EF_SIGNING_CERT_SHA256` que fixa a impressão do certificado. O workflow falha de forma segura se faltar uma delas e confere identidade, versão e assinatura antes de publicar.

**A chave de assinatura não está neste repositório.** Para atualizar uma instalação existente, é obrigatório obter a mesma chave que assinou aquele APK. Se foi instalado APK DEBUG gerado por um CI descartável, a mesma assinatura pode estar irrecuperável. Nesse caso exporte backup e migre para um APK de release com assinatura persistente; não desinstale até conferir o backup.

## Configurar sem expor chave

No computador, no Android Studio ou via `keytool`, crie o keystore PKCS#12 e guarde uma cópia redundante offline e a senha em gerenciador de segredos. Não publique o arquivo `.jks`/`.p12` no GitHub. Adicione o conteúdo Base64 como secret do repositório e as três credenciais restantes como secrets. Adicione a impressão do certificado SHA-256 à variável indicada. Se pretende atualizar um APK atual, compare **antes** a impressão digital exibida por `apksigner verify --print-certs` dos dois APKs; se divergir, NÃO instale por cima.

## Estado de liberação

A ausência de confirmação da configuração dos secrets e a falta de teste físico de atualização impedem a declaração de Beta distribuível. Um APK debug isolado pode ser fornecido exclusivamente para validação funcional.
