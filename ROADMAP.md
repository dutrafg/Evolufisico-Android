# EvoluFísico Android — Roadmap

## Fase 1 — Fundação
- [x] Compose + Material Design 3, Android 10+.
- [x] Cadastro de clientes, IDs estáveis, AES-GCM/Android Keystore, tema.
- [x] Compilação CI e unit tests.
- [ ] PIN/biometria, recuperação segura, banco transacional.

## Fase 2 — Avaliações
- [x] Avaliações, anamnese básica, medidas, IMC e RCQ vinculados a cada cliente.
- [x] Histórico, validação, edição e exclusão, arquivo criptografado.
- [ ] Protocolos avançados (Pollock, dobras cutâneas, BIA), campos especializados e migração da Web.
- [ ] Testes instrumentados com dados fictícios e dispositivos reais.

## Fase 3 — Evolução e PDFs (0.3.0-alpha)
- [x] Comparativo por registro, mensal e trimestral (última medição válida de cada período).
- [x] Peso, IMC, cintura, quadril, braço, coxa e RCQ, sem projeções clínicas.
- [x] Gráfico nativo Compose com histórico acessível em texto.
- [x] PDF nativo paginado com gráfico e tabela, sem o painel do app.
- [x] Exportação pela pasta de escolha do usuário via SAF, sem armazenamento amplo.
- [x] Testes unitários para cálculo e agrupamento (execução no CI).
- [ ] Testes instrumentados de criação/abertura do PDF, paginação, visualização em aparelho real.
- [ ] Personalização do cabeçalho/identidade profissional e relatórios combinando indicadores.

## Fase 4 — Cobranças e dados
- [ ] Contas a receber, pagamentos pendentes, ação Receber.
- [ ] Armazenamento SAF, backup cifrado e restauração com integridade.

## Fase 5 — Homologação
- [ ] Auditoria de segurança, acessibilidade, QA Android real, AAB assinado.
