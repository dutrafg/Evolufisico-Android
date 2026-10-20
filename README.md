> **Protótipo sem criptografia:** novos registros em `clients.json`, `assessments.json`, `forms.json` e `charges.json` ficam legíveis no armazenamento interno do aplicativo. Novos backups são arquivos `.json` sem senha. Dados pessoais reais não devem ser usados nesta fase. Antes de atualizar, mantenha cópia de seus backups antigos.

# EvoluFísico Android — Alpha 2 (2.0.3-alpha.2)

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

## Alpha 3 — calendário mensal e ciclo progressivo

**Alpha 3** inicia um novo ciclo funcional 0–100 sem apagar os dados da Alpha 2. Versão Android `3.0.0-alpha.3`, `versionCode=105`, pacote `br.com.evolufisico.android` preservado.

O primeiro incremento acrescenta ao módulo **Agenda** uma visualização mensal iniciada na segunda-feira, com navegação para o mês anterior e seguinte, contagens por estado, indicador de atendimentos ativos por dia, seleção de dia e botão **Ir para hoje**. Ao escolher um dia, os atendimentos são filtrados para aquela data; um novo atendimento começa com o dia pré-preenchido. Os testes unitários cobrem o calendário de outubro, fevereiro de ano bissexto, horários e a separação de atendimentos concluídos e cancelados.

O aplicativo ainda usa JSON local sem criptografia conforme a decisão de desenvolvimento, não devendo receber registros reais sensíveis. O APK de teste permanece sujeito à compatibilidade de assinatura exigida pelo Android.

### Alpha 3 — agendamento semanal e registro de faltas (3.0.1-alpha.3)

Na Agenda, ao criar um atendimento é possível escolher de **1 a 12 repetições semanais**. O aplicativo verifica conflitos com horários já registrados e só salva o conjunto completo se todas as datas estiverem disponíveis. A escolha do dia no calendário preenche a data inicial da série. Os registros continuam individuais em `operations.json`.

Adicionada a situação **Faltou**, com ação *Registrar falta*, filtro Faltas e contador na Agenda. As situações anteriores permanecem válidas. A visualização mensal distingue os totais por estado.

É criação em lote, não sincronização com calendário externo nem cobrança automática. As etapas 51–100 de Alpha 3 permanecem planejadas.

### Alpha 3 — comparecimento e calendário ICS (3.0.2-alpha.3)

O módulo **Comparativos** diferencia *Faltou* de *Cancelado*. Apresenta faltas por mês e uma taxa de comparecimento calculada somente sobre **atendimentos concluídos + faltas**; compromissos futuros e cancelamentos são excluídos do denominador. O cálculo é informativo e baseado nos registros locais.

A **Agenda** permite exportar os atendimentos filtrados e ainda agendados como arquivo `.ics` pela pasta escolhida no Android (SAF). O arquivo usa horários locais flutuantes, identificador único por atendimento, escape de descrição e quebra de linhas UTF-8. Pode ser importado manualmente por aplicativos de calendário, sem autenticação na nuvem. Atendimentos com status Concluído, Cancelado ou Faltou não são exportados para novos compromissos.

Testes JUnit verificam competências financeiras, comparecimento, seleção de estados e a sintaxe do calendário.

## Alpha 4 — Resumo operacional por cliente (4.0.0-alpha.4)

Introduz o módulo nativo **Resumo por cliente**, exibido na tela inicial e na navegação desktop. Calcula sob demanda — sem adicionar tabelas nem modificar registros — atendimentos agendados, concluídos, faltas, cancelamentos, metas ativas/concluídas, pagamentos recebidos, pendentes e vencidos. Valores financeiros permanecem em centavos internamente; comparecimento usa apenas concluídos + faltas no denominador. Os cartões permitem abrir Agenda, Metas e Cobranças. Busca pelo nome do cliente e filtros Com pendências, Em atraso, Com faltas e Com metas. Os totais são recalculados ao tocar Atualizar dados.

Estes incrementos formam a **primeira entrega da Alpha 4**, sem alegar que as 100 etapas do ciclo estejam integralmente homologadas. A próxima Alpha 5 terá funcionalidades adicionais e um APK de teste. A assinatura debug não resolve atualização sobre instalações assinadas com outro certificado.

## Alpha 5 — exportação operacional e instalação paralela (5.0.0-alpha.5)

- **Resumo por cliente:** exportação CSV dos cartões filtrados, contendo comparecimento, faltas, metas, cobranças recebidas, pendentes e vencidas. Seleção da pasta/arquivo pelo Android SAF. Valores são preservados em centavos; separador `; ` e BOM UTF-8. Nomes são escapados para impedir injeção de fórmulas na planilha.
- **Agenda:** ação **Compartilhar** em cada atendimento, abrindo o seletor nativo Android com uma mensagem contendo cliente, data, serviço, duração, situação e observações opcionais. Não há envio automático: o usuário escolhe explicitamente o destino.
- **APK DEBUG de homologação:** usa o identificador **`br.com.evolufisico.android.alpha5`** em vez do `br.com.evolufisico.android` principal. Assim o aplicativo de teste pode ser instalado ao lado do antigo sem substituí-lo ou apagar seus dados, mesmo quando as assinaturas são diferentes. Seu banco fica **isolado** e começa vazio. Para migrar dados, exporte um backup JSON pelo app antigo e restaure no Alpha 5 (somente numa base vazia); mantenha o original instalado até validar os registros. 
- A variante **RELEASE** conserva o identificador original. Atualização sobre a mesma instalação ainda depende da assinatura de distribuição permanente, que não está configurada. Não confundir o APK DEBUG paralelo com APK de atualização.
- Ainda há trabalho da Alpha 5 (etapas 41–100) e testes em aparelhos reais; a contagem de fases não substitui homologação para a Beta.

## Alpha 6 — CSV da Agenda (6.0.0-alpha.6)

A Agenda agora exporta **todos os atendimentos filtrados** para CSV, incluindo faltas e cancelamentos, pelo seletor SAF. O arquivo possui BOM UTF-8, ponto-e-vírgula, nomes dos clientes, duração, status e observações, com escape de fórmulas e conteúdo delimitado. Sem alteração de formato do banco. APK debug independente `br.com.evolufisico.android.alpha6`.

## Alpha 7 — visão semanal e atendimentos pendentes (7.0.0-alpha.7)

A Agenda recebe contadores de atendimentos **hoje**, **próximos sete dias**, **pendentes com datas anteriores à atual**, **concluídos no mês** e **faltas no mês**. Novos filtros **Semana** e **Pendentes** respeitam o status Agendado. O filtro Dia só pode ser selecionado após a escolha de uma data no calendário. Os CSV e ICS preservam os filtros existentes e não fazem sincronização externa automática.

A build debug Alpha 7 usa o identificador `br.com.evolufisico.android.alpha7`, para instalação em paralelo com o aplicativo principal e com os APKs de homologação Alpha 5/6. **Ela inicia com base própria vazia**: para dados de teste, use backup JSON externo e restaure apenas em base vazia. Não desinstale a versão antiga sem verificar o backup. Essa opção **não resolve** a assinatura de atualização no mesmo pacote; assinatura de distribuição permanente segue pendente. Use dados fictícios enquanto o JSON estiver sem criptografia.

## EvoluFísico Android Alpha 8 a 10 — novas funcionalidades

### Alpha 8 (8.0.0-alpha.8, versionCode 112)
Duplicação de atendimento para sete dias depois com novo ID e validação de conflito, confirmação para Concluir, Cancelar ou Registrar falta, reabertura de atendimento sem conflito, testes JUnit. Para preservar o app original, a variante de testes usa `br.com.evolufisico.android.alpha8`.

### Alpha 9 (9.0.0-alpha.9, versionCode 113)
Busca na Agenda sem diferenças de acentos/maiúsculas, inclusive observações; filtro explícito por cliente; contagem de resultados; exportação ICS/CSV baseada numa cópia dos registros visíveis no momento da escolha do arquivo. Variante debug: `br.com.evolufisico.android.alpha9`.

### Alpha 10 (10.0.0-alpha.10, versionCode 114)
Painel no início com os cinco próximos atendimentos, por data e horário, e indicador de atendimentos antigos ainda agendados, com atalho para Agenda. Ordenação determinística, dados inválidos ignorados e módulos existentes preservados. Variante debug: `br.com.evolufisico.android.alpha10`. A versão de produção retém `br.com.evolufisico.android`.

**Dados:** todas as alterações acima atuam sobre o JSON já usado, sem esquema novo. Cada variante debug tem dados próprios; não atualiza nem lê diretamente os dados do app principal. Backup JSON/SAF é a ponte de migração, conferindo a restauração em base vazia antes de excluir qualquer instalação. Certificado fixo para APK com o mesmo pacote permanece pendente. Estes APKs de teste não devem ser usados com dados sensíveis reais enquanto os registros novos estiverem sem criptografia.

Veja `docs/ALPHAS_7_A_10_100_ETAPAS.md` para as quatro listas de 100 etapas. Etapas sem validação permanecem pendentes, mesmo quando a versão já está compilando.


## Alpha 10.1 até Alpha 15 — painel e navegação revisados

- **10.1**: tela inicial deixou de apresentar todos os módulos e agora mantém visão resumida de clientes, Agenda e Cobranças, três próximos atendimentos e atalhos essenciais.
- **11**: menu inferior Início, Clientes, Agenda, Cobranças, Módulos; rail lateral simplificado; catálogo pesquisável por categorias Atendimento, Gestão e Relatórios.
- **12**: chips para selecionar categoria no diretório e combinar com pesquisa.
- **13**: cartão contextual de pendências e ações, que aparece somente quando existem itens que exigem acompanhamento.
- **14**: favoritar até quatro módulos, com persistência local e sem recolocar os cartões na Home.
- **15**: botão Voltar e navegação Android coerentes com módulos e páginas principais.

Consulte [matriz de 600 checkpoints](docs/ALPHAS_10_A_15_100_ETAPAS.md). Este documento NÃO afirma a conclusão de todos os 600 itens. Não apagar a instalação original: `applicationIdSuffix=.alpha15` cria pacote de teste isolado, sem a assinatura oficial permanente. Os dados JSON de prototipagem não são cifrados e requerem dados fictícios.


## Alpha 16–20: melhorias operacionais

- **Alpha 16:** módulos recentes, com apenas os três últimos IDs conhecidos e preferências locais.
- **Alpha 17:** ordenação manual dos quatro favoritos no diretório, sem acrescentar cartões na Home.
- **Alpha 18:** Cobranças tem filtro Próximos 7 dias, busca normalizada e totais da seleção.
- **Alpha 19:** filtro de cobranças por cliente, combinável com a busca e situação; usa IDs únicos.
- **Alpha 20:** CSV das cobranças filtradas via Android SAF, valores em centavos, neutralização básica de fórmulas e testes.

Veja [matriz de 500 checkpoints](docs/ALPHAS_16_A_20_100_ETAPAS.md). A matriz não afirma conclusão integral. O APK debug Alpha 20 usa o identificador independente `br.com.evolufisico.android.alpha20`, sem acesso automático ao banco do original. Preserve o APK antigo e verifique backup antes de migrar. Sem criptografia de registros novos durante esta etapa do protótipo, utilize dados fictícios. Atualização na mesma instalação exige assinatura permanente ainda não configurada.


## Alpha 21–25: renovação visual (Material Design 3)

O início apresenta quatro indicadores com cores suaves, próximos atendimentos reais e acesso rápido. A área Módulos usa cartões em grade com busca, filtros, favoritos e recentes; layout com 1/2/3 colunas em telas estreitas/telefones/tablets. Cores e formas claro/escuro receberam padronização corporativa. A Alpha 25 acrescenta ícones e QA automatizado. Código e checkpoints em `docs/ALPHAS_21_A_25_VISUAL.md`.

APK debug independente (`.alpha25`), banco próprio. Antes de mudar de instalação, faça backup JSON e valide restauração. Assinatura oficial permanente e QA visual físico ainda pendentes.
