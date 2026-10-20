# QA — EvoluFísico Android 0.1.0-alpha

## Verificação realizada no pacote

- [x] Estrutura de diretórios Kotlin, AndroidManifest e recursos XML.
- [x] Presença de configuração Gradle e workflow CI.
- [x] Exigência de Android 10+ e ausência da permissão de Internet.
- [x] Persistência criptografada via Android Keystore + AES-GCM, sem armazenamento em texto puro no arquivo de clientes.
- [x] Dados do app excluídos de backup/transferência automática.
- [x] Validação de nome e proteção contra substituição silenciosa em erro de acesso a dados.
- [ ] **Build Gradle/Android não executado**: SDK e Gradle indisponíveis neste ambiente.
- [ ] **Testes unitários não executados**: requerem Gradle/SDK.
- [ ] Testes em emulador e Android físico.
- [ ] Verificação de acessibilidade, telas pequenas, rotação, modo escuro e comportamento de reinstalação.
- [ ] Testes de perda de chave Keystore e corrupção de arquivo (devem mostrar erro, não sobrescrever dados).
- [ ] Auditoria de segurança aprofundada e pentest.

## Checklist manual após abrir no Android Studio

1. Compilar `assembleDebug` e executar `testDebugUnitTest`.
2. Criar um cliente e reiniciar o processo para conferir persistência.
3. Editar e excluir cliente; verificar que nomes vazios são rejeitados.
4. Alternar tema, fechar e reabrir aplicativo.
5. Testar sem Internet, em Android 10 e versão atual.
6. Inspecionar arquivos internos para confirmar que `clients.enc` não contém os nomes legíveis.
7. Forçar falha de leitura usando um arquivo corrompido e confirmar que a interface informa o problema sem apagar o arquivo.

Nenhum item não testado deve ser marcado como aprovado.
