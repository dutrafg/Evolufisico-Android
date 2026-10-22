# Beta 20 — ciclo de código Beta 11 a Beta 20

Versão `0.1.0-native-beta.20`, versionCode 620, pacote `br.com.evolufisico.nativo`, Android 10+. Mantém os 16 módulos, cartões e navegação personalizada anteriores. Entrega de código-fonte; não representa dez APKs compilados ou homologados.

## Divisão do trabalho

Mantidas as três frentes e sua prioridade relativa: funções 25, polimento 25, QA/correções 10. A tabela registra mudanças concretas; não é uma declaração de 60.000 etapas executadas por versão. Testes parametrizados são casos de QA, não funcionalidades nem etapas de desenvolvimento.

| Marco de código | Mudança |
|---|---|
| Beta 11 | Motor de histórico de cargas por identificador permanente do cliente |
| Beta 12 | Aba Evolução de cargas em Treinos com seleção explícita de cliente |
| Beta 13 | Períodos 30/90/365 dias e datas personalizadas, com validação |
| Beta 14 | Agregação diária por exercício: séries, repetições, carga máxima externa e volume |
| Beta 15 | Comparação com o dia anterior disponível e CSV do filtro, sem nome/ID de cliente |
| Beta 16 | PDF Android nativo para sessões e modelos com séries e instruções |
| Beta 17 | Inclusão de 1–10 séries por lote, respeitando 200 no treino e validação integral antes de adicionar |
| Beta 18 | Filtros de sessões por cliente/período, contagem filtrada e seleção desambiguada de homônimos |
| Beta 19 | Reabrir sessão antes de editar; captura de falhas de geração/salvamento PDF e limpeza de arquivo parcial/buffers |
| Beta 20 | Auditoria estática, nove testes focados, matriz de 10.000 casos preparados e nova tentativa real de compilação |

## Regras e limites dos recursos

- História de cargas exige cliente selecionado. IDs diferentes não compartilham resultados, mesmo com nome igual.
- Apenas sessões concluídas, não modelos, e séries marcadas com números válidos entram no histórico.
- Datas futuras são excluídas; limites inicial/final são inclusivos. Períodos invertidos são rejeitados.
- Nomes são aparados, espaços repetidos e caixa são normalizados; exercícios diferentes/variantes não são inferidos ou mesclados por similaridade.
- Sessões do mesmo exercício no mesmo dia são agregadas. Não se atribui ordem artificial a sessões no mesmo dia.
- Carga e volume são externos; zero não estima peso corporal, resistência de equipamento nem intensidade clínica.
- CSV usa aspas e neutralização de fórmulas existentes, até 20.000 linhas. Não inclui nome ou ID do cliente, mas os exercícios permanecem texto informado pelo usuário.
- PDF reproduz os dados registrados, com cliente quando vinculado, e deve ser conferido antes de compartilhar.
- Dados persistidos mantêm o formato e são incluídos nos backups já existentes; não foi introduzido novo tipo de registro.

## Resultado da tentativa de APK

Comando realmente tentado:

```sh
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --console=plain
```

O wrapper tentou baixar `gradle-9.3.1-bin.zip`, mas falhou com `java.net.SocketException: Network is unreachable` e código de saída 1. A distribuição não está em cache e o SDK Android não está disponível. Log em `qa/BUILD_BETA20.log`.

**Nenhum APK Beta 20 foi gerado.** Não foi usado nem renomeado APK anterior. Testes JUnit/Lint e homologação em aparelho não foram executados. A auditoria estática passou: 45 arquivos Kotlin de produção, 16 destinos. Os testes novos estão preparados, sem declaração de aprovação.

## Homologação quando o toolchain estiver disponível

1. Executar testes/Lint/build e conferir pacote, versão e assinatura. Uma atualização sobre APK anterior exige o certificado original.
2. Criar dois clientes homônimos e sessões com cargas distintas; conferir isolamento do histórico.
3. Incluir sessões no início/fim do período, fora dele, no futuro, abertas e modelos; conferir exclusões.
4. Duas sessões no mesmo dia/exercício devem somar séries/repetições/volume e manter a carga máxima do dia.
5. Comparar dias com aumento, redução e zero de carga. Sem dia anterior, não mostrar variação fictícia.
6. Exportar CSV com caracteres especiais/fórmula em exercício e verificar em planilha; conferir ausência de nome/ID de cliente.
7. Adicionar lote válido e lote que excede 200; este último não deve inserir linhas parciais.
8. Concluir uma sessão, verificar bloqueio de edição/marcações, reabrir e editar.
9. Exportar PDF de treino/modelo com 200 séries, instruções longas, observações e caracteres PT-BR; cancelar e simular falha de destino.
10. Conferir layout claro/escuro, fontes ampliadas, smartphones/tablets, favoritos, ordenação e demais módulos preservados.
