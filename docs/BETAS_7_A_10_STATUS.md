# Betas 7 a 10 — o que foi executado

| Beta | Implementações reais | Verificações de 50.000 |
|---|---|---|
| 7 | Ícones pastel nas cartas de Módulos; acabamento de cartões, botões e Home | 50.000 planejadas, não finalizadas |
| 8 | Cartão-resumo financeiro, Agenda compacta, contatos com avatar e calendário | 50.000 planejadas, não finalizadas |
| 9 | Layout de Avaliações, Formulários, Evolução e Configurações consistente | 50.000 planejadas, não finalizadas |
| 10 | Revisão de paridade contra o EvoluFísico2; teste automatizado da contagem, APK | 50.000 planejadas, não finalizadas |

Os itens são produzidos pelo gerador em `scripts/generate_beta_roadmap.py`, sob demanda, com 200.000 IDs no conjunto. O teste verifica contagem, sequência e duplicatas. Para cada Beta, não há alegação de 50.000 funcionalidades ou testes executados. Leia `BETA_10_PARIDADE_EVOLUFISICO2.md` antes de definir a próxima fase.

## Aceite mínimo

- [x] CI Beta 10 compilação, unitários e Lint aprovados
- [x] Emulador Android 10 da Beta 10 aprovado
- [x] APK Beta 10 baixado, extraído e integridade validada
- [ ] Homologação manual em celular e tablet real (pendente)
- [ ] Backup/restauração real testado (pendente)
- [ ] Paridade web-Android completa (pendente)
