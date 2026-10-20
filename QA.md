# QA — EvoluFísico Android v0.10.0-alpha

## Cobertura adicionada (implementação)
- [x] Validação referencial entre clientes, avaliações e cobranças, integridade ao carregar
- [x] Perguntas por nível, validação de campos, compatibilidade de leitura com JSON cifrado anterior
- [x] Edição e estorno de cobranças, resumo mensal por pagamento/vencimento, testes com centavos
- [x] NavigationRail de desktop/tablet, rolagens em formulários
- [x] Checagem e download de GitHub Releases e APK local via SAF em Configurações
- [x] Antes de iniciar instalação: packageName, versionCode crescente e assinatura digital
- [x] HTTPS, lista de hosts de redirect permitidos, SHA-256 quando publicado, limite 250MB e FileProvider
- [x] Confirmação de backup criptografado externo e instalação sujeita ao consentimento do Android
- [x] Workflow de release assinado com segredos privados, bloqueado se não configurados
- [x] Testes JUnit novos de política de atualização, integridade e cálculos

## Não homologado
- [ ] CI da 0.10.0-alpha: APK, JUnit, Lint, testes instrumentados compilados, AAB — verificar Actions
- [ ] Execução dos testes instrumentados em emulador Android
- [ ] Atualização de APK em aparelho real preservando dados locais/Keystore
- [ ] Recusa de APK com outra assinatura em aparelho real
- [ ] Restauração de .efbak em instalação vazia após desinstalação
- [ ] Publicação da primeira release APK estável assinada: inexistente até configurar secrets
- [ ] Segurança LGPD, UX leitor de tela, testes Android 10 a versões recentes

Build da 0.6.0-alpha: [GitHub Actions aprovado](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39427100179).
**Continua protótipo: não utilizar dados reais sensíveis.**
