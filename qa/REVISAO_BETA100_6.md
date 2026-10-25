# Revisão 6 — HTML anexado como referência exclusiva

A referência passou a ser reference/app.html, preservado integralmente e fixado por SHA-256. As regras ativas não consultam mais nenhum repositório; instruções e metadados antigos estão isolados como histórico.

Implementações: Organização (notas, tarefas e metas descritivas no calendário), edição de cards, nove modelos exatos de anamnese e modelos próprios, PDF AcroForm, matemática EF70 e relatório editável/PDF, importação aditiva do JSON de organização e backup nativo criptografado desse conjunto. Detalhes e limitações em PARIDADE_HTML.md.

Validação final: 50704 testes JUnit em 31 suítes, zero falhas/erros/ignorados; classes dos relatórios conferem com os fontes atuais. A execução limpa removeu uma antiga classe de teste, evitando contagem duplicada. Lint: zero erros, 30 avisos. Não foram desativadas verificações para ocultar falhas.

O teste de EF70 compara 48 vetores com o JavaScript executado do próprio HTML. Uma verificação independente confirmou os nove modelos e os 77 textos das perguntas. O PDF sintético foi inspecionado com parser e renderização: duas páginas, oito campos de resposta editáveis e quatro campos de identificação somente leitura; árvore de campos e widgets coincidentes. Perguntas longas geram páginas adicionais sem corte.

Auditoria estática: 61 arquivos Kotlin, nove módulos, oito contratos de área segura, 60 pares fixos de contraste aprovados e dois XML válidos. Auditoria estática não comprova pixels nem comportamento Android.

Não foi gerado APK. A execução final só chama testes e Lint. O HTML original e os dados do usuário não foram alterados. Notificações, SAF, Keystore, migração de dados reais, leitores PDF Android, paridade visual e as diferenças funcionais remanescentes da matriz ainda dependem de trabalho/homologação. Não se afirma que tudo do HTML foi concluído.
