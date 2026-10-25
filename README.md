# EvoluFísico Android nativo — Beta 13

Android nativo em Kotlin e Jetpack Compose, inspirado no EvoluFisico2 3.0.0 Release.

## Ajustes desta versão

- Layout travado no modo compacto em celulares e tablets, com conteúdo central limitado a 600 dp.
- Removida a variação de quatro indicadores em telas largas; o Início conserva sempre dois cartões.
- Cada módulo agora usa um ícone correspondente à sua função.
- O seletor flutuante lista apenas módulos principais; Agenda e Tarefas permanecem como funções acessadas pelo Início.
- Nenhum modo desktop ou estendido está disponível nesta interface nativa.

## Funcionalidades das Betas 5 a 10

- Início com tarefas e cobranças pendentes e atalhos.
- Avaliações com peso, altura, cintura, quadril, percentual de gordura informado e medidas opcionais; migração preserva avaliações anteriores.
- Gráfico descritivo de evolução do peso no Perfil Unificado.
- Nove modelos de anamnese com roteiros próprios.
- Tarefas editáveis, avisos de vencimento, validação de datas/horários e restauração de backups schema 1 e 2.

## Interface

Tema claro/escuro, dez cores de destaque acessíveis, barra inferior fixa e conteúdo em coluna compacta. Os ícones do sistema acompanham o tema.

## Diferenças conhecidas em relação ao EvoluFisico2 3.0.0

Ainda faltam métodos antropométricos e relatórios EF68/EF70/EF77, exportação PDF de formulários, fotografias, notificações e sincronização entre aparelhos. O percentual de gordura é um valor informado pelo profissional, não calculado pelo app. O APK é beta de avaliação.

## Compilação e dados

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

Os registros ficam no aparelho. Exporte um backup antes de atualizar ou restaurar. Versão `0.1.0-native-beta.11`, código `715`.
