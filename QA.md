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
- [ ] **Build e testes da 0.3.0-alpha ainda precisam ser confirmados no GitHub Actions**.
- [ ] Teste instrumentado/visual do PDF, inclusive caracteres e relatórios com múltiplas páginas.
- [ ] Teste com aparelho Android 10+ físico, orientação, acessibilidade, modo escuro e seletor de arquivos.
- [ ] Auditoria de proteção ao exportar informações sensíveis e proteção por PIN/biometria.
- [ ] Backups seguros e migração da Web não implementados.

## Histórico de QA anterior

0.2.0-alpha: [build e testes aprovados](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39416578439).

**Atenção:** PDFs exportados não são criptografados; o projeto ainda é um protótipo com funções de segurança pendentes.
