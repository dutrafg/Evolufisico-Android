# Beta 4 — navegação adaptada e módulos reorganizáveis

Versão de código `0.1.0-native-beta.4`, versionCode 604. Mantidos os 16 módulos, seus identificadores de rota e os registros já existentes.

## Implementado

- Removido o drawer lateral e seu gesto de arraste. Smartphones usam um painel modal de tela inteira, aberto pelo botão Módulos ou pelo ícone superior.
- Menu lateral permanente restrito a dispositivos com largura mínima de 600 dp e janela atual de ao menos 900 dp. Telefones em paisagem continuam sem menu lateral.
- Botão “Editar ordem dos módulos”, com ações acessíveis para subir/descer um módulo.
- Alterações são revisadas antes de salvar; cancelar não grava. Descartar uma ordem modificada exige confirmação.
- Ordem em edição preservada na recriação da tela pelo Android.
- Os quatro primeiros módulos compõem a barra inferior dos smartphones. Todos os demais continuam acessíveis em Módulos.
- Preferência persistida localmente, válida para painel mobile e menu de telas grandes.
- Restaurar padrão sem substituir a preferência até tocar em Salvar.
- Preferência antiga/inválida é normalizada: IDs inválidos e repetições são removidos, módulos ausentes são acrescentados.
- Favoritos, busca, atalhos de criação e proteção contra descarte de editores mantidos.
- Corrigido o reagendamento de lembretes da Beta 3: a política recebe o tempo até o atendimento, e não o tempo restante até o aviso. Isso evita aplicar duas vezes o intervalo escolhido.

## Validação desta sessão

- Auditoria estática: PASSOU, 41 arquivos Kotlin e 16 destinos.
- Acrescentados seis testes JUnit para ordem padrão, preferências corrompidas, movimentos, limites, reversão e persistência.
- Testes Kotlin/Android, Lint e APK: NÃO EXECUTADOS. O wrapper tentou baixar Gradle 9.3.1 e recebeu `Network is unreachable`. Não há Gradle em cache nem SDK Android local.
- Inspeção visual em dispositivo/emulador: pendente. Os testes não são apresentados como executados.

## Homologação após compilar

1. Smartphone retrato e paisagem: não deve haver sidebar ou drawer ao puxar a borda.
2. Módulos → Editar ordem: mover Treinos para o primeiro lugar; salvar; confirmar barra inferior e reabrir o app.
3. Alterar ordem e cancelar: confirmar descarte e verificar que a ordem anterior permanece.
4. Rotacionar durante edição: conferir que a ordem em edição não se perde.
5. Restaurar padrão e salvar; conferir todos os 16 módulos, favoritos, busca e navegação.
6. Em tablet largo, conferir menu lateral e a mesma ordem.
7. Com lembretes ativos e prazo de 1 h, agendar atendimento em 90 min: o trabalho deve ser programado para aproximadamente 30 min depois, sujeito às restrições Android.

```sh
bash gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
python3 qa/check_source.py
```
