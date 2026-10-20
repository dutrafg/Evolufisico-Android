# QA — EvoluFísico Android 0.25.0-alpha

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

## Investigação de incompatibilidade de assinatura (0.20.1-alpha)

- [x] Reproduzida por comparação dos certificados extraídos dos APKs debug anteriores: `0.15.0-alpha` = `CCFD2796386C85221B0201750296D0554010F6EA1717546451D4FD5D8F4EF884`; `0.20.0-alpha` = `B3DE359672040E308185822EF7FAC4B80063630E1B89B246A5B8A84302631A20`.
- [x] Confirmação: certificados de assinatura diferentes tornam **impossível** uma atualização direta preservando a instalação.
- [x] Diagnóstico no aplicativo exibe versão instalada e resumo do certificado, e erro de incompatibilidade distingue certificados.
- [x] Artifact debug renomeado com **NAO-ATUALIZAVEL** para evitar confusão.
- [x] Workflow de release publica impressão digital do certificado e, a partir da segunda release, rejeita assinatura diferente da release anterior.
- [ ] Workflow de release **não pode executar ainda** sem uma chave permanente configurada pelo proprietário como segredo do GitHub.
- [ ] Testar com dois APKs release assinados usando exatamente o mesmo keystore, com versionCode crescente, em Android real e com backup externo.
- [ ] CI de `0.20.1-alpha` a conferir antes de marcar a compilação aprovada.

## Backup automático e tela de restauração — fases 21 a 25

- [x] Scheduler WorkManager periódico 24 h com atraso inicial calculado para o horário escolhido
- [x] Seleção da pasta SAF, exigindo permissão persistente de leitura e escrita
- [x] Credencial cifrada com IV gerado por Android Keystore AES-GCM, nunca senha em texto simples em SharedPreferences
- [x] Desativar o agendamento cancela o WorkManager e remove credencial cifrada, sem excluir backups
- [x] Validação não destrutiva do backup gerado por releitura/autenticação/contagem; tentativa de excluir arquivo incompleto
- [x] Registro de último sucesso/erro e status consultável em Configurações
- [x] Restauração bloqueada para base com registros e exige senha de no mínimo 10 caracteres; mensagem explicativa
- [x] JUnit para horário diário e verificação de argumentos inválidos
- [x] Teste Android instrumentado de selagem, leitura e eliminação da credencial Keystore
- [x] **CI da 0.25.0-alpha aprovada**: [GitHub Actions 39443816780](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39443816780): APK, testes unitários, Android Lint, compilação instrumentada, AAB de release não assinado e artifact debug publicados.
- [x] **3/3 testes instrumentados Android 10 aprovados**: [emulador 39443816033](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39443816033). Exercitam gravação de dados, backup, restauração, política de screenshots e a credencial cifrada no Keystore.
- [x] ZIP APK extraído e verificado sem erros; `app-debug.apk` de **59.353.919 bytes**, SHA-256 `0227720bb10a585950159e6a02d2228752e02ab34caedf45b426ccd9e901da7d`.
- [ ] QA WorkManager em dispositivo físico e provedor SAF real, inclusive reinício/Doze
- [ ] Teste de recuperação com arquivo incompleto e perda de permissões
- [ ] Android não garante disparo exato no minuto, mesmo que horário desejado esteja salvo

**Aviso:** APK debug CI usa assinatura efêmera; não desinstale 0.15.0-alpha ou outro app com dados sem verificar um backup externo. Nenhuma versão alpha dispensa homologação com dados fictícios.
