# EvoluFísico Android nativo — Beta 15

Android nativo em Kotlin e Jetpack Compose, baseado no EvoluFisico2 3.0.0 Release (main atualizado em 25/10/2026).

## Funcionalidades

- **Início:** indicadores do dia, calendário, agenda editável, próximos atendimentos, tarefas, reavaliações vencidas e cobranças pendentes com valores ocultáveis.
- **Perfil Unificado:** cadastro editável, resumo da última avaliação, histórico com comparação de medidas e gráfico de peso, anamneses e cobranças vinculadas.
- **Avaliações:** registro e edição de peso, altura, perímetros, percentual de gordura informado, sete dobras cutâneas, sexo e idade da avaliação; estimativa Pollock 7 registrada no histórico local.
- **Relatórios do Perfil:** exportação em PDF de avaliações e anamneses, com identificação do aluno, dados disponíveis, observações e dados profissionais definidos em Configurações. Anamneses longas são paginadas.
- **Evolução e Análises Completas:** seleção de três ou quatro datas (mínimo de 21 dias), tendências descritivas por indicador, completude documental, avisos quando há mudança de direção, rascunho revisável e exportação PDF protegida por aluno/período.
- **Calculadora:** IMC, estimativas Navy/RFM e Pollock 7 (soma, densidade, percentual e massas), Harris-Benedict, Mifflin-St Jeor, gasto diário e zonas de frequência cardíaca, com validação de limites.
- **Anamnese:** nove modelos, formulários personalizados, vínculo com aluno, edição, exclusão e exportação em PDF.
- **Cobranças:** cadastro, edição, recebimento, exclusão e vínculo opcional com aluno.
- **Configurações:** temas claro/escuro, dez cores, identificação profissional, prazo de reavaliação (30/60/90 dias), ocultação de valores, backup/restauração manual e backup automático diário para uma pasta escolhida, em horário configurável (o Android pode adiá-lo por economia de bateria).
- **Navegação compacta:** módulos principais no seletor flutuante; Agenda e Tarefas são funções acessadas pelo Início. Interface limitada a 600 dp, sem modo desktop/estendido.

## Verificação automatizada

O CI executa `:app:testDebugUnitTest`, que também compila o código principal. A suíte atual cobre 23 casos: fórmulas e validação do Laboratório incluindo Pollock 7, janelas temporais, tendências e completude das análises, persistência/restauração das sete dobras, operações de cadastro/edição/exclusão dos dados locais, vínculos e restauração de backup, geração/paginação dos PDFs e agendamento do backup diário.

A verificação não substitui homologação visual em aparelhos físicos. Nenhum APK foi gerado nesta etapa.

## Diferenças em relação ao EvoluFisico2 3.0.0

Pollock 7 está implementado como estimativa de composição corporal. Análises Android agora compara tendências documentadas e gera PDF, mas ainda não porta todos os motores EF68/EF70/EF77 nem a interpretação por correlações do aplicativo web. Ainda faltam outros protocolos de dobras, registros de bioimpedância e fotografias, notificações e sincronização entre aparelhos. Percentual de gordura informado é separado das estimativas Navy/RFM e não constitui diagnóstico.

## Compilação e dados

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

Os dados ficam no aparelho. Exporte um backup antes de restaurar. Versão `0.1.0-native-beta.15`, código `719`.
