# QA — EvoluFísico Android 0.20.0-alpha

## Alterações e verificações adicionadas
- [x] Formulários: busca e filtros sem gravar ou transmitir respostas
- [x] Ficha do cliente: associação dos quatro conjuntos de dados por ID
- [x] PDF de ficha por SAF, com paginação e sem captura do painel
- [x] Cobranças: edição, exclusão e estorno com confirmação; totais mensais em `Long`
- [x] Verificação de arquivo .efbak sem gravação, isolada da restauração
- [x] Extensão do teste instrumentado: inspeção de arquivo criptografado não deve alterar clientes locais
- [x] Testes unitários de resumos mensais, edição e estorno
- [x] **CI da 0.20.0-alpha aprovada**: [GitHub Actions 39438460878](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39438460878): APK, unit tests, Lint, testes instrumentados compilados e AAB não assinado
- [x] **Testes instrumentados Android 10 executados e aprovados**: [emulador 39438460754](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39438460754), 2/2 testes aprovados, sem falhas
- [ ] Testes visuais em smartphone e tablet: PDF, diálogo Ficha, filtros e rolagem
- [ ] Validar backup/atualização em aparelho real e certificado de assinatura estável
- [ ] Auditoria de segurança e LGPD; revisão de PDFs com dados sensíveis

## Histórico de homologação

- Fases 11–15: [GitHub CI aprovada](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39436219594).
- 2 testes instrumentados executados com sucesso em [emulador Android 10](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39436219723), incluindo Keystore, backup e restauração com dados fictícios.

**Atenção:** aprovação de compilação ou testes unitários não substitui QA no aparelho do usuário. A atualização por cima depende de assinatura compatível; desinstalar apaga dados internos e chaves Android Keystore.

## Artefatos conferidos

- [x] ZIP debug de 0.20.0-alpha verificado sem corrupção, contendo `app-debug.apk`.
- [x] APK debug extraído com **58.187.368 bytes** e SHA-256 `67b3f4862065756cf2c3ac07ab57838fc17210f64877031e1bdf4a035a4eea69`.
- [ ] **Teste com seu aparelho Android e dados fictícios ainda pendente**, incluindo PDFs exportados e backup em pasta externa.
