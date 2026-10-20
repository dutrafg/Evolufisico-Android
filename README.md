# EvoluFísico Android — v0.10.0-alpha

App Android nativo (Kotlin / Compose / Material 3), Android 10+. Continuação independente das versões Web, Windows e Linux.

## Fases 7 a 10
- **Fase 7 — Segurança:** conferência dos vínculos entre clientes, avaliações e cobranças; checagem antes de abrir os dados; recuperação de restauração interrompida; limite de 90 s para exceção de sessão em seletor de arquivos; bloqueio Android. **Capturas de tela liberadas nas versões alpha/beta/RC para testes**, com `FLAG_SECURE` ativado automaticamente na versão estável (número sem sufixo).
- **Fase 8 — Anamnese:** campos opcionais para histórico de saúde, sono/hábitos e experiência de treinamento com perguntas-exemplo diferentes para Iniciante, Intermediário e Avançado. Não preencher automaticamente respostas pessoais. Dados antigos legíveis e campos novos incluídos no banco AES-GCM e backup portátil.
- **Fase 9 — Cobranças:** editar ou excluir lançamentos pendentes, estornar recebimentos com confirmação, resumos mensais, pendências e filtros. Valores em centavos `Long`. Estorno aqui altera status local, **não é operação bancária**.
- **Fase 10 — Responsividade:** menu lateral em telas >=840dp, menu inferior no celular, melhores rolagens em avaliações e cobranças.
- **Configurações > Atualizações:** consultar APKs oficiais publicados em [GitHub Releases](https://github.com/dutrafg/Evolufisico-Android/releases) ou selecionar APK local. Verificar `applicationId`, `versionCode` superior e assinatura digital idêntica antes de abrir o instalador oficial do Android. Requer autorização para instalação por esta fonte; não instala silenciosamente. HTTPS, hosts restritos, SHA-256 se anunciado na release, limite 250MB, cache privado e FileProvider.
- **Backup:** exportar `.efbak` para pasta externa via seletor Android e senha (AES-256-GCM e PBKDF2). Restaurar somente em base vazia.

## Atualização x desinstalação: proteção de dados

Uma **atualização por cima** com o mesmo identificador de pacote e assinatura compatível normalmente preserva arquivos internos e chaves Android Keystore. O sistema Android decide e pede consentimento.

Já uma **desinstalação normal remove os dados privados e as chaves Android Keystore**: não é tecnicamente possível assegurar sua permanência após uma desinstalação comum. **Antes de desinstalar**, exporte o backup protegido `.efbak` para um local fora dos dados do app, guarde a senha e confirme o arquivo. Após instalar a versão nova, restaure na base vazia. O PDF não é backup restaurável.

**Os APKs de depuração anteriores** foram criados por execuções diferentes de CI e podem ter assinaturas temporárias diferentes. Portanto, a atualização por cima pode ser recusada; o atualizador identifica essa situação. NUNCA desinstale sem backup externo conferido.

## Assinatura estável para próximas versões

O workflow manual `.github/workflows/android-publish-signed-release.yml` exige quatro GitHub Actions Secrets:
`EF_KEYSTORE_BASE64`, `EF_KEYSTORE_PASSWORD`, `EF_KEY_ALIAS` e `EF_KEY_PASSWORD`.
Prepare e guarde uma **única chave privada de distribuição permanente** fora do repositório; configure os segredos pelo GitHub Settings > Secrets and variables > Actions. Execute o workflow na branch main com tag como `v0.10.0-alpha`. O workflow executa testes e Lint, valida com `apksigner` e publica APK assinado no GitHub Releases. Sem segredos, ele para sem publicar. Não existe release com APK no GitHub neste momento; o botão de consulta exibirá ausência até a primeira publicação assinada.

## Compilar e homologar

JDK17, Gradle8.13, SDK Android API36:
```sh
gradle :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:bundleRelease
```
O projeto precisa de `gradle wrapper --gradle-version 8.13` em ambiente local com Gradle para obter os arquivos do wrapper não presentes no repositório.

**Limitações:** v0.10 continua alpha. Atualizações dentro do app são por APK fora da Play Store, requerem interação humana e não garantem dados caso seja necessário desinstalar. Testes instrumentados/visuais de atualização e recuperação ainda precisam de homologação Android física. Relatórios PDF exportados não são criptografados. Proteções LGPD, assinatura de produção e testes de segurança independentes não estão concluídos.
