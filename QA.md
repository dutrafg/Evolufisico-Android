# QA — EvoluFísico Android 0.20.0-alpha

## Alterações e verificações adicionadas
- [x] Formulários: busca e filtros sem gravar ou transmitir respostas
- [x] Ficha do cliente: associação dos quatro conjuntos de dados por ID
- [x] PDF de ficha por SAF, com paginação e sem captura do painel
- [x] Cobranças: edição, exclusão e estorno com confirmação; totais mensais em `Long`
- [x] Verificação de arquivo .efbak sem gravação, isolada da restauração
- [x] Extensão do teste instrumentado: inspeção de arquivo criptografado não deve alterar clientes locais
- [x] Testes unitários de resumos mensais, edição e estorno
- [ ] CI desta versão: compilar APK, unit tests, Lint, testes instrumentados compilados e AAB
- [ ] Executar teste instrumentado Android 10 desta versão
- [ ] Testes visuais em smartphone e tablet: PDF, diálogo Ficha, filtros e rolagem
- [ ] Validar backup/atualização em aparelho real e certificado de assinatura estável
- [ ] Auditoria de segurança e LGPD; revisão de PDFs com dados sensíveis

## Histórico de homologação

- Fases 11–15: [GitHub CI aprovada](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39436219594).
- 2 testes instrumentados executados com sucesso em [emulador Android 10](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39436219723), incluindo Keystore, backup e restauração com dados fictícios.

**Atenção:** aprovação de compilação ou testes unitários não substitui QA no aparelho do usuário. A atualização por cima depende de assinatura compatível; desinstalar apaga dados internos e chaves Android Keystore.
