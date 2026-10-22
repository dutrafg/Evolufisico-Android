# Beta 33 — funções até a Beta 28, integração e revisão final

Código `0.1.0-native-beta.33`, versionCode 633, Android 10+. Mantidos os 16 módulos e as rotas. Os marcos abaixo descrevem o ciclo de código; não são treze APKs compilados/homologados.

## Entregas por marco

| Marco | Trabalho concreto |
|---|---|
| Beta 21 | Visão de dossiê consolidada por ID permanente, sem unir homônimos |
| Beta 22 | Seções Resumo/Avaliações/Agenda/Tarefas/Notas com paginação; removido limite silencioso de dez tarefas |
| Beta 23 | Edição de nome, telefone e e-mail na avaliação mais recente; ID, medidas e avaliações antigas preservados |
| Beta 24 | PDF de resumo do dossiê, com limites explícitos de detalhes e sem fotos/conteúdo de notas |
| Beta 25 | CSV de agenda do período, nomes opcionais |
| Beta 26 | CSV de notas, conteúdo desativado por padrão e opção explícita de inclusão |
| Beta 27 | Filtros por cliente em tarefas/hábitos/notas e criação vinculada ao filtro ativo |
| Beta 28 | Reutilização dos exercícios já digitados no banco, com busca e revisão das séries |
| Beta 29 | Navegação do dossiê mantém o ID em Evolução/Metas/Treinos/Hábitos/Tarefas/Notas |
| Beta 30 | Histórico de cargas acumulado em passagem direta e cache por dados/cliente/período; snapshot do dossiê reaproveitado |
| Beta 31 | Dez testes JUnit de regressão preparados para dossiê, contatos, CSV e biblioteca |
| Beta 32 | Somente polimento/correções: espaços de editores, fontes, seleção de paletas, acessibilidade dos controles |
| Beta 33 | Somente correções/QA: erro de exclusão da agenda, falhas de reutilização, paginação de serviços e verificações finais |

A prioridade das três frentes anteriores foi mantida. Não se declara execução de uma contagem artificial de etapas nem conclusão de toda funcionalidade possível. A biblioteca reutiliza dados do próprio usuário; não é uma base externa multimídia de exercícios.

## Comportamentos relevantes

- O contato editado fica na avaliação mais recente; registros antigos mantêm os nomes/contatos históricos. O relacionamento continua por ID.
- Dossiê inclui cobrança quando o cliente está em qualquer um dos dois vínculos. Cobranças conjuntas entram pelo valor integral, sem rateio; isso aparece na tela e no PDF.
- PDF resume até 10 avaliações, 10 atendimentos ordenados por data e 20 tarefas pendentes; o restante continua acessível no aplicativo e no backup.
- O CSV de notas sempre contém título/categoria/estado e só inclui o corpo quando a opção é ativada. Nomes são outra opção independente.
- Ao filtrar um cliente e criar tarefa/hábito/nota/treino, o vínculo vem preenchido e pode ser revisado no editor.
- A biblioteca copia somente exercício/repetições/carga para uma nova série com ID próprio e execução limpa. Notas pessoais e nomes dos clientes não são copiados.
- O cálculo de cargas mantém os critérios anteriores: cliente, dias, execução, exclusão de modelos/futuro e normalização de espaços.
- A otimização remove listas intermediárias e evita recálculo ao somente pesquisar, mas não foi medida em benchmark Android.

## QA realmente executado

- Auditoria estática: PASSOU, 48 arquivos Kotlin de produção e 16 destinos.
- `qa/check_palette.py`: PASSOU, 60 pares fixos de texto/cor, alvo 4,5:1; menor valor 6,8547:1. Não cobre interpolação, estados desabilitados nem telas renderizadas.
- XML: dois arquivos bem formados.
- ZIP: integridade CRC verificada ao empacotar.
- Tentativa de JUnit/Lint/APK: falhou antes das tarefas ao baixar Gradle 9.3.1, `Network is unreachable`, código 1. Sem distribuição em cache e sem SDK Android local.

**Não há APK Beta 33 gerado.** Dez novos testes foram escritos em `Beta33WorkspaceTest`, porém não executados. Não houve instalação, validação visual ou execução Android; o relatório não os apresenta como aprovados. Log em `qa/BUILD_BETA33.log`.

## Homologação pendente

1. Compilar, executar JUnit/Lint e instalar com certificado compatível com a série anterior.
2. Conferir dossiês de homônimos, cobranças conjuntas e todas as páginas de listas com mais de 30 registros.
3. Editar contato, conferir preservação de medidas e histórico e restaurar backup num ambiente de teste.
4. Abrir módulos pelo dossiê; confirmar ID/filtro e vínculo dos novos registros. Limpar o filtro deve mostrar todos os clientes.
5. Exportar agenda/notas com nomes e conteúdo ligados/desligados; testar aspas, acentos, quebras e prefixos de fórmula.
6. Reutilizar exercício; confirmar novo ID de série, estado não marcado, limite de 200 e validação de valores.
7. Conferir PDF do dossiê com listas extensas e cliente sem dados opcionais.
8. Conferir retrato/paisagem, larguras 320–1120 dp, fontes 100/150/200%, teclado, claro/escuro e dez paletas.
9. Validar botões de edição, cancelamento, navegação e descarte em todos os módulos.
10. Verificar notificações, backup criptografado, CSV e PDFs em aparelho real.
