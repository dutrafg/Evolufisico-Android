> Registro histórico da consolidação inicial. Estado atual: revisão 3, versionCode 702. Consulte REVISAO_BETA100_3.md e STATUS_MODULOS_BETA100.md.

# Beta 100 — correção da área segura e consolidação

Código `0.1.0-native-beta.100`, versionCode 700. Mantidos pacote `br.com.evolufisico.nativo`, Android 10+, dados, dez paletas e 16 módulos.

## Problema relatado e causa encontrada

A captura fornecida mostra o cabeçalho ocupando a área da barra de notificações do Android. No código havia um topBar personalizado de Surface/Row sem aplicação dos insets superiores. O Scaffold espera que componentes de topo próprios tratem seus insets; a reserva de espaço do corpo não protege automaticamente esse cabeçalho.

## Correção implementada primeiro

- Inicialização explícita com `enableEdgeToEdge()`.
- Shell inteiro protegido por `windowInsetsPadding(WindowInsets.safeDrawing)`, antes da medição de seus filhos.
- Fundo do app cobre a região externa, mas ícones, botões e textos ficam na área útil.
- `contentWindowInsets` do Scaffold zerado porque o pai já reservou o espaço; conteúdo aplica e consome o padding do Scaffold.
- Recortes da câmera, barras laterais em paisagem e navegação inferior tratados pelos insets reais, sem supor uma altura fixa.
- Painel de módulos e editores de tela inteira usam a mesma área segura, inclusive teclado.
- Aparência dos ícones de status/navegação acompanha a seleção claro/escuro.

Referências oficiais consultadas:
- https://developer.android.com/develop/ui/compose/system/insets-ui
- https://developer.android.com/develop/ui/compose/system/material-insets

## Continuação da consolidação

- Listas do repositório desempatem datas iguais por ID, reduzindo mudanças de ordem após salvar/reabrir.
- CSV verifica tamanho lógico antes da montagem das linhas; o total expandido de séries usa Long para a contagem.
- Preservados todos os recursos anteriores, sem migração de registros ou novos tipos no banco.
- Quatro testes de regressão preparados: limite de agenda/notas/séries e exportação exatamente no limite.
- Oito verificações estáticas específicas da integração da área segura.

A numeração Beta 100 é um marco de consolidação. Não se afirma que foram desenvolvidas/homologadas 67 versões intermediárias, nem que todo recurso imaginável do aplicativo está pronto.

## Verificação desta sessão

- Auditoria estática: PASSOU, 48 arquivos Kotlin de produção e 16 destinos.
- `check_window_contract.py`: oito condições de integração presentes; não é medição de layout nem teste Android.
- `check_palette.py`: 60 pares fixos aprovados; menor contraste 6,8547:1. Sem validação visual dos estados completos.
- XML: dois arquivos bem formados.
- ZIP: CRC íntegro verificado ao empacotar.
- Nova tentativa real de JUnit/Lint/APK: código 1, `Network is unreachable` ao baixar Gradle 9.3.1; sem distribuição em cache nem SDK Android local. Log em `qa/BUILD_BETA100.log`.

**Não houve compilação de APK ou execução dos testes JUnit. A correção está implementada no fonte e ainda não foi confirmada em aparelho/emulador.**

## Homologação obrigatória da correção visual

1. Instalar compilação com assinatura compatível e abrir Início em retrato. Cabeçalho, menu e busca devem ficar totalmente abaixo da barra de status.
2. Testar claro/escuro: ícones do Android devem ter contraste e não se confundir com o fundo.
3. Girar para paisagem e testar câmera/recorte lateral, tablet e multiwindow.
4. Conferir navegação por gestos e por três botões; não pode haver sobreposição nem espaço inferior duplicado.
5. Abrir teclado em busca/editores e depois fechar; conteúdo e ações devem continuar alcançáveis.
6. Abrir Módulos, organizar ordem, editar avaliação/treino/tarefa e conferir todas as áreas superiores/inferiores.
7. Fontes 100/150/200%: cabeçalho pode crescer para baixo, nunca para a área de notificações.
8. Conferir scroll do Início com muitos registros e botões flutuantes sem ocultar permanentemente o último item.

```sh
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
python3 qa/check_source.py
python3 qa/check_window_contract.py
python3 qa/check_palette.py
```
