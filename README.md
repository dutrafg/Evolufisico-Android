# EvoluFísico Android — 0.15.0-alpha

Aplicativo nativo Kotlin + Jetpack Compose, Android 10+, destinado a testes com dados **fictícios**. O repositório Android é separado dos projetos Web, Windows e Linux.

## Fases 11 a 15

**Fase 11 — Módulo Formulários:** novo item na navegação e na Área de trabalho, com cadastro de respostas ligadas ao ID do cliente, histórico por data, edição, exclusão com confirmação e armazenamento local próprio.

**Fase 12 — Modelos e anamnese:** cinco modelos com exemplos por questão: Anamnese Iniciante, Intermediário, Avançado, Prontidão para Atividade Física e Hábitos e Rotina. Perguntas obrigatórias, textos e respostas Sim/Não possuem validação; exemplos não são respostas preenchidas automaticamente. O módulo é independente da anamnese simplificada do módulo Avaliações.

**Fase 13 — Relatórios:** exportação de formulário preenchido em PDF nativo, paginado, pelo seletor SAF. Não incorpora o painel do aplicativo. PDF exportado não é criptografado.

**Fase 14 — Backup e consistência:** respostas ficam em `forms.enc` com criptografia AES-256-GCM no Android Keystore e escrita `AtomicFile`. Backups portáteis `.efbak` protegidos por senha incluem formulários, clientes, avaliações e cobranças. A restauração aceita backups antigos sem o campo `forms` e mantém a regra de importar somente em banco vazio. O mecanismo de recuperação de importações interrompidas contempla os quatro conjuntos de dados; a exclusão de cliente é bloqueada quando houver formulários associados.

**Fase 15 — Segurança e QA:** correção crítica da gravação AES-GCM em `clients.enc`, `assessments.enc` e `charges.enc`. Agora o **Android Keystore gera o IV** na operação de cifragem, e o aplicativo grava esse IV no mesmo formato de arquivo existente para poder descriptografar normalmente. IV escolhido pelo aplicativo em operação de cifragem com chave Keystore padrão gerava `Caller-provided IV not permitted`. Foram adicionados testes unitários dos modelos e teste instrumentado de gravação, leitura e backup com dados simulados. A execução do CI e de testes físicos deve ser comprovada separadamente.

## Navegação e compatibilidade

No celular, a barra inferior dá acesso a **Início, Clientes, Avaliações, Formulários e Cobranças**. Evolução continua disponível na Área de trabalho, e todas as áreas estão no menu lateral de tablets/telas largas. Configurações permanece acessível pelo ícone no topo.

A versão é `0.15.0-alpha` (`versionCode=15`), que avança cinco fases a partir da `0.10.0-alpha`. Atualizações instaladas **sobre** a mesma assinatura e identificador mantêm normalmente o banco; a desinstalação comum apaga arquivos internos e chaves Keystore. Antes de desinstalar, salve um backup criptografado em pasta externa ao aplicativo e guarde a senha.

**Atenção:** APKs `debug` de execuções distintas do GitHub Actions podem usar assinaturas incompatíveis. O mecanismo Configurações > Atualizações valida identificador, assinatura e versão e não força desinstalação. A publicação de APKs assinados permanentemente requer configurar segredos do keystore no workflow `android-publish-signed-release.yml`.

## Como compilar

JDK 17, Android SDK API36, Gradle8.13:

```bash
gradle :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:bundleRelease
```

O workflow [Android CI](https://github.com/dutrafg/Evolufisico-Android/actions) executa compilação, testes unitários, lint, compilação dos instrumentados e AAB não assinado. Compilar testes instrumentados não significa executá-los num dispositivo. Utilize o workflow manual `Android Emulator QA` para execução em um emulador descartável, nunca em dispositivo com dados reais.

## Limites importantes

- O teste instrumentado ainda depende da execução real em emulador para certificar a correção do Android Keystore. O sucesso dos unit tests Kotlin, por si só, não reproduz o erro do seu smartphone.
- Relatórios PDF são exportados sem criptografia; o conteúdo pode revelar dados pessoais. A liberação de capturas de tela para alpha/beta/RC continua ativa; a versão estável numerada bloqueará capturas.
- A triagem de prontidão é apenas informativa e não substitui diagnóstico médico ou avaliação física profissional.
- Não há migração automática de dados da versão Web, backup automático nem integração própria com Google Drive.
- Ainda faltam testes físicos, auditoria de segurança, políticas LGPD, assinatura estável e homologação antes de utilizar dados sensíveis reais.
