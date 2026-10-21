# Beta 11 — atualizações sem criar um segundo aplicativo

## Diagnóstico dos APKs anteriores

- O **DEBUG Beta 10** possuía `applicationId=br.com.evolufisico.android.beta10`.
- O **DEBUG Beta 11** possui `applicationId=br.com.evolufisico.android.beta11`.
- O **RELEASE oficial** mantém `applicationId=br.com.evolufisico.android`.
- Os APKs DEBUG publicados como artefatos de CI recebem uma **assinatura temporária**, diferente entre execuções.

O Android só faz atualização no lugar quando **pacote e assinatura coincidem**, com `versionCode` crescente. Portanto, **não há correção no instalador que consiga atualizar diretamente o DEBUG Beta 10 com o DEBUG Beta 11 nem substituir uma assinatura temporária perdida**.

## Como prosseguir sem perder dados

1. **Não desinstalar nenhuma das cópias atuais.**
2. Na cópia que contém os registros, usar **Configurações → Dados e privacidade → Backup**; exportar `.efbak` para fora do aplicativo e conferir se o arquivo pode ser restaurado.
3. No GitHub, gerar e publicar o APK **RELEASE assinado** pela chave fixa, após o CI e QA. É o **único canal de atualizações oficiais**, com pacote `br.com.evolufisico.android`.
4. Caso o aparelho já tenha o RELEASE oficial com **mesmo certificado**, instalar a nova versão sobre a antiga.
5. Caso o aparelho só tenha DEBUG (Beta 10 ou Beta 11), a mudança para o RELEASE é uma **migração única**: instalar o RELEASE separadamente, restaurar o backup nele e comparar os registros antes de remover qualquer cópia de testes.
6. Nas próximas versões do RELEASE, manter o mesmo `applicationId`, a mesma chave de assinatura permanente e incrementar `versionCode`.

## Build e publicação

- **Usar**: `Publish signed Android APK`, escolhendo a branch `beta-11-interface-polish` com a tag `v97.0.0-beta.11`.
- A publicação exige `EF_KEYSTORE_BASE64`, `EF_KEYSTORE_PASSWORD`, `EF_KEY_ALIAS`, `EF_KEY_PASSWORD` (secrets) e `EF_SIGNING_CERT_SHA256` (variable) no GitHub, definidos pelo proprietário.
- O workflow compara o certificado com o fingerprint fixado e, se existir release anterior, com o certificado da release anterior. **Jamais cria outra chave no lugar da original.**
- **Não distribuir como atualização** os artefatos `QA-DEBUG-NAO-ATUALIZAVEL` ou o AAB unsigned. Eles servem apenas para testes.
- Testar o comportamento em Android real antes de apagar o aplicativo antigo. Uma release nova não migra automaticamente os dados privados de outro `applicationId`.

## Para o técnico conferir

```bash
aapt dump badging aplicativo.apk | head -1
apksigner verify --print-certs aplicativo.apk
adb shell pm list packages | grep evolufisico
```

O backup exportado deve ser mantido até concluir a restauração e validar os dados.
