# Relatório de bugs — EvoluFísico Android, revisão 13

**Referência analisada:** `EvoluFisico2_1.0.19_PreBeta_Cabecalho_Notificacoes_TESTE.html`  
**Contexto de QA recebido:** `EvoluFisico2_1.0.29_Relatorio_QA_GitHub.md`  
**Aplicativo nativo:** Beta 100, `versionCode 712` (revisão 13)  
**Escopo:** comparação funcional do HTML com Kotlin/Compose, reprodução determinística de regras de caixa e notificações, regressões unitárias e compilação Android.

## Bugs reproduzidos na referência HTML

| ID | Achado | Efeito | Tratamento nesta revisão |
|---|---|---|---|
| BUG-01 | A validação mensal verifica o formato `AAAA-MM-DD`, mas aceita datas inexistentes, como `2026-02-31`. | Um lançamento pode aparecer no mês de uma data impossível. | A implementação Android usa `LocalDate.parse`; foram acrescentados casos inválidos à suíte do razão mensal. |
| BUG-02 | O total financeiro aceita `Infinity` como valor numérico. | Um único registro corrompido pode produzir total infinito e contaminar o resumo. | O modelo Android armazena centavos em `Long`; os testes de importação e razão cobrem valores delimitados e datas válidas. |
| BUG-03 | O parser de horário usa `Date` com normalização, permitindo entradas impossíveis, por exemplo `12:99`. | Lembretes podem ser criados para um horário diferente do digitado. | A agenda Android faz parse estrito com `LocalTime.parse`; foi mantida validação estrita na rotina nativa. |
| BUG-04 | A deduplicação do calendário conta compromissos cancelados como se já cobrissem uma reavaliação prevista no mesmo dia. | A exportação `.ics` pode omitir um evento válido. | A agenda Android filtra registros por estado e identidade; a exportação não foi alterada nesta revisão. |
| BUG-05 | A mensagem de exportação informa todos os eventos, mesmo quando o arquivo limita a gravação a 500. | A confirmação pode declarar eventos como exportados apesar de terem sido omitidos. | Não há exportador `.ics` correspondente nesta interface nativa; fica registrado como pendência ao portar essa função. |

## Atualizações Android desta revisão

- Adicionado o modo visual **Liquid Glass** em Aparência, com superfície tonal, profundidade discreta e movimento compatível com a opção de efeitos essenciais.
- Preservado o botão flutuante único **Módulo** e a abertura inicial em **Início**, conforme a preferência definida anteriormente para o Android.
- Adicionadas opções de som e vibração para notificações, usando canais nativos. O Android continua controlando volume, permissões e substituições feitas pelo usuário nas configurações do canal.
- Adicionado teste de notificação e atalho às configurações do canal do aplicativo.
- Ampliadas as antecedências de lembrete para 5 e 10 minutos e adicionada a opção **No horário**, com tolerância de execução de até 60 segundos para atrasos do WorkManager.
- O histórico de cobranças e os backups existentes não tiveram alteração de esquema.

## Verificações

- Parser JavaScript inline do HTML e testes determinísticos das regras financeiras/notificações: registrados em `qa/test_html_1019.cjs`. O teste confirma cinco comportamentos problemáticos da referência enviados para portabilidade/correção no Android.
- Contratos estáticos de origem, janelas seguras, paridade de navegação e efeitos visuais: `qa/check_source.py`, `qa/check_window_contract.py`, `qa/check_html_ui.py`, `qa/check_visual_effects.py`, `qa/check_dashboard_grid.py`.
- Testes unitários Android, Lint e compilação: resultado final em `qa/BUILD_BETA100_REVISAO13.log`.
- Verificação de pacote, assinatura, versão, DEX e hash do APK: `qa/APK_BETA100_REVISAO13.json` e arquivos associados.

## Limitações conhecidas

O relatório 1.0.29 enviado descreve verificações anteriores de ícones e navegação; ele não comprova estes resultados nem cobre integralmente segurança, fuzzing, persistência, PDFs e cálculos antropométricos. Este ambiente não disponibiliza um dispositivo/emulador Android utilizável para conferir gestos, notificações do sistema em segundo plano, aparência renderizada ou instalação física. O WorkManager pode atrasar lembretes por economia de bateria. A exportação nativa `.ics` ainda precisa ser implementada para que BUG-04 e BUG-05 recebam equivalência funcional completa.
