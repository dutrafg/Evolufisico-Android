# EvoluFísico Android nativo — Beta 100 revisão 14

Aplicativo Kotlin/Jetpack Compose para Android 10+, versionCode 713.

## Referência exclusiva

O desenvolvimento segue somente `reference/app.html`, cópia íntegra do arquivo `EvoluFisico2_1.0.29_PreBeta_Icones_QA_CORRIGIDO.html` anexado pelo usuário. O SHA-256 está em `qa/HTML_REFERENCE.json`. **Não há vínculo de desenvolvimento, consulta automática ou sincronização com o repositório Evolufisico2.** O nome original do HTML e marcadores de seus formatos de dados são mantidos apenas para identificar a referência e importar arquivos compatíveis.

O HTML original não foi alterado. A implementação continua Android nativa, sem WebView. `AGENTS.md` agora exige conferir o arquivo e seu hash; novas referências dependem de novo arquivo fornecido pelo usuário.

## Navegação

Início → Organização → Avaliações → Evolução → Calculadora → Formulários → Análises → Cobranças → Configurações.

- **Organização:** agenda e calendário unificados, notas fixadas/arquivadas com lembretes, tarefas com prioridade/horário e metas descritivas com prazo/situação. As datas dessas três áreas aparecem no calendário e abrem seus editores. Busca, filtros, exclusão confirmada, conclusão/reabertura, vínculo por identificador e cards reorganizáveis/redimensionáveis.
- **Início:** reescrito a partir do último painel do HTML: saudação/data, Eventos de hoje/Prazos vencidos/Tarefas pendentes/Metas ativas, prioridades, próximos acontecimentos, insights, próximos sete dias e ações do dia. Gerenciamento removido do Início e preservado dentro de Organização. Atalhos abrem a seção e a data correspondentes. Indicadores e painéis podem ser movidos e redimensionados por Editar layout; Restaurar layout padrão limpa apenas essas preferências.
- **Navegação:** os nove módulos ficam dentro do único botão flutuante “Módulo”, no canto inferior esquerdo e acima da área de navegação do Android. O aplicativo abre em Início. Não há barra lateral em smartphones. O menu conserva a ordem dos módulos da referência.

- **Formulários:** nove modelos com perguntas transcritas do HTML; três por nível. Abas Modelos/Respostas/Criar formulário. Biblioteca pesquisável, modelos próprios, personalização de modelos prontos, respostas vinculadas ao aluno, histórico e PDF AcroForm com identificação somente leitura e respostas editáveis. Edição/exclusão de um modelo preserva as perguntas das respostas anteriores.
- **Análises:** seleção de 3–4 datas, intervalo mínimo de 21 dias, trajetória e suficiência EF70, regressão temporal, comparabilidade entre métodos, sinais/ações, síntese editável e PDF. Os índices são exploratórios e não representam diagnóstico ou precisão clínica.
- **Monitoramento EF68:** gráficos de medidas por data, tabela acessível dos valores, transições comparáveis e eventos de consistência, oscilação, platô aparente, inversão e intervalos longos. Gordura de métodos diferentes não conecta a curva. Sem previsões futuras.
- **Laboratório EF77:** Navy, RFM, ABSI, FFMI/normalizado, água corporal estimada por Watson, FC máxima por Tanaka e comparador dos métodos. Amplitude ≥8 p.p. bloqueia referência composta e FFMI; limites operacionais e bloqueios de equações adultas para menores.
- Os cálculos, avaliações agrupadas A–Z/lista/grade, evolução, cobranças, armazenamento criptografado, permissões e relatórios compatíveis foram reaproveitados.

## Organização: dados e portabilidade

Exportar organização gera `.efbackup` criptografado com agenda, tarefas, notas e metas. Importar backup nativo usa a senha original. Importar JSON do HTML aceita o envelope de organização do arquivo anexado, até 3 MB, sem sobrescrever registros existentes; colisões diferentes interrompem a transação. Nomes são vinculados a cadastro somente quando há uma única correspondência; homônimos permanecem sem vínculo automático.

O JSON geral do HTML e o backup nativo não são formatos intercambiáveis. A exportação reversa de Organização gera o mesmo envelope JSON web (até 3 MB e 10000 registros). O JSON não é criptografado e conserva os campos previstos no HTML; categorias nativas, datas de criação de notas e identificadores de vínculo não fazem parte desse formato. Para preservar toda a informação e os vínculos, use o backup nativo. Tarefas aceitam até 10000 caracteres de detalhes, compatíveis com o HTML. Backups completos conservam tipos legados e os novos modelos/metas; versões nativas antigas não restauram os novos tipos. O backup automático .efdevice depende da chave desta instalação do Android Keystore. Preserve cópias portáteis e a instalação anterior.

## Validar e compilar

```sh
bash qa/run_validation.sh --apk
node qa/html/generate_ef70_cases.cjs
node qa/html/generate_ef68_cases.cjs
node qa/html/generate_ef77_cases.cjs
```

O primeiro comando executa auditorias, testes JUnit, Lint e empacotamento. Sem --apk executa apenas os testes e Lint. Os geradores executam os motores JavaScript presentes no HTML fixado, sem rede: 48 vetores EF70, 36 EF68 e 16 EF77; o JUnit compara os resultados com a implementação Kotlin.

Ambiente: JDK 17, Gradle 9.3.1, SDK 37, targetSdk 36. Configurações locais de rede, certificados e chaves privadas não integram o projeto.

## Verificação e limites

Resultado atual e relatório de bugs: `qa/RELATORIO_BUGS_REVISAO14.md`. A análise JavaScript da referência está em `qa/test_html_1029.cjs`. Testes unitários, auditoria de hash e Lint não comprovam aparência, notificações, SAF, Keystore ou restauração real em dispositivo Android.

## Atualização da revisão 14

O HTML 1.0.29 passou a ser a referência canônica. A análise encontrou cinco falhas no JavaScript original: data impossível no caixa mensal, valor financeiro infinito, horário impossível em lembretes, compromissos inativos ocultando retornos na exportação e contagem de exportação maior que o limite do arquivo. A implementação Android já usa datas/horários estritos e valores em centavos inteiros; os dois casos de exportação são documentados como diferenças de funcionalidade nativa.

Configurações não duplica mais o painel de dados profissionais. O código preserva o modo Liquid Glass, as opções nativas de notificações, o botão Módulo e o início em Início.
