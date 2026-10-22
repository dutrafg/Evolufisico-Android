# EvoluFísico — Migração do APK beta 7.1.2 para Android nativo

## Identificação da origem

**APK de referência:** `EvoluFisico-Android-1.0.0-beta.7.1.2.apk`, ~500 KB, WebView + assets HTML/JS.

**Application ID da origem:** `br.com.evolufisico.android.desktopbase`.
**SHA-256 do certificado da origem:** `E9:07:97:90:78:9F:B8:64:6A:B3:8F:BB:C4:41:BC:34:A9:E0:A7:DA:79:2C:E4:A4:CB:9D:EB:79:7B:48:02:65`.

O novo projeto usa ID diferente por segurança, preservando a instalação antiga. Não existe chave de assinatura da versão anterior neste pacote.

## Matriz de transposição

| Origem JS/HTML | Kotlin nativo | Estado de implementação |
|---|---|---|
| Identificação + avaliações + perímetros | `data/Entities.kt` e `EvaluationsScreen.kt` | Implementado em código |
| Pollock 7 + bioimpedância | `domain/Anthropometry.kt`, Avaliações, Calculadora | Implementado em código; validação matemática limitada |
| Fotos de 5 posições | Compressão JPEG e fotos nos registros AES-GCM | Implementado em código; SVG legados não exibidos |
| Aderência de treino/dieta, opção nunca praticou | `domain/Metrics.kt`, Avaliações, Análises | Implementado; N/A não recebe pontuação zero |
| Agenda/recorrência + tarefas | `HomeScreen.kt`, `AppointmentNotifications.kt` | Implementado em código; lembretes sujeitos ao agendamento não exato do Android |
| Histórico/evolução/análise | `EvolutionScreen.kt`, `AnalysisScreen.kt` | Implementado em código; motor original de parecer automático não reproduzido integralmente |
| Anamneses | `IntakeScreen.kt` | Implementado em código: questionários nativos por nível |
| Cobranças | `BillingScreen.kt`, `Financial.kt` | Implementado em código: descontos, parcelas, 8 serviços |
| Três PDFs | `system/Reports.kt` | Implementado em código; fidelidade pixel a pixel não verificada |
| Preferências | `SettingsScreen.kt`, `NativeApp.kt` | Implementado em código |
| Banco Android e backups | `data/CryptoStore.kt`, WorkManager | Implementado em código; restauração ainda não testada em Android |
| Backup exportado em JSON/.efx | `data/LegacyImporter.kt` | Implementado em código, mas **não homologado em dispositivo nem com arquivos reais** |

## Casos que exigem homologação, não estão completos/garantidos

1. **Build real:** sincronização Gradle, compilação com SDK, execução instrumentada (nenhum realizado aqui).
2. **Migração real:** validar `.json` e `.efx` antigos com amostras verdadeiras (sem dados pessoais no repositório), checksums, fotos, IDs duplicados e histórico financeiro. A compatibilidade com `.efx` depende de ele conter `kdf`; pacotes vinculados exclusivamente ao Android Keystore anterior não são portáveis.
3. **Parecer inteligente:** a versão web contém heurísticas próprias e modelos que não foram integralmente portados. Em vez de fabricar classificações médicas, a versão nativa mostra indicadores e campos preenchidos por profissional.
4. **Design pixel a pixel:** os componentes e cores foram implementados com Material 3, mas a fidelidade ao APK e o comportamento em 320dp/360dp, tablet, rotação e dark mode dependem de testes visuais reais.
5. **Notificações:** permissão em Android 13+, agendamento aproximado e políticas de bateria precisam ser validados no dispositivo.
6. **PDF:** paginação, imagens longas, impressoras, acessibilidade e caracteres especiais requerem verificação no Android.
7. **Segurança:** realizar auditoria de chave, restauração, dados corrompidos, vulnerabilidades de dependências e uso real do Keystore antes de qualquer uso com pacientes/clientes.

## Verificações realizadas no ambiente

- Inspeção estrutural dos assets do APK fornecido, manifesto, certificados e mapeamento de formulários.
- **58 asserções do domínio Kotlin executadas com sucesso**, cobrindo IMC, RCQ, gordura, Pollock, TMB, aderência N/A, preço e distribuição de parcelas.
- Modelos JSON e importador Kotlin compilados isoladamente contra shims de assinatura de API (somente verificação de tipos, não teste funcional de parser Android).
- Inspeção estrutural de fontes pelo script em `qa/check_source.py`.
- **NÃO EXECUTADOS:** compilação Android Gradle, testes de UI Compose, instrumentação, compilação de APK e homologação.

Nenhuma alteração foi publicada no GitHub ou Vercel. Nenhum APK de origem foi substituído ou desinstalado.

## Continuidade — 0.3.0-alpha (2026-10-21)

**Implementado em código-fonte:** RCE, BRI, Harris-Benedict 1919/1984, Katch-McArdle, PAL, estimativa de gasto energético, simulação de composição-alvo, reserva cardíaca de Karvonen; PAR-Q auto-relatado; conferências matemáticas, de transcrição BIA e de série temporal; associação de avaliação nova a cliente existente; cortesia financeira; ajuda e ações responsivas.

**Aprimoramento da migração:** uso de `cliente_id` do backup original para impedir fusão indevida de homônimos, mapeamento de medidas planas/aninhadas, leitura de `status` para cobrança, conversão de subtotal/desconto com BigDecimal, normalização explícita de datas. Fotos legadas SVG ficam preservadas nos dados brutos mas ainda não são exibidas no PDF nativo.

**Segurança:** leitor de arquivos limitado por tamanho sem `readNBytes`, adequado ao Android 10; validação de tipos também na importação `.efdevice`. Nenhum backup de paciente foi utilizado nos testes; importador real ainda não homologado.

**Verificações realizadas:** 58 asserções do domínio executadas na JVM; parser legado compilado usando somente assinaturas simuladas de `org.json`/`Base64` (essa compilação **não** prova que o parser funciona no Android); teste estrutural estático. Sem SDK Android e Gradle local, sem compilação de APK e sem execução real de Compose/PDF.

## Continuidade — 0.4.0-alpha (2026-10-21)

- Criado `domain/IntegrityAuditor.kt`: relatório local não destrutivo de consistência de registros, cruzando cinco categorias. Exibição pelo botão "Verificar integridade dos registros" em Configurações. Alertas incluem validação temporal e financeira; nenhuma aptidão, risco clínico ou diagnóstico automático é emitido.
- As rotinas `importEncrypted` e `importDeviceEncrypted` passam pelo mesmo importador transacional validado. Duplicatas **idênticas** são ignoradas; duplicatas com conteúdo diferente interrompem a importação sem gravar nenhum registro. `importLegacy` mantém política de recusa de duplicidade.
- Navegação Android "Voltar" nos quatro editores agora pede confirmação antes de descartar alterações.
- Executadas **74 asserções do núcleo Kotlin** e verificação estática de 25 arquivos Kotlin. O teste usa *shims* de `org.json` e Android Base64: não é prova de funcionamento da persistência/criptografia ou da interface em Android.
- **PENDENTE:** SDK Android, compilação Gradle real, teste UI Compose e testes de interoperabilidade com dados de backup do APK antigo.
