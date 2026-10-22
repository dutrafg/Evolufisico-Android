# Resultados — 0.600.0-native-alpha

## Entregas

- Fechadas as etapas A101–A600: 450 vetores de regressão A151–A600 identificados individualmente e associados a métodos JUnit.
- Os vetores cobrem progresso de metas, divisão de parcelas em centavos, sobreposição de agenda, cálculo de IMC, reconciliação Pollock, linha do tempo e aderência.
- Auditoria de integridade, proteção de buffers, backup e restauração, regras de cálculo, antropometria e evolução fazem parte da base nativa.
- Versão Android: `0.600.0-native-alpha`, `versionCode=600`.

## Verificação executada

- `:app:testDebugUnitTest`: **519 testes**, 0 falhas, 0 erros, 0 ignorados.
- `:app:lintDebug`: **PASSOU**, 0 erros e 15 avisos.
- `python3 qa/check_source.py`: **PASSOU**, 28 arquivos Kotlin, 8 telas nativas e sequência completa A151–A600 validada.
- O APK debug foi compilado após as verificações acima e validado quanto à assinatura e metadados.

Os 450 IDs são vetores de regressão executáveis, não 450 funcionalidades distintas. Testes automatizados não substituem QA visual nem homologação em aparelho/emulador. Backup/restauração real, SAF, notificações/WorkManager e PDFs ainda precisam de validação manual.
