# Critérios para iniciar a versão Beta — EvoluFísico Android

O rótulo Beta não depende de completar uma sequência arbitrária de 100 etapas.

## Funcionalidade obrigatória
- [ ] Clientes: CRUD, edição, persistência, prevenção de perda de dados
- [ ] Avaliações e evolução: formulários e cálculos conferidos, PDFs funcionais
- [ ] Formulários: todos os modelos e histórico por cliente
- [ ] Agenda: calendário, conflitos, cancelamentos e vínculo individual
- [ ] Metas: progresso, validações e vínculo por cliente
- [ ] Cobranças e Serviços: valores, recibos e estados consistentes
- [ ] Busca, relatórios, indicadores e anotações: testes funcionais

## Dados e migração
- [ ] Exportação e restauração externa exercitadas em Android real
- [ ] Backup diário confirmado em pasta SAF com app fechado e após reiniciar o aparelho
- [ ] Migração a partir de dados/arquivos antigos sem perda
- [ ] Atualizações de APK pela mesma assinatura permanente e versão crescente
- [ ] Definir proteção apropriada de dados antes de Beta público com registros reais

## Qualidade
- [ ] JUnit, Android Lint e testes instrumentados verdes no último commit
- [ ] Testes manuais completos em smartphone Android 10+ e outra versão recente
- [ ] Responsividade, modo claro/escuro e acessibilidade verificados
- [ ] Nenhum bug crítico de armazenamento, instalação ou geração de arquivos pendente

Somente quando os requisitos estiverem comprovados deve-se alterar a versão para Beta. Enquanto isso, continuaremos em ciclos Alpha, preservando o histórico e sem solicitar confirmações intermediárias para desenvolvimento e QA.
