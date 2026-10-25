# EvoluFísico Android nativo

Início de uma nova implementação Android nativa, baseada no **EvoluFísico2 3.0.0** da branch `main` do repositório web (`version.json`, commit `4c4d28fbb5a8fa8bab226fdafcf5359bf80d8733`). A referência funcional descreve a Dashboard Secretária, o Perfil Unificado, agenda, tarefas, reavaliações, cobranças, análises e privacidade financeira.

## Direção técnica

- Kotlin e Jetpack Compose; sem WebView.
- Operação offline como requisito de produto.
- Layout adaptável a celulares e tablets.
- Identificador do aplicativo mantido: `br.com.evolufisico.nativo`.
- Dados fictícios não são inseridos. Até a conexão com persistência, a tela mostra estados vazios.

## Estado desta base

A primeira etapa cria o projeto Gradle/Compose, tema grafite e verde-petróleo, navegação principal, dashboard inicial, lista de módulos e seletor de tema. Os módulos ainda são marcadores de navegação; cálculos, perfil persistido, banco local, agenda, backup, relatórios, migração e sincronização de versão ainda precisam ser implementados e homologados.

## Próximas etapas sugeridas

1. Revisar design e navegação no celular e tablet.
2. Definir modelo de dados e persistência local com Room; documentar importação e exportação antes de migrar dados.
3. Implementar Perfil Unificado e cadastro/busca de alunos.
4. Portar cálculos e regras de avaliação com casos de referência.
5. Implementar agenda, cobranças, evolução, formulários e relatórios.
6. Adicionar backup/restauração e cobrir migração de versões.
7. Homologar APK atualizado em aparelho físico antes de distribuir.

O código atual do EvoluFísico2 é a fonte funcional de referência; seus cálculos e regras devem ser portados e validados individualmente. A aprovação de QA web não equivale à homologação Android.
