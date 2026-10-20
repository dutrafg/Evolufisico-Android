# EvoluFísico Android — Roadmap

## Fases 1 a 15 (implementadas em versões anteriores)
- [x] Clientes, avaliações, formulários, evolução, PDFs, cobranças, backup criptografado e autenticação Android
- [x] IV gerado pelo Android Keystore ao salvar os quatro bancos, teste instrumentado Android 10
- [x] Formulários por iniciante/intermediário/avançado, prontidão e hábitos
- [ ] Migração Web, protocolos clínicos adicionais, homologação em aparelhos reais

## Fase 16 — Busca de formulários
- [x] Filtros Todos/Anamneses/Outros e pesquisa local por nome do modelo ou data
- [ ] Personalização de modelos mediante gestão de versões de perguntas

## Fase 17 — Ficha de cliente
- [x] Resumo consolidado somente leitura por cliente em Clientes > Ficha
- [x] Listagens resumidas de avaliações, formulários e situação financeira
- [ ] Calendário individual completo e navegação direta para edição de outros módulos

## Fase 18 — Ficha PDF
- [x] Exportação via SAF de PDF paginado para o cliente selecionado
- [x] Medidas existentes e histórico financeiro descritivo, sem diagnóstico
- [ ] PDF com senha, assinatura e testes visuais em aparelhos

## Fase 19 — Financeiro
- [x] Busca de cobrança e filtro de vencidas
- [x] Edição/exclusão de pendentes e estorno confirmado de recebimentos
- [x] Resumos mensais por competência de vencimento/recebimento em centavos
- [ ] Pagamentos parciais, trilha auditável de estornos e integração bancária

## Fase 20 — Segurança, backup e QA
- [x] Verificador de backup portátil sem escrita e com validação de integridade/senha
- [x] Testes instrumentados de inspeção não destrutiva e testes unitários financeiros
- [ ] Auditoria LGPD e segurança, testes físicos de atualização e restauração
- [ ] Assinatura permanente dos APKs de distribuição e política de recuperação

**Status:** versão alpha. Não utilizar dados reais de clientes antes da homologação final.

## Fases 21 a 25 — Backups e confiabilidade
- [x] **21:** agendamento diário via WorkManager com horário configurável e seleção persistente de pasta SAF
- [x] **22:** senha portátil armazenada apenas como credencial AES-GCM cifrada no Android Keystore e removida ao desligar o agendamento
- [x] **23:** criar arquivo novo sem sobrescrita, reler/verificar backup criptograficamente, informar falhas sem apagar dados
- [x] **24:** restaurar apenas em banco vazio, explicar botão desabilitado, deixar inspeção não destrutiva disponível
- [x] **25:** painel com estado, última execução, último erro; testes de calendário e credencial do Keystore
- [ ] Homologar execução diária durante Doze, reinicialização e provedor SAF externo no aparelho real
- [ ] Auditoria de privacidade e segurança, retenção configurável com confirmação, recuperação ante interrupções físicas

## Fases 26 a 30 — Verificação e operação de backup
- [x] **26:** botão de backup imediato com a pasta e senha já configuradas, sem esperar até o dia seguinte
- [x] **27:** executor comum manual/automático, gravação em arquivo separado, releitura e autenticação do backup antes de declarar sucesso
- [x] **28:** últimas tentativas, último sucesso, contagem, nome do arquivo, erro recente e alerta após 36 h sem confirmação
- [x] **29:** testes de senha de backup incorreta, corrupção do arquivo e alteração da credencial cifrada; não sobrescrever base existente
- [x] **30:** testes unitários adicionais, compilação instrumentada e QA de regressão
- [ ] Validar visualmente e com uso real em Android 10+, pasta SAF local ou externa, reinício, Doze e restrições de fabricante
- [ ] Implementar uma release de distribuição assinada de forma permanente para permitir atualizações sem reinstalar

## Fases 31–40 — Distribuição assinada

- [x] 31. Scripts para criar keystore privado no PC do proprietário
- [x] 32. Assistência para cadastrar segredos e fingerprint público no GitHub
- [x] 33. Preflight de assinatura, identidade de pacote e tag/versionName
- [x] 34. Rejeição de troca de certificado entre releases
- [x] 35. Publicação de APK release com certificado e checksum
- [x] 36. Seleção semântica da release oficial mais nova
- [x] 37. Checksum SHA-256 e certificado verificados antes do download
- [x] 38. Validação do arquivo .efbak contra registros locais antes da instalação
- [x] 39. CI com chave descartável para testar assinatura sem distribuí-la
- [x] 40. Testes unitários, documentação e versionamento v0.40.0-alpha
- [ ] Criar a primeira chave privada e cadastrar no GitHub Secrets (ação manual, não autorizada nesta conexão)
- [ ] Publicar APK RELEASE oficial após configurar a chave e aprovar o CI
- [ ] Homologar atualização entre dois APKs RELEASE assinados com a mesma chave em aparelho físico

## Ajuste temporário 0.40.1-alpha
- [x] Simplificar armazenamento local para JSON sem criptografia durante o desenvolvimento.
- [x] Migrar de forma não destrutiva a leitura dos arquivos cifrados antigos.
- [x] Gerar backups JSON sem senha e continuar aceitando .efbak antigos com senha.
- [ ] Revisar a proteção de dados e políticas de criptografia quando o produto estiver funcionalmente completo.

## Fases 41–100 — Operação diária (0.100.0-alpha)

### 41–60: Agenda
- [x] 41 Modelo Appointment; 42 JSON atômico; 43 cliente vinculado; 44 data ISO; 45 hora; 46 duração validada; 47 serviço; 48 observações; 49 criar; 50 editar
- [x] 51 exclusão confirmada; 52 concluir; 53 cancelar; 54 filtro Hoje; 55 Futuras; 56 Concluídas; 57 Canceladas; 58 busca por cliente; 59 busca por serviço e data; 60 detecção de conflitos e ordenação cronológica

### 61–80: Metas
- [x] 61 modelo Goal; 62 vínculo por cliente; 63 criar; 64 editar; 65 alvo; 66 valor inicial; 67 valor atual; 68 unidade; 69 prazo; 70 validação numérica e data
- [x] 71 barra de progresso; 72 metas descendentes; 73 limite percentual de 0 a 100; 74 concluir; 75 reabrir; 76 filtro Ativas; 77 filtro Concluídas; 78 ordenar por prazo; 79 excluir com confirmação; 80 resumo na ficha e no PDF

### 81–100: Serviços e integração
- [x] 81 modelo ServiceItem; 82 cadastro; 83 edição; 84 exclusão; 85 ativação; 86 desativação; 87 preço em centavos; 88 duração; 89 busca; 90 filtro de ativos
- [x] 91 ordem alfabética; 92 escolher serviço no agendamento; 93 preencher duração; 94 escolher serviço na cobrança; 95 preencher preço; 96 atalhos na Área de trabalho; 97 contadores do painel; 98 menu lateral com rolagem; 99 backup/restauração das operações e compatibilidade com backups antigos; 100 validações e testes automatizados

### Próxima homologação
- [ ] Testar em dispositivos físicos Android 10+ toda a navegação, teclado, formulários, rolagens e geração de PDF
- [ ] Testar restore sem perder dados, SAF em diferentes provedores e atualização com assinatura idêntica
- [ ] Criar assinatura release permanente e auditar privacidade antes de usar dados reais

## Ciclo Alpha 2 — etapas 1 a 100

### Bloco A — Estrutura e dados (1–20)
- [x] 1 Criar versão Alpha 2; 2 preservar funções anteriores; 3 Configurações com Preferências; 4 entrada Dados e Privacidade
- [x] 5 armazenamentos sob Dados e Privacidade; 6 backup manual; 7 pasta SAF; 8 backup diário; 9 status e horário; 10 verificação do backup
- [x] 11 restauração segura; 12 verificação da integridade; 13 bloqueio de sessão; 14 manter Atualizações fora de Dados e Privacidade
- [x] 15 novo módulo Relatórios; 16 escolha do relatório; 17 filtro por cliente; 18 período; 19 CSV via SAF; 20 identificação de versão

### Bloco B — Exportação e produtividade (21–40)
- [x] 21 CSV de clientes; 22 avaliações; 23 formulários; 24 agenda; 25 metas; 26 cobranças; 27 serviços; 28 resumo geral
- [x] 29 codificação UTF-8 com BOM; 30 separador compatível com Excel BR; 31 escape de aspas; 32 escape de quebras; 33 tratamento de células que se parecem com fórmulas
- [x] 34 exportação somente leitura; 35 contagem monetária exata em centavos; 36 nome de arquivo padronizado; 37 estados de processamento; 38 mensagens de erro
- [x] 39 busca por nome em Clientes; 40 ordenação A–Z/Z–A
- [ ] Testes Android físicos de seleção de destino, compatibilidade com Excel/LibreOffice e comportamento de teclado

### Bloco C — Fluxos avançados (41–60)
- [ ] Melhorias futuras: busca global, edição contextual de clientes, filtros salvos e ações de múltiplos registros, histórico e indicadores operacionais

### Bloco D — Relatórios e automações (61–80)
- [ ] Melhorias futuras: relatórios gráficos, alertas opcionais, PDF consolidado, personalização de templates e planejamento de sessões

### Bloco E — Qualidade e publicação (81–100)
- [ ] Melhorias futuras: testes de interface/integração, acessibilidade, recuperação do armazenamento, homologação física, assinatura release e preparação da versão estável

**Regra:** cada item só é marcado quando existir implementação verificável no código; compilação e testes da Alpha 2 têm resultado separado no QA.

### Bloco C — 41 a 60 (implementado)
- [x] 41–44 indicadores operacionais calculados com datas injetáveis, sem mutar registros
- [x] 45–47 identificar cobranças vencidas, próximas em 7 dias e totais em centavos
- [x] 48–50 identificar atendimentos futuros (7 dias) e agendamentos com datas passadas
- [x] 51–53 identificar metas vencidas ou próximas em 14 dias, sem inferências clínicas
- [x] 54–56 filtrar por Cobranças/Agenda/Metas, ordenar por data e identificar cliente
- [x] 57–59 navegação dos alertas aos módulos e botão de atualizar indicadores
- [x] 60 testes unitários para prazos, status e totais sem dependência da data real
- [ ] Teste de interface no smartphone após compilação da versão `2.0.1-alpha.2`.

### Bloco D — Alpha 2 etapas 61–80 (implementado)
- [x] 61–63: métricas mensais com intervalo de 1 a 24 meses
- [x] 64–66: valores de vencimentos, recebimentos por data efetiva e pendências em centavos
- [x] 67–69: atendimentos agendados, concluídos e cancelados
- [x] 70–72: isolamento por cliente e ordenação cronológica
- [x] 73–75: comparativos com barras e valores detalhados
- [x] 76–78: escolher 6 ou 12 meses, avançar e retroceder período
- [x] 79–80: testes de competências financeiras e filtros

### Bloco E — Alpha 2 etapas 81–90 (implementado)
- [x] 81–83: modelo de evento da linha do tempo por cliente e datas
- [x] 84–86: eventos de avaliações, formulários e atendimentos
- [x] 87–88: cobranças e recebimentos por data
- [x] 89: prazos de metas incluídos
- [x] 90: oito eventos mais recentes na Ficha, com teste de isolamento

### Alpha 2 etapas 91–100 (homologação em andamento)
- [x] 91–93: testes unitários de indicadores, comparativos e linha do tempo
- [ ] 94–96: compilação, Lint e testes Android do commit 2.0.2-alpha.2
- [ ] 97: validação visual de Dados e Privacidade no Android físico
- [ ] 98: exportação CSV para pasta SAF e leitura em planilhas
- [ ] 99: atualização APK com assinatura fixa e migração verificada
- [ ] 100: QA funcional completo em aparelho físico

## Próximo ciclo autorizado: etapas 101–200
- [ ] 101–120: produtividade e navegação contextual
- [ ] 121–140: relatórios financeiros e avaliações avançadas
- [ ] 141–160: automação de atendimentos e formulários
- [ ] 161–180: importação, exportação e recuperação offline
- [ ] 181–200: acessibilidade, estabilidade e homologação da distribuição

Etapas pendentes não são marcadas como prontas sem implementação e testes.

## Alpha 2 — ampliação 101–120 (2.0.3-alpha.2)

- [x] 101 Criar motor de busca local; 102 normalizar acentos; 103 não distinguir maiúsculas; 104 limitar resultados; 105 ordenar relevância
- [x] 106 pesquisar clientes; 107 avaliações; 108 formulários; 109 agenda; 110 metas
- [x] 111 pesquisar serviços; 112 cobranças; 113 pesquisar anotações; 114 filtrar resultados por módulo; 115 abrir módulo correspondente
- [x] 116 modelo de anotação por cliente; 117 validar data, categoria, título e texto; 118 CRUD, fixação e filtros; 119 integrar backup, CSV, ficha, PDF e proteção na exclusão de clientes
- [x] 120 testes unitários de busca, anotações e teste instrumentado de restauração estendidos
- [ ] Testes visuais e de navegação em smartphone Android real, assinatura release fixa e homologação de dados
