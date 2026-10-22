# Entrega Beta 1 — escopo verificável

## Como a solicitação foi tratada

O pedido de 10.000 etapas orientou uma ampliação do produto. Esta entrega não declara 10.000 etapas de desenvolvimento nem 10.000 funcionalidades prontas. Há oito novos módulos, mudanças de navegação/design/dados e uma matriz separada de **10.000 cenários automatizados de QA**. Os cenários não são contabilizados como funcionalidades.

## Recursos implementados

- [x] Clientes: catálogo pesquisável por nome, telefone e e-mail, sem distinção de acentos.
- [x] Clientes: dossiê com histórico, dados cadastrais, agenda, financeiro vinculado, treinos, hábitos e notas.
- [x] Clientes: nova avaliação com reaproveitamento do cadastro e ID permanente.
- [x] Agenda: calendário mensal com marcador de registros e destaque da data atual.
- [x] Agenda: seleção explícita de data, retorno para hoje e navegação entre meses.
- [x] Agenda: edição, exclusão confirmada, conclusão e filtro de situação.
- [x] Agenda: reutilização da recorrência e da checagem transacional de conflitos.
- [x] Tarefas: criação, edição, detalhes, prazo, conclusão e reabertura.
- [x] Tarefas: filtros pendentes, hoje, atrasadas, concluídas e todas.
- [x] Metas: edição de alvos de peso/gordura, prazo e observações.
- [x] Metas: preservação da linha de base enquanto o alvo permanece igual.
- [x] Metas: remoção de alvos e validação coerente com a avaliação.
- [x] Metas: busca, filtros e indicador de progresso.
- [x] Treinos: sessões com cliente, data, duração, exercícios em texto, observações e esforço opcional.
- [x] Treinos: edição, conclusão, reabertura, duplicação como novo planejamento e exclusão confirmada.
- [x] Treinos: busca por cliente, nome ou exercício e filtros por situação/data.
- [x] Treinos: timer de descanso local, pausa, retomada, presets, reinício e acréscimo de 30 segundos.
- [x] Hábitos: quantidade, meta, unidade, data, observações e cliente opcional.
- [x] Hábitos: edição, exclusão, repetição para hoje e progresso limitado ao intervalo 0–100%.
- [x] Hábitos: busca e filtros por data/atingimento da meta.
- [x] Notas: título, conteúdo, cliente opcional, data de edição e fixação.
- [x] Notas: busca no conteúdo, filtros, edição, fixar/desafixar e exclusão confirmada.
- [x] Relatórios: períodos de 7, 30 e 90 dias ou todo o histórico até hoje.
- [x] Relatórios: avaliações, atendimentos, faltas, financeiro por vencimento, treinos e hábitos.
- [x] Relatórios: distribuição financeira por serviço e acesso ao PDF financeiro completo.
- [x] Relatórios: compartilhamento explícito de resumo agregado sem nomes.
- [x] Navegação: 16 destinos, agrupados em Principal, Acompanhamento, Gestão e Ferramentas.
- [x] Navegação: painel permanente a partir de 900 dp e drawer em telas compactas.
- [x] Navegação: barra inferior para destinos principais e acesso a todos os módulos.
- [x] Navegação: busca de módulos no menu.
- [x] Navegação: busca global de módulos, clientes, notas e atendimentos.
- [x] Navegação: resultados abrem dossiê ou data correta da agenda e aplicam busca nas notas.
- [x] Navegação: menu flutuante com seis ações reais de criação.
- [x] Navegação: botão Voltar fecha atalhos ou drawer antes de sair do módulo.
- [x] Edição: confirmação antes de descartar ao trocar de módulo.
- [x] Edição: novos editores em diálogo de tela inteira, com teclado/áreas do sistema respeitados.
- [x] Edição: falhas de persistência são apresentadas nos formulários em vez de fechar o aplicativo.
- [x] Design: indicadores em duas/três colunas conforme largura disponível.
- [x] Design: cartões de prioridades para tarefas e cobranças vencidas.
- [x] Design: conteúdo centralizado com largura máxima e espaço para o menu flutuante.
- [x] Design: dez tons escuros de cor principal e tema claro/escuro persistido.
- [x] Configurações: seções Aparência, Dados e privacidade, Avisos e Sobre.
- [x] Desempenho: paginação de 30 registros nas listas principais de avaliações, cobranças e novos módulos.
- [x] Dados: três novos modelos tipados, serialização JSON e persistência criptografada.
- [x] Dados: novos tipos incluídos nos backups portátil e local, com validação antes da restauração.
- [x] Dados: regras de título, identidade, vínculo, datas, números finitos, limites e textos.
- [x] Dados: leitura completa antes de publicar atualização de estado das coleções.
- [x] Auditoria: validação dos novos tipos, IDs duplicados e vínculos sem perfil cadastrado.
- [x] Acessibilidade: rótulo de data, quantidade e seleção nos dias do calendário; ícone auto-mirrored de notas.
- [x] QA: 40 testes focados das regras de workspace e quatro testes de auditoria.
- [x] QA: 10.000 cenários determinísticos, relatados individualmente pelo JUnit parametrizado.

## Evidências finais

- Compilação Kotlin e `:app:assembleDebug`: **PASSARAM**.
- `:app:testDebugUnitTest`: **10563 testes**, 0 falhas, 0 erros, 0 ignorados.
- `:app:lintDebug`: **0 erros**, 14 avisos (target SDK e sugestões de APIs KTX).
- Auditoria estática: **34 arquivos Kotlin e 16 destinos**, com integração dos novos tipos ao backup.
- APK: `br.com.evolufisico.nativo`, `versionCode=601`, `versionName=0.1.0-native-beta.1`, Android 10+.
- Assinatura debug v2 validada e certificado igual ao APK Alpha 600. A compatibilidade de assinatura foi comprovada; a instalação em aparelho não foi executada.
- SHA-256 do APK: `5d2a15a4ccc4ad4086ac26ad14457cd9ed94c7ced1c523e0671c5a542ff66e55`.

Resumo por classe, avisos e metadados em `qa/BUILD_BETA1.json`. A lista de homologação manual está em `MIGRACAO_E_QA.md`. Não houve execução visual em emulador/aparelho nesta entrega.
