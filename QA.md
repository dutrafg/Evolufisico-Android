# QA — EvoluFísico Android 0.2.0-alpha

## Cobertura implementada / revisão estática

- [x] Avaliações ligadas a IDs de clientes; histórico não depende do nome.
- [x] Anamnese: objetivo, nível de atividade, frequência semanal e limitações.
- [x] Antropometria com campos numéricos, vírgula e ponto decimal, faixa validada.
- [x] IMC = peso / (altura em m)^2; RCQ = cintura / quadril.
- [x] Entradas NaN/Infinity, datas futuras e cliente inexistente são rejeitados.
- [x] Gravação cifrada AES-GCM/Keystore e AtomicFile; arquivos não incluídos no backup do sistema.
- [x] Bloqueio de exclusão de cliente que tenha avaliações.
- [x] Testes JUnit 4 adicionados para validações e fórmulas.
- [x] Workflow preparado para rodar assembleDebug/testDebugUnitTest e publicar artifact em caso de sucesso.

## Verificações ainda necessárias

- [ ] **Compilação GitHub Actions:** verificar a execução mais recente. A CI da versão 0.1 falhou; na primeira tentativa faltava Android SDK, na segunda havia incompatibilidade Compose/API 37. A versão 0.2 fixa BOM anterior.
- [ ] Confirmar resultados dos testes em Actions (não marcar como aprovados antes de executar).
- [ ] Testes em emulador/telefone Android 10+ e versão recente.
- [ ] Avaliação de segurança de chaves, corrupção de arquivos, exclusão e recuperação.
- [ ] Acessibilidade e layout em telas pequenas/teclado físico.
- [ ] Teste de comportamento com clientes antigos da v0.1 e registros quebrados.
- [ ] Migração/importação Web, backup e restauração não implementados.

**Não inserir dados reais sensíveis antes de autenticação, backup cifrado e QA de segurança.**
