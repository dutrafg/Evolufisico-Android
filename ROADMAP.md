# EvoluFísico Android · Roadmap

## Fase 1 — Fundação
- [x] Projeto Android 10+, Compose/Material Design 3; clientes com IDs, AES-GCM local
- [x] CI, testes unitários

## Fase 2 — Avaliações
- [x] Anamnese básica, medidas, histórico e IMC/RCQ
- [ ] Pollock, dobras, bioimpedância e migração Web

## Fase 3 — Evolução
- [x] Comparação individual, mensal/trimestral e relatórios PDF nativos
- [ ] Validação de paginação PDF em aparelhos reais

## Fase 4 — Cobranças e backup
- [x] Pendências, Receber, armazenamento AES-GCM e backup portátil por senha via SAF
- [x] Restauração após validação em banco vazio
- [ ] Rotina de backup automático com estratégia de chaves aprovada

## Fase 5 — Segurança e QA (0.6.0-alpha)
- [x] Tela de autenticação do Android antes dos módulos, Android 10+ (biometria forte ou credencial quando suportado)
- [x] Bloqueio no background/manual, FLAG_SECURE, permissão USE_BIOMETRIC
- [x] Compilação de testes instrumentados e workflow de execução em emulador via disparo manual
- [ ] Testes instrumentados **executados**, testes físicos, pentest, threat model e LGPD
- [ ] Autorização por usuário/perfil, validação criptográfica de autenticação por operação

## Fase 6 — Robustez e distribuição (0.6.0-alpha)
- [x] Busca por cliente/serviço e filtro de cobranças atrasadas
- [x] Verificação de integridade dos dados nas Configurações
- [x] Jornal de restauração: detectar importação interrompida e limpar registros parciais
- [x] Confirmação antes de selecionar backup a restaurar
- [x] CI com lint, testes unitários, APK debug, testes instrumentados compilados e AAB de release sem assinatura
- [ ] Validar rollback de restauração sob quedas reais e SAF externo; transação multi-arquivo robusta
- [ ] Assinatura de distribuição, pipeline de publicação, documento de privacidade
- [ ] Acessibilidade com leitor de tela, layouts em tablets e testes de orientação de tela

**Fases 5 e 6 ainda não representam liberação para produção.**
