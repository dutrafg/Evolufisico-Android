# QA — EvoluFísico Android 0.15.0-alpha

## Incidente de gravação relatado em aparelho real

**Evidência:** tela do Android exibia `Falha ao salvar: Caller-provided IV not permitted`.

**Causa identificada no código:** as três classes `EncryptedClientStore`, `EncryptedAssessmentStore` e `EncryptedChargeStore` tentavam inicializar `AES/GCM/NoPadding` em modo `ENCRYPT_MODE` com IV fornecido pelo aplicativo, comportamento recusado por chaves do Android Keystore com `randomizedEncryptionRequired=true`.

**Alteração:** iniciar `Cipher.ENCRYPT_MODE` somente com a chave; recuperar `cipher.iv` (12 bytes) gerado pelo Android Keystore e salvá-lo no cabeçalho anterior de cada arquivo. A leitura via `DECRYPT_MODE` continua usando o IV do arquivo. Não houve exclusão de dados nem mudança de alias de chave.

## Cobertura das fases 11–15
- [x] Módulo Formulários, histórico por cliente e proteção ao excluir cliente
- [x] Cinco templates e validação de tipos/campos, JUnit adicionado
- [x] PDF nativo do formulário sem captura da UI
- [x] `forms.enc` AES-GCM com IV gerado por Keystore, `AtomicFile`
- [x] Backup portátil inclui formulários; importa backups antigos sem campo `forms`
- [x] Restaurar em base vazia e recuperar importação incompleta em quatro arquivos
- [x] Teste instrumentado ampliado: dados fictícios, quatro bancos, exportação e restauração
- [ ] Android CI v0.15.0-alpha (build, JUnit, lint, AAB) — **aguarda execução**
- [ ] Teste instrumentado executado em emulador Android 10+ — **não confirmado**
- [ ] Teste físico do erro original ao salvar novo cliente — **não realizado**
- [ ] Inspeção visual dos PDFs, acessibilidade, segurança/privacidade e testes LGPD

## Observação

Capturas permitidas em versões alpha/beta/RC; estáveis com `FLAG_SECURE`. Para problemas em aparelho real, preservar dados existentes e **não desinstalar sem backup externo confirmado**, pois as chaves do Keystore normalmente serão apagadas.

Versão anterior v0.10.0-alpha: [CI aprovada](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39432533524).
