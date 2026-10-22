# Resultados — 0.150.0-native-alpha

## Entregas

- A autoverificação detecta metas sem linha de base válida e conflitos de horário atravessando a meia-noite.
- Novos testes de finanças, auditoria, cálculos metabólicos, antropometria e timeline aumentam a suíte de 43 para 69 casos.
- Dados temporários de PDF, backup e importação são limpos depois do uso; backup automático remove arquivo incompleto sem apagar cópias anteriores.
- Backup portátil salvo em pasta usa nome com horário para reduzir colisões no mesmo dia.
- Corrigidos avisos de API obsoleta na formatação de localidade e ícones compatíveis com leitura RTL.
- Versão Android: `0.150.0-native-alpha`, versionCode 150.

## Verificação executada

- `:app:testDebugUnitTest`: PASSOU, 69 testes, 0 falhas/erros.
- `:app:lintDebug`: PASSOU, 0 erros; avisos existentes permanecem registrados no relatório.
- Auditoria estática de módulos e recursos nativos: executada.
- `assembleDebug`/`packageDebug`: não executados. O APK continua adiado até o fim do ciclo.

## Ciclo de desenvolvimento

`qa/ETAPAS_101_600.md` contém os 500 IDs solicitados. A150 está concluída com evidência; A151–A600 ficam abertas e não são apresentadas como feitas. Testes automatizados não substituem QA visual, Android real/emulador, restauração real ou inspeção visual dos PDFs.
