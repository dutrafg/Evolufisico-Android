# Roadmap — EvoluFísico Android

## Fase 1 — Fundação
- [x] Kotlin, Compose, Material Design 3, Android 10+.
- [x] Clientes, IDs estáveis e criptografia local Android Keystore.
- [x] Builds e testes unitários via GitHub Actions.
- [ ] PIN/biometria e banco transacional robusto.

## Fase 2 — Avaliações
- [x] Anamnese básica, medições e histórico vinculados ao cliente.
- [x] IMC/RCQ, validação e armazenamento cifrado.
- [ ] Pollock, dobras cutâneas, bioimpedância, migração de dados Web.

## Fase 3 — Evolução
- [x] Gráficos, agrupamento mensal/trimestral e PDF nativo.
- [x] Exportação PDF por seletor Android.
- [ ] Testes instrumentados do PDF em Android real.

## Fase 4 — Cobranças e dados (0.4.0-alpha)
- [x] Cadastro de cobranças em centavos inteiros por cliente.
- [x] Pendentes/recebidas, resumo e botão Receber com confirmação.
- [x] Arquivo financeiro AES-GCM, validação e proteção de vínculos.
- [x] Backup portátil cifrado por senha, incluindo clientes, avaliações e cobranças.
- [x] Pasta selecionável via SAF, exportação manual com arquivo novo por execução.
- [x] Importação criptografada com validação prévia e bloqueio de sobrescrita de bases não vazias.
- [x] Testes unitários de centavos e criptografia portátil.
- [ ] **Backup automático e sincronização contínua** de pasta; criar estratégia segura de gerenciamento de chaves antes de ativar.
- [ ] Mesclagem e substituição transacional após homologação; restauração atual aceita **somente base vazia**.
- [ ] Parcelamentos, edição/estorno de recebimentos e cobrança recorrente.

## Fase 5 — Homologação
- [ ] Testes instrumentados para DocumentProvider, restauração real e interrupção de energia.
- [ ] Autenticação, segurança, acessibilidade, QA Android físico, assinatura de AAB.
