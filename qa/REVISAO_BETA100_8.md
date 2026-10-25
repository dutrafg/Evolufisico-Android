# Beta 100 — revisão 8

Pedido: concluir ajustes de média prioridade e reestruturar o Android de acordo com o HTML anexado, removendo a tela Módulos inexistente no original.

## Mudanças concretas

1. Removidos o destino/tela Módulos, seu diálogo e ícone de aplicativos. Os nove destinos reais permanecem na ordem do HTML.
2. Navegação móvel reescrita como faixa horizontal deslizante; seleção de outra página traz sua aba à área visível. Barra lateral somente em telas grandes, recolhível e com preferência persistida.
3. Retirados a busca global e o botão flutuante global acrescentados no Android. Nova avaliação fica nos acessos contextuais.
4. Início reescrito conforme o último script Home do HTML: quatro KPIs operacionais, prioridades, próximos acontecimentos, insights, sete dias e ações do dia.
5. Regras do resumo preservam ausência de registros, excluem compromissos cancelados e itens de planejamento concluídos/arquivados, não duplicam uma tarefa urgente já vencida, ordenam próximos itens por data/horário e limitam a cinco. Metas pausadas contam como ativas, conforme o HTML.
6. Painel anterior de agenda/tarefas/retornos preservado dentro de Organização, acessível por Planejar agenda e retornos. Sem exclusão de registros ou alteração do formato do banco/backup.
7. Atalhos do Início abrem Agenda/Tarefas/Notas/Metas na Organização; compromissos abrem sua data no calendário.
8. Formulários usa os acessos Modelos, Respostas e Criar formulário. Navegação interna de Organização, Formulários e Configurações usa linha deslizante; opções de respostas continuam a se ajustar por linhas.
9. Indicadores compactos em duas colunas no celular; gráfico semanal de sete barras lado a lado, com valores em texto e descrição para leitor de tela. Ações dos cards quebram em linhas; removido espaço reservado ao antigo FAB. Busca de tarefas urgentes reutiliza o conjunto de IDs vencidos.
10. Saída do painel de gestão bloqueada durante edição de compromisso; Salvar/Cancelar mantêm a proteção existente. Atalho Abrir avaliações conectado ao aluno selecionado.
11. Personalização do Início preservada conforme o último script do HTML: grupos homeStats/homeCards, mover para antes/depois, larguras e restaurar padrão, com preferências persistidas separadas do banco.
12. Auditoria estática específica compara nomes/estrutura com o HTML e impede o retorno da tela Módulos. Novos testes JUnit exercitam os indicadores e limites do Início.

## Limites

A implementação permanece Kotlin/Compose nativa. O HTML original foi preservado e seu SHA conferido. Não se afirma igualdade pixel a pixel nem homologação em aparelho. PDFs e painéis mais complexos ainda possuem diferenças descritas em PARIDADE_HTML.md. Relatórios da revisão 7 são históricos; o resultado desta revisão está em BUILD_BETA100_REVISAO8.json.
