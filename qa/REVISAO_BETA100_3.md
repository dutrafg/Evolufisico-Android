# Beta 100 — revisão 3

Mantidos pacote e versionName; versionCode elevado de 701 para 702. Esta entrega contém implementação em código-fonte, não APK homologado.

## Implementação

- Catálogo próprio de exercícios: CRUD, arquivo reversível, unicidade por nome normalizado, busca por nome/grupo/equipamento/instrução, filtro de grupo, paginação e CSV protegido contra fórmulas. Integrado ao banco e backup completo como categoria `exercise`, sem migração destrutiva do SQLite.
- Seletores de exercício com Catálogo/Histórico; troca do exercício de uma série mantém repetições/carga para revisão e desmarca sua execução. Treinos salvos preservam o texto original, independentemente de edição/exclusão do catálogo.
- Aplicação de modelo preserva o cliente filtrado e limpa execução anterior.
- Formulários: paginação, busca normalizada, filtro por ID, seção no dossiê e PDF nativo. Rótulos das perguntas centralizados para tela e PDF. Triagem não respondida continua ausente, não vira resposta negativa.
- Dossiê abre Análises, Cobrança e Relatórios com contexto do cliente. Financeiro conjunto mantém valor integral, sem rateio. Resumo e CSV filtrados; PDF financeiro identificado como completo.
- Timer com relógio monotônico, pausa em milissegundos, duração manual, limite de uma hora e estado salvo fora da navegação dos módulos.
- Configurações apresentam estado persistido do backup automático, horário e último sucesso, reagindo a alterações de preferências.

## Correções

- Nomes digitados em cobranças e formulários não fazem associação automática de pessoas. Seleção explícita por ID e limpeza do vínculo ao editar nome manualmente. Cobrança em dupla não aceita o mesmo ID duas vezes.
- Evolução, Análises e PDF usam data/ID para desempatar avaliações do mesmo dia.
- Regras estruturais de avaliações, cobranças, formulários e agenda aplicadas antes de salvar/restaurar. Restauração valida campos numéricos opcionais antes que um valor malformado possa virar ausência. IDs, textos, parcelas, datas, montantes e coleções têm limites.
- Auditoria de conflitos da agenda usa ordenação e um intervalo representativo por colisão, incluindo virada do dia, sem a varredura quadrática anterior. Auditoria amplia checagem de vínculos de tarefas/agenda/segundo cliente.
- Exportação lê todas as categorias em uma transação, respeitando os limites de 10.000 registros e 16 MB. Categoria desconhecida interrompe o backup. Catálogo global fica fora do backup individual.
- Preparação, leitura, criptografia, gravação e restauração dos backups usam Dispatchers.IO, com bloqueio de ações concorrentes pela interface. Cancelamento propaga a exceção de coroutine; limpeza fica em finally. Strings e cópias internas do runtime não têm eliminação garantida.
- Falha de atualização da tela depois de uma restauração gravada é informada como tal; não se afirma que a importação foi desfeita. Falhas isoladas de reagendamento de lembretes não transformam restauração concluída em falha.
- Quebra de linhas no PDF usa Paint.breakText, evitando reduzir o texto caractere por caractere. Controles de cobrança e salvamento de formulários quebram linha em telas estreitas.

## Evidência executada

Auditoria estática: 58 arquivos Kotlin de produção, 16 destinos. Oito contratos estáticos de área segura aprovados. 60 pares fixos de contraste aprovados, mínimo 6,8547:1. Dois XML válidos. Verificação de integridade do ZIP aprovada na preparação da entrega. Nada disso comprova compilação ou renderização Android.

24 novos testes JUnit preparados: catálogo, isolamento de clientes homônimos, validações financeiras/formulários, perguntas de triagem, ordenação de avaliações, timer, conflitos de agenda e escopo do backup. NÃO EXECUTADOS. O roteiro puro Kotlin também não executou, por ausência de kotlinc.

A tentativa `bash qa/run_validation.sh` passou nas auditorias estáticas e parou antes de compilar no download do Gradle 9.3.1 (`Network is unreachable`). Não há Android SDK instalado. Testes JUnit, Lint, APK, restauração real, SAF e homologação visual permanecem NÃO EXECUTADOS. Consulte BUILD_BETA100_REVISAO3.log e .json.

## Compatibilidade e limites

Backups completos desta revisão podem conter `exercise`; versões anteriores não conhecem essa categoria. Restaure com esta revisão ou posterior. Backups antigos continuam sujeitos às validações do importador; arquivos malformados são recusados, sem sobrescrever registros. Catálogo próprio não representa biblioteca multimídia externa. Timer não emite alarme em segundo plano. A seleção de pasta guarda uma autorização SAF, cuja disponibilidade real depende do Android.

Esta revisão não é uma contagem auditada de 100.000 etapas. A prioridade continua dividida entre implementação e correção. A conclusão dos módulos depende da execução da bateria Android e da homologação listada em STATUS_MODULOS_BETA100.md.
