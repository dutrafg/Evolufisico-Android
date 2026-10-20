# EvoluFísico Android — 0.3.0-alpha

Versão nativa para **Android 10+**, Kotlin e Jetpack Compose, independente das versões Web/Windows/Linux.

## Disponível no protótipo

- **Clientes:** cadastro/edição/exclusão, IDs estáveis, dados locais cifrados com AES-256-GCM (Android Keystore).
- **Avaliações:** histórico por cliente; anamnese inicial; peso, altura e medidas de cintura, quadril, braço e coxa; IMC e relação cintura/quadril; validações; armazenamento cifrado.
- **Evolução (Fase 3):** indicador selecionável (peso, IMC, cintura, quadril, braço, coxa ou RCQ), linha de tendência e histórico numérico por cliente. Comparação por avaliação, último registro **com medida válida** em cada mês e último registro **com medida válida** em cada trimestre. Variação absoluta e percentual, sem interpretações clínicas.
- **Relatório PDF nativo:** profissional, com identificação do cliente, datas, indicador, gráfico e tabela de evolução com paginação automática. O relatório é desenhado via `android.graphics.pdf.PdfDocument`, nunca imprime a interface do app.
- **Salvar PDF:** `ActivityResultContracts.CreateDocument(application/pdf)` oferece o seletor de arquivos do sistema (SAF), sem requerer permissão ampla de armazenamento nem usar arquivos temporários abertos.
- **Tema claro/escuro** e navegação.
- **Sem Internet** e sem integração com Google Drive.

## Como gerar o APK

Abra no Android Studio (JDK 17, Android SDK API 36, Gradle 8.13) ou compile com `gradle :app:assembleDebug`. Testes unitários: `gradle :app:testDebugUnitTest`.

O projeto contém `gradle/wrapper/gradle-wrapper.properties`, mas não o JAR do Gradle Wrapper. Num ambiente com Gradle instalado, execute `gradle wrapper --gradle-version 8.13` para gerar scripts/JAR oficiais.

O workflow em [Actions](https://github.com/dutrafg/Evolufisico-Android/actions) compila, testa e, se aprovado, publica o artifact `Evolufisico-Android-debug`. O APK é de desenvolvimento (assinatura debug).

## Privacidade e limites importantes

- **Dados de avaliações são sensíveis.** Não use dados reais de clientes antes da implantação de autenticação (PIN/biometria), backup criptografado, testes de segurança e validação de funcionamento em aparelhos reais.
- PDF exportado não possui senha nem criptografia. O usuário é responsável pelo armazenamento seguro do arquivo e por não compartilhá-lo indevidamente.
- IMC, RCQ e variações são **indicadores descritivos**, não diagnósticos; nenhum percentual de gordura é inferido.
- Só há comparação de avaliações **existentes**: nenhuma medida é estimada. Ausência de valores opcionais pode reduzir pontos do gráfico.
- Não existe migração da base Web, dobras cutâneas/Pollock/BIA, biometria, Cobranças, backup ou restauração nesta versão. A falta desses recursos não foi marcada como concluída.
- Perder o Android Keystore ou desinstalar o app pode tornar os dados irrecuperáveis. Exportar PDF **não** é backup restaurável do banco de dados.

Veja `QA.md` e `ROADMAP.md`.
