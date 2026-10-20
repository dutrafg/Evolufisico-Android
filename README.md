# EvoluFísico Android — v0.4.0-alpha

Aplicativo **Kotlin + Jetpack Compose + Material Design 3**, Android 10+ (API 29), independente das versões Web, Windows e Linux.

## Funcionalidades implementadas

**Clientes:** cadastro, edição e exclusão com IDs estáveis; exclusão bloqueada se houver avaliações ou cobranças. Armazenamento local cifrado AES-256-GCM/Android Keystore.

**Avaliações:** histórico por cliente, anamnese inicial, dados antropométricos, cálculos de IMC e RCQ, validações e arquivo criptografado.

**Evolução:** gráficos de peso, IMC, cintura, quadril, braço, coxa e RCQ; agrupamento mensal/trimestral por último dado válido; relatório PDF gerado com Android PdfDocument; seletor de destino via SAF.

**Cobranças (Fase 4):** lançar cobranças associadas ao cliente (serviço, vencimento e valor em **centavos inteiros**), ver saldos pendentes/recebidos, filtrar lançamentos e **marcar como recebido com confirmação**. Persistência local separada `charges.enc`, criptografada com AES-256-GCM/Keystore e escrita atômica. Os lançamentos usam a moeda BRL. Não há integração bancária ou emissão fiscal.

**Backup e restauração (Fase 4):**

- Exportar **backup portátil criptografado** (`.efbak`): clientes + avaliações + cobranças, protegidos com senha de no mínimo 10 caracteres, PBKDF2-HMAC-SHA256 (250.000 iterações, salt aleatório) e AES-256-GCM autenticado.
- Exportação via seletor Android `CreateDocument`, sem permissão ampla de arquivos, ou seleção de pasta com `OpenDocumentTree` e permissão persistente, criando **um novo arquivo a cada exportação**.
- Restaurar pelo seletor Android `OpenDocument` usando a senha informada. Validação prévia de formato, autenticação criptográfica, IDs únicos, dados e vínculos entre cliente/avaliação/cobrança.
- **Regra de segurança:** restauração só é permitida em instalação com dados de clientes, avaliações **e** cobranças vazios. Dados existentes nunca são sobrescritos nem mesclados. Em falha durante a gravação, tenta reverter os conjuntos que estavam vazios; interrupção do aparelho pode requerer intervenção manual. Conserve o arquivo de backup original.
- A senha nunca é salva em preferências ou no GitHub. Não existe recuperação de senha: sem ela o backup não pode ser aberto.

**Importante:** backup na pasta escolhida é **manual**. O aplicativo ainda **não faz sincronização automática** de banco nem backup em segundo plano. A senha não é armazenada, por segurança. A pasta escolhida pode ser atendida por um provedor externo selecionado pelo usuário, mas não existe integração própria com Google Drive.

## Instalar e testar

No [GitHub Actions](https://github.com/dutrafg/Evolufisico-Android/actions), abra o workflow mais recente com conclusão **success**, baixe o artefato `Evolufisico-Android-debug`, extraia `app-debug.apk` e instale no Android 10+ (depuração). **O resultado só deve ser tratado como compilado/testado após a execução de CI.**

Android Studio: SDK API 36, Gradle 8.13, JDK 17. Com Gradle local execute `gradle :app:assembleDebug :app:testDebugUnitTest`. O wrapper possui as propriedades da distribuição, mas ainda requer gerar o `gradle-wrapper.jar` com `gradle wrapper --gradle-version 8.13`.

## Limitações de segurança / dados pessoais

- **Não usar dados reais de pacientes ou clientes nesta versão alpha**, antes de proteção por PIN/biometria, backup de recuperação devidamente homologado e testes em aparelho real.
- Arquivos de backup são cifrados, mas **PDFs exportados não possuem senha/criptografia**.
- Não existe sistema financeiro completo de faturamento, parcelas, renegociação ou estorno; lançamentos recebidos não são reversíveis pela interface desta versão.
- Ainda faltam protocolos avançados de avaliação (Pollock/BIA/dobras), migração da versão Web, sincronização automática, auditoria de segurança, testes instrumentados e homologação em aparelhos.
- Desinstalar o aplicativo pode destruir dados locais vinculados ao Keystore. Crie e confirme um backup exportado antes de fazer alterações irreversíveis.

Consultar `ROADMAP.md` e `QA.md` para entregas e pendências.
