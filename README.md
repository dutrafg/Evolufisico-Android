# EvoluFísico Android nativo — Beta 2

Aplicativo nativo em Kotlin e Jetpack Compose, baseado no EvoluFísico2 3.0.0 publicado na branch `main` (referência: `version.json`, commit `4c4d28fbb5a8fa8bab226fdafcf5359bf80d8733`). Não usa WebView.

## Beta 2

- Dashboard compacto com paleta verde, cartões menores e estados ligados aos dados locais.
- Navegação inferior com cores próprias do EvoluFísico e correção do espaçamento causado por insets repetidos.
- Cadastro de alunos, Perfil Unificado, histórico de avaliações e cálculo de IMC.
- Agenda funcional para criar, consultar e excluir atendimentos, com data, hora, local e observações.
- Migração SQLite para a Agenda preserva alunos e avaliações já existentes.
- Tema claro/escuro persistido; dados permanecem no aparelho.

## Próximas etapas

Tarefas, cobranças, análises, formulários, exportação, backup/restauração, cofre e migração de dados ainda precisam de implementação nativa. Os módulos pendentes permanecem identificados no aplicativo.

## Compilar

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

O CI publica o APK debug de cada compilação como artefato do GitHub Actions. APK debug é para teste e pode não atualizar instalações assinadas com outra chave.

## Fonte

A referência funcional é o código do EvoluFísico2 em `src/app.template.html`, junto do `README.md` e do QA 3.0. A versão nativa está sendo portada por fluxos com validação específica para Android.
