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
- [ ] Build e testes da versão 0.4.0-alpha: **aguardando verificação de execução no GitHub Actions**.
- [ ] Teste com Android físico: concessão/revogação de pastas, documentos em cartão SD, provedores remotos, pouco espaço disponível.
- [ ] Testes instrumentados end-to-end de exportação/restauração com dados fictícios.
- [ ] Testes de energia desligada durante importação; reversão e recuperação de arquivos.
- [ ] PIN/biometria, proteção contra leitura com celular desbloqueado e pentest.
- [ ] Revisão LGPD e políticas de retenção de dados e de exportação.
- [ ] Backup automático e sincronização contínua: **não implementados**.

## Registro anterior
0.3.0-alpha: [GitHub Actions aprovado](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39417903230).

**Status:** projeto alpha; não utilizar registros reais sensíveis de clientes.
