# QA — EvoluFísico Android 0.40.0-alpha

## Alterações e verificações adicionadas
- [x] Formulários: busca e filtros sem gravar ou transmitir respostas
- [x] Ficha do cliente: associação dos quatro conjuntos de dados por ID
- [x] PDF de ficha por SAF, com paginação e sem captura do painel
- [x] Cobranças: edição, exclusão e estorno com confirmação; totais mensais em `Long`
- [x] Verificação de arquivo .efbak sem gravação, isolada da restauração
- [x] Extensão do teste instrumentado: inspeção de arquivo criptografado não deve alterar clientes locais
- [x] Testes unitários de resumos mensais, edição e estorno
- [x] **CI da 0.20.0-alpha aprovada**: [GitHub Actions 39438460878](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39438460878): APK, unit tests, Lint, testes instrumentados compilados e AAB não assinado
- [x] **Testes instrumentados Android 10 executados e aprovados**: [emulador 39438460754](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39438460754), 2/2 testes aprovados, sem falhas
- [ ] Testes visuais em smartphone e tablet: PDF, diálogo Ficha, filtros e rolagem
- [ ] Validar backup/atualização em aparelho real e certificado de assinatura estável
- [ ] Auditoria de segurança e LGPD; revisão de PDFs com dados sensíveis

## Histórico de homologação

- Fases 11–15: [GitHub CI aprovada](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39436219594).
- 2 testes instrumentados executados com sucesso em [emulador Android 10](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39436219723), incluindo Keystore, backup e restauração com dados fictícios.

**Atenção:** aprovação de compilação ou testes unitários não substitui QA no aparelho do usuário. A atualização por cima depende de assinatura compatível; desinstalar apaga dados internos e chaves Android Keystore.

## Artefatos conferidos

- [x] ZIP debug de 0.20.0-alpha verificado sem corrupção, contendo `app-debug.apk`.
- [x] APK debug extraído com **58.187.368 bytes** e SHA-256 `67b3f4862065756cf2c3ac07ab57838fc17210f64877031e1bdf4a035a4eea69`.
- [ ] **Teste com seu aparelho Android e dados fictícios ainda pendente**, incluindo PDFs exportados e backup em pasta externa.

## Investigação de incompatibilidade de assinatura (0.20.1-alpha)

- [x] Reproduzida por comparação dos certificados extraídos dos APKs debug anteriores: `0.15.0-alpha` = `CCFD2796386C85221B0201750296D0554010F6EA1717546451D4FD5D8F4EF884`; `0.20.0-alpha` = `B3DE359672040E308185822EF7FAC4B80063630E1B89B246A5B8A84302631A20`.
- [x] Confirmação: certificados de assinatura diferentes tornam **impossível** uma atualização direta preservando a instalação.
- [x] Diagnóstico no aplicativo exibe versão instalada e resumo do certificado, e erro de incompatibilidade distingue certificados.
- [x] Artifact debug renomeado com **NAO-ATUALIZAVEL** para evitar confusão.
- [x] Workflow de release publica impressão digital do certificado e, a partir da segunda release, rejeita assinatura diferente da release anterior.
- [ ] Workflow de release **não pode executar ainda** sem uma chave permanente configurada pelo proprietário como segredo do GitHub.
- [ ] Testar com dois APKs release assinados usando exatamente o mesmo keystore, com versionCode crescente, em Android real e com backup externo.
- [ ] CI de `0.20.1-alpha` a conferir antes de marcar a compilação aprovada.

## Backup automático e tela de restauração — fases 21 a 25

- [x] Scheduler WorkManager periódico 24 h com atraso inicial calculado para o horário escolhido
- [x] Seleção da pasta SAF, exigindo permissão persistente de leitura e escrita
- [x] Credencial cifrada com IV gerado por Android Keystore AES-GCM, nunca senha em texto simples em SharedPreferences
- [x] Desativar o agendamento cancela o WorkManager e remove credencial cifrada, sem excluir backups
- [x] Validação não destrutiva do backup gerado por releitura/autenticação/contagem; tentativa de excluir arquivo incompleto
- [x] Registro de último sucesso/erro e status consultável em Configurações
- [x] Restauração bloqueada para base com registros e exige senha de no mínimo 10 caracteres; mensagem explicativa
- [x] JUnit para horário diário e verificação de argumentos inválidos
- [x] Teste Android instrumentado de selagem, leitura e eliminação da credencial Keystore
- [x] **CI da 0.25.0-alpha aprovada**: [GitHub Actions 39443816780](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39443816780): APK, testes unitários, Android Lint, compilação instrumentada, AAB de release não assinado e artifact debug publicados.
- [x] **3/3 testes instrumentados Android 10 aprovados**: [emulador 39443816033](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39443816033). Exercitam gravação de dados, backup, restauração, política de screenshots e a credencial cifrada no Keystore.
- [x] ZIP APK extraído e verificado sem erros; `app-debug.apk` de **59.353.919 bytes**, SHA-256 `0227720bb10a585950159e6a02d2228752e02ab34caedf45b426ccd9e901da7d`.
- [ ] QA WorkManager em dispositivo físico e provedor SAF real, inclusive reinício/Doze
- [ ] Teste de recuperação com arquivo incompleto e perda de permissões
- [ ] Android não garante disparo exato no minuto, mesmo que horário desejado esteja salvo

**Aviso:** APK debug CI usa assinatura efêmera; não desinstale 0.15.0-alpha ou outro app com dados sem verificar um backup externo. Nenhuma versão alpha dispensa homologação com dados fictícios.

## Validação das Fases 26–30

- [x] Unificação entre execução automática agendada e execução imediata pelo botão
- [x] Gravação em novo arquivo SAF com releitura, verificação criptográfica e limpeza tentativa em falha
- [x] Bloqueio intra-processo para não haver backups concorrentes gerados pelo próprio aplicativo
- [x] Registro de tentativa, sucesso verificado, nome de arquivo, contagem e último erro; alerta após 36 h
- [x] Testes unitários para regra de alerta de backup atrasado e cómputo de próximo horário
- [x] Testes instrumentados reforçados: arquivo cifrado adulterado, senha incorreta, credencial AES-GCM adulterada
- [x] **CI da 0.30.0-alpha aprovada**: [GitHub Actions 39457780192](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39457780192): build APK, JUnit, Lint, compilação de instrumentados, AAB não assinado e upload de APK debug
- [x] **3/3 testes instrumentados Android 10 aprovados**: [GitHub Actions 39457779961](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39457779961). Inclui as verificações de corrupção de backup e credencial Keystore
- [ ] Execução real do WorkManager/SAF e verificação de restauração em aparelho físico
- [ ] Assinatura estável para instalação por atualização; não disponível em build DEBUG CI

### APK verificado (0.30.0-alpha)

- [x] Artifact debug `Evolufisico-Android-QA-DEBUG-NAO-ATUALIZAVEL` baixado, ZIP íntegro.
- [x] `app-debug.apk` com **59.353.919 bytes**, SHA-256 `af3676792a3cb11460862a26e1fdde062ef0cf7de6d38b78a50a81becb27ba9f`.
- [ ] Testar em smartphone o botão `Fazer backup agora`, acesso SAF real, execução diária no horário aproximado, Doze e bloqueios do fabricante.
- [ ] Criar APK RELEASE com assinatura fixa. **APK DEBUG CI não atualiza a versão 0.15 com certificado diferente.**

## Validação das Fases 31–40

- [x] Configuração guiada de chave privada em Windows e Linux, excluída do Git
- [x] Workflow release exige 4 secrets e variável SHA-256 pública (identidade imutável)
- [x] Recusa tag diferente do versionName, ID de pacote divergente e troca de certificado
- [x] Release inclui APK, arquivo SHA-256 e impressão digital do certificado
- [x] Busca semântica da release oficial mais recente e descarte de APKs debug
- [x] Validação SHA-256 obrigatória, fingerprint igual ao instalado ANTES de baixar
- [x] Preflight de backup .efbak criptograficamente verificado e comparado ao banco atual
- [x] JUnit novo para versão semântica, checksum e impressão digital
- [x] Teste efêmero de assinatura APK RELEASE no CI (sem chave privada de produção)
- [x] **CI 0.40.0-alpha aprovada**: [run 39475453138](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39475453138) compilou APK, JUnit, Lint, AAB e fez teste de assinatura RELEASE com chave descartável sem distribuição
- [x] **Android 10 emulador aprovado: 3/3 testes instrumentados**, [run 39475453131](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39475453131)
- [ ] Primeira release oficial depende de executar script privado no PC e cadastrar secrets
- [ ] Teste de atualização sobre release de MESMO certificado em smartphone
- [ ] Migração DEBUG legado para RELEASE exige backup externo verificado e plano de restauração

## Artefato de testes 0.40.0-alpha

- [x] Arquivo ZIP de debug extraído e validado sem erros.
- [x] APK debug SHA-256 `1872d80fe3344532e9fd5c71f03fe4a5cf8710d26c8a6231574c316ca7e9b1de`, tamanho **59.386.687 bytes**.
- [x] Certificado de laboratório exclusivo do CI foi usado apenas para teste do fluxo de assinatura, nunca como identidade permanente de distribuição.
- [ ] **Assinatura privada de produção NÃO cadastrada**: depende de execução local de script de setup com GitHub CLI e arquivamento seguro da chave pelo proprietário.
- [ ] **APK RELEASE oficial NÃO publicado**: requer os 4 GitHub Secrets e PIN SHA-256 de certificado para acionar o workflow.
- [ ] Verificar atualização de dois APKs RELEASE sucessivos assinados com a mesma chave no dispositivo real.

## JSON temporário de desenvolvimento — 0.40.1-alpha
- [x] Novas gravações locais usam `*.json` sem cifra, com `AtomicFile`.
- [x] Leitura legada dos quatro `*.enc` com chave Keystore e cópia para JSON sem apagar os arquivos antigos.
- [x] Novos backups são JSON sem senha; leitura do formato `.efbak` protegido anterior permanece.
- [x] Agendamento automático sem credencial criptográfica, validação de backup ainda realizada.
- [x] Teste instrumentado ajustado para JSON e importação de backup antigo.
- [x] Build 0.40.1-alpha aprovado: [CI 39479954849](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39479954849), APK, unit tests, Lint, testes instrumentados compilados, AAB não assinado e teste de assinatura CI.
- [x] Emulador Android 10 aprovado: [3 de 3 testes instrumentados — 39479954763](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39479954763), JSON novo e backup .efbak antigo.
- [x] APK debug exportado do CI e ZIP íntegro: SHA-256 `8723db1182ed6e47963b39cdc0a32063c6009a60c940681e814f417789b82b2f`.
- [ ] Migração do banco legada em smartphone com dados antigos: ainda não homologada.
- [ ] Testar migração dos bancos AES-GCM em dispositivo contendo dados anteriores.
- [ ] Uso de dados reais sensíveis **não permitido neste protótipo sem criptografia**.

## Testes da versão 0.100.0-alpha — Fases 41–100

- [x] Validar modelos de Agenda: datas, duração, situação, IDs de clientes e conflitos de horários
- [x] Validar Metas: metas ascendentes/descendentes, zero, percentuais, prazo e valores finitos
- [x] Validar Serviços: centavos inteiros, limites e duração
- [x] Interface integrada: atalhos Home, menu lateral rolável, Agenda, Metas e Serviços, ficha de cliente, PDF e cobranças
- [x] JSON operacional incluído no backup, validação de vínculos e restauração sem sobrescrita; backups antigos sem `operations` aceitos
- [x] Teste instrumentado estendido: grava/carrega dados operacionais, exporta backup, restaura e confere clientes e serviços
- [x] CI 0.100.0-alpha aprovada: [run 39482980500](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39482980500) — compilação APK, testes unitários, compilação de testes instrumentados, Android Lint, AAB e teste de assinatura descartável.
- [x] QA em Android 10 aprovada: [run 39482980507](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39482980507), 3/3 testes instrumentados sem falhas.
- [ ] Testes visuais em smartphone e tablet físicos
- [ ] Homologação de assinatura de APK release com chave permanente
- [ ] Protótipo sem criptografia: não utilizar dados sensíveis reais

### Pacote validado v0.100.0-alpha

- [x] ZIP debug baixado do artifact `12292003375` e testado sem erros.
- [x] `app-debug.apk`: 59.730.810 bytes, SHA-256 `e9857db023269984c3ef63846f62c5bf7ddc7e6f6c8d530dc7295d5b26fa961e`.
- [ ] QA manual em Android real, validação visual dos diálogos, edição e preenchimento automático de serviços e verificação de backup SAF externo.
- [ ] Assinatura de distribuição fixa pendente; este APK debug pode ser incompatível com a assinatura das versões já instaladas.
- [ ] Segurança/criptografia desativada temporariamente: só dados fictícios.

## Ciclo Alpha 2 — reorganização de Configurações e relatórios

- [x] Configurações abre com Preferências, Dados e Privacidade e Atualizações
- [x] Segurança (bloquear sessão), dados, backup manual, diário, integridade, inspeção e restauração agrupados sob Dados e Privacidade
- [x] Módulo Relatórios com oito formatos CSV, filtro por cliente/período e exportação via SAF
- [x] CSV UTF-8 com BOM, tratamento de aspas/quebras e prevenção básica contra células de fórmula
- [x] Busca e ordenação de clientes no módulo Clientes
- [x] JUnit adicionado: filtros de período, isolamento por cliente, centavos inteiros e escape CSV
- [ ] CI Alpha 2: executar build, testes unitários, Android Lint e AAB
- [ ] Executar teste instrumentado Android 10 nesta versão
- [ ] Teste visual de navegação Configurações e exportação para pasta real no smartphone
- [ ] Preparação de release com assinatura permanente — ainda pendente

### Alpha 2 · bloco 41–60 (2.0.1-alpha.2)
- [x] Motor de indicadores adicionado para cobranças a vencer/atrasadas, compromissos da semana e metas com prazo próximo/vencido.
- [x] Filtros locais e acesso aos módulos de origem, sem edição ou notificação em segundo plano.
- [x] Testes unitários adicionais com data fixa, verificação de centavos e ausência de falsos positivos quando não há registros.
- [ ] CI da 2.0.1-alpha.2 — ainda não verificado.
- [ ] Execução em emulador e QA visual desta revisão — ainda não verificados.

### Alpha 2 — comparativos e linha do tempo (2.0.2-alpha.2)
- [x] Cálculos mensais por competência e recebimento, com filtro por cliente.
- [x] Testes para valores em centavos e meses diferentes.
- [x] Linha do tempo com cinco tipos de eventos e teste de isolamento.
- [x] Interfaces de comparativos e linha do tempo implementadas.
- [ ] CI Android 2.0.2-alpha.2 ainda não verificado.
- [ ] Testes instrumentados e QA visual desta versão pendentes.

### Alpha 2 — etapa seguinte 2.0.3-alpha.2

- [x] Busca global por oito categorias com normalização de acentos, filtro e limite de resultados
- [x] Anotações por cliente, CRUD, categorias, busca, fixação e exclusão confirmada
- [x] Compatibilidade JSON de `operations.notes`, backups, restauração, CSV, ficha, PDF e bloqueio de exclusão
- [x] Testes JUnit de busca global, regras de anotações e exportação CSV
- [x] Teste instrumentado atualizado para conferir anotação após backup/restauração
- [ ] CI e QA em emulador pendentes da nova versão
- [ ] Teste funcional de filtros, teclado e relatórios em smartphone físico
- [ ] App ainda usa JSON sem criptografia e assinatura debug não garante atualização da instalação anterior

## Alpha 2 — validação final do ciclo 2.0.3-alpha.2

- [x] **CI aprovado:** [run 39488146237](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39488146237); compilação, unit tests, Android Lint, AAB e verificação descartável de assinatura
- [x] **Instrumentados no Android 10:** [run 39488146518](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39488146518), 3 de 3 testes aprovados
- [x] Artifact `Evolufisico-Android-QA-DEBUG-NAO-ATUALIZAVEL` id `12294142206` extraído, ZIP sem corrupção
- [x] APK `app-debug.apk`: **60.156.912 bytes**, SHA-256 `2b99f0f08a7d9b05579d39deabab4f71932e634af13d04764cbd065fac5b25f3`
- [x] Ícone de Comparativos corrigido, mantendo módulo e navegação existentes
- [ ] Testes manuais de experiência real no celular: Configurações, exportação SAF, Agenda, Busca e Anotações
- [ ] APK **debug** não garante compatibilidade de assinatura com builds anteriores; não desinstalar sem backup externo validado
- [ ] Armazenamento JSON permanece sem criptografia nesta fase de desenvolvimento

## Alpha 3 — Agenda calendário (3.0.0-alpha.3)

- [x] Calendário mensal com dias e semanas corretos e contagem de atendimentos
- [x] Seleção de dia e pré-preenchimento do próximo atendimento, sem alterar o JSON existente
- [x] Testes JUnit de mês, ano bissexto, estados e filtro de data
- [ ] CI / compilação APK, Lint e testes JUnit na versão 3.0.0-alpha.3
- [ ] Testes instrumentados Android 10 da versão 3.0.0-alpha.3
- [ ] Teste visual do calendário no smartphone, diferentes tamanhos de tela e contraste
- [ ] Homologação de backup externo, atualização com assinatura e segurança de dados

## Alpha 3 — agendamentos semanais e faltas (3.0.1-alpha.3)

- [x] Séries semanais de 1–12 atendimentos com IDs distintos e datas ISO.
- [x] Conflitos na série invalidam toda a operação antes de gravar.
- [x] Agenda mantém edição individual, conclusão e cancelamento.
- [x] Novo estado e filtro "Faltou"; resumo da Agenda e calendário.
- [x] JUnit para calendário, série semanal, colisão e estado Faltou.
- [ ] CI e testes instrumentados da versão 3.0.1-alpha.3.
- [ ] Teste real de navegação, calendários e diálogos em smartphone.

## Alpha 3 — comparecimento mensal e exportação ICS (3.0.2-alpha.3)

- [x] Comparativos mensais mostram faltas; taxa de comparecimento calculada sobre concluídos + faltas
- [x] Exportação SAF de agenda em iCalendar, somente registros agendados e filtrados
- [x] Testes de UTF-8, folding de linhas, escape de descrição, datas e exclusão de cancelamentos
- [ ] CI e testes instrumentados da versão 3.0.2-alpha.3
- [ ] Verificar leitura dos arquivos ICS em calendário externo no smartphone

## Alpha 3 — integração e critérios finais (ciclo 0–100)

- [x] Teste instrumentado ampliado para confirmar que a restauração conserva atendimentos, leitura mensal e saída ICS.
- [x] Regras de conflito e série semanal cobertas por JUnit.
- [ ] Teste de compatibilidade ICS em aplicativo de calendário real.
- [ ] Teste visual em Android 10+ com diferentes configurações de fonte.
- [ ] Validação de backup/restauração SAF no smartphone.
- [ ] Assinatura de distribuição fixa para atualizar sem reinstalar. O APK debug não resolve isso.
- [ ] Somente após essas etapas, considerar a homologação final da Alpha 3.

## Alpha 4 4.0.0-alpha.4

- [x] Resumo por cliente calculado do armazenamento atual, sem banco auxiliar.
- [x] JUnit: pagamentos vencidos vs futuros, valores recebidos e pendentes, faltas e progresso de comparecimento.
- [x] Filtros por nome, atraso, metas, cobranças e faltas com navegação.
- [ ] Compilação e testes unitários CI da Alpha 4 aguardando execução.
- [ ] QA em emulador Android 10 e testes visuais pendentes.
- [ ] APK debug pode não atualizar assinatura anterior; mantenha backup externo verificado.

## Alpha 5 — pacote de homologação paralelo (5.0.0-alpha.5)

- [x] Resumo por cliente exportável em CSV sem modificar registros.
- [x] Proteção contra fórmulas e caracteres separadores no CSV.
- [x] Mensagem de compartilhamento de atendimento gerada localmente e conferida por JUnit.
- [x] APK debug isolado por applicationId, sem substituir o app original.
- [ ] Compilação CI/Android Lint e testes unitários da Alpha 5.
- [ ] Testes instrumentados em Android 10 da Alpha 5.
- [ ] Instalação paralela e restauração de backup em smartphone real — pendente.
- [ ] Validação visual dos novos botões e conteúdo compartilhado — pendente.


## Alpha 6 e Alpha 7 — novas rotinas

- [x] Exportador CSV da Agenda com BOM UTF-8, todos os estados filtrados e neutralização de fórmulas
- [x] Testes JUnit para ordenação, cabeçalho, aspas, quebras de linha e estados
- [x] Indicadores da Agenda por data e status, filtros Semana e Pendentes
- [x] Testes JUnit para início/fim da semana, atrasos, estados e mudança de mês
- [ ] Confirmar CI, lint, unitários e emulador Android 10 do commit Alpha 7
- [ ] Testar abertura do CSV em planilha do smartphone e cancelamento do SAF
- [ ] Testar restauração JSON para a instalação paralela Alpha 7 em dispositivo físico
- [ ] Comprovar atualização da variante oficial com assinatura release persistente

**Atenção:** pacotes debug das Alpha 5/6/7 são isolados para proteger o principal. Sem a mesma assinatura, não substituem instalações anteriores. Dados JSON do protótipo ainda não estão criptografados.


## Alpha 8–10: QA incremental

- [x] Novos testes JUnit: validação de duplicação +7 dias, conflitos e reversão de status (Alpha 8)
- [x] Novos testes JUnit: busca sem acentos, seleção por cliente e observações (Alpha 9)
- [x] Novos testes JUnit: próximas sessões ordenadas, limite e datas inválidas (Alpha 10)
- [ ] Registrar evidência de CI e emulador Alpha 8, 9 e 10 após a execução
- [ ] Validar em Android físico tela inicial, Agenda, duplicação, confirmação, filtros, arquivos CSV/ICS
- [ ] Confirmar restauração de backup JSON numa base isolada Alpha 10 e totais por cliente
- [ ] Homologação integral das 100 etapas de cada Alpha ainda não demonstrada
- [ ] Sem criptografia: usar somente dados fictícios


## Alpha 10.1–15 — avaliação da reorganização

- [x] Separar a Home de módulos e manter indicadores do dia e próximos atendimentos
- [x] Adicionar novo diretório e menu inferior direcionado a atividades principais
- [x] Cobrir rota dos módulos, buscas, filtros, atalhos e favoritos em JUnit
- [x] Implementar hierarquia Voltar / Módulos no Android
- [ ] Confirmar sucesso final de CI e testes instrumentados do último commit
- [ ] Validar acessibilidade, rolagem, ícones e navegação em aparelho físico
- [ ] Confirmar experiência de usuário em telas 320–420dp e tablets
- [ ] Testar migração/restauração entre pacotes isolados (sem apagar original)
- [ ] Resolver distribuição oficial com assinatura permanente
- [ ] Homologar matriz de 600 checkpoints individualmente


## Alpha 16–20: testes e acompanhamento

- [x] JUnit para histórico recente e deduplicação
- [x] JUnit para alteração da ordem dos favoritos
- [x] JUnit para regras financeiras de data e busca normalizada
- [x] JUnit para isolamento de cliente por ID
- [x] JUnit para CSV financeiro, centavos e caracteres de planilha
- [ ] CI final, Android Lint e testes Android instrumentados da Alpha 20
- [ ] Teste de navegação e filtros num smartphone Android real
- [ ] Teste físico de CSV pelo seletor SAF e planilhas
- [ ] Assinatura de distribuição permanente e instalação sobre APK anterior


## Alpha 21–25 — QA do redesign

- [x] Testes unitários acrescentados para iniciais, datas Hoje/Amanhã e número de colunas da grade
- [x] Módulos e armazenamento mantidos, sem mudança de schema JSON
- [x] Tema claro/escuro e navegação existentes preservados
- [ ] Conferir resultado final Android CI / lint / emulador para Alpha 25
- [ ] Testar em aparelho real fontes grandes, sobreposição, contraste e toque
- [ ] Testar restauração de backup JSON de teste em pacote isolado Alpha 25
- [ ] Validar assinatura release persistente antes de atualizar aplicativo oficial
