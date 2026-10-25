# EvoluFísico Android nativo — Beta 4

Aplicativo Android nativo em Kotlin e Jetpack Compose, com navegação e identidade visual inspiradas nas capturas do EvoluFisico2. Os registros ficam armazenados localmente.

## Nesta versão

- Dashboard adaptável a celulares e tablets, com alunos, avaliações, agenda, pendências e módulos favoritos.
- Cadastro, busca, edição, perfil e exclusão de alunos; registro e histórico de avaliações.
- Agenda local com criação, consulta e exclusão de atendimentos.
- Tarefas, cobranças com ação de recebimento e opção para ocultar valores, e formulários de anamnese.
- Análises descritivas de evolução e calculadora de IMC, cintura/estatura e cintura/quadril.
- Preferências de tema claro/escuro e dez cores de destaque.
- Exportação e restauração manual de backup JSON, incluindo alunos, avaliações, agenda, tarefas, formulários e cobranças.
- Migração SQLite da Beta 3 preserva os dados existentes.

## Comparação com EvoluFisico2 3.0.0

A estrutura visual das telas apresentadas foi aproximada e as áreas principais agora têm fluxos nativos. Ainda há diferenças funcionais para a versão web: esta Beta não porta todos os métodos antropométricos, motores de análise e relatórios do EF68/EF70/EF77, os nove modelos de anamnese com campos próprios, fotos, notificações e integração de calendário. Ela também não sincroniza dados entre aparelhos. Por isso, o APK é uma beta de avaliação, não uma versão equivalente ou pronta para publicação.

## Compilação

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

O CI publica um APK debug para teste. O pacote e o identificador Android permanecem iguais às versões beta anteriores.

## Referência

O EvoluFisico2 3.0.0 em `dutrafg/Evolufisico2` é a referência funcional e visual.
