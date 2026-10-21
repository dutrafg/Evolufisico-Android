# Beta 5 — análise de paridade EvoFísico2 Web × EvoluFísico Android

Fontes: [web](https://github.com/dutrafg/Evolufisico2) — `README.md`, `assets/js/05-app-runtime.js`, `12-calculation.js`, `20-lab.js`, `22-diagnostics.js`, `24-billing.js`, `28-forms-clean.js` e [Android](https://github.com/dutrafg/Evolufisico-Android/tree/beta-5-development) — modelos `data/`, telas Compose e serviços `backup/`.

Este documento é uma **análise do código e da documentação**, não uma homologação funcional end-to-end do web. Os estados são conservadores.

| Domínio | EvoluFísico2 (referência) | Android Beta 4 → Beta 5 | Diferença e prioridade |
|---|---|---|---|
| Clientes | ID estável e contatos para relacionar avaliação | ID/nome; Beta 5 implementou contatos opcionais | Testar migração real e fichas — P0 |
| Avaliações | Fluxos ricos com cálculos e impressão | Peso, altura, cintura, quadril, braço, coxa, anamnese básica | Protocolos e medidas adicionais faltam — P0 |
| Composição corporal | Rotinas de cálculos, bioimpedância e Pollock | Não há equivalência integral nos modelos Android inspecionados | Implementar com validação de protocolos — P1 |
| Formulários | Formulário multi-etapas e funções de rascunho | Modelos fixos por nível, salvamento e PDF | Editor flexível e rascunhos persistentes — P1 |
| Evolução | Comparadores, análise histórica e ferramentas de decisão | Evolução e relatórios mais enxutos | Igualar indicadores e critérios de comparação — P1 |
| Cobranças | Histórico, recebimento, exportação e cofre opcional | Cadastro, recebimento, filtros CSV | Paridade financeira e proteção — P0/P1 |
| Agenda | Agenda/notificações no módulo web | Agenda local/ICS/CSV e sugestões livres | Lembretes e fluxos completos — P1 |
| Relatórios PDF | Layout de impressão amplo e HTML para PDF | PDFs nativos de avaliação, evolução, formulário, cliente | Harmonizar campos, páginas e visual — P0/P1 |
| Dados | IndexedDB/localStorage, exportação e backup do web | JSON interno próprio; backup JSON Android distinto | **Não há importação direta do backup web validada** — P0 |
| Privacidade | Cofre opcional e proteções em módulos web | Parte dos dados novos permanece em JSON legível | Proteção real necessária para dados pessoais — P0 |
| Configurações | Aparência, dados, manutenção e diagnósticos | 10 paletas e páginas de configurações | Opções específicas e diagnósticos mais amplos — P1 |
| Visual | CSS corporativo e telas responsivas | Jetpack Compose responsivo parcial | Ajustes de tablets, formulários e avaliação — P1 |

## Primeiro incremento concreto da Beta 5

- Cadastro de clientes ganha telefone, e-mail e nascimento opcionais, preservando ID.
- Gravação e leitura mantêm `schema=1` e aceitam campos ausentes de cadastros antigos.
- Backup exporta e importa contatos novos, sem invalidar backups antigos.
- Busca por nome, telefone e e-mail; exibição na ficha e no PDF.
- Testes unitários cobrem dados opcionais, validação, ID estável e busca.

## Critérios de saída

1. CI completo e emulador Android 10 aprovados.
2. Validação manual da migração e da restauração de backups reais ainda **pendente**.
3. Testes em smartphone e tablet físico **pendentes**.
4. Assinatura permanente/in-place não presumida; foco atual em desenvolvimento funcional.
5. Os 10.000 checkpoints permanecerão pendentes até evidência específica; não declarar funcionalidade implementada pelo simples registro no roteiro.
