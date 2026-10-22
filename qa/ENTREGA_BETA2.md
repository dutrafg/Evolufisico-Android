# Beta 2 nativa — ampliação, polimento e QA

Versão `0.1.0-native-beta.2`, versionCode 602, Android 10+. Mantém os 16 módulos da Beta 1.

## Escopo solicitado e evidência

A solicitação distribuiu 60.000 etapas em 25.000 para funções, 25.000 para polimento e 10.000 para testes/correções. Esta entrega registra **mudanças concretas**, sem declarar que foram executadas 50.000 etapas de implementação e design. A matriz de 10.000 cenários novos pertence exclusivamente à frente de QA; não é contada como funcionalidade nem como etapa de desenvolvimento.

## 1. Funções dos módulos existentes

- [x] Treinos: modelo estruturado de série, com ID, exercício, repetições, carga externa e marcação de execução.
- [x] Treinos: inclusão, edição, duplicação, remoção e reordenação de séries.
- [x] Treinos: limite de 200 séries, sem IDs duplicados, números não finitos ou valores fora das faixas aceitas.
- [x] Treinos: criação e edição de modelos reutilizáveis dentro do módulo existente.
- [x] Treinos: usar modelo como nova sessão, mantendo conteúdo e limpando execução, esforço e IDs anteriores.
- [x] Treinos: transformar sessão em modelo sem reter vínculo de cliente ou estado de execução.
- [x] Treinos: marcar/desmarcar série com gravação imediata; sessões concluídas precisam ser reabertas para alterar marcações.
- [x] Treinos: progresso das séries, aviso de todas marcadas e confirmação explícita para concluir.
- [x] Treinos: permite concluir com séries não marcadas após confirmação, sem alterar artificialmente sua situação.
- [x] Treinos: volume externo em kg·rep calculado somente a partir das séries marcadas; não estima peso corporal ou resistência de máquina.
- [x] Treinos: busca inclui nomes de exercícios estruturados.
- [x] Relatórios: modelos não contam como sessões ou execução.
- [x] Relatórios: seis exportações CSV por período — avaliações, cobranças, treinos, séries, hábitos e tarefas.
- [x] Relatórios: nomes de clientes são opcionais e desativados por padrão; IDs não são exportados.
- [x] Relatórios: exportação pelo seletor de arquivos nativo, com limpeza do buffer após salvamento/cancelamento.
- [x] Relatórios: CSV UTF-8/BOM, separador ponto e vírgula, aspas e quebras de linha escapadas.
- [x] Relatórios: neutralização de prefixos de fórmula, inclusive após espaços, BOM ou caracteres invisíveis/controláveis.
- [x] Relatórios: limite de 20.000 linhas e controle de tamanho antes do envio ao seletor.
- [x] Tarefas: prioridade Normal/Alta/Urgente, categoria e vínculo opcional de cliente.
- [x] Tarefas: ordenação com pendências antes de concluídas, prioridade, prazo e ID estável.
- [x] Tarefas: filtros Urgentes/Vinculadas, busca em categoria/cliente e adiamento de um dia.
- [x] Clientes: dossiê mostra tarefas vinculadas por ID.
- [x] Notas: categoria, arquivamento reversível, filtros de ativas/arquivadas e busca em categoria.
- [x] Notas: prévia curta, leitura completa e recolhimento.
- [x] Hábitos: filtro dos últimos sete dias e histórico sob demanda.
- [x] Hábitos: sequência de dias e metas atingidas sem contar duplicatas do mesmo dia ou datas futuras.
- [x] Hábitos: histórico mantém clientes, unidades e títulos separados.
- [x] Cobrança: filtro de vencidas e busca sem distinção de acentos.
- [x] Dados: serialização dos campos adicionais e leitura de backups antigos com valores padrão.
- [x] Dados: séries e novos atributos são incluídos nos backups e validados na restauração.

## 2. Layout, design e paletas

- [x] Paletas centralizadas, com dez pares de cores claras/escuras e nomes consistentes.
- [x] Correção do modo escuro, que antes ignorava a cor selecionada.
- [x] Superfícies, contêineres, texto, bordas e seleção coordenados para claro/escuro.
- [x] Prévia de cor nas Configurações acompanha o tema ativo.
- [x] Teste de contraste dos pares principais de texto nas dez paletas.
- [x] Layout compacto com preferência persistida, sem reduzir alvos padrão de toque.
- [x] Texto ampliado com preferência persistida e alturas de linha correspondentes.
- [x] Indicadores adaptam colunas ao espaço e à escala de fonte do Android.
- [x] Cartões com bordas discretas, espaçamento coordenado e cantos moderados.
- [x] Campos de linha única agora usam comportamento explícito de linha única.
- [x] Teclado numérico/decimal para repetições, carga e esforço.
- [x] Editor de séries com ações em linha rolável para telas estreitas.
- [x] Favoritar/desfavoritar módulos com nomes acessíveis e persistência local.
- [x] Grupo Favoritos no menu e mensagem quando a busca de módulos não encontra resultados.
- [x] Estados salváveis e posição de rolagem dos módulos preservados ao navegar, quando não há uma abertura específica por atalho.
- [x] Calendário não tenta construir o seletor com uma data fora da faixa suportada.

## 3. Correções e testes

- [x] Meta numérica inválida na avaliação é rejeitada, em vez de ser descartada silenciosamente.
- [x] Meta já ultrapassada mostra distância restante zero.
- [x] Cobranças vinculadas a clientes homônimos são agrupadas por ID; contatos sem ID continuam agrupados por nome.
- [x] Falha de persistência ao alternar pagamento é apresentada ao usuário.
- [x] Cancelar em editores de cobrança/formulário confirma descarte.
- [x] Diálogo de descarte não fica encoberto pela busca global.
- [x] Busca global não oferece nota arquivada que não apareceria no filtro inicial de Notas.
- [x] Templates não acrescentam sessões fictícias ao painel, dossiê, relatórios ou exportações.
- [x] Paginação com cálculo de limites sem overflow no último intervalo.
- [x] 26 testes focados para modelos, séries, volume, hábitos, prioridade, CSV, notas, metas e contraste.
- [x] 10.000 cenários novos em `Beta2MatrixTest`, além dos cenários preservados da Beta 1.

## Limites de validação

A versão é uma Beta debug destinada a teste. Não houve execução visual nem instalação em aparelho/emulador neste ambiente. SAF, atualização com dados reais, restauração criptografada em dispositivo, PDFs, notificações e comportamento sob economia de bateria ainda exigem homologação manual. Os cálculos de contraste e de responsividade não comprovam visualmente todas as telas.

O resultado final da suíte, Lint, assinatura e metadados é registrado após a compilação em `qa/BUILD_BETA2.json`.

## Resultado final

- `:app:testDebugUnitTest`: **20.589 testes**, 0 falhas, 0 erros e 0 ignorados.
- Nova matriz Beta 2: **10.000 cenários**, relatados individualmente pelo JUnit parametrizado.
- `:app:lintDebug`: **0 erros, 17 avisos**.
- `:app:assembleDebug`: **PASSOU**.
- Auditoria estática: **40 arquivos Kotlin e 16 destinos**.
- APK versionCode **602**, `0.1.0-native-beta.2`, Android 10+.
- Assinatura APK v2 validada; certificado igual ao APK Beta 1. Não comprova instalação/atualização em aparelho.
- SHA-256 do APK: `332f3818a376b237a2b2124a7123552048f8b5f66143ada4385b767e80c509b3`.
