# EvoluFísico Android nativo — Beta 100 de consolidação

Aplicativo Kotlin, Jetpack Compose e Material 3 para Android 10+ (`minSdk 29`). Pacote `br.com.evolufisico.nativo`; `versionCode=702`; `versionName=0.1.0-native-beta.100`. Sem WebView e sem permissão de internet.

## Espaço de trabalho: 16 módulos

| Módulo | Recursos |
|---|---|
| Início | Indicadores responsivos, prioridades, atalhos, agenda rápida e tarefas |
| Clientes | Busca sem acentos, dossiê, histórico por ID e reavaliação |
| Agenda | Calendário mensal, seleção de data, recorrência, filtros, edição e conflitos |
| Avaliações | Cadastro, perímetros, dobras, BIA, fotos, aderência, metas e PDF |
| Evolução | Comparação de avaliações, gráficos temporais e PDF |
| Metas | Alvos de peso/gordura, linhas de base, prazos e progresso |
| Treinos | Séries estruturadas/em lote, modelos, execução, filtros por cliente/período, histórico diário de carga, CSV e PDF nativo |
| Hábitos | Registro diário, meta, unidade, progresso, sequência e últimos sete dias |
| Formulários | Anamnese por nível e triagem autorreferida |
| Análises | Indicadores e explicações das métricas |
| Cobrança | Serviços, pagamentos, descontos, parcelas e PDF |
| Tarefas | Prioridades, categorias, cliente, prazos, adiamento e conclusão |
| Notas | Categorias, arquivo reversível, prévia, busca, fixação e vínculo ao cliente |
| Relatórios | Períodos, volume, financeiro, resumo e oito exportações CSV pelo seletor Android |
| Calculadora | Ferramentas de cálculo existentes |
| Configurações | Aparência, Dados e privacidade, Avisos e Sobre |

No smartphone, Módulos abre um painel de tela inteira, sem barra lateral ou gesto de arraste na borda. Use “Editar ordem dos módulos”, as setas e “Salvar ordem” para reorganizar. Os quatro primeiros formam a barra inferior. Cancelar preserva a ordem anterior e “Restaurar padrão” pode ser revisado antes de salvar. Em tablets/telas grandes, o menu lateral segue a mesma ordem. Busca local, favoritos e seis atalhos de criação são preservados. Dez paletas claras/escuras, favoritos, texto ampliado e layout compacto são persistidos. Os indicadores se adaptam à largura e à escala de fonte.

## Início e cartões — ciclo de código Beta 5–10

Indicadores maiores com ícone em bloco suave, valor à direita, título e descrição; atalhos e módulos em cartões compactos, com sombras discretas e grade adaptada à fonte e à largura. No painel Módulos há filtro de favoritos e limpeza de busca. A ordem personalizada da Beta 4 é mantida.

O Início inclui resumo de 7, 30 ou 90 dias e pendências de tarefas, cobranças e reavaliações previstas até hoje. O toque numa reavaliação abre seu dossiê por ID. Os números vêm dos registros; não há indicadores inventados de risco de abandono ou gráficos decorativos. Tarefas e agenda usam paginação; tarefas têm filtros e confirmação para excluir.

## Treinos — ciclo de código Beta 11–20

Em Treinos → Evolução de cargas, selecione um cliente por ID e um período. São consideradas somente séries marcadas de sessões concluídas, sem modelos ou datas futuras. O histórico agrega o mesmo exercício por dia, com séries, repetições, carga máxima externa e volume; compara o último dia ao anterior disponível. Nomes diferentes de exercício não são equiparados automaticamente. O CSV do filtro não inclui nome ou ID de cliente.

Sessões e modelos podem ser exportados em PDF pelo seletor nativo. O editor inclui lotes de 1 a 10 séries, validados antes da inclusão, até 200 no treino. Sessões concluídas precisam ser reabertas para editar. A lista de sessões ganhou filtros por cliente e últimos 30/90 dias. Seletores preservam a distinção entre clientes homônimos.

## Revisão Beta 20.1 — layout e QA

Filtros quebram linhas em vez de ficarem escondidos numa faixa horizontal. O campo de data usa um ícone de 48 dp, o calendário mantém largura mínima e rolagem em telas estreitas, e indicadores empilham ícone/valor quando falta espaço ou a fonte é grande. Paginação mostra o estado acima dos botões. A barra inferior e o botão flutuante cedem espaço ao teclado.

Clientes homônimos com IDs importados de prefixo semelhante recebem rótulos distintos, independentemente do idioma do aparelho. Exercícios colados de PDFs normalizam espaços não separáveis. Valores financeiros negativos são rejeitados no domínio; a prévia de edição trata entradas inválidas antes de calcular o total.

Oito testes de regressão foram adicionados, mas não executados. Auditoria estática e leitura de XML passaram. JUnit/Lint continuam bloqueados pelo download do Gradle sem rede. Não houve validação visual em Android.

## Ciclo até a Beta 33

Até a Beta 28: dossiê por seções com paginação, edição de contato na avaliação mais recente, PDF de resumo, CSV de agenda/notas, filtros de cliente em tarefas/hábitos/notas e reutilização de exercícios do próprio banco. Conteúdo de notas só entra no CSV com opção explícita; títulos/categorias continuam no arquivo.

Betas 29–31: contexto do cliente preservado ao abrir Evolução, Metas, Treinos, Hábitos, Tarefas e Notas; criação já vinculada quando há cliente filtrado; histórico de cargas acumulado sem lista intermediária de séries e resultados reaproveitados ao apenas digitar a busca.

Betas 32–33 reservadas a correções: edição com espaço inferior apropriado, hierarquia de fontes, controles acessíveis, nomes das paletas centralizados, paginação de serviços, tratamento de falha ao reutilizar exercício e correção de aviso de falha após excluir agenda. Não houve benchmark nem homologação Android.

## Correção da área de notificações — Beta 100

A estrutura principal passou a reservar `WindowInsets.safeDrawing` antes de medir cabeçalho, navegação e conteúdo. O Scaffold não reaplica os insets do sistema, e o conteúdo consome seu padding. Edição e painel de módulos também tratam recortes de tela e teclado. Os ícones das barras Android acompanham claro/escuro. Não há altura fixa presumida para a barra de notificações.

A continuação consolida ordenação estável das listas por data/ID e verificação do limite de 20.000 linhas antes de montar os CSVs. A Beta 100 é um marco de código, não uma alegação de 67 versões intermediárias compiladas ou testes em aparelho. A correção da sobreposição ainda exige homologação Android.

## Dados e compatibilidade

Todos os registros, inclusive treinos, hábitos e notas, usam o banco AES-GCM protegido pelo Android Keystore. Os novos tipos integram o backup portátil com senha e o backup desta instalação. Backups antigos continuam aceitos. Não use uma versão anterior para editar/restaurar dados desta Beta: ela pode ignorar campos novos, como séries, prioridade e arquivo de notas. A restauração recusa conflitos e não substitui silenciosamente registros.

Clientes são vinculados por `studentId`; nomes iguais não unem históricos. A exclusão de uma avaliação não apaga automaticamente outros registros. A auditoria informa vínculos que perderam seu perfil.

Em Configurações → Avisos, o lembrete de agenda pode ser programado para aproximadamente 15 min, 30 min, 1 hora ou 24 horas antes. O Android pode atrasar o aviso; não é alarme exato.

O timer de descanso opera na tela de Treinos e não é um alarme em segundo plano. Exercícios podem ser preenchidos em texto e/ou séries estruturadas; esta entrega não inclui biblioteca multimídia ou motor de prescrição automática. Metas de hábitos são informadas pelo usuário.

## Compilar e verificar

JDK 17, Android SDK Platform 36, Build Tools 36.0.0 e Gradle 9.3.1.

```sh
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
python3 qa/check_source.py
```

A compilação debug destina-se a testes; esta entrega contém o código-fonte e não inclui um novo APK. A série mantém o mesmo pacote e aumenta o versionCode da Beta 33 (633) para 702; a atualização requer a mesma assinatura. Não desinstale a instalação anterior sem antes proteger seus dados em backup portátil.

Veja `qa/ENTREGA_BETA100.md` para escopo e evidências. Testes automatizados não substituem homologação em aparelho/emulador, inspeção visual ou restauração real.

A revisão preserva os testes anteriores e acrescenta quatro testes para limites das exportações e oito verificações estáticas da integração de insets. A auditoria estática e 60 pares fixos de contraste foram verificados; isso não valida visualmente todos os estados da interface. Os testes Kotlin/Android não foram executados neste ambiente: a distribuição Gradle 9.3.1 não está em cache, a rede bloqueou o download, e não há Android SDK local. Não há APK Beta 100 validado nesta entrega. Consulte `qa/ENTREGA_BETA100.md` para o estado e os comandos de compilação. Os cenários de QA não representam etapas de desenvolvimento concluídas. O CSV exportado é texto aberto para planilhas e não substitui o backup criptografado.

## Beta 100 — revisão 2

Implementado backup individual com senha no dossiê, por ID permanente. Inclui notas arquivadas vinculadas; exclui cobranças conjuntas, modelos globais e registros sem vínculo. Usa o formato portátil existente e a restauração em Configurações, sem sobrescrever conflitos.

Correções: exportações limitadas a 10.000 registros, limpeza de buffers em exceções, fechamento do banco no backup automático, remoção de arquivo parcial no salvamento manual e leitura limitada com progresso mesmo quando a leitura em bloco retorna zero.

Auditorias estáticas aprovadas. Dez testes JUnit adicionados, ainda não executados. A tentativa de testes/Lint/APK falhou antes da compilação, no download do Gradle (rede indisponível). Não representa 100.000 etapas executadas nem conclusão de todos os módulos. Consulte `qa/REVISAO_BETA100_2.md`.

## Beta 100 — revisão 3

`versionCode=702`. Catálogo próprio de exercícios em Treinos → Biblioteca: cadastro, edição, busca por grupo/equipamento, arquivo reversível, seleção/substituição de exercícios e CSV filtrado. O catálogo está no banco criptografado e no backup completo. Não altera retroativamente séries de treinos. Não inclui GIFs, imagens ou conteúdo obtido de bases externas.

Formulários agora têm busca sem acentos, paginação, filtro por cliente, acesso pelo dossiê e PDF nativo com os rótulos das perguntas. Cobrança deixou de vincular pessoas automaticamente pelo nome. Análises, Cobrança e Relatórios preservam o cliente selecionado no dossiê. O PDF financeiro continua explicitamente completo; os indicadores e CSV dos relatórios respeitam o filtro.

Timer de descanso com duração manual de 1–3600 segundos, pausa precisa e estado mantido ao navegar. Sem notificação em segundo plano. Modelos de treino aproveitam o cliente filtrado. Evolução, Análises e PDF usam a mesma ordenação por data/ID.

Backups e restaurações passaram para processamento de IO com indicador de progresso; as exportações leem uma única transação do banco. A tela de configurações mostra pasta selecionada, ativação, horário e último backup automático concluído. Validações estruturais foram integradas ao salvamento e à restauração, incluindo rejeição de medidas não numéricas em campos opcionais e números inteiros fracionários. Mudanças nessas validações podem recusar backups malformados antes aceitos parcialmente.

24 testes JUnit novos preparados, não executados. Auditorias estáticas aprovadas. Consulte `qa/REVISAO_BETA100_3.md`, `qa/STATUS_MODULOS_BETA100.md` e o log de compilação. Não há APK novo. Não se declara conclusão de 100.000 etapas ou homologação dos 16 módulos.


### Adaptação de compilação no GitHub Actions

O pacote-fonte da revisão 3 foi alinhado ao SDK Android 36 disponível no runner de CI, mantendo `targetSdk=36`, `minSdk=29`, `applicationId` e `versionCode=702`. A Compose BOM 2026.09.00 foi substituída por 2025.06.01, utilizada na Beta 20.1 aprovada, porque as dependências mais novas exigem `compileSdk 37`. Essas alterações são de compilação e não homologam o comportamento em dispositivo.
