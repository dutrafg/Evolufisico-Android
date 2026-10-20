# EvoluFísico Android — roteiro visual Alpha 21–25

O objetivo é aproximar a interface do mockup aprovado. Os exemplos do mockup não são dados reais. A compilação de teste usa identificador independente para não sobrescrever a instalação existente.

## Alpha 21 — Tela inicial

- [x] Indicadores em quatro cartões
- [x] Atendimentos reais com iniciais
- [x] Datas e horários inteligíveis
- [x] Ações de acompanhamento condicionais
- [x] Atalhos de Clientes e Agenda
- [ ] Testar em aparelho físico

## Alpha 22 — Módulos em grade

- [x] Favoritos em cartões
- [x] Histórico de módulos acessados
- [x] Grade com agrupamento por categoria
- [x] Busca e filtros preservados
- [x] Conservar ordenação dos favoritos
- [ ] Testar interface em aparelho físico

## Alpha 23 — Identidade visual

- [x] Paleta azul corporativa
- [x] Superfícies claras neutras
- [x] Dark mode com contraste consistente
- [x] Cantos levemente arredondados
- [x] Barra inferior e lateral harmonizadas
- [ ] Validação visual em celular

## Alpha 24 — Responsividade

- [x] Uma coluna em telas estreitas
- [x] Duas colunas em smartphones
- [x] Três colunas em tablets
- [x] Áreas de toque ampliadas
- [x] Limite de quatro favoritos indicado
- [ ] Testar acessibilidade e fontes grandes

## Alpha 25 — Acabamento e QA

- [x] Ícones em métricas e atalhos
- [x] Revisar cadastro e navegação sem alterações de dados
- [x] Testes JUnit de datas e layout
- [x] Android Lint e compilação CI
- [x] Emulador Android 10
- [ ] Homologação física e assinatura permanente

## Validação e limites

- A aprovação de JUnit, Android Lint e emulador será registrada somente quando o GitHub Actions terminar com sucesso.
- O QA visual em dispositivo físico, leitor de tela, fontes ampliadas e restauração real seguem pendentes.
- A Alpha 25 não instala sobre variantes anteriores: identificador de testes `br.com.evolufisico.android.alpha25`.
- Não alterar os formatos do banco, clientes, avaliações, agenda e cobranças nesta rodada visual.
- A assinatura permanente do APK oficial está pendente e nenhuma chave descartável de CI deve ser distribuída.
- As matrizes antigas com 100 checkpoints por versão não estão inteiramente homologadas.
