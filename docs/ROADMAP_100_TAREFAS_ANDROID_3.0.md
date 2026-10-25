# EvoluFísico Android — 100 tarefas técnicas / versão nativa 3.0

Este documento é a **matriz de aceitação**, não uma declaração de conclusão. Uma tarefa só é marcada com `[x]` quando há implementação verificável e teste correspondente. Testes em hardware exigem aparelho real; CI não os substitui.

## Base técnica e paridade

- [ ] **T001** Auditar a estrutura Kotlin/Compose, Gradle e permissões
- [ ] **T002** Comparar a versão publicada do EvoluFisico2 com a referência Android
- [ ] **T003** Mapear todos os módulos, funções e miniaplicativos da web
- [ ] **T004** Preservar histórico e migrações entre betas
- [x] **T005** Usar versão real do BuildConfig em todas as telas
- [x] **T006** Validar esquema SQLite e índices existentes
- [ ] **T007** Separar regras de domínio da interface Compose
- [ ] **T008** Adicionar testes de regressão em cada motor
- [x] **T009** Impedir CI simultâneo desnecessário na mesma branch
- [ ] **T010** Auditar bibliotecas, permissões e componentes exportados

## Início e rotina

- [ ] **T011** Validar indicadores do dashboard com registros reais
- [ ] **T012** Corrigir atualização visual ao salvar/altear dados
- [ ] **T013** Testar calendário mensal em meses curtos e bissextos
- [ ] **T014** Exibir compromissos e local sem perder dados
- [x] **T015** Testar tarefas concluídas e pendentes
- [x] **T016** Conectar lista de cobranças ao botão Receber
- [ ] **T017** Validar reavaliações em atraso com datas reais
- [ ] **T018** Respeitar privacidade financeira em todos os cards
- [ ] **T019** Evitar gestos de arraste que bloqueiem a rolagem
- [ ] **T020** Homologar dimensões e responsividade em tablet

## EvoStore e miniaplicativos

- [x] **T021** Criar catálogo com identificadores imutáveis
- [ ] **T022** Oferecer pesquisa textual funcional
- [x] **T023** Organizar miniaplicativos por categoria
- [ ] **T024** Persistir instalação e desinstalação
- [ ] **T025** Instalar Minha Lista com checkbox e edição reais
- [ ] **T026** Instalar Minha Agenda com ações reais
- [ ] **T027** Instalar Recebimentos respeitando privacidade
- [ ] **T028** Instalar Meus Alunos e Reavaliações vinculados
- [ ] **T029** Instalar IMC, Evolução, Formulários e EFI funcionais
- [ ] **T030** Testar ordem, dimensões e permanência do estado dos miniaplicativos

## Perfil Unificado

- [ ] **T031** Pesquisar alunos por nome e dados reais
- [x] **T032** Criar cadastro com campos obrigatórios
- [x] **T033** Editar aluno sem perder vínculos
- [x] **T034** Abrir avaliações existentes pelo identificador estável
- [x] **T035** Criar nova avaliação mantendo identidade do aluno
- [x] **T036** Editar e excluir avaliação com segurança
- [x] **T037** Exibir histórico, medições e tendência temporal
- [x] **T038** Integrar anamneses ao perfil do aluno
- [ ] **T039** Integrar cobranças sem expor valor ocultado
- [ ] **T040** Homologar fotos, PDFs e todas as ações do perfil

## Avaliações

- [x] **T041** Validar datas e medidas obrigatórias
- [x] **T042** Validar medidas opcionais e limites plausíveis
- [ ] **T043** Identificar método e condições de aferição
- [x] **T044** Implementar protocolos Pollock 3 e 7 separados
- [x] **T045** Preservar fonte e dispositivo da bioimpedância
- [ ] **T046** Capturar até quatro fotografias sem estourar memória
- [x] **T047** Persistir fotografias em backup e restauração
- [x] **T048** Registrar eventos de contexto sem inferências causais
- [x] **T049** Exportar relatório completo com fotos e dados profissionais
- [ ] **T050** Testar edição, histórico e PDFs em smartphone e tablet

## Evolução e inteligência

- [x] **T051** Comparar somente avaliações do mesmo aluno
- [x] **T052** Exigir três/quatro datas para análises científicas
- [x] **T053** Exigir 21 dias totais e 7 dias entre as últimas datas
- [x] **T054** Rejeitar método ou aparelho não comparável
- [x] **T055** Implementar EF78 consistência e procedência
- [x] **T056** Implementar EF79 SEM/MDC e fonte documentada
- [x] **T057** Implementar EF80 massa e coerência de composição
- [x] **T058** Implementar EF81 eventos declarados sem causalidade
- [x] **T059** Implementar EF82 tendências exploratórias sem extrapolação
- [ ] **T060** Homologar EF68, EF70, EF77 e PDFs de análises separados

## Calculadora e laboratório

- [x] **T061** Validar IMC com limites e entradas inválidas
- [ ] **T062** Validar cintura/altura e cintura/quadril
- [x] **T063** Verificar fórmulas Navy e RFM por sexo
- [x] **T064** Validar Pollock 3 por sexo e sítios exigidos
- [x] **T065** Validar Pollock 7 e idade de aplicação
- [ ] **T066** Diferenciar medidas diretas e estimativas derivadas
- [x] **T067** Verificar Harris–Benedict e Mifflin–St Jeor
- [x] **T068** Validar TDEE, fatores de atividade e zonas cardíacas
- [x] **T069** Bloquear entradas ausentes e divisões inválidas
- [ ] **T070** Comparar resultados com referência e testes numéricos

## Formulários e financeiro

- [ ] **T071** Disponibilizar nove modelos de anamnese
- [ ] **T072** Criar e editar formulários personalizados
- [x] **T073** Persistir perguntas/respostas com vínculo ao aluno
- [x] **T074** Reabrir anamnese sem substituir conteúdo
- [x] **T075** Exportar anamnese longa em PDF paginado
- [x] **T076** Criar cobrança com aluno opcional
- [x] **T077** Editar cobrança mantendo situação de pagamento
- [ ] **T078** Receber pendências e registrar histórico financeiro
- [ ] **T079** Filtrar recebidas, pendentes e vencidas
- [x] **T080** Testar integridade dos dados financeiros no backup

## Configurações, dados e segurança

- [ ] **T081** Alternar tema claro e escuro em todos os módulos
- [ ] **T082** Aplicar paleta de dez cores de destaque
- [ ] **T083** Ocultar valores financeiros na dashboard e nos miniaplicativos
- [ ] **T084** Persistir identificação profissional para PDFs
- [ ] **T085** Configurar retorno/reavaliação em 30, 60 e 90 dias
- [x] **T086** Validar backup JSON antes de substituir dados existentes
- [x] **T087** Verificar origem, duplicatas e vínculos na restauração
- [ ] **T088** Preservar backup automático anterior se nova gravação falhar
- [ ] **T089** Garantir armazenamento offline e migração confiável
- [ ] **T090** Completar diagnóstico, criptografia opcional e avisos seguros

## QA, integração e publicação

- [ ] **T091** Executar testes unitários de todos os modelos
- [x] **T092** Executar testes de persistência SQLite/Robolectric
- [x] **T093** Executar testes de PDFs com conteúdo real
- [x] **T094** Executar testes de cálculos e ciência EFIA
- [ ] **T095** Testar restauração de backups antigos e corrompidos
- [ ] **T096** Testar UI clara/escura em smartphone e tablet físicos
- [ ] **T097** Testar navegação, rolagem e formulários em aparelhos reais
- [ ] **T098** Verificar recuperação após fechamento, reinstalação e atualização
- [ ] **T099** Gerar, assinar e testar APK somente após zero bloqueios críticos
- [ ] **T100** Comparar APK final com referência web e publicar relatório QA

## Auditoria objetiva — 25/10/2026, commit de código 188b937

**48 tarefas concluídas no escopo de código com evidência de testes; 52 permanecem em aberto.** Os 60 testes automatizados e o Android Lint passaram na execução [GitHub Actions #40219180246](https://github.com/dutrafg/Evolufisico-Android/actions/runs/40219180246).

Critério de baixa: implementação presente na branch e evidência por testes automatizados e/ou compilação validada, conforme a natureza da tarefa. Tarefas que também exigem ensaio de interface ou aparelho físico permanecem abertas até homologação; portanto esse progresso **não equivale a 48 fluxos homologados em hardware**, nem à paridade completa com o EvoluFisico2.

As 52 pendências incluem verificações de navegação e responsividade, testes específicos do calendário e dos miniaplicativos, conclusão de recursos financeiros e privacidade, equivalência EF68/EF70/EF77, migrações entre versões antigas, testes de instalação/atualização no Android, assinatura compatível e aprovação do APK. O número pode aumentar se a reauditoria revelar requisitos novos da referência web.

## Critério de saída

Nenhum item aberto, compilação e testes automatizados aprovados no commit final, restauração e migração sem perda de dados, fluxo de instalação/atualização em dispositivos reais aprovado, paridade funcional conferida contra a referência atualizada. **Não distribuir APK como estável com bloqueios pendentes.**

## Incrementos já implementados antes desta matriz

- Beta 17: núcleo científico EF78–EF82, Kotlin/Compose, SQLite v9, backup schema 6, relatórios e 42 testes verdes no CI.
- Incremento atual: número da versão derivado do BuildConfig, inspeção de backup antes de restaurar, backups diários não destrutivos e catálogo EvoStore com dez miniaplicativos funcionais. **Nova rodada de CI e homologação ainda exigidas.**
