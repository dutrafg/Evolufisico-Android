> Revisão 12 · versionCode 711: Início com encaixe em colunas por altura, medidas inteiras e arraste pela alça no modo Editar layout. Posições e larguras salvas; menu Módulo preservado. Consulte qa/REVISAO_BETA100_12.md. Validação em aparelho pendente.

> Revisão 11 · versionCode 710 · referência atual: HTML 1.0.4 Pré-Beta anexado. Modos de efeitos +20%/3D reforçado, busca local, perfil profissional, Início executivo e caixa mensal com PDFs. Menu Módulo flutuante preservado. Veja qa/REVISAO_BETA100_11.md; notas abaixo de revisões anteriores são históricas.

> Revisão 10 (versionCode 709): a pedido do usuário, todos os nove módulos ficam no botão flutuante “Módulo”, à esquerda e afastado da área do Android. Sem barra inferior/lateral. Nova abertura inicia em Início. Consulte qa/REVISAO_BETA100_10.md. Verificação visual em aparelho pendente.

# Matriz de adaptação do HTML anexado — revisão 9

Referência: reference/app.html; identidade e hash em HTML_REFERENCE.json. Não consultar o GitHub como fonte de produto. O documento original permanece íntegro.

| Área do HTML | Implementação nativa | Limite/validação necessária |
|---|---|---|
| Início e saudação | Reescrito segundo o último script Home do HTML: quatro indicadores operacionais, prioridades, próximos acontecimentos, insights, sete dias e atalhos; gestão movida para Organização | Comparar o arranjo visual exato em dispositivos reais |
| Organização | Nono módulo, após Início; agenda/calendário, notas, tarefas e metas descritivas; controles legados de agenda e retornos preservados | Permissões e navegação visual em Android |
| Notas | Criar/editar, buscar, vincular, fixar, arquivar/restaurar, lembrar no calendário, excluir | Não há alarme de segundo plano específico para lembretes de notas |
| Tarefas | Data, horário, prioridade Normal/Alta/Urgente, detalhes, vínculo, filtros, concluir/reabrir e excluir | Horário participa do calendário; não adiciona alarme exato Android |
| Metas | Objetivo, acompanhamento descritivo, cliente opcional, prazo, Em andamento/Pausada/Concluída | São separadas das metas antropométricas da avaliação |
| Calendário integrado | Eventos, tarefas, metas e notas; abrir registro pela data e abrir data pelo registro | Layout e fonte ampliada precisam de homologação física |
| Cards editáveis | Controles de ordem/largura em Organização e no novo Início (homeStats/homeCards), com restauração do padrão; Início apresenta resumos sem cadastros | Controles explícitos por botão; gesto de arraste não foi homologado |
| Nove anamneses | Textos e níveis transcritos do PRESETS; busca, filtros, resposta por aluno e histórico | Campos são texto livre, conforme os modelos originais |
| Meus modelos | Criar, editar, personalizar um modelo pronto, excluir sem apagar respostas | Limite nativo de 60 questões por modelo |
| PDF de anamnese | AcroForm offline, nomes/IDs protegidos, respostas multiline editáveis, múltiplas páginas | Conferir preenchimento/salvamento em leitores PDF Android |
| EF70 | Trajetória, suficiência, regressão, consistência, sinais e ações; 48 vetores executados do JavaScript como referência | A conversão de medições de cada avaliação ainda exige conferir protocolos e dados reais |
| Relatório de análises | Síntese editável sem mudar avaliações; PDF com identidade/datas/ressalvas | Gráficos temporais, transições e eventos EF68 adicionados; laboratório EF77 ampliado. Arranjo integral de painéis/PDFs web ainda difere |
| Importação de Organização | JSON específico do HTML, limites e validação; transação aditiva e recusa de conflitos | Não substitui bases existentes como o importador de testes web |
| Exportação de Organização | Backup nativo criptografado e exportação reversa no JSON web com limites/validação | JSON web não guarda categorias/datas de criação nativas/IDs de vínculo e não possui criptografia |
| PDFs de avaliações/evolução/cobranças | Geradores Android existentes, preservando seletor nativo e cancelamento | Não são cópias pixel a pixel do CSS de impressão do HTML |
| Avaliações/Calculadora/Cobranças | Implementações preservadas; comparador e equações EF77 ampliados e comparados com JavaScript | Auditoria exaustiva de todos os cálculos/serviços contra este HTML continua necessária |

## Desvinculação aplicada

- AGENTS.md foi substituído: referência local e hash, sem consulta a repositório.
- UPSTREAM_EVOLUFISICO2.json e a matriz anterior foram movidos para qa/history/revisao5 apenas como histórico.
- README, navegação e a auditoria atual usam o HTML anexado.
- O arquivo anexado e seu item original não foram editados nem sobrescritos.

## Estado honesto

Esta entrega implementa as lacunas funcionais identificadas acima. **Não representa conclusão de toda a paridade do HTML**: os itens explicitamente limitados continuam pendentes. Instalação, aparência em Android, notificação real, SAF e Keystore não foram homologados neste ambiente. APK de depuração solicitado nesta revisão; resultado e assinatura em BUILD_BETA100_REVISAO9.json.

## Navegação na revisão 9

Tela “Módulos”, ícone de grade, busca global e FAB global retirados. Navegação móvel em uma única faixa horizontal, com os nove destinos do HTML, sem ocultá-los atrás de um décimo destino. Barra lateral somente em telas grandes e com opção de recolher. Formulários usa Modelos/Respostas/Criar formulário. Comparações visuais em Android real permanecem pendentes.
