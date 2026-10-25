# EvoluFísico Android nativo — Beta 16

Android nativo em Kotlin e Jetpack Compose, com referência funcional no EvoluFisico2 3.1.5 Release (main consultada em 25/10/2026). A paridade integral ainda depende das etapas listadas abaixo.

## Funcionalidades

- **Início:** indicadores do dia, calendário, agenda editável, próximos atendimentos, tarefas, reavaliações vencidas e cobranças pendentes com valores ocultáveis.
- **Perfil Unificado:** cadastro editável, resumo da última avaliação, histórico com comparação de medidas e gráfico de peso, anamneses e cobranças vinculadas.
- **Avaliações:** registro e edição de peso, altura, perímetros, percentual de gordura informado (método não registrado), sete dobras cutâneas, sexo e idade; bioimpedância com percentual, massa livre e equipamento; até quatro fotos otimizadas por avaliação, exibidas no perfil, incluídas no backup e incorporadas ao PDF da avaliação (até quatro, duas por página); estimativa Pollock 7 registrada separadamente no histórico local.
- **Relatórios do Perfil:** exportação em PDF de avaliações e anamneses, com identificação do aluno, dados disponíveis, observações e dados profissionais definidos em Configurações. Anamneses longas são paginadas. Relatórios de avaliação incluem fotos JPEG com páginas fotográficas próprias e validação do vínculo entre aluno e avaliação.
- **Evolução e Análises Completas:** seleção de três ou quatro datas (mínimo de 21 dias), tendências descritivas por indicador, completude documental, avisos quando há mudança de direção, comparação de Pollock 7 e bioimpedância somente quando registrados na mesma data, rascunho revisável e exportação PDF protegida por aluno/período.
- **Calculadora:** IMC, estimativas Navy/RFM e Pollock 7 (soma, densidade, percentual e massas), Harris-Benedict, Mifflin-St Jeor, gasto diário e zonas de frequência cardíaca, com validação de limites.
- **Anamnese:** nove modelos, formulários personalizados, vínculo com aluno, edição, exclusão e exportação em PDF.
- **Cobranças:** cadastro, edição, recebimento, exclusão e vínculo opcional com aluno.
- **Configurações:** temas claro/escuro, dez cores, identificação profissional, prazo de reavaliação (30/60/90 dias), ocultação de valores, backup/restauração manual e backup automático diário para uma pasta escolhida, em horário configurável (o Android pode adiá-lo por economia de bateria).
- **Navegação compacta:** módulos principais no seletor flutuante; Agenda e Tarefas são funções acessadas pelo Início. Interface limitada a 600 dp, sem modo desktop/estendido.

## Verificação automatizada

O CI executa `:app:testDebugUnitTest`, que também compila o código principal. A suíte contempla 29 casos: fórmulas e validação do Laboratório incluindo Pollock 7, janelas temporais, tendências, completude e comparação pareada das análises, persistência/restauração de medidas, fotos e backup, operações de cadastro/edição/exclusão dos dados locais, vínculos e restauração de backup, geração/paginação dos PDFs, inclusão da bioimpedância nos relatórios e agendamento do backup diário.

A verificação não substitui homologação visual em aparelhos físicos. Nenhum APK foi gerado nesta etapa.

## Diferenças em relação ao EvoluFisico2 3.1.5

Pollock 7 está implementado como estimativa de composição corporal e a bioimpedância pode ser registrada separadamente. Análises Android compara tendências documentadas e evidências pareadas na mesma data, mas ainda não porta todos os motores EF68/EF70/EF77 nem a interpretação por correlações do aplicativo web. Ainda faltam outros protocolos de dobras, notificações e sincronização entre aparelhos. Fotos são incorporadas aos PDFs da avaliação, mas a revisão visual em dispositivo físico permanece pendente. Percentual de gordura informado é separado das estimativas e não constitui diagnóstico.

## Compilação e dados

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

Os dados ficam no aparelho. Exporte um backup antes de restaurar. Versão `0.1.0-native-beta.16`, código `720`. A Beta 15 foi preservada na branch anterior; nenhum APK foi gerado nesta etapa.
