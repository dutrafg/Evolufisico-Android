# Roadmap — EvoluFísico Android

## Fases 1 a 10
- [x] Clientes, avaliações, evolução e PDF, cobranças, backup portátil
- [x] Autenticação Android, tema, política de capturas por versão, atualizador APK via GitHub Releases/SAF
- [x] Checagens de vínculos, anamnese básica, resumos financeiros e layout responsivo
- [ ] Distribuição assinada com chave estável, homologação Android físico e ensaios de recuperação

## Fase 11 — Formulários
- [x] Módulo próprio no menu, cadastro de registros por ID do cliente e histórico
- [x] Edição e exclusão com confirmação
- [x] Campos de dados estruturados, validação e arquivo separado

## Fase 12 — Templates
- [x] Anamnese para iniciantes, intermediários e avançados, cada uma com exemplos
- [x] Modelos de prontidão e hábitos, perguntas obrigatórias e seleções Sim/Não
- [ ] Editor de templates personalizados e assinatura digital de consentimento juridicamente validada

## Fase 13 — Relatórios de formulários
- [x] Geração de PDF independente do painel, paginação e exportação pelo SAF
- [ ] Testes instrumentados visuais de renderização, integração de logotipo e PDF com senha

## Fase 14 — Backup
- [x] AES-GCM com Keystore para `forms.enc`, escrita atômica
- [x] Incluir formulários em backup `.efbak`, restauração em base vazia e proteção contra exclusão de cliente
- [x] Retrocompatibilidade com backups sem formulários
- [ ] Testar falha abrupta/restauração em Android real, backup agendado seguro

## Fase 15 — Segurança e testes
- [x] Consertar o uso de IV fornecido pelo app nos três bancos antigos, agora IV do Android Keystore
- [x] Manter formato de arquivo/nonce para leitura de registros já gravados
- [x] Estender teste instrumentado para gravar/ler formulários e restaurar backup com todos os módulos
- [ ] Executar teste instrumentado no emulador e validar o erro original no aparelho do usuário
- [ ] Auditoria externa de segurança e homologação LGPD
