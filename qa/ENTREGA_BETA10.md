# Beta 10 — ciclo de código Beta 5 a Beta 10

Código `0.1.0-native-beta.10`, versionCode 610. Mantidos os 16 módulos e suas rotas. Este é um pacote-fonte de desenvolvimento; não é uma sequência de seis APKs compilados/homologados.

## Divisão das frentes

Mantida a prioridade relativa solicitada: funções 25, polimento 25, testes/correções 10. O trabalho abaixo é verificável no código. Não se declara que 60.000 etapas foram executadas por Beta; casos de teste não são contados como etapas de implementação.

| Marco de código | Funções | Polimento | Testes/correções |
|---|---|---|---|
| Beta 5 | Indicadores e atalhos continuam abrindo os módulos | Componente de cartão grande para Início | Valores e descrições permanecem ligados aos registros |
| Beta 6 | Painel filtra favoritos; busca pode ser limpa | Módulos em cartões compactos e grade | Estado vazio e contagem dos módulos visíveis |
| Beta 7 | Resumo de atividade em 7, 30 e 90 dias | Seleção de período consistente | Limites inclusivos, futuro e modelos de treino excluídos |
| Beta 8 | Pendências de tarefas, cobranças e reavaliações | Painel recolhível e cartões compactos | Última avaliação por cliente/ID; nenhum risco clínico ou de abandono inferido |
| Beta 9 | Filtros de tarefas, paginação e exclusão confirmada no Início | Atendimentos com seletor nativo de data | Ordenação estável de reavaliações; ações exibem falhas de gravação |
| Beta 10 | Reavaliação pendente abre o dossiê correto | Colunas respeitam largura, espaços e escala de fonte | Sete testes focados e matriz de 10.000 cenários preparados |

## Referência visual aplicada

- Início: cartões elevados maiores, bloco suave para ícone, valor alinhado à direita, título e informação auxiliar abaixo.
- Módulos/atalhos: cartões compactos com ícone, nome e informação auxiliar; favoritos mantidos.
- Cantos moderados, cores semânticas da paleta selecionada, superfícies claras/escuras e sombra discreta.
- Smartphone mantém painel em tela inteira e edição explícita da ordem. Não há sidebar ou gesto lateral de drawer.
- A imagem de referência contém indicadores de risco/tendências que este app não calcula. Não foram inventados dados nem gráficos para imitá-los.

## Verificação

- `python3 qa/check_source.py`: PASSOU; 43 arquivos Kotlin de produção, 16 destinos.
- Integridade ZIP: verificada ao empacotar.
- Sete testes JUnit focados e 10.000 casos de `Beta10MatrixTest`: PREPARADOS, NÃO EXECUTADOS.
- `testDebugUnitTest`, `lintDebug`, `assembleDebug`: BLOQUEADOS. O wrapper tentou baixar Gradle 9.3.1 e recebeu `Network is unreachable`; também não há SDK Android local.
- Não foi produzido APK Beta 10. APKs anteriores não foram renomeados.
- Não houve homologação visual/em dispositivo. A aproximação ao estilo da referência foi implementada em código, sem afirmar igualdade visual verificada.

## Homologação necessária

1. Compilar e executar testes/Lint; instalar com a mesma assinatura da série para preservar atualização.
2. Smartphone retrato/paisagem e tablet: conferir cartões, fontes de 100/150/200%, valores financeiros longos e ausência de sidebar no telefone.
3. Reordenar módulos, salvar, fechar/abrir o app; filtrar favoritos, limpar busca e conferir todos os 16 módulos.
4. Criar registros nos limites de 7/30/90 dias e no futuro; conferir contagens. Modelos e sessões abertas não podem contar como treinos concluídos.
5. Homônimos devem abrir dossiês diferentes; uma avaliação mais recente deve substituir o prazo de reavaliação anterior no painel.
6. Listas acima de 30 tarefas/atendimentos devem manter todos os registros alcançáveis; cancelar exclusão não deve apagar.
7. Verificar backups, PDFs, CSV, notificações e atualização com dados reais.

```sh
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
python3 qa/check_source.py
```
