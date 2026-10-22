# Beta 20.1 — revisão de layout, redimensionamento e QA

Código `0.1.0-native-beta.20.1`, versionCode 621. A solicitação de 100.000 etapas foi tratada como uma revisão ampla. Este relatório registra mudanças reais e não declara 100.000 etapas concluídas.

## Correções implementadas

1. Filtros em FlowRow com quebra de linha para acomodar fontes maiores.
2. Botões de backup, lembretes e organização de módulos passam a quebrar linha.
3. Calendário acionado por ícone de 48 dp, deixando mais espaço ao campo de data.
4. Ajuda com alvo de 48 dp e títulos marcados semanticamente como cabeçalhos.
5. Campos genéricos limitam minLines à faixa aceita, evitando configuração inconsistente de linhas.
6. Paginação com estado em linha separada e anúncio acessível; página salva ajustada quando a última página desaparece após exclusões.
7. Espaço do teclado respeitado; navegação inferior e botão flutuante ficam ocultos enquanto ele está aberto.
8. Indicadores empilham ícone/valor em cartões estreitos ou fonte ampliada.
9. Grade tolera medida transitória de largura zero na chamada de UI sem relaxar validação do domínio.
10. Calendário mensal com largura mínima de 350 dp e rolagem em aparelhos mais estreitos; células com altura mínima de 64 dp.
11. Título do mês separado dos botões para não competir pelo espaço horizontal.
12. Mês inicial/salvo inválido tratado e navegação limitada aos anos 1–9999.
13. Inclusão rápida de tarefa com campo em largura inteira.
14. Rótulos de clientes homônimos usam prefixo de ID suficiente para diferenciar IDs importados semelhantes.
15. Comparação de nomes de clientes independente do locale do dispositivo.
16. Exercícios com espaços não separáveis de PDF normalizados no agrupamento e na abertura do histórico.
17. Valores/descontos negativos rejeitados antes da subtração, evitando overflow; prévia financeira protege entradas ainda inválidas.

## Verificações realmente executadas

- `python3 qa/check_source.py`: PASSOU, 46 arquivos Kotlin de produção e 16 destinos.
- Parse XML: 2 arquivos bem formados. Isso não comprova compilação dos recursos Android.
- Integridade ZIP: CRC conferido ao empacotar.
- Tentativa `bash gradlew :app:testDebugUnitTest :app:lintDebug --console=plain`: falhou antes das tarefas, código 1, `Network is unreachable` ao baixar Gradle 9.3.1.

Oito testes em `PolishQaTest` foram preparados para IDs semelhantes/curtos, avaliações duplicadas, locale turco, limites financeiros e espaços de PDF. **Não foram executados**. Também não houve execução Android, testes visuais ou novo APK. O log está em `qa/BUILD_BETA20_1.log`.

## Matriz de homologação visual pendente

Conferir larguras 320/360/412/600/900/1120 dp, retrato/paisagem, fontes 100/150/200% e claro/escuro. Em cada configuração:

- Início: números financeiros longos, títulos grandes, cartões e inclusão de tarefa.
- Módulos: filtros, favoritos, edição de ordem, Cancelar/Salvar/Restaurar.
- Agenda: navegação nos meses-limite, datas inválidas, células e rolagem.
- Editores: teclado aberto, calendário, campos longos e descarte.
- Listas: 0/1/30/31/61 itens e exclusão da última página.
- Cobrança: preço/desconto negativos, vazios, zero e desconto maior que o valor.
- Clientes: homônimos com IDs cujo prefixo é igual e idiomas de aparelho diferentes.
- Treinos: mesmo exercício com espaços normais/não separáveis e variantes que devem continuar separadas.

Esta matriz está documentada para execução, não foi marcada como aprovada.
