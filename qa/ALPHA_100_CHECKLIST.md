# Checklist de desenvolvimento — 100 Alpha

Estado de conclusão de implementação no código: **100/100 marcos**. “Concluído” neste documento significa implementação presente e verificação de código; não significa homologação visual ou funcional em aparelho. O APK foi intencionalmente adiado para depois desta etapa.

Os IDs são uma trilha de revisão das capacidades entregues na base nativa e nesta continuidade. Evidência automatizada atual: `:app:testDebugUnitTest` (27 testes, zero falhas), `:app:lintDebug` (0 erros, 15 avisos), auditoria estática `qa/check_source.py`. Recursos que exigem Android real permanecem no plano de homologação, sem status de aprovação.

## 01–10 · Base Android nativa

- [x] **A001** Projeto Kotlin com Gradle Wrapper incluído.
- [x] **A002** Interface Jetpack Compose e Material 3.
- [x] **A003** ApplicationId nativo preservado.
- [x] **A004** minSdk 29 declarado.
- [x] **A005** compileSdk 37 declarado.
- [x] **A006** Navegação por oito módulos.
- [x] **A007** Tema claro/escuro configurável.
- [x] **A008** Sem WebView na aplicação.
- [x] **A009** Sem permissão de rede desnecessária.
- [x] **A010** versionCode 100 e nome 0.100.0-native-alpha.

## 11–20 · Persistência e proteção de dados

- [x] **A011** Repositório central para entidades do aplicativo.
- [x] **A012** Persistência local SQLite.
- [x] **A013** Payloads de registros criptografados.
- [x] **A014** Chave de dados protegida pelo Android Keystore.
- [x] **A015** Backup portátil protegido por senha.
- [x] **A016** Backup local vinculado à instalação identificado como .efdevice.
- [x] **A017** Importador legado JSON/.efx presente.
- [x] **A018** Importação com limites de tamanho de arquivo.
- [x] **A019** Auditoria local de consistência disponível.
- [x] **A020** Exportação e restauração com fluxos separados.

## 21–35 · Avaliações físicas

- [x] **A021** Identidade permanente do aluno separada da avaliação.
- [x] **A022** Nova reavaliação conserva studentId.
- [x] **A023** Campos de medidas e antropometria.
- [x] **A024** Cálculo de IMC com ausência de dados preservada.
- [x] **A025** Cálculo Pollock 7 com dados suficientes.
- [x] **A026** Bioimpedância e respostas extensíveis.
- [x] **A027** Fotos com limite de leitura e compressão.
- [x] **A028** Aderência sem penalizar itens não aplicáveis.
- [x] **A029** Validação de intervalos para medidas.
- [x] **A030** Avisos de inconsistência sem diagnóstico.
- [x] **A031** Exclusão de avaliação com confirmação.
- [x] **A032** Edição protegida contra descarte acidental.
- [x] **A033** Metas de peso e percentual de gordura persistidas.
- [x] **A034** Prazo e observações da meta validados.
- [x] **A035** Compatibilidade da meta com nova reavaliação.

## 36–45 · Evolução e comparação

- [x] **A036** Históricos filtrados por studentId.
- [x] **A037** Homônimos mantidos em históricos separados.
- [x] **A038** Séries usam somente valores disponíveis.
- [x] **A039** Gráfico proporcional ao intervalo de dias.
- [x] **A040** Séries constantes são suportadas.
- [x] **A041** Avaliações no mesmo dia podem ser selecionadas individualmente.
- [x] **A042** Comparação de duas avaliações por ID.
- [x] **A043** Indicadores corporais e aderência listados.
- [x] **A044** Variação do período apresentada sem interpolar dados.
- [x] **A045** Metas e progresso mostrados no histórico.

## 46–60 · Agenda

- [x] **A046** Criar e editar compromissos.
- [x] **A047** Validação de data e horário.
- [x] **A048** Verificação de conflito de duração.
- [x] **A049** Conflitos que atravessam meia-noite identificados.
- [x] **A050** Horários adjacentes permitidos.
- [x] **A051** Estados cancelado/concluído/falta tratados na disponibilidade.
- [x] **A052** Edição conserva ID do compromisso.
- [x] **A053** Recorrência limitada a 12 semanas.
- [x] **A054** Série validada antes de gravar.
- [x] **A055** Gravação de recorrência em transação.
- [x] **A056** Falha de gravação reportada no formulário.
- [x] **A057** Contato livre não é associado a homônimo automaticamente.
- [x] **A058** Perfil de aluno selecionável para associação explícita.
- [x] **A059** Lembretes persistem IDs, não dados clínicos.
- [x] **A060** Notificação usa texto genérico na tela bloqueada.

## 61–70 · Calculadora e indicadores

- [x] **A061** IMC disponível na calculadora.
- [x] **A062** RCQ disponível.
- [x] **A063** Equações metabólicas disponíveis.
- [x] **A064** Cálculos exigem entradas válidas.
- [x] **A065** Valores ausentes não geram estimativas artificiais.
- [x] **A066** RCE e BRI verificam altura positiva.
- [x] **A067** Estimativa de peso-alvo exige gordura atual válida.
- [x] **A068** Karvonen exige frequência cardíaca coerente.
- [x] **A069** Unidades e precisão apresentadas na tela.
- [x] **A070** Resultados não finitos são filtrados.

## 71–80 · Anamnese e análises

- [x] **A071** Formulários de anamnese por nível.
- [x] **A072** Respostas vinculadas ao ID de aluno.
- [x] **A073** Reabertura preserva respostas.
- [x] **A074** Histórico de saúde disponível.
- [x] **A075** PAR-Q sinaliza revisão profissional quando aplicável.
- [x] **A076** Indicadores de análise tratam N/A explicitamente.
- [x] **A077** Alertas de medidas abruptas pedem conferência.
- [x] **A078** Ajuda contextual acessível nos módulos.
- [x] **A079** Seleção de aluno usa ID, não somente nome.
- [x] **A080** A interface usa componentes nativos Android.

## 81–90 · Cobrança, relatórios e configurações

- [x] **A081** Cadastro de cobrança e serviço.
- [x] **A082** Valores monetários armazenados em centavos.
- [x] **A083** Desconto aplicado ao total.
- [x] **A084** Parcelas distribuem centavos restantes.
- [x] **A085** Cortesia excluída das somas de recebimento.
- [x] **A086** Vínculo de segundo aluno disponível.
- [x] **A087** Relatório de avaliação em PdfDocument.
- [x] **A088** Relatório de evolução em PdfDocument.
- [x] **A089** Relatório financeiro em PdfDocument.
- [x] **A090** Configurações oferecem auditoria, tema e backup.

## 91–100 · Integração Alpha 100

- [x] **A091** Modelo de metas separado da apresentação UI.
- [x] **A092** Meta aceita progresso crescente ou decrescente.
- [x] **A093** Progresso limitado de 0% a 100%.
- [x] **A094** Distância restante calculada em unidade da meta.
- [x] **A095** Valores ausentes ou não finitos não inventam progresso.
- [x] **A096** Prazo reporta dias restantes ou vencidos.
- [x] **A097** Meta rejeita alvo igual à linha de base.
- [x] **A098** DatePicker nativo reutilizável para datas.
- [x] **A099** Data digitada e selecionada permanecem sincronizadas.
- [x] **A100** Pacote Alpha 100 preparado como código-fonte sem APK.

## Homologação ainda pendente

- [ ] Abrir e percorrer telas em aparelho/emulador Android; testar tamanhos de tela, rotação, teclado e temas.
- [ ] Validar fluxo real de backup/restauração, importação legada, permissões e notificações.
- [ ] Conferir paginação e conteúdo dos PDFs no Android.
- [ ] Compilar e testar o APK depois de concluído Alpha 100, conforme a sequência definida.

Esses itens estão fora da contagem dos 100 marcos de implementação; não foram declarados concluídos porque exigem execução em dispositivo ou produção posterior do APK.
