# Revisão 11 — HTML 1.0.4 Pré-Beta

Referência substituída exclusivamente pelo novo HTML anexado, íntegro em reference/app.html; SHA-256 em HTML_REFERENCE.json. O item HTML original não foi modificado. O hash anterior permanece em HTML_REFERENCE_PREVIOUS.json.

| Novidade encontrada | Adaptação Android |
|---|---|
| Efeitos Avançada / Essencial | Preferência local persistente aplicada aos cartões compartilhados dos nove módulos |
| Contornos, brilho difuso, sombras e hover | Elevação, iluminação e deslocamento nativos com multiplicador 1,2. Inclinação 0,48° × 1,2 × 2 = 1,152°, elevação base 21,6dp, ativa 33,6dp e deslocamento 4,8dp. São equivalências de projeto, não comparação fotométrica entre CSS e Android |
| Acessibilidade | Sem animação quando o Android desativa animadores. Essencial remove decoração dos cartões. Os gestos observam sem consumir toque, preservando botões e rolagem |
| Blur | Camada decorativa separada do conteúdo, sem desfocar textos. Android 12+ aplica blur nativo de 18dp; Android 10/11 mantém iluminação por gradiente como alternativa. Não é backdrop-filter do navegador |
| Busca global | Dentro do menu Módulo e no Início; índice local efêmero de módulos, configurações, clientes, avaliações, agenda, notas, tarefas e metas. Filtro por categoria e até 60 resultados; abre cliente ou seção/data/busca correspondente |
| Perfil inicial | Nome obrigatório se ausente; apelido, CREF e local opcionais. Reutiliza preferências existentes e preenche novos cadastros sem modificar avaliações antigas |
| Início executivo | Retornos vencidos/em até 14 dias, avaliações, resumo de evolução por datas distintas, recebido no mês e pendências financeiras. Atalhos contextuais com IDs estáveis |
| Caixa mensal | Recebido pela data efetiva de pagamento; pendências pelo vencimento. Meses anteriores preservados, seleção de mês e PDF mensal/completo respeitando cliente selecionado. Cortesias excluídas. Legados sem data de pagamento usam vencimento com aviso explícito |
| Correção de navegação do HTML | Mantida a adaptação nativa aprovada: botão único Módulo flutuante à esquerda, afastado da área segura. Sem barra inferior/lateral nem cabeçalho adicional. Início na nova composição |

Não altera cálculos clínicos, entidades persistidas, criptografia, IDs ou formato dos backups. Diferenças de peso não são classificadas como diagnóstico.

Limites: sem instalação/emulador/aparelho real; nenhuma alegação de igualdade visual integral. Verificar fonte ampliada, desempenho GPU, teclado, rotação, efeitos e PDF no celular. Mantidas as pendências anteriores da matriz de paridade, incluindo protocolos completos e equivalência visual dos PDFs.
