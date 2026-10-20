# QA — EvoluFísico Android 0.4.0-alpha

## Escopo da revisão de código

- [x] Cobranças em centavos `Long`, sem valores financeiros em `Double` no armazenamento.
- [x] Controle de recebimentos pendentes e recebidos; confirmação ao receber.
- [x] Referência por ID e proteção contra excluir cliente com cobranças.
- [x] AES-GCM com Android Keystore no armazenamento financeiro e AtomicFile.
- [x] Exportação criptografada portátil via senha, salt e nonce aleatórios.
- [x] Criptografia PBKDF2-HMAC-SHA256 + AES-256-GCM autenticado; senha não persistida.
- [x] Backup inclui clientes, avaliações e cobranças; limites de tamanho e registros.
- [x] Importação valida dados e relacionamentos **antes** de gravar, impede sobrescrever base preenchida.
- [x] SAF: CreateDocument, OpenDocument e OpenDocumentTree; permissões persistentes para a pasta.
- [x] Testes unitários para aritmética financeira, senha incorreta, integridade alterada e roundtrip criptográfico **adicionados**.
- [x] **Build e testes da versão 0.4.0-alpha aprovados em 20/10/2026:** [GitHub Actions 39423939587](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39423939587), commit `88dc46986c51d131c3d601bc6882ddbe4af5a285`. Tarefas `:app:assembleDebug` e `:app:testDebugUnitTest` e envio do APK concluídos com sucesso.
- [x] ZIP inspecionado, `testzip()` sem erros. APK debug extraído (57.720.709 bytes); SHA-256: `f6b56d3b383905c7b99bcfd1146fcd7069f66be56ee4629346e4a7c5724bc0a4`.
- [x] Arquivos originais de Evolução e relatórios preservados da Fase 3 após correção da árvore de publicação.
- [ ] Teste com Android físico: concessão/revogação de pastas, documentos em cartão SD, provedores remotos, pouco espaço disponível.
- [ ] Testes instrumentados end-to-end de exportação/restauração com dados fictícios.
- [ ] Testes de energia desligada durante importação; reversão e recuperação de arquivos.
- [ ] PIN/biometria, proteção contra leitura com celular desbloqueado e pentest.
- [ ] Revisão LGPD e políticas de retenção de dados e de exportação.
- [ ] Backup automático e sincronização contínua: **não implementados**.

## Registro anterior
0.3.0-alpha: [GitHub Actions aprovado](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39417903230).

**Status:** projeto alpha; não utilizar registros reais sensíveis de clientes.
