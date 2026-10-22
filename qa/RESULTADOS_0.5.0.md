# Resultados — 0.5.0-native-alpha

## Execução

- `:app:assembleDebug`: PASSOU, APK completo gerado.
- `:app:testDebugUnitTest`: PASSOU, 10 testes, zero falhas/erros.
- Núcleo Kotlin: 109 asserções PASSARAM; modelos/importador compilados contra shims, sem teste funcional do parser Android.
- Auditoria estrutural: PASSOU, 27 fontes Kotlin e oito telas presentes.
- `:app:lintDebug`: PASSOU; avisos estão no relatório incluído. Isto não equivale a uma auditoria completa de segurança.
- `apksigner verify`: PASSOU, certificado Android Debug.
- APK: pacote `br.com.evolufisico.nativo`, versionCode 5, minSdk 29, targetSdk 36.
- SHA-256 do APK: `83ebcc8c1878fd60d031959b999b22bba0b87794054919775312573c27af6b4a`.

## Casos novos executados

Agenda cruzando meia-noite; horários adjacentes; situações cancelado/concluído/faltou; conflito na última ocorrência de uma série; IDs duplicados; sobreposição interna; edição do mesmo ID; recorrência na mudança de ano; data, hora, duração e contato inválidos. Evolução com avaliações na mesma data, intervalos reais, valores constantes, data inválida e valores não finitos.

## Limites

Sem execução em Android real ou emulador, testes instrumentados, QA visual, inspeção dos PDFs, homologação de importação legada ou testes reais de SQLite/Keystore/SAF. A atomicidade da gravação está implementada em transação SQLite, mas não foi testada sob falha de armazenamento em aparelho. O APK segue alpha para testes.

## Android Lint

0 errors, 15 warnings
