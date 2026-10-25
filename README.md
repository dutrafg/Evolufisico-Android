# EvoluFísico Android nativo — Beta 1

Base nativa independente inspirada no EvoluFísico2 3.0.0 publicado na branch `main` (referência: `version.json`, commit `4c4d28fbb5a8fa8bab226fdafcf5359bf80d8733`). O app é escrito em Kotlin e Jetpack Compose, sem WebView.

## Beta 1

- Dashboard com contagem real de alunos e avaliações locais e estados vazios sem dados fictícios.
- Cadastro e listagem de alunos com telefone, e-mail e nascimento.
- Perfil Unificado por ID permanente do aluno.
- Inclusão, consulta e exclusão de avaliações vinculadas ao perfil.
- Cálculo de IMC com validação de entradas; medidas ausentes não são inventadas.
- Calculadora de IMC separada.
- Tema claro/escuro persistido.
- Banco SQLite local, sem permissão de Internet.

## Limites desta Beta

Agenda, tarefas, cobranças, análises, formulários, exportação de relatórios, backup/restauração, cofre e migração de dados ainda não fazem parte da Beta 1. Os módulos não implementados aparecem identificados como planejados. Não importe dados reais sem um backup externo.

## Compilar

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

O CI publica o APK debug de cada compilação aprovada como artefato do GitHub Actions. APK debug é para teste e pode não atualizar instalações assinadas com outra chave.

## Fonte e planejamento

A referência funcional é o código fonte do EvoluFísico2, atualmente em `src/app.template.html`, junto do `README.md` e do QA 3.0. A versão nativa será portada por fluxos, com validação específica no Android; QA do aplicativo web não comprova o comportamento nativo.
