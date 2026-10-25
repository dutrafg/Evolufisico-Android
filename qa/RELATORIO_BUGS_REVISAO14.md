# Relatório de bugs — Beta 100, revisão 14

**Referência:** `EvoluFisico2_1.0.29_PreBeta_Icones_QA_CORRIGIDO.html`  
**SHA-256:** `f9c3837590bfb229eb050d5320b1775c19711ac54ff478506f77a69577adf3c1`  
**Android:** versionCode 713; Kotlin e Jetpack Compose, sem WebView.

## Achados no HTML 1.0.29

| ID | Achado reproduzido/analisado | Situação na versão Android |
|---|---|---|
| BUG-01 | O caixa mensal reduz a data ao padrão `AAAA-MM`; datas impossíveis, como 31 de fevereiro, podem entrar no mês. | Corrigido na implementação nativa: `LocalDate.parse` rejeita o dia inexistente. Teste existente em `MonthlyLedgerTest`. |
| BUG-02 | A soma financeira usa `Number(valor)`, que aceita `Infinity` e pode contaminar os totais mensais. | Mitigado no modelo nativo: valores são centavos inteiros; regras de restauração rejeitam valores não finitos e fora do limite. |
| BUG-03 | O lembrete valida a forma `HH:MM`, mas não os limites; `12:99` é normalizado para outra hora pelo JavaScript. | Corrigido nativamente com `LocalTime.parse` e `Agenda.validate`; incluído teste de regressão. |
| BUG-04 | A deduplicação de retornos considera qualquer compromisso no mesmo dia, inclusive cancelado/concluído, e pode ocultar a reavaliação prevista no arquivo ICS. | A exportação ICS do HTML não foi portada para o Android nesta revisão; portanto, não há esse fluxo nativo a corrigir. Registrar como diferença de paridade. |
| BUG-05 | A exportação corta o arquivo em 500 eventos, mas a confirmação usa a contagem anterior ao corte. | A exportação ICS ainda não existe no Android; se portada, a quantidade informada deverá refletir os eventos realmente gravados. |

O teste `qa/test_html_1029.cjs` analisa os caminhos relevantes do JavaScript e reproduz os três primeiros casos; os dois achados de exportação são identificados pelo limite e pelo caminho de deduplicação presentes no código. Os arquivos de HTML e QA foram mantidos íntegros como referência e registro do usuário.

## Mudanças Android nesta revisão

- Substituída a referência anterior pela cópia exata da 1.0.29, com conferência automática do SHA-256.
- Mantidos os nove destinos já implementados, o menu flutuante único **Módulo** e a abertura em **Início**.
- Removido o painel duplicado de dados profissionais em Configurações.
- Atualizados os testes de lembrete para as escolhas da interface, incluindo “No horário” e a tolerância de execução tardia do WorkManager.
- Acrescentado teste de rejeição de datas e horários impossíveis na agenda nativa.

## Validação

| Verificação | Resultado |
|---|---|
| JavaScript inline do HTML | 53 blocos analisados sintaticamente |
| Achados reproduzidos/analisados | 5 |
| Contratos estáticos, referência/hash, módulos, navegação, paletas, efeitos e grade | Aprovados |
| Testes unitários Android | 50.764 passaram; 0 falhas |
| Android Lint | Aprovado |
| APK debug | Gerado, versionCode 713; assinatura debug verificada |
| Teste visual em aparelho/emulador | Não executado |

**APK SHA-256:** `4aae92975b3a0a767566e19f0e7a43346c45a217ca4a9e107766e7e0b415c4cb`  
**Certificado debug SHA-256:** `13143b4f4cf17053b01ab56ed953bb5ed3f3015856624463339860e6a92df7bd`

