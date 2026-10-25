# EvoluFísico Android nativo — Beta 15

Android nativo em Kotlin e Jetpack Compose, baseado no EvoluFisico2 3.0.0 Release (main atualizado em 25/10/2026).

## Funcionalidades

- **Início:** indicadores do dia, calendário, agenda editável, próximos atendimentos, tarefas, reavaliações vencidas e cobranças pendentes com valores ocultáveis.
- **Perfil Unificado:** cadastro editável, resumo da última avaliação, histórico com comparação de medidas e gráfico de peso, anamneses e cobranças vinculadas.
- **Avaliações:** registro e edição de peso, altura, perímetros, percentual de gordura informado e medidas opcionais; validação dos dados e histórico local.
- **Relatórios do Perfil:** exportação em PDF de avaliações e anamneses, com identificação do aluno, dados disponíveis, observações e dados profissionais definidos em Configurações. Anamneses longas são paginadas.
- **Evolução e Análises Completas:** seleção de aluno, comparação descritiva e sinalização de dados ausentes ou de baixa comparabilidade.
- **Calculadora:** IMC, estimativas Navy/RFM, Harris-Benedict, Mifflin-St Jeor, gasto diário e zonas de frequência cardíaca, com validação de limites.
- **Anamnese:** nove modelos, formulários personalizados, vínculo com aluno, edição, exclusão e exportação em PDF.
- **Cobranças:** cadastro, edição, recebimento, exclusão e vínculo opcional com aluno.
- **Configurações:** temas claro/escuro, dez cores, identificação profissional, prazo de reavaliação (30/60/90 dias), ocultação de valores e backup/restauração manual com confirmação.
- **Navegação compacta:** módulos principais no seletor flutuante; Agenda e Tarefas são funções acessadas pelo Início. Interface limitada a 600 dp, sem modo desktop/estendido.

## Verificação automatizada

O CI executa `:app:testDebugUnitTest`, que também compila o código principal. A suíte atual cobre 12 casos: fórmulas e validação do Laboratório, operações de cadastro/edição/exclusão dos dados locais, vínculos e restauração de backup, além de geração e paginação dos PDFs.

A verificação não substitui homologação visual em aparelhos físicos. Nenhum APK foi gerado nesta etapa.

## Diferenças em relação ao EvoluFisico2 3.0.0

Ainda faltam as equações de dobras e relatórios completos EF68/EF70/EF77, fotografias nas avaliações, notificações e sincronização entre aparelhos. A área de Análises no Android oferece comparações descritivas básicas, não os relatórios analíticos completos do aplicativo web. Percentual de gordura informado é separado das estimativas Navy/RFM e não constitui diagnóstico.

## Compilação e dados

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

Os dados ficam no aparelho. Exporte um backup antes de restaurar. Versão `0.1.0-native-beta.15`, código `719`.
