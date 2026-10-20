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
