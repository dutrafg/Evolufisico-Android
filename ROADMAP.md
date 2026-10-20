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
