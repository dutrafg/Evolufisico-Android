# EvoluFísico Android · v0.6.0-alpha

**Fases 5 e 6 combinadas — proteção local, QA e preparação de distribuição.** Projeto Kotlin + Jetpack Compose + Material Design 3 para Android 10+ (API 29), totalmente independente das versões Web, Windows e Linux.

## Módulos existentes

- **Clientes**: inclusão, edição e exclusão com IDs estáveis; impede remoção quando existem avaliações ou cobranças vinculadas.
- **Avaliações**: anamnese inicial, peso, altura, circunferências, IMC e relação cintura/quadril; histórico por cliente e arquivo AES-GCM.
- **Evolução**: gráficos por indicador e período (individual, mensal e trimestral), histórico e relatórios PDF nativos, salvos via seletor Android.
- **Cobranças**: lançamentos por cliente em centavos, contas pendentes/recebidas, botão Receber, filtro de **atrasadas** e busca por cliente ou serviço.
- **Configurações**: tema, verificação de integridade, backup protegido, restauração e botão para bloquear o app.

## Fase 5 — Proteção e QA

- **Autenticação por credencial do Android** obrigatória antes de acessar dados. Android 11+ oferece o diálogo nativo para biometria forte ou PIN/padrão/senha do dispositivo; Android 10 utiliza a confirmação segura de credencial do sistema. Se o aparelho não tiver um bloqueio seguro configurado, o aplicativo não disponibiliza os módulos.
- **Bloqueio ao sair do aplicativo** e botão manual de bloqueio. Durante a abertura legítima do seletor de arquivos SAF, o app preserva a sessão para concluir a operação; esse fluxo deve ser homologado em dispositivo real.
- **FLAG_SECURE** em todas as telas para reduzir exposição por capturas de tela e miniaturas dos aplicativos recentes. Esta proteção não substitui controles do sistema operacional.
- Permissão restrita `USE_BIOMETRIC` sem pedido de acesso amplo ao armazenamento ou Internet.
- Testes unitários adicionais e **testes instrumentados Android compiláveis** para criptografia, restauração isolada e proteção de janela. O workflow `Android Emulator QA (manual)` permite executar os instrumentados em emulador descartável. Compilar testes instrumentados **não equivale a executá-los**.

## Fase 6 — Confiabilidade e preparação de entrega

- **Cobranças:** filtro de vencidas e pesquisa por nome do cliente ou serviço; filtros horizontalmente roláveis em celulares pequenos.
- **Restauração recuperável:** marcador de transação interno criado antes de gravar os dados. Se houver interrupção durante a importação para uma base previamente vazia, o próximo acesso tentará remover dados parcialmente restaurados, sem modificar o backup original. A remoção do marcador e o rollback precisam de validação com falhas reais de energia; **não há garantia de transação multi-arquivo perfeita**.
- **Confirmação de restauração**, verificação de integridade e registros válidos, bloqueio contra sobrescrita de bases preenchidas.
- **GitHub Actions:** build de debug, testes unitários, compilação de testes instrumentados, Android Lint e geração de AAB de release **sem assinatura de distribuição**; somente APK de debug é adequado para testes.
- **Versão:** atualização de código e tela para `0.6.0-alpha`, saltando `0.5.0-alpha` porque foram desenvolvidas duas fases juntas.

## Backup, senha e privacidade

Clientes, avaliações e cobranças ficam cifrados em arquivos separados pelo Android Keystore com AES-256-GCM. Backups portáteis `.efbak` utilizam senha de no mínimo 10 caracteres, derivação PBKDF2-HMAC-SHA256 (250 mil iterações) e AES-256-GCM com nonce aleatório. A exportação e a pasta de destino são escolhidas pelo usuário via SAF, **sem backup automático ou integração própria com Drive**; a senha não fica salva. A restauração aceita apenas banco local vazio e inclui validações anteriores à gravação.

**Limites:** PDFs exportados continuam sem criptografia; liberar o aplicativo com bloqueio de tela não vincula criptograficamente as chaves do banco ao resultado biométrico; não existe trilha de auditoria fiscal, sincronização automática nem importação de bases da versão Web. Não usar com dados reais sensíveis antes da homologação em aparelho físico, de testes de recuperação de falhas e de revisão da LGPD.

## Compilação e distribuição

Use JDK 17, Gradle 8.13, Android SDK API 36:
```bash
gradle :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:bundleRelease
```
O projeto contém `gradle-wrapper.properties`, mas requer gerar `gradle-wrapper.jar` a partir de uma instalação legítima de Gradle executando `gradle wrapper --gradle-version 8.13`.

No [GitHub Actions](https://github.com/dutrafg/Evolufisico-Android/actions), baixe `Evolufisico-Android-debug` somente de uma execução aprovada. O AAB `UNSIGNED` é exclusivamente artefato de build, não é versão pronta para Google Play. Para distribuição pública falta configuração de assinatura privada, políticas de dados, testes instrumentados reais e homologação.

Veja `ROADMAP.md` e `QA.md`.
