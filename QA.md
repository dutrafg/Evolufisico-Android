# QA — EvoluFísico Android 0.3.0-alpha

## Revisão de implementação e cobertura dos testes

- [x] Série de evolução usa **ID do cliente**; registros de outros clientes excluídos.
- [x] Ordenação por data ISO com desempate determinístico por ID.
- [x] Agrupamento mensal e trimestral escolhe o **último registro com medida presente**, evitando preencher lacunas fictícias.
- [x] Variação absoluta/percentual de duas ou mais medições; uma medição não produz falso progresso.
- [x] NaN/datas inválidas e medidas ausentes são ignoradas para o indicador selecionado.
- [x] Gráfico nativo, histórico textual e seleção de indicador/período.
- [x] PDF gerado via PdfDocument, com quebra de páginas e sem captura da interface principal.
- [x] PDF salvo por Android SAF (CreateDocument), sem permissões genéricas de armazenamento.
- [x] JUnit adicionado para isolamento de clientes, agrupamentos, valores ausentes e variações.
- [x] **Build e testes da 0.3.0-alpha aprovados**: [GitHub Actions 39417903230](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39417903230), commit `e7ee4d02c3f50ecbe3c56d4cef961a62f1a74eb6`. Etapas `:app:assembleDebug` e `:app:testDebugUnitTest` concluídas com sucesso.
- [x] Artifact `Evolufisico-Android-debug` gerado. ZIP e APK extraído tiveram integridade verificada; SHA-256 APK: `df8c990ca10814738de6f9dda1a05145ae3b73c7a670ffb897c70c1bc4b52cd7`.
- [ ] Teste instrumentado/visual do PDF, inclusive caracteres e relatórios com múltiplas páginas.
- [ ] Teste com aparelho Android 10+ físico, orientação, acessibilidade, modo escuro e seletor de arquivos.
- [ ] Auditoria de proteção ao exportar informações sensíveis e proteção por PIN/biometria.
- [ ] Backups seguros e migração da Web não implementados.

## Histórico de QA anterior

0.2.0-alpha: [build e testes aprovados](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39416578439).

**Atenção:** PDFs exportados não são criptografados; o projeto ainda é um protótipo com funções de segurança pendentes.
