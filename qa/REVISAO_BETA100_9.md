# Beta 100 — revisão 9

A captura enviada mostra revisão 7, com seletor de cores em linhas de largura total, grade no topo e botão flutuante. A revisão 8 já retirou grade/Módulos/FAB, mas ainda preservava o cabeçalho nativo e o seletor de cores antigo.

## Correção

- Cabeçalho nativo extra removido no celular. A proteção por WindowInsets.safeDrawing permanece. Controle da barra lateral restrito a telas grandes.
- Aparência passa a ser o primeiro cartão de Geral; Dados profissionais vem em seguida. Duas colunas somente com espaço suficiente.
- Paletas reescritas segundo o CSS ef-colors-list do HTML: botões que quebram em linhas, espaçamento 9, amostra 18, cantos 10, padding 8/10 e contorno selecionado 2. Área mínima de toque 48 no Android, nome legível e estado selecionado acessível.
- Sem RadioButton desenhado ou opção de cor ocupando toda a linha. Dez cores, callbacks, persistência e tema escuro preservados.
- Nome/CREF mantêm as mesmas chaves de preferências. Banco clínico, chaves, backups e registros não foram alterados.

## Verificação

A auditoria estática compara os contratos de navegação/cores com reference/app.html e confere seu hash. Resultado de compilação, testes, Lint e assinatura: BUILD_BETA100_REVISAO9.json. Evidências da revisão 8 são históricas. Não há homologação visual em aparelho neste ambiente.
