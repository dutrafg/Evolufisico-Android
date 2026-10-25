# EvoluFísico Android nativo — Beta 3

Aplicativo Kotlin + Jetpack Compose baseado no EvoluFísico2 3.0. Início com dashboard compacto, cartões adaptáveis para celular/tablet, navegação inferior, Perfil Unificado, avaliações, calculadora de IMC e Agenda local.

## Novidades da Beta 3

- Favoritar módulos na tela Módulos; os atalhos aparecem no Início e ficam salvos entre sessões.
- Indicadores reais de alunos sem avaliação e reavaliações há 90 dias ou mais.
- Dashboard responsivo: quatro indicadores em linha em telas largas e grade de duas colunas em celulares.
- Agenda com criação, consulta e exclusão de atendimentos armazenados localmente.
- Migração SQLite da Beta 2 preserva cadastros de alunos, avaliações e compromissos.
- Tema claro/escuro persistido.

## O que ainda falta portar

Formulários, Análises, Cobranças, Tarefas, backup/restauração, exportação de relatórios e a personalização completa da interface do EvoluFisico2 ainda estão pendentes. Esses módulos aparecem no app, mas não têm todos os fluxos da versão web.

## Compilação

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

O CI publica um APK debug para cada compilação bem-sucedida. Esta variante é destinada a teste.

## Referência

O EvoluFisico2 3.0 em `dutrafg/Evolufisico2` é a referência funcional. Esta versão nativa está sendo portada por etapas.
