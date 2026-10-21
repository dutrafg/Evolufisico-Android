# Beta 10 — Paridade funcional EvoluFísico2 (Web) × EvoluFísico Android

**Referência consultada:** [repositório web](https://github.com/dutrafg/Evolufisico2) na `main` (README e `assets/js/`) e [repositório Android](https://github.com/dutrafg/Evolufisico-Android/tree/beta-10-parity-review) (Jetpack Compose e repositórios). Análise do código; não é certificação funcional em dispositivos físicos.

## Matriz de funcionalidades

| Função | Web EvoluFísico2 | Android Beta 10 | Paridade e prioridade |
|---|---|---|---|
| Área de trabalho e atalhos | Dashboard e módulos principais | Indicadores e favoritos, atalhos diretos para editores | **Parcial**; UX com dados reais e ações avançadas — P1 |
| Clientes | ID permanente, contatos, migração cautelosa | ID estável, telefone/e-mail/nascimento, busca, ficha/PDF | **Parcial**; migração web→Android e fluxos complexos — P0 |
| Avaliação física | Antropometria, dobras, Pollock, medidas e análises | Medidas e IMC/RCQ, quatro perímetros extras, histórico | **Parcial**; protocolos de dobras e cálculos completos não equivalentes — P0 |
| Bioimpedância e laboratório | BIA, Pollock, US Navy, RFM, verificações no Lab | Não identificada equivalência completa de laboratório na estrutura nativa | **Pendente** — P0/P1, validar fórmulas clinicamente |
| Formulários | Formulário personalizado, rascunho em etapas, PDF preenchível (AcroForm) | Templates de anamnese por nível, preenchimento, armazenamento e PDF | **Parcial**; editor dinâmico, rascunhos, PDF editável — P0 |
| Evolução | Análise avançada, comparações, perfis e alertas | Gráfico de tendências por indicador, períodos, PDF e comparativos | **Parcial**; análises e interpretação avançadas — P1 |
| Cobranças | Valores, recebimentos, filtros, cofre, histórico e backup | Valores em centavos, recebimento/estorno, pesquisa, CSV e backup | **Parcial**; sincronizar todos os comportamentos de cobrança/cofre — P1 |
| Agenda | Atendimentos e lembretes com som/vibração, limitações de navegador | Calendário, estados, recorrência, sugestões, CSV/ICS | **Parcial**; preferências e lembretes nativos — P1 |
| Relatórios e PDF | Impressão de avaliações, histórico e relatórios completos | PDFs nativos de cliente, avaliação, evolução e formulário | **Parcial**; revisar paginação, fidelidade de campos e estilos — P1 |
| Diagnóstico e autocorreção | Testes por módulo, exportação técnica, recuperação não destrutiva | Integridade e manutenção locais, testes no CI | **Parcial**; cobertura diagnóstica por módulo — P2 |
| Privacidade | Cofre opcional para alguns dados e controles web | Backup e controles de privacidade, persistência legível em partes do protótipo | **Não homologada para dados reais** — P0 |
| Backup e migração | Backup web com checksum, financeiro e restauração/desfazer | Backup JSON nativo, pasta SAF, rotina diária e restauração | **Parcial**; **formato web NÃO é automaticamente compatível** — P0 |
| Responsividade | CSS corporativo com modos desktop/celular/tablet | Compose responsivo com NavigationRail, grade e cartões | **Parcial**; homologar tablets e fontes ampliadas — P1 |
| Treinos (tela presente no mockup) | Não figura entre quatro módulos principais descritos no README web | Não há módulo de Treinos nativo implementado neste app | **Mockup aspiracional; não declarar recurso funcional** — escopo separado |

## Evidências técnicas consultadas

- [README Web](https://github.com/dutrafg/Evolufisico2/blob/main/README.md): quatro áreas principais, formulário em cinco etapas, migração, financeiro e backup.
- [Cálculos web](https://github.com/dutrafg/Evolufisico2/blob/main/assets/js/12-calculation.js) e [Lab](https://github.com/dutrafg/Evolufisico2/blob/main/assets/js/20-lab.js): Pollock 7 dobras, BIA, Navy, RFM.
- [Formulários web](https://github.com/dutrafg/Evolufisico2/blob/main/assets/js/28-forms-clean.js): templates personalizáveis, rascunhos e PDF preenchível.
- [Notificações web](https://github.com/dutrafg/Evolufisico2/blob/main/assets/js/27-notifications.js): lembrete, som e vibração.
- [Diagnósticos web](https://github.com/dutrafg/Evolufisico2/blob/main/assets/js/22-diagnostics.js): testes internos e autocorreção limitada.
- [Android Beta 10](https://github.com/dutrafg/Evolufisico-Android/tree/beta-10-parity-review/app/src/main/java/br/com/evolufisico/android): telas Compose, dados, backups e relatórios.

## Resultado do ciclo 7–10

**Implementado:** redesign incremental do Início e Módulos (Beta 7), Clientes/Agenda/Cobranças (Beta 8), Avaliações/Formulários/Evolução/Configurações (Beta 9); esta análise de paridade, versionamento e QA final (Beta 10).

**Não implementado por gerar checklist:** as 200.000 verificações planejadas (50.000 por Beta) **não representam entregas**. Gerar com `python3 scripts/generate_beta_roadmap.py 10 --output beta10.md`; a validação de contagem é executada no CI. A implementação completa dos 10 mockups não está concluída: dados e imagens demonstrativos não substituem módulos reais.

## Próximo desenvolvimento, na ordem

1. **P0 — Cadastro/backup/migração:** importação assistida web→Android com prévia, referências e reversão; testes ponta a ponta.
2. **P0 — Avaliação física:** dobras, Pollock 7, métodos complementares e avisos de adequação clínica; testes numéricos com amostras conhecidas.
3. **P0 — Formulários:** editor de perguntas, rascunhos persistidos e PDF preenchível, por cliente.
4. **P1 — Evolução/PDF:** comparação por período e identidade visual equivalente, sem tabelas truncadas.
5. **P1 — Agenda:** notificações Android nativas, consentimento e tratamento de segundo plano.
6. **P1 — Tablet e acessibilidade:** testes reais em orientações, fontes maiores, TalkBack, teclado e alto contraste.
7. **P0 — Homologação de dados:** backup/restore real, privacidade e atualização do APK sem perda; dependem da assinatura correta.

**Status:** candidata Beta 10 para testes, *não* versão completa equivalente ao EvoluFísico2. Um CI verde comprova apenas os testes automatizados existentes.
