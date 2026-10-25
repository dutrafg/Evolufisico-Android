# Revisão 12 — cards do Início e edição por arraste

Correção baseada no print enviado: quatro indicadores se distribuíam em três mais um devido à medição fracionada de larguras; o FlowRow alinhava a próxima linha pela altura do painel mais alto, produzindo espaços vazios sob painéis menores.

- Grade nativa de doze faixas com cálculo em pixels inteiros: preserva as larguras do HTML e elimina a quebra por arredondamento.
- Cada próximo painel ocupa a região disponível mais baixa, sem aguardar o painel alto da coluna vizinha. As alturas são medidas pelo conteúdo, incluindo fonte ampliada.
- Em Editar layout, alça Arrastar com alvo destacado e movimento visual. Soltar sobre outro card altera a ordem; cancelar o gesto não salva nenhuma mudança. O modo normal mantém rolagem e botões sem captura pelo arraste.
- Movimentação entre as colunas dentro de cada grupo (indicadores / painéis). Não é um canvas com coordenadas absolutas nem permite sobrepor cartões ou misturar os dois grupos. O encaixe automático mantém o painel utilizável em outras larguras.
- Rolagem automática nas bordas superior/inferior durante o gesto. Mantidos controles Subir/Descer e largura como alternativa acessível.
- Ordem e tamanhos continuam nas mesmas preferências locais. Restaurar layout limpa apenas essas preferências; dados e backups não mudam.
- Efeitos de inclinação dos cards ficam suspensos durante a edição, evitando disputa com arraste. Fora da edição os efeitos configurados permanecem.
- Botão Módulo flutuante e inicialização em Início preservados. HTML de referência não modificado.

Validação: testes de encaixe, limites, não sobreposição, quatro indicadores na linha, fonte ampliada, painel de largura total e movimentos de primeira/última posição. Contratos estáticos de integração e compilação Android. Testes físicos de toque, rotação e rolagem não executados; não afirmar homologação visual.
