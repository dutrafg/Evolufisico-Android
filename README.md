# EvoluFísico Android nativo — Beta 17

Android nativo em Kotlin e Jetpack Compose, com referência funcional no EvoluFisico2 3.1.5 Release (main consultada em 25/10/2026). A paridade integral ainda depende das etapas listadas abaixo.

## Funcionalidades

- **Início:** indicadores do dia, calendário, agenda editável, próximos atendimentos, tarefas, reavaliações vencidas e cobranças pendentes com valores ocultáveis.
- **Perfil Unificado:** cadastro editável, resumo da última avaliação, histórico com comparação de medidas e gráfico de peso, anamneses e cobranças vinculadas.
- **Avaliações:** registro e edição de peso, altura, perímetros, percentual de gordura informado (método não registrado), sete dobras cutâneas, sexo e idade; bioimpedância com percentual, massa livre e equipamento; até quatro fotos otimizadas por avaliação, exibidas no perfil, incluídas no backup e incorporadas ao PDF da avaliação (até quatro, duas por página); estimativas Jackson–Pollock 3 e 7 dobras mantidas separadas no histórico local, com limites explícitos de idade e entradas.
- **Relatórios do Perfil:** exportação em PDF de avaliações e anamneses, com identificação do aluno, dados disponíveis, observações e dados profissionais definidos em Configurações. Anamneses longas são paginadas. Relatórios de avaliação incluem fotos JPEG com páginas fotográficas próprias e validação do vínculo entre aluno e avaliação.
- **Evolução e Análises Completas:** seleção de três ou quatro datas (mínimo de 21 dias), tendências descritivas por indicador e cinco verificações EFIA (EF78–EF82), completude documental, métodos e dispositivos comparáveis, checagens aritméticas e eventos explícitos EF81. Exige 21 dias totais e pelo menos sete dias entre as duas últimas datas; inclui rascunho revisável e PDF protegido por identidade/período.
- **Calculadora:** IMC, estimativas Navy/RFM e Jackson–Pollock 3/7 (soma, densidade, percentual e massas), Harris-Benedict, Mifflin-St Jeor, gasto diário e zonas de frequência cardíaca, com validação de limites.
- **Anamnese:** nove modelos, formulários personalizados, vínculo com aluno, edição, exclusão e exportação em PDF.
- **Cobranças:** cadastro, edição, recebimento, exclusão e vínculo opcional com aluno.
- **Configurações:** temas claro/escuro, dez cores, identificação profissional, prazo de reavaliação (30/60/90 dias), ocultação de valores, backup/restauração manual e backup automático diário para uma pasta escolhida, em horário configurável (o Android pode adiá-lo por economia de bateria).
- **Navegação compacta:** módulos principais no seletor flutuante; Agenda e Tarefas são funções acessadas pelo Início. Interface limitada a 600 dp, sem modo desktop/estendido.

## Verificação automatizada

O CI executa `:app:testDebugUnitTest`, que também compila o código principal. A suíte contém 42 casos de teste identificados no código: fórmulas e validação do Laboratório incluindo Pollock 7, janelas temporais, tendências, completude e comparação pareada das análises, persistência/restauração de medidas, fotos e backup, operações de cadastro/edição/exclusão dos dados locais, vínculos e restauração de backup, geração/paginação dos PDFs, inclusão da bioimpedância nos relatórios e agendamento do backup diário.

A verificação automatizada compila o código principal, mas não substitui homologação visual nem validação em aparelhos físicos. Não foi gerado APK nesta etapa.

## Diferenças em relação ao EvoluFisico2 3.1.5

Os cinco motores EF78–EF82 têm uma adaptação nativa ligada às avaliações locais; isto ainda não é uma certificação de paridade clínica ou algorítmica integral com o web. EF68/EF70/EF77 continuam com a cobertura descritiva previamente implementada, sem toda a lógica avançada da referência. O protocolo adicional Jackson–Pollock 3 dobras não substitui Jackson–Pollock 7 nem BIA. EF81 aceita somente eventos estruturados explicitamente informados (não infere adesão de notas livres); EF79 só calcula MDC com SEM positivo e fonte informada; estes campos agora são editáveis na tela de Análises e acompanhados da procedência no PDF, sem certificação automática da fonte. Não estão implementados sincronização automática entre aparelhos, notificações de segundo plano nem outros protocolos antropométricos. Persistem pendências de testes em aparelhos reais.

## Compilação e dados

Requer JDK 17 e Android SDK 36:

```sh
./gradlew :app:assembleDebug
```

Os dados ficam no aparelho. Exporte um backup antes de restaurar. Versão `0.1.0-native-beta.17`, código `721`. A Beta 16 foi preservada na branch `native/3.0-beta16-photo-reports`; o desenvolvimento novo está isolado na `native/3.0-beta17-five-pending`. Nenhum APK foi gerado nesta etapa.

## EF78–EF82 — critérios e proteção

| Motor | Função nativa | Restrições |
|---|---|---|
| EF78 | Consistência, lacunas, protocolos e aparelhos | Não confirma padronização não registrada |
| EF79 | Diferenças descritivas e MDC95 condicionado | SEM positivo **e** origem documental necessários; formulário nativo exporta fonte e resultado no PDF; não inventa precisão |
| EF80 | Auditoria aritmética de massa e fonte | Coletas independentes não são tratadas como uma só aferição |
| EF81 | Eventos explícitos de treino/alimentação | Sem inferir causa ou adesão de observações livres |
| EF82 | Tendências exploratórias Theil–Sen e mínimos quadrados | Com 3–4 datas, não atesta estabilidade nem detecta outliers automaticamente |

A migração SQLite **v8 → v9** adiciona `context_event` sem apagar registros preexistentes.
O backup foi ampliado para **schema 6**, com restauração de versões 1–5 preservada quando compatível.
Relatórios de análise bloqueiam intervalos menores que 21 dias, menos de sete dias entre as últimas duas coletas, registros de alunos diferentes e séries insuficientes. A auditoria EF78–EF82 é anexada como seção própria no PDF, independente da síntese editável. Comparações derivadas de composição dependem da procedência de protocolo e aparelho declarados. Indicadores anteriores do resumo e do PDF também bloqueiam comparação longitudinal de BIA com aparelho diferente ou ausente, mudanças de sexo/equação para Pollock 7 e percentuais informados sem método. São resultados **exploratórios**, sujeitos à revisão profissional.

A implementação não ativa serviços em nuvem e continua utilizável offline. Alterações nesta branch não mudam a `main`.
