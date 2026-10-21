# EvoluFísico Android — Beta 5 | Plano de 10.000 etapas

**Referências verificadas em 21/10/2026:** [`dutrafg/Evolufisico2`](https://github.com/dutrafg/Evolufisico2) e [`dutrafg/Evolufisico-Android`](https://github.com/dutrafg/Evolufisico-Android). **Versão-alvo:** 91.0.0-beta.5. **10.000 etapas são planejamento, não 10.000 entregas.**

20 áreas × 5 funcionalidades × 5 operações × 20 verificações = **10.000 checkpoints individuais e numerados**. Todos começam pendentes; evidências de implementação ficam no relatório de paridade e nos testes automatizados. Não classificar como quase funcional sem medir os fluxos essenciais ponta a ponta.

- [Bloco 01 — Cadastro e identidade do cliente e Avaliação física básica](BETA_5_10000_ETAPAS/BLOCO_01.md) · 00001–01000
- [Bloco 02 — Perimetria e medidas e Composição corporal](BETA_5_10000_ETAPAS/BLOCO_02.md) · 01001–02000
- [Bloco 03 — Formulários e Evolução e histórico](BETA_5_10000_ETAPAS/BLOCO_03.md) · 02001–03000
- [Bloco 04 — Relatórios e impressão e Cobranças e serviços](BETA_5_10000_ETAPAS/BLOCO_04.md) · 03001–04000
- [Bloco 05 — Agenda e recorrências e Metas e acompanhamento](BETA_5_10000_ETAPAS/BLOCO_05.md) · 04001–05000
- [Bloco 06 — Anotações e prontuário e Painel e navegação](BETA_5_10000_ETAPAS/BLOCO_06.md) · 05001–06000
- [Bloco 07 — Dados e migração e Backup e restauração](BETA_5_10000_ETAPAS/BLOCO_07.md) · 06001–07000
- [Bloco 08 — Cofre e privacidade e Operação offline](BETA_5_10000_ETAPAS/BLOCO_08.md) · 07001–08000
- [Bloco 09 — Configurações e aparência e Responsividade e tablets](BETA_5_10000_ETAPAS/BLOCO_09.md) · 08001–09000
- [Bloco 10 — Testes e segurança e Versões e distribuição](BETA_5_10000_ETAPAS/BLOCO_10.md) · 09001–10000

## Prioridades de implementação

**P0:** dados dos clientes, avaliação física ampliada, integridade das avaliações e backups, fluxos de criar/editar/excluir/consultar, relatórios mínimos funcionais.

**P1:** bioimpedância e protocolos apenas com fórmulas e validações metodológicas verificadas; formulário em etapas com rascunho; evolução por indicadores; migração assistida do web para Android.

**P2:** acabamento visual, relatórios avançados, diagnósticos estendidos, otimizações de tablets e acessibilidade.

**Portão da Beta quase funcional:** cadastro, avaliação, anamnese, evolução, cobrança, agenda, PDF e backup devem passar pelo teste funcional completo. A conformidade de segurança e atualização in-place continua requisito para distribuição com dados reais.
