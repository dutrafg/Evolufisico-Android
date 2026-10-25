# Beta 100 — revisão 4

## Revisão 4 — compilação e validação executadas em 22/10/2026

APK debug gerado e assinatura v2 verificada: versão 0.1.0-native-beta.100, código 703, Android 10+ (API 29), target 36, compile 37. Compilação limpa concluída com os testes, Lint e assembleDebug.

- JUnit: 50.670 testes em 28 suítes; zero falhas, erros ou ignorados.
- QA puro Kotlin: 109 verificações aprovadas; compilador embutido no Gradle, com shims de compilação JSON/Base64. Não substitui o runtime Android.
- Lint: zero erros e 20 avisos (19 sugestões de extensões KTX e um de targetSdk 36). Não foram desativadas verificações nem criada baseline para esconder erros. A atualização do target requer teste das mudanças de comportamento em aparelho.
- Auditoria estática: 58 arquivos Kotlin, 16 destinos, oito contratos de área segura e 60 pares de contraste aprovados. Dois XML de recursos/manifest foram analisados.
- APK: CRC íntegro, pacote/versão conferidos e catálogo de exercícios, temporizador, questionários e escopo de backup encontrados no DEX. Manifest final sem permissão INTERNET.

Correção: datas de calendário inválidas agora lançam IllegalArgumentException de forma consistente com o contrato de validação. Um teste existente detectou a regressão; a expectativa do teste foi preservada. Limites de memória/concorrência foram configurados no Gradle. Artefatos antigos foram removidos com clean, eliminando conflitos de classes de compilações anteriores.

O ambiente de compilação foi reconstruído com JDK completo, Gradle 9.3.1 e SDK 37 oficial. Configurações locais de rede e certificados não fazem parte do aplicativo.

Pendente: instalação e navegação em Android real/emulador; conferência visual de barras do sistema, telas estreitas e fontes ampliadas; permissões, notificações, SAF, Keystore, restauração e timer ao alternar telas/aplicativos. Compilar todos os destinos não significa que todos os fluxos foram homologados. A chave é de debug; não é uma assinatura de produção. Compatibilidade de atualização com APKs anteriores depende de os certificados coincidirem; preservar backup antes de qualquer mudança de instalação.

Evidências: BUILD_BETA100_REVISAO4.log, BUILD_BETA100_REVISAO4.json, LINT_BETA100_REVISAO4.txt, DOMAIN_BETA100_REVISAO4.log, APK_BETA100_REVISAO4.json e junit-beta100-r4/.

