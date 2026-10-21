# Beta 11 — polimento visual e responsividade

**Base:** branch `beta-10-parity-review` · **Implementação:** `beta-11-interface-polish`.
**Versão:** `97.0.0-beta.11` · **versionCode:** `204`.
**Debug package:** `br.com.evolufisico.android.beta11`. É uma instalação paralela de QA, não atualiza o Beta 10 nem o aplicativo oficial.

## Melhorias reais no código
- Conteúdo máximo de 1100 para 1440 dp, reduzindo espaços inutilizados em tablets e janelas amplas.
- Gutter adaptativo de 16/20/24 dp; cartões de métricas em 1, 2 ou 4 colunas segundo a largura de conteúdo.
- Catálogo e favoritos preenchem a última linha, sem colunas vazias.
- Ícones de módulo têm cores por categoria; superfícies e sombras mais consistentes.
- Tipografia com linhas mais legíveis, cantos e alturas ajustados.
- Lista de clientes ocupa apenas a área rolável remanescente, sem empurrar o restante do layout.
- Avaliações, Formulários, Evolução e Relatórios agora centralizam o conteúdo em janelas amplas; Cobranças e Agenda usam espaçamento responsivo.
- Resumo por cliente, Indicadores, Comparativos e Busca global também usam largura centrada e margens adaptativas.
- Novos testes de regressão para telas estreitas e matriz de **100.000 combinações lógicas** (1000 larguras × 10 paletas × 2 temas × 5 escalas de fonte).

## Continuação: navegação e acessibilidade
- Calendário com sete células de pelo menos 48 dp e rolagem horizontal conjunta
  no caso de telas pequenas; o TalkBack anuncia data, hoje, seleção e atendimentos.
- O dia atual recebe fundo distinto, sem se confundir com o dia selecionado.
- Atalhos de Clientes e Agenda ficam em coluna em telas estreitas.
- Favoritos exibem pictogramas e fundos coerentes com os do catálogo;
  controles de favoritos e de reordenação têm áreas de toque de 48 dp.
- Configurações com cartão dedicado ao modo escuro e margens adaptativas.
- Barra superior com título truncado e superfície uniforme.

## Alcance da matriz automatizada
São verificações repetidas de invariantes dos breakpoints, cor de superfície e seleções.
**Não são 100 mil etapas manuais, renders de tela, correções visuais, ou testes reais do Compose**.
A matriz não demonstra ausência de cortes de texto, contraste WCAG ou acessibilidade TalkBack.

## Smoke de UI em Compose
- Dois testes instrumentados adicionais no emulador, com árvore semântica, ações e
  renderização real para um cartão favorito e o calendário em um container de 320 dp.
- A captura para memória comprova desenho de superfície; não há validação
  visual manual, de snapshots dourados ou de densidades reais.

## Critérios de aceite ainda exigidos
- [ ] CI build + unit tests + Android Lint no commit Beta 11.
- [ ] Emulador Android 10 e testes instrumentados.
- [ ] Inspeção visual com fonte 200%, paisagem, tela pequena, tablet e modo escuro.
- [ ] Instalação física e teste de backups antes de qualquer mudança para release.
- [ ] APK de QA verificado; não usar com dados reais sensíveis.
