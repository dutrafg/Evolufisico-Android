# EvoluFísico Android nativo — Beta 10

Android nativo em Kotlin e Jetpack Compose, desenvolvido a partir da identidade visual do EvoluFisico2 3.0.0 Release.

## Betas 5 a 10

- **Beta 5:** Início exibe tarefas e cobranças pendentes com atalhos para os módulos.
- **Beta 6:** avaliação recebe percentual de gordura estimado e medidas opcionais de pescoço, peitoral, braço e coxa; migração SQLite preserva as avaliações existentes.
- **Beta 7:** Perfil Unificado exibe gráfico descritivo do histórico de peso.
- **Beta 8:** os nove modelos de anamnese têm roteiros de perguntas próprios e respostas com hierarquia visual melhor.
- **Beta 9:** tarefas podem ser editadas e as vencidas ficam sinalizadas.
- **Beta 10:** datas e horários são validados com limites reais; cobranças vencidas são destacadas; backups novos usam schema 2 e continuam aceitando backups schema 1.

## Interface

Tema claro/escuro, contraste das barras do sistema, dez cores de destaque acessíveis, navegação inferior e layout adaptável. Na versão móvel, a tela inicial conserva os dois indicadores principais, agenda e central de atenção do aplicativo de referência.

## Diferenças conhecidas em relação ao EvoluFisico2 3.0.0

Ainda faltam todos os métodos antropométricos, motores e relatórios EF68/EF70/EF77, impressão/exportação PDF dos formulários, fotografias da avaliação, notificações e sincronização entre aparelhos. As medidas adicionadas são entradas registradas; o percentual de gordura é informado pelo profissional e não calculado pelo app. O APK é uma beta de avaliação.

## Compilação e dados

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

Os dados permanecem locais. Exporte um backup antes de instalar uma atualização ou restaurar um arquivo. O identificador Android foi preservado. Versão final: `0.1.0-native-beta.10`, código `714`.
