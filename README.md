# EvoluFísico Android — 0.2.0-alpha

Aplicativo **Android nativo**, independente das versões Web, Windows e Linux, em **Kotlin / Jetpack Compose / Material Design 3**. Idioma: português brasileiro. Android 10+.

## Recursos implementados

- **Clientes:** cadastrar, editar e excluir; IDs estáveis e armazenamento AES-256-GCM (Android Keystore), com escrita atômica.
- **Avaliações — Fase 2:** escolher cliente, criar/editar/excluir avaliações, consultar histórico individual ordenado pela data.
- **Anamnese:** objetivo, nível (iniciante/intermediário/avançado), frequência semanal e campo de limitações/cuidados.
- **Antropometria:** data, peso, altura, cintura, quadril, braço e coxa, com valores obrigatórios e opcionais validados.
- **Indicadores automáticos:** IMC e relação cintura/quadril (RCQ), sem classificação ou diagnóstico clínico.
- **Proteção dos vínculos:** a exclusão de um cliente com avaliações é bloqueada; exclua as avaliações desse cliente primeiro.
- **Persistência de avaliações:** `assessments.enc` separado de `clients.enc`, ambos com AES-GCM, chave mantida no Android Keystore. Falhas de leitura/salvamento são exibidas e os dados não são descartados silenciosamente.
- **Interface:** modo claro/escuro persistido, telas Início, Clientes, Avaliações, Evolução, Cobranças, Configurações.
- **Sem permissão de Internet** e sem integração com Google Drive.

Os dados de anamnese são sensíveis. A criptografia de arquivos não substitui autenticação no aplicativo: **não use com dados reais de pacientes/clientes até concluir PIN/biometria, política de backup e testes de segurança**.

## Abrir no Android Studio

Abra esta pasta como projeto Gradle, com Android SDK API 36, Java/JDK 17 e Gradle 8.13. O projeto publica a configuração `gradle-wrapper.properties`, mas ainda não inclui o binário `gradle-wrapper.jar`: execute `gradle wrapper --gradle-version 8.13` uma vez num ambiente com Gradle instalado, ou use Gradle 8.13 diretamente.

Compilar: `gradle :app:assembleDebug`. Testes: `gradle :app:testDebugUnitTest`.

O workflow em `.github/workflows/android-ci.yml` executa essas etapas no GitHub Actions e, se ambas passarem, publica um APK de depuração no artifact **Evolufisico-Android-debug**. Consulte a aba [Actions](https://github.com/dutrafg/Evolufisico-Android/actions); nunca presuma que a compilação passou antes de conferir a execução.

## Limitações e próximas etapas

- Avaliações da versão Web não são importadas automaticamente. Exigem um migrador validado com consentimento e verificação de dados.
- Protocolos completos de dobras cutâneas, bioimpedância, fórmulas específicas de percentual de gordura e PDF clínico ainda não foram implementados.
- Evolução, Cobranças, PIN/biometria, exportação, backup e restauração ainda estão pendentes.
- O formato atual (JSON cifrado) é adequado ao protótipo, não substitui banco transacional com schema/versionamento para produção.
- Desinstalar o app ou perder o Keystore pode tornar os dados irrecuperáveis enquanto não existir backup cifrado/exportação.

Veja `ROADMAP.md` e `QA.md` para o escopo e a verificação.
