# QA · EvoluFísico Android 0.6.0-alpha

## Checagens estruturais e testes adicionados

- [x] Autenticação Android bloqueia a renderização dos módulos até confirmação do sistema
- [x] Android 11+: diálogo com biometria forte e/ou credencial; Android 10: confirmação segura do dispositivo
- [x] FLAG_SECURE configurado na Activity, relock ao background e bloqueio manual
- [x] Permissão USE_BIOMETRIC; nenhuma permissão de Internet nem MANAGE_EXTERNAL_STORAGE
- [x] Cobranças vencidas identificadas somente quando pendentes, cálculo em centavos
- [x] Busca de cliente/serviço com limite de tamanho; filtros roláveis
- [x] Diário de restauração escrito com fsync; limpeza de registros parciais em recuperação
- [x] Verificação de integridade e diálogo de confirmação antes da seleção de backup
- [x] Testes JUnit de atraso e testes anteriores de cripto, validação e evolução preservados
- [x] Teste Android instrumentado criado para roundtrip Keystore/backup e janela FLAG_SECURE
- [x] **CI aprovada em 20/10/2026**: [GitHub Actions #39427100179](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39427100179), commit `ca5ee156243bf040af77c5d1996a1ad7746b75a2`. `assembleDebug`, `testDebugUnitTest`, `assembleDebugAndroidTest`, `lintDebug` e `bundleRelease` concluídos com sucesso.
- [x] APK debug disponibilizado no artifact `Evolufisico-Android-debug`; 57.753.521 bytes; SHA-256 `6c7ef27a31ac7583d4c1209f50464a1dc6fbbae317cce1da7e5bc91b5f917e0b`. ZIP verificado sem erros.
- [x] AAB compilado e arquivado como `Evolufisico-Android-UNSIGNED-release-aab`, **sem assinatura de distribuição**; ZIP verificado sem erros.
- [ ] Testes instrumentados estão **compilados**, mas não **executados**. Use o workflow `Android Emulator QA (manual)` para homologação em emulador Android 10.
- [ ] Execução dos instrumentados no emulador: **workflow manual disponível; ainda não foi executado**
- [ ] Testes em Android real, compatibilidade biométrica por fabricante, Android 10, SAF e cancelamento
- [ ] Falhas forçadas no disco, queda de energia durante restauração e cópia SAF incompleta
- [ ] Criptografia dos PDFs exportados, autenticação por operação e permissão por usuário não disponíveis
- [ ] Revisão LGPD e modelo de ameaças, assinatura privada, QA de acessibilidade e publicação comercial

## Resultado anterior

0.4.0-alpha: [build e testes unitários aprovados no GitHub](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39423939587).

**Não usar dados pessoais sensíveis reais antes de homologação.**
