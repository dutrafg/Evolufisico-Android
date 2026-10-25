# Assinatura estável e atualizações Android

A assinatura precisa ser **a mesma** em todas as versões instaladas em atualização.
Trocar apenas `versionCode` ou `applicationId` não corrige um APK com certificado diferente.

## Regras

1. Mantenha o pacote `br.com.evolufisico.nativo`.
2. Mantenha o arquivo de chave e o alias de release em armazenamento privado e recuperável.
3. Configure os segredos do repositório GitHub: `EF_KEYSTORE_BASE64`, `EF_RELEASE_STORE_PASSWORD`, `EF_RELEASE_KEY_ALIAS` e `EF_RELEASE_KEY_PASSWORD`.
4. Configure a variável `EF_APK_READY=true` **somente após** todas as 100 tarefas de aceite, testes automatizados, testes em aparelhos, dados e migrações terem sido aprovados.
5. A partir desse momento, execute manualmente o workflow `android-signed-release.yml` na branch homologada; ele faz testes, lint, compilação assinada, conferência criptográfica da assinatura e arquivamento do APK com SHA-256.
6. Guarde o fingerprint do certificado e compare com o da versão anterior antes de instalar como atualização.

## Limites e incompatibilidades

- O workflow não executa publicação automática; sem sinal de prontidão e segredos, não gera um APK.
- O certificado de um APK beta assinado com outra chave não pode ser substituído por um certificado novo numa atualização por cima. Preservar uma instalação antiga requer a chave original; nunca force uma atualização de assinatura incompatível.
- Se a chave antiga não estiver disponível, exporte e confira backup **antes** de qualquer desinstalação e restaure somente na nova instalação homologada.
- Não armazene keystore, senhas ou segredo codificado diretamente no código público, na documentação ou nos logs.
- O APK assinado não é considerado estável sem testes de instalação e restauração em dispositivo real.

## Variáveis do Gradle

O build `release` recebe a chave pelos parâmetros de ambiente `EF_RELEASE_KEYSTORE_FILE`, `EF_RELEASE_STORE_PASSWORD`, `EF_RELEASE_KEY_ALIAS` e `EF_RELEASE_KEY_PASSWORD`. O processo de CI usa o `RUNNER_TEMP` e elimina a cópia transitória da chave ao final.
