# Atualização segura — EvoluFísico Android

## Por que 0.15.0-alpha não atualiza para 0.20.0-alpha

O Android exige certificado de assinatura compatível para atualizar um aplicativo sem apagá-lo. O GitHub Actions gerou os APKs DEBUG anteriores com certificados temporários diferentes:

| APK | SHA-256 da assinatura |
|---|---|
| 0.15.0-alpha | `CCFD2796386C85221B0201750296D0554010F6EA1717546451D4FD5D8F4EF884` |
| 0.20.0-alpha | `B3DE359672040E308185822EF7FAC4B80063630E1B89B246A5B8A84302631A20` |

Não é possível alterar a identidade de assinatura de uma instalação existente com um botão, o comando `adb install -r` ou um novo APK, **sem a chave original**.

## Migração sem perder registros

1. **Não desinstale a 0.15.0-alpha.** Abra Configurações, informe uma senha longa e exporte o backup `.efbak` para Downloads, Documentos, cartão externo ou um provedor SAF confiável. Confirme que o arquivo realmente aparece na pasta.
2. Copie esse arquivo para um segundo dispositivo ou computador. Guarde a senha separadamente.
3. Descriptografe e **verifique a integridade com uma ferramenta offline confiável**, sem enviar o arquivo ou a senha para terceiros. O ChatGPT disponibilizou um HTML offline para essa validação independente. Esta etapa não substitui um teste de restauração final.
4. Configure uma chave de assinatura permanente no GitHub (abaixo) e publique o primeiro APK RELEASE oficial do projeto.
5. **Somente após confirmar o backup e a assinatura da nova release**, desinstale a versão DEBUG antiga, instale a release oficial e restaure o `.efbak` na base nova e vazia.
6. Confira clientes, avaliações, formulários e cobranças. Daí em diante, mantenha a mesma chave de release em todas as versões futuras, com `versionCode` crescente.

**Aviso:** se a 0.15.0-alpha instalada não tiver exportação de backup funcional, pare aqui. Não desinstale nem apague os dados. Será necessário construir outra estratégia de migração segura.

## Criar o keystore (uma única vez)

Crie a chave permanente no seu **computador**, preferencialmente Android Studio / JDK 17, com o comando:

```sh
keytool -genkeypair -v -keystore evolufisico-distribution.jks -alias evolufisico \
  -keyalg RSA -keysize 3072 -validity 10000
```

Guarde o arquivo e as senhas em cofre seguro, separado do repositório. A perda dessa identidade também impedirá futuras atualizações.

No GitHub, em **Settings > Secrets and variables > Actions**, cadastre:

- `EF_KEYSTORE_BASE64`: conteúdo Base64 integral do arquivo `.jks`, gerado localmente (no Windows PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\\caminho\\evolufisico-distribution.jks"))`).
- `EF_KEYSTORE_PASSWORD`: senha do keystore.
- `EF_KEY_ALIAS`: `evolufisico`.
- `EF_KEY_PASSWORD`: senha privada do alias.

Execute **Actions > Publish signed Android APK > Run workflow**, na branch `main`, com a tag que corresponde ao `versionName` do `app/build.gradle.kts` (por exemplo, `v0.20.1-alpha`). O workflow verifica a assinatura e publica o APK oficial em [GitHub Releases](https://github.com/dutrafg/Evolufisico-Android/releases). Em releases seguintes compara o certificado com o último publicado e recusa assinatura diferente.

**Nunca use os artifacts `QA-DEBUG-NAO-ATUALIZAVEL` para atualizações diretas em outros dispositivos.** Eles são apenas para testes em instalações descartáveis.

## O que o atualizador faz

O atualizador consulta GitHub Releases, baixa APK oficial via HTTPS ou escolhe arquivo local e verifica pacote, versionCode crescente e certificado. Quando compatível, abre o instalador padrão Android, que solicita consentimento. Isso não é uma atualização silenciosa igual ao mecanismo exclusivo da Play Store.

Relatórios PDF não são backups restauráveis. A desinstalação padrão remove os dados locais e o Android Keystore.
