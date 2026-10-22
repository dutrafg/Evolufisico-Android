# Beta 100 — revisão 2 (versionCode 701)

## Implementação entregue

Backup portátil individual no dossiê do cliente, protegido por senha de pelo menos oito caracteres. Seleção exclusivamente pelo ID permanente; inclui avaliações, formulários, agenda, tarefas, treinos, hábitos, notas (inclusive arquivadas) e cobranças individuais vinculadas. Não inclui cobranças conjuntas, modelos globais ou registros sem vínculo. O arquivo mantém o formato EF-NATIVE-BACKUP; restauração pelo fluxo existente em Configurações. Conflitos continuam recusados sem sobrescrever registros existentes. Não é um PDF ou CSV.

## Correções entregues

- Exportações portátil e automática recusam mais de 10.000 registros, limite da restauração.
- Buffers de texto claro dos backups são zerados também em exceções de criptografia, limite ou análise JSON. Isso não garante eliminação de Strings e cópias internas da memória gerenciada.
- Backup automático fecha sua conexão ao banco e limpa o buffer mesmo quando a criação do destino falha.
- Falha ao salvar backup manual tenta remover o arquivo parcial.
- Leitura limitada usa leitura de um byte quando a leitura em bloco retorna zero, impedindo repetição sem progresso e mantendo o limite. Restauração portátil passou a usar essa função.
- Mantida a correção anterior da área segura da tela Android.

## Verificação e limites

Auditoria estática aprovada: 49 arquivos Kotlin de produção e 16 destinos. Oito verificações estáticas da integração da área segura aprovadas; 60 pares fixos de contraste aprovados (mínimo 6,8547:1); XML analisado sem erro. Essas verificações não executam a interface.

Adicionados dez testes JUnit: cinco de seleção/isolamento do backup individual e cinco de leitura limitada. Preparados, NÃO EXECUTADOS. Testes, Lint e assembleDebug foram solicitados em conjunto; falharam antes da compilação no download do Gradle 9.3.1, com Network is unreachable. Veja BUILD_BETA100_REVISAO2.log. Não há SDK Android ou distribuição Gradle disponível localmente. Não foi gerado APK nesta revisão.

Pendentes: compilação Kotlin, testes JUnit/Android, restauração real (inclusive senha incorreta e conflitos), validação de SAF e falhas de escrita, teste visual em aparelhos de diferentes tamanhos e fontes. Esta revisão é uma entrega parcial do pedido amplo: não comprova 100.000 etapas, conclusão de todos os módulos ou ausência de bugs. A divisão 50% implementação / 50% correção é uma prioridade de trabalho, não uma contagem auditada de esforço.
