# EvoluFísico Android — 0.1.0-alpha

Projeto **Android nativo** e independente das versões Web, Windows e Linux.

## Escopo entregue na etapa inicial

- Kotlin + Jetpack Compose + Material Design 3; interface em português.
- Compatível com Android 10+ (minSdk 29).
- Navegação: Início, Clientes, Avaliações, Evolução, Cobranças e Configurações.
- **Clientes**: adicionar, editar e excluir, com validação de nome, IDs estáveis e persistência local.
- **Criptografia local**: registros de nomes protegidos com AES-256-GCM e chave no Android Keystore; falha de leitura/gravação não é mascarada.
- Tema claro/escuro persistido localmente.
- Sem permissão de Internet nem integração com Google Drive.
- Testes unitários de validação e um workflow de compilação CI.

**Importante:** Avaliações, Evolução e Cobranças ainda são *telas de estrutura*, sem lógica de produção. Não há migração de clientes/avaliações da versão web, backup, PDF ou autenticação nesta etapa. A criptografia protege os arquivos em repouso, mas **não equivale a bloqueio de acesso ao aplicativo**.

## Abrir no Android Studio

1. Descompacte a pasta `Evolufisico-Android` e abra-a no Android Studio (`Open`).
2. Configure **JDK 17**, Android SDK **API 36** e Gradle **8.13**.
3. Este pacote contém as configurações do Gradle Wrapper, mas **não inclui o arquivo binário `gradle-wrapper.jar`** porque não estava disponível no ambiente de geração. Instale Gradle 8.13 e execute `gradle wrapper --gradle-version 8.13` na raiz do projeto uma vez, ou ajuste o projeto para usar uma distribuição Gradle local no Android Studio. O comando gera os scripts e JAR oficiais do wrapper.
4. Sincronize o Gradle e execute a configuração `app` num emulador Android 10+ ou celular com depuração USB.
5. Para criar um APK de teste, execute `gradle :app:assembleDebug` (ou `./gradlew :app:assembleDebug` após configurar o wrapper).

Nenhuma compilação Android real foi executada neste ambiente, que não possui o Android SDK/Gradle. Veja `QA.md`.

## Código-fonte no GitHub

Repositório oficial desta edição Android: `https://github.com/dutrafg/Evolufisico-Android`.

O workflow em `.github/workflows/android-ci.yml` é disparado em pushes para `main`, instala o Gradle e o Android SDK, e executa build e testes unitários. A publicação do código não constitui aprovação do build; consulte a aba **Actions** para ver o resultado.

Não copie bancos de dados reais, senhas, certificados, arquivos `local.properties` ou `*.jks` para o repositório.

## Fontes de referência

EvoluFísico Web: `https://github.com/dutrafg/EvoluFisico`. Projeto reescrito nativamente, **não** é uma cópia funcional do app web.
