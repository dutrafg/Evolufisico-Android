> **Protótipo sem criptografia:** novos registros em `clients.json`, `assessments.json`, `forms.json` e `charges.json` ficam legíveis no armazenamento interno do aplicativo. Novos backups são arquivos `.json` sem senha. Dados pessoais reais não devem ser usados nesta fase. Antes de atualizar, mantenha cópia de seus backups antigos.

# EvoluFísico Android — Alpha 2 (2.0.0-alpha.2)

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

## Fases 21–25 — Backup diário e recuperação (0.25.0-alpha)

### Fase 21 — Agendamento diário
Em **Configurações > Dados e backups > Backup automático diário**, selecione a pasta de backups pelo seletor Android, digite sua senha portátil `.efbak` (mínimo 10 caracteres), escolha um horário em relógio 24 h e toque em **Ativar backup diário**. Para alterar horário/pasta/senha, reconfigure o agendamento. Desativar preserva os arquivos existentes.

A execução usa **AndroidX WorkManager**, agendado uma vez por dia com período de 24 h e atraso inicial calculado para o próximo horário escolhido. O Android pode adiar trabalhos por Doze, modo economia de bateria, bateria fraca, restrições do sistema ou indisponibilidade do provedor da pasta. Portanto o horário é **preferencial/aproximado, não exato**. O WorkManager persiste o agendamento normalmente mesmo com aplicativo fechado e reinícios, desde que o Android mantenha as permissões e os dados do aplicativo.

### Fase 22 — Credencial protegida
Para poder gravar backup quando o app estiver fechado, a senha precisa ficar disponível localmente para o processo de trabalho. O aplicativo **cifra a senha com uma chave AES-256-GCM não exportável do Android Keystore** e grava apenas o texto cifrado em preferências internas. Ela é decifrada apenas durante a execução do backup e nunca enviada para serviços externos. **Quem possui acesso desbloqueado ao aparelho pode potencialmente acionar o backup**, portanto mantenha o sistema bloqueado. O aplicativo não recupera a senha portátil após desinstalação — o Keystore é perdido. Guarde-a também num gerenciador de senhas ou local seguro separado.

### Fase 23 — Verificação de cada arquivo
A rotina cria um **novo arquivo `EvoluFisico_AUTO_...efbak`**, incluindo clientes, avaliações, formulários e cobranças, na pasta escolhida. Depois **reabre o arquivo, valida autenticação criptográfica e os vínculos** e compara a contagem de registros. Se a verificação falhar, tenta excluir o arquivo incompleto, grava o motivo do erro e não marca a execução como sucesso. Ela não sobrescreve backups manuais nem arquivos de dias anteriores. Esta versão não apaga backups antigos automaticamente.

### Fase 24 — Restauração sem confusão
O botão de restaurar é habilitado somente quando **a base local está vazia** e uma senha com no mínimo 10 caracteres foi digitada. Se há registros, uma mensagem explica por que a restauração não está disponível. Use **Verificar arquivo .efbak** para conferir um backup sem alterar os registros existentes. Não tente desinstalar o app atual sem arquivo externo já verificado e senha guardada. Com APK de assinatura diferente, primeiro é necessário preparar uma migração por backup.

### Fase 25 — Qualidade, estado e testes
Configurações apresenta horário configurado, estado ligado/desligado, último backup validado e última falha. Testes unitários cobrem o cálculo do horário preferido; testes instrumentados exercitam a cifragem e leitura de credenciais no Keystore. Build de APK e testes instrumentados no Android devem ser aprovados pelo CI da versão antes de homologar esta entrega.

**Importante:** o agendamento funciona de forma aproximada e não equivale a um alarme exato. A exportação não é sincronização própria com Google Drive. Se a pasta selecionada for atendida por um provedor em nuvem, a sincronização dos arquivos é responsabilidade desse provedor.

### Atenção ao problema anterior de assinatura

Esta versão continua sendo um APK **DEBUG não atualizável sobre builds DEBUG anteriores gerados com certificados diferentes**. Nenhum código pode contornar a verificação de assinatura do Android. Para proteger seus registros na 0.15.0-alpha, não desinstale sem backup criptografado externo conferido. A distribuição de releases com assinatura fixa ainda precisa da chave privada estável no GitHub.

## Fases 26–30 — Teste imediato do backup e confiabilidade (0.30.0-alpha)

### Fase 26 — Backup agora
Após ativar o backup diário em Configurações, toque em **Fazer backup agora e verificar arquivo**. O aplicativo usa a pasta SAF e a senha já cifrada pelo Android Keystore; não espera pelo horário agendado. Não altera o próximo horário do trabalho periódico. O botão só aparece habilitado com o agendamento ativo.

### Fase 27 — Executor seguro e verificação real
A mesma implementação atende o WorkManager e o botão de teste imediato. Cria um arquivo novo `EvoluFisico_AUTO_*.efbak`, grava pelo seletor de documentos Android, reabre e valida a cifra AES-GCM e a integridade do conteúdo antes de confirmar sucesso. Em falha, registra o erro e tenta apagar o arquivo incompleto; nunca sobrescreve backups anteriores. A operação é serializada no processo para impedir duas exportações simultâneas vindas do aplicativo.

### Fase 28 — Histórico e alerta de atraso
Configurações mostra última **tentativa**, último **sucesso verificado**, nome do arquivo, número de registros e último erro. Exibe alerta quando o backup diário está ativo e decorrem mais de 36 horas sem confirmação desde o agendamento/último sucesso. Esse alerta informa uma possível falha, não é confirmação de execução em segundo plano.

### Fase 29 — Segurança e recuperação
Foram acrescentados testes instrumentados Android que verificam: senha errada e arquivo corrompido recusados na inspeção do backup, sem alterar registros locais; credencial Keystore adulterada recusada. A restauração permanece limitada à base vazia, com pré-verificação não destrutiva disponível. **Não desinstale o aplicativo atual sem antes exportar e verificar seu backup externo e guardar a senha.**

### Fase 30 — Testes, versão e documentação
Versão `0.30.0-alpha` (`versionCode=30`), testes unitários do estado de backup e calendário, compilação, Android Lint, AAB não assinado, teste Android instrumentado no emulador (quando aprovado). Testes físicos de Doze, persistência de permissões do provedor SAF, reinício e bateria permanecem pendentes. Nenhum APK debug CI se torna atualizável por cima de outro certificado distinto: é necessário configurar uma chave de assinatura de distribuição permanente.

**Limites do agendamento:** WorkManager não garante o minuto exato nem execução após encerramento forçado pelo usuário/fornecedor. O backup local usa a pasta escolhida; o app não possui integração própria com Google Drive.

## Fases 31–40: releases oficiais e atualização (0.40.0-alpha)

1. Gerar keystore permanente com script local Windows/Linux.
2. Enviar chave cifrada em Base64 aos GitHub Secrets sem incluí-la em commits.
3. Exigir secrets, fingerprint e versão/tag antes de assinar.
4. Impedir alteração de certificado entre releases.
5. Gerar APK, checksum e fingerprint oficiais verificados por apksigner.
6. Selecionar a release mais nova pelo número semântico, sem APKs debug.
7. Rejeitar download sem SHA-256 e certificado oficial.
8. Confirmar backup .efbak autenticado que contenha exatamente os registros locais antes de instalar.
9. Testar assinatura de release em CI com chave descartável (NÃO distribui essa chave).
10. JUnit, Android CI, emulador e documentação de migração.

**Ativação da assinatura depende de ação sua.** O acesso GitHub disponível neste chat permite atualizar código, mas NÃO cadastrar segredos. Execute no SEU computador o script `scripts/setup-release-signing.ps1` (Windows) ou `scripts/setup-release-signing.sh` (Linux/macOS), com JDK 17 e GitHub CLI autenticado. O script pede senha localmente, cria `evolufisico-release.p12` fora do repositório, cadastra 4 secrets e a variável pública `EF_SIGNING_CERT_SHA256`. Guarde duas cópias privadas da chave e senha fora do GitHub. Não compartilhe o keystore no chat.

Depois execute [Publish signed Android APK](https://github.com/dutrafg/Evolufisico-Android/actions/workflows/android-publish-signed-release.yml) com tag `v0.40.0-alpha`, somente após o CI aprovar e os secrets serem configurados. A primeira release ainda **NÃO existe**.

**A versão antiga debug de assinatura diferente não aceitará a primeira release por cima.** Preservar e verificar backup portátil antes de uma migração é obrigatório; a desinstalação apaga os dados privados.

## Simplificação temporária de armazenamento (0.40.1-alpha)

Para acelerar o desenvolvimento funcional:
- O novo armazenamento local é JSON com gravação atômica, sem AES/Keystore para **novas gravações**.
- Ao abrir registros antigos, o app ainda consegue ler os arquivos `.enc` usando a chave já existente na instalação e gravar uma cópia JSON; **os arquivos cifrados anteriores não são apagados**.
- Novos backups manuais e automáticos são `.json` sem senha, contendo os quatro módulos. A restauração continua permitida apenas com a base vazia.
- Backups antigos `.efbak` com AES-GCM ainda podem ser verificados/restaurados mediante a senha original.
- A rotina diária dispensa guardar credenciais criptográficas; o agendamento, a pasta SAF e a validação do conteúdo continuam funcionando.

**Compatibilidade:** atualização direta ainda depende da mesma assinatura do APK. A retirada da criptografia **não corrige certificados incompatíveis** nem torna segura uma desinstalação sem backup. Não usar clientes reais sensíveis enquanto o protótipo estiver sem criptografia.

## Fases 41–100 — Atendimentos, metas e serviços

A versão **0.100.0-alpha** introduz três módulos funcionais, com dados guardados em **`operations.json`** local. A API de armazenamento é intencionalmente simples, sem criptografia, para continuar o desenvolvimento do protótipo.

- **Agenda** (Fases 41–60): criar, editar, excluir com confirmação e vincular atendimentos a clientes; registrar data, hora, duração, serviço, observações e situação; concluir/cancelar atendimento; validar campos e conflitos entre intervalos; pesquisar por cliente/serviço/data; filtros Hoje/Futuras/Concluídas/Canceladas; ordenar cronologicamente; indicadores de atendimentos do dia e concluídos. No celular, abra Agenda pela Área de trabalho; em telas largas, pelo menu lateral.
- **Metas** (Fases 61–80): objetivo individual por cliente, valor inicial, alvo, atual, unidade e prazo; edição e exclusão confirmada; progresso visual até 100%, inclusive metas descendentes; concluir/reabrir; filtrar ativas/concluídas; ordenar por prazo; resumo na ficha individual e no PDF.
- **Serviços** (Fases 81–91): catálogo com cadastro, edição, exclusão, ativação/desativação, pesquisa, preço em centavos inteiros, duração em minutos e ordenação alfabética.
- **Integrações** (Fases 92–100): escolha de serviço ativo no agendamento e na cobrança; preenchimento automático da duração/preço; atalhos e indicadores na Área de trabalho; navegação lateral rolável; inclusão de atendimentos, metas e catálogo em backup JSON e restauração de base vazia. Backups antigos sem a seção `operations` continuam legíveis, com listas vazias para os três módulos. Bloqueio de exclusão de cliente com registros vinculados. Testes Kotlin e Android de integração ampliados.

Os nomes das fases representam **incrementos funcionais de um mesmo módulo**, não 60 aplicativos independentes. Os três módulos ainda são protótipos e não oferecem pagamentos bancários, integração com calendário externo, notificações ou sincronização na nuvem.

**Avisos de compatibilidade:** o formato JSON é legível; não utilize informações pessoais sensíveis reais. O APK continua sujeito à mesma assinatura digital exigida pelo Android para atualizar sem desinstalar; a migração por backup não contorna essa exigência.

## Ciclo Alpha 2 — primeiro bloco funcional

**Configurações** passam a ter duas áreas principais: Preferências (modo escuro) e **Dados e Privacidade**, onde ficam armazenamentos locais, backups manuais e automáticos, restauração, verificação de integridade e bloqueio da sessão. A busca de atualizações continua no nível principal. Não foram excluídas funções de backup.

**Relatórios:** novo módulo acessível pela Área de trabalho no celular ou pelo menu lateral nas telas grandes. Exporta arquivos CSV de resumo, clientes, avaliações, formulários, agenda, metas, cobranças e serviços. Seleção de cliente e intervalo opcional de datas; exportação via seletor nativo Android (SAF), sem rede. Arquivos CSV UTF-8 com BOM, separador ponto-e-vírgula e aspas. O tratamento de células procura evitar execução acidental de fórmulas por aplicativos de planilha. Valores monetários de cobranças são exportados em centavos inteiros.

**Clientes:** busca por nome e ordenação A–Z / Z–A sem alterar a ordem real no banco. Mensagem quando não há resultado.

**Versão:** `2.0.0-alpha.2` (Android `versionCode=101`). Alpha 2 identifica um novo ciclo de desenvolvimento: não significa que 100 etapas já estejam homologadas. O ciclo anterior 41–100 permanece no repositório.

**Observações:** ainda é um protótipo com JSON legível, sem criptografia de dados novos. A atualização do APK continua exigindo certificado compatível; não desinstale a instalação anterior sem validar backup.

## Alpha 2 — bloco 41–60 · Indicadores

Novo módulo **Indicadores** na Área de trabalho e menu lateral. Exibe totais de cobranças atrasadas e a vencer em até 7 dias, atendimentos agendados para a próxima semana, atendimentos com datas passadas e metas próximas do prazo ou vencidas. Filtros por área e abertura direta dos módulos originais. Resumo local informativo, sem envios externos. Versão de desenvolvimento `2.0.1-alpha.2` (`versionCode=102`).

## Alpha 2 — blocos 61–90 (2.0.2-alpha.2)

**Comparativos mensais:** gráfico de barras simples com recebimentos por data efetiva, vencimentos por mês, saldo ainda pendente e atendimentos agendados/concluídos/cancelados. Filtro por cliente, alternância de 6 ou 12 meses e navegação entre períodos, mantendo valores em centavos inteiros. O painel é apenas de leitura e não executa cobranças.

**Linha do tempo por cliente:** ficha individual agora mostra até oito acontecimentos recentes com avaliações, formulários, atendimentos, cobranças (e recebimentos) e prazos de metas. Os eventos são classificados por data e limitados ao ID do cliente.

Versão de desenvolvimento `2.0.2-alpha.2`, `versionCode=103`, sem alterar o identificador de pacote Android nem o formato dos arquivos JSON. Atualização no mesmo aparelho continua exigindo **certificado de assinatura compatível**.

## Alpha 2 — Busca global e Anotações por cliente (2.0.3-alpha.2)

Depois de Indicadores e Comparativos, adicionamos **Busca global** sem rede e **Anotações** vinculadas a cada cliente.

- Busca global ignora diferenças de maiúsculas e acentos, permite selecionar um módulo, oferece até 100 resultados ordenados e abre a seção correspondente. Inclui clientes, avaliações, formulários, agenda, metas, serviços, cobranças e anotações.
- Anotações possuem data, categoria, título, conteúdo, fixação, criação, edição, exclusão confirmada, filtros por cliente/categoria e busca textual.
- `operations.json` inclui `notes` de forma compatível com bancos anteriores. O conteúdo integra backups JSON, restauração de base vazia, relatórios CSV, ficha e PDF do cliente e busca global.
- O bloqueio da exclusão de clientes agora também protege os registros de Anotações associados.
- A versão passa a ser `2.0.3-alpha.2` (`versionCode=104`). Mantidos Indicadores, Comparativos e linha do tempo da versão anterior.

O armazenamento continua sem criptografia nesta fase de protótipo. Use apenas dados fictícios.
