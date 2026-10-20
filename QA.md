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

- [x] **Compilação GitHub Actions verificada em 20/10/2026:** execução [39416578439](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39416578439), commit `4b88368fce6ae559ae9a4171837404f0d1b2703e`, resultado **success**.
- [x] `:app:assembleDebug` e `:app:testDebugUnitTest` passaram no GitHub Actions; artefato `Evolufisico-Android-debug` publicado.
- [x] ZIP do artifact inspecionado (contém `app-debug.apk`, integridade do ZIP conferida).
- [ ] Testes em emulador/telefone Android 10+ e versão recente.
- [ ] Avaliação de segurança de chaves, corrupção de arquivos, exclusão e recuperação.
- [ ] Acessibilidade e layout em telas pequenas/teclado físico.
- [ ] Teste de comportamento com clientes antigos da v0.1 e registros quebrados.
- [ ] Migração/importação Web, backup e restauração não implementados.

**Não inserir dados reais sensíveis antes de autenticação, backup cifrado e QA de segurança.**
