# EvoluFísico Android — 0.20.1-alpha

Aplicativo Android 10+ nativo em Kotlin / Jetpack Compose / Material Design 3. Versão alpha para testes com dados fictícios, mantida separada dos projetos Web, Windows e Linux.

## Implementações das Fases 16 a 20

### Fase 16 — Formulários: organização e filtros
- Busca local no histórico pelo título do modelo ou pela data ISO.
- Filtros **Todos**, **Anamneses** e **Outros**, sem transmitir respostas para serviços externos.
- Mantidos cadastro, edição, exclusão confirmada, vínculo por ID do cliente, cinco modelos de perguntas e PDF individual.
- As respostas continuam criptografadas em `forms.enc`.

### Fase 17 — Ficha individual do cliente
- Nova ação **Ficha** no módulo Clientes.
- Painel individual de leitura com resumo de avaliações, formulários e cobranças, incluindo valores pendentes.
- Consulta pela identidade estável do cliente, sem misturar dados entre cadastros.
- Não altera os arquivos locais durante a consulta.

### Fase 18 — Relatório consolidado
- Exportação da ficha individual em **PDF nativo**, com datas de avaliações, modelos preenchidos, cobranças e status.
- Paginação e linhas ajustadas à largura, sem imprimir o painel principal.
- Destino selecionado pelo usuário pelo seletor de arquivos do Android (SAF).
- **PDFs exportados não são criptografados**, portanto mantenha-os em armazenamento seguro.

### Fase 19 — Cobranças
- Busca por nome ou serviço e novo filtro de cobranças **atrasadas**.
- Edição e exclusão de lançamentos pendentes.
- Estorno de recebimento com confirmação, retornando o status para pendente. Isso **não** executa estorno bancário/fiscal.
- Resumos mensais de vencimentos e recebimentos, com valores mantidos internamente em **centavos inteiros**.
- Arquivo `charges.enc` e formato de backup mantidos.

### Fase 20 — Recuperação e QA
- Nova função **Configurações > Verificar arquivo .efbak**: descriptografa, autentica e valida os dados de backup com a senha fornecida, mostra a contagem de clientes, avaliações, formulários e cobranças e **não grava nada no banco**.
- A restauração continua restrita a uma base local vazia, após validação. A operação de inspeção é separada da restauração.
- Testes de validação de dinheiro e inspeção de backups foram ampliados.
- Compilação Android, testes unitários, Lint e execução real de testes instrumentados precisam de aprovação no GitHub Actions da versão 0.20.0-alpha antes de considerar o APK validado.

## Como atualizar sem perder dados

Atualizar por cima com o mesmo ID de pacote `br.com.evolufisico.android`, assinatura compatível e `versionCode` superior costuma preservar arquivos e chaves no Android. Não há instalação silenciosa; o Android precisa confirmar a atualização. A opção **Configurações > Atualizações** checa ID, versão e assinatura.

**Desinstalar normalmente remove dados locais e o Android Keystore.** Antes de desinstalar, exporte um arquivo `.efbak` para uma pasta externa ao aplicativo, anote a senha e use **Verificar arquivo .efbak**. Depois, numa instalação nova com base vazia, use Restaurar backup. APKs debug gerados por execuções distintas do GitHub Actions podem ter certificados incompatíveis: **não desinstale sem backup conferido**.

## Testes

No Android Studio/Gradle 8.13 e SDK API 36:
```sh
gradle :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:bundleRelease
```
Existe também o workflow [QA em emulador Android 10](https://github.com/dutrafg/Evolufisico-Android/actions/workflows/android-emulator-qa.yml) para testar AES-GCM/Keystore, gravação/leitura e backup. Testes em emulador não substituem validação física.

Captura de tela continua permitida nas versões alpha/beta/RC, com bloqueio `FLAG_SECURE` definido para versões estáveis. Ainda faltam homologação de protocolos clínicos avançados, migração Web, auditoria LGPD, assinatura de APK permanente e segurança física antes de inserir dados reais sensíveis.

## Correção das atualizações — versão 0.20.1-alpha

**Diagnóstico comprovado pela inspeção das assinaturas dos APKs:** a 0.15.0-alpha, a 0.20.0-alpha e outras versões DEBUG foram assinadas por certificados **diferentes**. O GitHub Actions cria uma chave temporária quando não há uma identidade de assinatura configurada. O Android recusa instalar qualquer APK com uma assinatura diferente por cima da instalação atual, mesmo que o ID e a versão estejam corretos.

**Não há como alterar a assinatura da instalação 0.15.0-alpha sem a chave privada de quem a assinou.** A nova versão apresenta diagnósticos de certificado e versão, e o CI agora identifica os APKs de debug como **NÃO ATUALIZÁVEIS**. Um workflow de release com keystore estável é o único caminho seguro para atualizações futuras. O workflow também bloqueia mudanças acidentais de certificado entre releases.

**Migrar com dados existentes:** na versão ainda instalada, exportar .efbak para fora do app, verificar integridade/senha com uma ferramenta confiável independente (ver arquivo HTML de verificação local fornecido no chat), conservar cópia redundante, e somente então instalar uma release oficialmente assinada em uma instalação limpa. Restaurar o .efbak em banco vazio. A desinstalação remove normalmente os dados internos e as chaves Keystore. Não desinstale antes de confirmar a integridade do backup!

O repositório atualmente **não tem nenhuma versão com APK publicada no GitHub Releases**, portanto "Procurar atualizações" não pode oferecer uma versão assinada até a configuração do workflow. Para ativar releases, é necessário criar e guardar um keystore privado duradouro **no computador do proprietário** e adicionar os quatro GitHub Actions Secrets: `EF_KEYSTORE_BASE64`, `EF_KEYSTORE_PASSWORD`, `EF_KEY_ALIAS`, `EF_KEY_PASSWORD`. Nunca envie chaves privadas ou senhas no chat nem as inclua em commits.
