# Roadmap EvoluFísico Android — v0.10.0-alpha

## Fases 1 a 6
- [x] Base Android10+ Compose, clientes, avaliações básicas, evolução e PDF, cobranças, backup portátil cifrado
- [x] Autenticação do Android, bloqueio de sessão, GitHub Actions, Lint e compilação dos testes instrumentados
- [ ] Testes físicos e instrumentados executados; LGPD; proteção de autenticação por operação

## Fase 7 — Integridade e segurança
- [x] Auditoria referencial cruzada entre módulos, incluindo testes unitários de registros órfãos
- [x] Limite de sessão de 90 segundos para seletor de arquivos
- [x] Recuperação de restauração incompleta
- [ ] Homologação de falhas reais, pentest e revisão de criptografia

## Fase 8 — Anamnese avançada por nível
- [x] Exemplos separados por iniciante, intermediário e avançado
- [x] Novos campos: saúde, hábitos e experiência; AES-GCM e backup compatíveis
- [ ] Protocolos clínicos avançados (Pollock/BIA/dobras), migração da Web

## Fase 9 — Financeiro
- [x] Editar/excluir pendentes, estornar recebimentos com confirmação
- [x] Resumo mensal por vencimento e pagamento, centavos inteiros
- [ ] Pagamentos parciais, trilhas de auditoria e integração bancária/fiscal

## Fase 10 — Interface e atualizações
- [x] NavigationRail em telas largas, menus móveis, rolagem corrigida
- [x] Atualizador em Configurações: GitHub Releases ou APK local por SAF
- [x] Valida pacote, assinatura e versionCode; confirmação de backup e instalador Android
- [x] Workflow de APK de release assinado **somente quando** segredos de assinatura configurados
- [ ] Cadastrar segredos de assinatura e publicar primeira release; ainda não existem assets APK em Releases
- [ ] Testar atualização preservando dados e Android Keystore em aparelhos físicos
- [ ] Acessibilidade, testes em tablet e compatibilidade com políticas de distribuição

**Desinstalar apaga dados locais.** A única recuperação confiável prevista é backup portátil cifrado em pasta externa + restauração com senha.
