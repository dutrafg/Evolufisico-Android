# Estado dos 16 módulos — Beta 100 revisão 3

Todos os destinos possuem implementação no código-fonte. Nenhum módulo desta revisão foi compilado ou homologado em Android neste ambiente. “Implementado” não significa “finalizado e validado”.

| Módulo | Implementação disponível | Homologação obrigatória (não executada) |
|---|---|---|
| Início | Indicadores, atalhos e agenda rápida | Criar tarefa/atendimento e conferir atualização dos números; testar tela de 320 dp com fonte ampliada. |
| Clientes | Dossiê, contato, histórico, backup individual | Cadastrar dois homônimos e conferir isolamento, abas, PDF e exportação/retorno do backup. |
| Avaliações | Medições, fotos, aderência e PDF | Salvar sem aderência aplicável; reavaliar; testar fotos e duas avaliações no mesmo dia. |
| Evolução | Gráficos, comparação e PDF | Comparar IDs distintos na mesma data e conferir ordem com Análises/PDF. |
| Calculadora | Ferramentas de cálculo | Conferir entradas inválidas, campos ausentes, arredondamento e unidades em cada ferramenta. |
| Formulários | Questionários, triagem, vínculos e PDF | Cadastrar por ID, mudar nome, testar perguntas sem resposta, trocar nível e conferir PDF completo. |
| Análises | Indicadores, explicações e histórico | Abrir por dossiê e conferir cliente/avaliação escolhidos, métricas ausentes e botões de ajuda. |
| Cobrança | Serviços, pagamentos, descontos e parcelas | Usar homônimos, dupla e cortesia; conferir filtro do cliente, centavos e PDF completo. |
| Configurações | Aparência, dados, backups e avisos | Trocar paletas/fontes; selecionar/revogar pasta; conferir estados e teste real de WorkManager. |
| Agenda | Calendário, recorrência, edição e conflitos | Testar adjacência, virada do dia, cancelamento, conflito e lote com erro sem gravação parcial. |
| Tarefas | Prioridades, filtros, prazos e conclusão | Criar/adiar/concluir/reabrir por ID; excluir com confirmação e testar vínculo removido. |
| Metas | Linhas de base, alvos e progresso | Testar ausência de meta, alvo igual à base, evolução inversa, prazo e reavaliação. |
| Treinos | Séries, catálogo, modelos, execução, timer e exportações | Cadastrar/arquivar ficha; trocar exercício; usar modelo com cliente; marcar série, concluir e reabrir; conferir CSV/PDF e timer ao navegar. |
| Hábitos | Diário, metas, histórico e sequência | Conferir duplicatas no dia, datas futuras, unidades diferentes, homônimos e progresso. |
| Notas | Busca, cliente, arquivo e fixação | Editar/fixar/arquivar/restaurar; verificar nota arquivada em backup individual e conteúdo opcional no CSV. |
| Relatórios | Indicadores, filtro por cliente e CSV | Conferir períodos e cliente, dupla sem rateio, nomes/conteúdo opcionais e limites de linhas. |

## Portões de conclusão

1. Instalar as dependências e executar `bash qa/run_validation.sh` até testes, Lint e assembleDebug terminarem com sucesso.
2. Executar os cenários acima em Android real/emulador, com claro/escuro, retrato/paisagem, fonte ampliada, teclado aberto e navegação por gesto/botões. Confirmar área de notificações livre.
3. Testar backup portátil completo/individual e desta instalação: senha errada, corrupção, ID conflitante, restauração repetida, novo catálogo, arquivo vazio, limite de tamanho, cancelamento do seletor, revogação de pasta e interrupção do app. Conferir ausência de sobrescrita/parcialidade.
4. Verificar PDF longo de formulário/treino/avaliação e CSV em planilha. Conferir cálculos, nomes, filtros e privacidade.
5. Gerar APK e instalar como atualização com a mesma assinatura usada no aplicativo anterior. Preservar dados e validar os 16 destinos.

Esses portões estão pendentes, não contam como trabalho concluído. Não existe estimativa honesta de “quantas etapas faltam” sem executar os testes e corrigir as falhas encontradas. A restrição atual é a indisponibilidade do Gradle/SDK e da homologação Android, não uma meta artificial de linhas ou versões.
