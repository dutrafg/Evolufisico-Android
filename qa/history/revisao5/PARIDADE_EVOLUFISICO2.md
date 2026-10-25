# Escopo e paridade com EvoluFísico2 — revisão 5

Fonte exclusiva: dutrafg/Evolufisico2, branch main, commit 7381ed1884ff7250a6659ec77762cb5eaf5bbe0d (22/10/2026). Foram lidos o entrypoint modular index.html, README, código de navegação, editor de painel e folhas visuais. Os HTMLs monolíticos anteriores não substituem o entrypoint atual.

| Referência web | Adaptação nativa desta revisão | Verificação ainda necessária |
|---|---|---|
| Início | Agenda rápida, calendário e tarefas reunidos no Início; quatro painéis com ordem, largura, altura mínima e restauração; saudação com nome profissional | Arranjo visual em telefone/tablet; teclado e rotação |
| Avaliações | Alunos agrupados por ID, A–Z sem acentos, lista ou grade adaptativa de até quatro colunas; histórico, nova avaliação e backup individual | Comparar ficha completa e PDF com os mesmos dados da web |
| Evolução | Reaproveitados histórico por identificador, comparação, gráficos e PDF | Conferir gráficos e equivalência de protocolos |
| Calculadora | Reaproveitadas ferramentas antropométricas nativas | Auditoria integral fórmula a fórmula contra os métodos EF77 atuais |
| Formulários | Reaproveitados questionários, vínculo por ID, busca e PDF | Correspondência de cada pergunta/modelo com os nove modelos da web |
| Análises | Ícone de cérebro; escolha explícita de 3–4 datas; bloqueio abaixo de três dias distintos ou 21 dias; métricas existentes com ajuda | EF68/EF70/EF77, relatório editável e painéis ricos ainda não têm paridade integral comprovada |
| Cobranças | Mantidos serviços, preços, parcelas, descontos, vínculo por ID e PDF nativo | Reconciliar catálogo de serviços atual e conferir centavos, filtros e totais em dispositivo |
| Configurações | Nome/CREF profissional, cores web, claro/escuro, dados/privacidade, manutenção e notificações; estado de erro de backup | Validar permissões, SAF, Keystore e agendamento Android |

## Remoções do produto

Retirados os destinos principais Clientes, Agenda, Tarefas, Metas, Treinos, Hábitos, Notas e Relatórios. Agenda e tarefas continuam somente como funções do Início; alunos e seus históricos ficam em Avaliações; metas já existentes nas avaliações foram mantidas, pois existem no formulário web.

Foram removidas da produção as telas dedicadas de treinos, progressão, catálogo, seletor de exercícios, temporizador, hábitos, notas, metas, relatórios gerais e dossiê de Clientes. Também saíram atalhos, busca de notas, favoritos e reorganização dos módulos. A ordem dos oito módulos passa a ser a da web; a organização personalizável se aplica aos painéis do Início.

Não foi efetuada exclusão destrutiva dos registros. O esquema, leitores e validadores legados necessários a backups de revisões anteriores permanecem como compatibilidade, sem rotas ou telas para reativar esses módulos. Backups completos conservam esses dados; backups individuais novos contêm apenas avaliações, anamneses, agenda e tarefas vinculadas, excluindo cobranças e módulos retirados. O código-fonte integral da revisão 4 continua disponível no histórico anterior.

## Correções de prioridade alta

- Geração de PDF fora da thread de interface; gravação PDF/CSV em IO com limpeza dos buffers e remoção de arquivo parcial após erro.
- Backup automático não executa se desativado, identifica pasta sem gravação, limita novas tentativas e mostra falha sem expor dados pessoais.
- Restauração aplica limite de 16 MB também ao conteúdo descriptografado.
- Lembretes consideram bloqueio do aplicativo/canal no Android, cancelam avisos antigos e usam identificador completo como tag para evitar colisões de hash.
- Calendário é aberto em painel com altura delimitada, evitando rolagens verticais aninhadas com altura infinita.
- Área segura Android foi preservada. O aplicativo permanece nativo, sem WebView e sem permissão INTERNET.

## Atualizações do diretório web

AGENTS.md exige consultar main antes de cada ciclo e antes de entregar. qa/UPSTREAM_EVOLUFISICO2.json registra a base consultada. Conferir o entrypoint e seus assets, comparar alterações de produto e portar somente o que pertence ao EvoluFísico2. Essa política não configura um monitor automático nem faz atualização silenciosa do aplicativo instalado.

## Limites desta entrega

Não se afirma que a interface é pixel a pixel idêntica à web nem que toda a paridade funcional está encerrada. A tabela discrimina o que falta comparar/portar. Não foi executada instalação, teste visual ou homologação física Android. Não foi gerado APK por solicitação do usuário. Testes de domínio e Lint não substituem esses testes de dispositivo.
