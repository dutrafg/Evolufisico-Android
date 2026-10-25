# Revisão 10 — navegação solicitada pelo usuário

Um único botão Módulo no canto inferior esquerdo, com afastamento de 24dp além da margem do Scaffold e da área segura do sistema. Abre uma lista rolável com todos os nove destinos. Substitui navegação inferior e lateral. Ao escolher um destino, fecha o menu; preserva proteção de edição não salva. Com teclado aberto o botão fica oculto. Espaço inferior reservado para não encobrir o final do conteúdo. Nova composição inicia sempre em Início, inclusive após restauração de processo; recriação de Activity também volta a Início.

A alteração de navegação é uma exceção explícita do usuário ao HTML. Dados e backups preservados. Validação em aparelho não realizada.
