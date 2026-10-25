# Estado atual — revisão 9 baseada no HTML anexado

Referência exclusiva: reference/app.html. Vínculo com diretório/repositório anterior encerrado por instrução do usuário.

Nove módulos: Início, Organização, Avaliações, Evolução, Calculadora, Formulários, Análises, Cobranças e Configurações.

Novas implementações e diferenças remanescentes estão em PARIDADE_HTML.md. O resultado executado está em BUILD_BETA100_REVISAO9.json. A antiga lista de oito módulos e a referência de repositório pertencem à revisão 5, agora histórica.

Pendências principais: homologação em Android de telas pequenas/fontes/rotação/teclado; PDF AcroForm em leitores reais; permissões/Keystore/SAF e backup/restore; auditoria exaustiva de métodos/serviços e portabilidade completa dos painéis EF68/EF77. A exportação reversa JSON web foi implementada com limites e testes de intercâmbio.

O pedido vigente autoriza implementar, validar e gerar APK. Esta revisão produz APK de depuração assinado, preservando a identidade da revisão 4 e incrementando versionCode para 708.

A revisão 9 remove a tela Módulos e reescreve o Início de acordo com o último painel do HTML, preservando os editores em Organização.
