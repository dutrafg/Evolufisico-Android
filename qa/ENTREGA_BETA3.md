# Beta 3 nativa — funções, polimento e QA

Versão preparada: `0.1.0-native-beta.3`, versionCode 603, Android 10+. Mantém o mesmo pacote e os 16 módulos da Beta 2.

## Escopo nas três frentes

A organização segue a distribuição pedida para a Beta 2: funções dos módulos existentes, polimento de layout/design, e testes/correção de bugs. Os itens concretos abaixo registram o trabalho desta versão. A matriz inclui 10.000 cenários automatizados, mas eles **não são etapas de desenvolvimento**.

### Funções dos módulos existentes

- [x] Configurações → Avisos permite escolher lembretes aproximadamente 15 min, 30 min, 1 hora ou 24 horas antes.
- [x] A escolha é persistida localmente e reagenda os atendimentos existentes quando os lembretes estão ativos.
- [x] O agendamento e o worker compartilham a mesma política; atrasos além da janela ou atendimentos já passados não geram aviso tardio.
- [x] A notificação segue aproximada via WorkManager, respeita a permissão e mantém apenas o ID do registro na fila.
- [x] Calendário mostra quantos atendimentos ativos, concluídos e cancelados há no dia selecionado.
- [x] Contadores mensais desconsideram faltas e cancelamentos.

### Layout e polimento

- [x] Calendário usa abreviações distintas para os sete dias da semana, evitando letras duplicadas sem contexto.
- [x] Configuração de prazo usa opções consistentes com a política de notificação.
- [x] Preferência de prazo aparece selecionada e atualiza a programação de avisos imediatamente.
- [x] Mensagem ao ativar os lembretes apresenta o prazo selecionado.

### Testes e correções

- [x] Corrigida repetição da seção “Metas registradas” no PDF de avaliação.
- [x] Criado teste unitário para valores válidos/inválidos e limites exatos do prazo e da janela de notificação.
- [x] Criada matriz JUnit de 10.000 casos cobrindo os quatro prazos, disparo, atraso, limites em milissegundos e notificações vencidas.
- [ ] Testes JUnit e Lint executados: bloqueados porque não há distribuição Gradle 9.3.1 em cache e a rede indisponível impediu o download.
- [ ] APK Beta 3 compilado/assinado: bloqueado pela ausência do Gradle e do Android SDK neste ambiente.
- [ ] Instalação e homologação visual em aparelho/emulador: pendentes.

## Como validar quando o toolchain estiver disponível

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
python3 qa/check_source.py
```

A auditoria estática `python3 qa/check_source.py` passou nesta sessão. Isso confirma algumas invariantes do código-fonte, não substitui compilação Kotlin/Android nem a execução dos testes. Não foi criado nem renomeado APK Beta 2 como Beta 3.

A Beta 3 acrescenta campos sem alterar o formato dos registros persistidos; preferência de prazo fica nas configurações locais. O horário de notificação segue aproximado e pode sofrer atrasos impostos pelo Android.
