# EvoluFísico Android nativo — Beta 20 de testes

Aplicativo Kotlin, Jetpack Compose e Material 3 para Android 10+ (`minSdk 29`). Pacote `br.com.evolufisico.nativo`; `versionCode=620`; `versionName=0.1.0-native-beta.20`. Sem WebView e sem permissão de internet.

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
| Relatórios | Períodos, volume, financeiro, resumo e seis exportações CSV pelo seletor Android |
| Calculadora | Ferramentas de cálculo existentes |
| Configurações | Aparência, Dados e privacidade, Avisos e Sobre |

No smartphone, Módulos abre um painel de tela inteira, sem barra lateral ou gesto de arraste na borda. Use “Editar ordem dos módulos”, as setas e “Salvar ordem” para reorganizar. Os quatro primeiros formam a barra inferior. Cancelar preserva a ordem anterior e “Restaurar padrão” pode ser revisado antes de salvar. Em tablets/telas grandes, o menu lateral segue a mesma ordem. Busca local, favoritos e seis atalhos de criação são preservados. Dez paletas claras/escuras, favoritos, texto ampliado e layout compacto são persistidos. Os indicadores se adaptam à largura e à escala de fonte.

## Início e cartões — ciclo de código Beta 5–10

Indicadores maiores com ícone em bloco suave, valor à direita, título e descrição; atalhos e módulos em cartões compactos, com sombras discretas e grade adaptada à fonte e à largura. No painel Módulos há filtro de favoritos e limpeza de busca. A ordem personalizada da Beta 4 é mantida.

O Início inclui resumo de 7, 30 ou 90 dias e pendências de tarefas, cobranças e reavaliações previstas até hoje. O toque numa reavaliação abre seu dossiê por ID. Os números vêm dos registros; não há indicadores inventados de risco de abandono ou gráficos decorativos. Tarefas e agenda usam paginação; tarefas têm filtros e confirmação para excluir.

## Treinos — ciclo de código Beta 11–20

Em Treinos → Evolução de cargas, selecione um cliente por ID e um período. São consideradas somente séries marcadas de sessões concluídas, sem modelos ou datas futuras. O histórico agrega o mesmo exercício por dia, com séries, repetições, carga máxima externa e volume; compara o último dia ao anterior disponível. Nomes diferentes de exercício não são equiparados automaticamente. O CSV do filtro não inclui nome ou ID de cliente.

Sessões e modelos podem ser exportados em PDF pelo seletor nativo. O editor inclui lotes de 1 a 10 séries, validados antes da inclusão, até 200 no treino. Sessões concluídas precisam ser reabertas para editar. A lista de sessões ganhou filtros por cliente e últimos 30/90 dias. Seletores preservam a distinção entre clientes homônimos.

## Dados e compatibilidade

Todos os registros, inclusive treinos, hábitos e notas, usam o banco AES-GCM protegido pelo Android Keystore. Os novos tipos integram o backup portátil com senha e o backup desta instalação. Backups antigos continuam aceitos. Não use uma versão anterior para editar/restaurar dados desta Beta: ela pode ignorar campos novos, como séries, prioridade e arquivo de notas. A restauração recusa conflitos e não substitui silenciosamente registros.

Clientes são vinculados por `studentId`; nomes iguais não unem históricos. A exclusão de uma avaliação não apaga automaticamente outros registros. A auditoria informa vínculos que perderam seu perfil.

Em Configurações → Avisos, o lembrete de agenda pode ser programado para aproximadamente 15 min, 30 min, 1 hora ou 24 horas antes. O Android pode atrasar o aviso; não é alarme exato.

O timer de descanso opera na tela de Treinos e não é um alarme em segundo plano. Exercícios podem ser preenchidos em texto e/ou séries estruturadas; esta entrega não inclui biblioteca multimídia ou motor de prescrição automática. Metas de hábitos são informadas pelo usuário.

## Compilar e verificar

JDK 17, Android SDK Platform 37, Build Tools 36.0.0 e Gradle 9.3.1.

```sh
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
python3 qa/check_source.py
```

A compilação debug destina-se a testes; esta entrega contém o código-fonte e não inclui um novo APK. A série mantém o mesmo pacote e aumenta o versionCode da Beta 10 (610) para 620; a atualização requer a mesma assinatura. Não desinstale a instalação anterior sem antes proteger seus dados em backup portátil.

Veja `qa/ENTREGA_BETA20.md` para escopo e evidências. Testes automatizados não substituem homologação em aparelho/emulador, inspeção visual ou restauração real.

A suíte preserva os testes anteriores e acrescenta nove testes focados e uma matriz de 10.000 casos para evolução de cargas, clientes, lotes e CSV. Os testes Kotlin/Android não foram executados neste ambiente: a distribuição Gradle 9.3.1 não está em cache, a rede bloqueou o download, e não há Android SDK local. Não há APK Beta 20 validado nesta entrega. Consulte `qa/ENTREGA_BETA20.md` para o estado e os comandos de compilação. Os cenários de QA não representam etapas de desenvolvimento concluídas. O CSV exportado é texto aberto para planilhas e não substitui o backup criptografado.
