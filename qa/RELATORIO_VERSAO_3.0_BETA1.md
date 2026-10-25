# EvoluFísico Android 3.0 — revisão de integração

## Implementado neste ciclo

- A referência canônica continua sendo a cópia exata do EvoluFísico2 3.0 aprovado; o hash registrado foi conferido.
- O menu Módulo passou a incluir Perfil Unificado como destino próprio, mantendo Início como tela inicial.
- A nova tela Compose reúne a seleção de alunos e uma ficha com abas de visão geral, avaliações, evolução e financeiro.
- A ficha consulta os registros locais já existentes e abre as telas completas de Avaliações, Formulários, Evolução e Cobranças com o identificador do aluno.
- O código não usa WebView e não duplica nem migra dados.
- Os testes de interface estática foram ajustados para inspecionar a referência publicada 3.0, em vez de exigir textos exclusivos do HTML anterior.

## Validação

Passaram: auditoria de escopo, contratos de UI estática, efeitos visuais, paleta, encaixe/arraste do painel e área segura. A conferência do SHA-256 da referência também passou.

Não executado: compilação Gradle/Android e testes JUnit, pois este ambiente não contém Gradle nem `kotlinc`, e não consegue baixar dependências. Nenhuma validação visual em dispositivo foi realizada.

## Limitações conhecidas

- Os perfis são derivados das avaliações existentes; ainda não há cadastro independente de aluno sem avaliação.
- A ficha lista contagem de formulários e abre o módulo completo, mas ainda não apresenta respostas de formulários dentro do Perfil Unificado.
- Precisa validar a compilação Compose e a navegação em aparelho antes de considerar a candidata 3.0 integrada.

## Continuidade após publicação

- Em 25/10/2026, a main avançou para `80bca6d7d3eba916ca02d5c3ac28604cb1e65ce2`; o check Vercel desse commit concluiu com `success`.
- O diff posterior ao artefato 3.0 foi a correção do build para reconhecer o ID da Dashboard com aspas simples ou duplas e a atualização do manifesto de release.
- Removida da Dashboard Android a ação “Nova avaliação”, conforme a publicação. A criação continua acessível em Avaliações e no Perfil Unificado.
- Ajustados os contratos estáticos para proteger a ausência desse botão na Dashboard.
- Testes estáticos da revisão atual passaram. A compilação Gradle continua bloqueada porque o wrapper precisa baixar Gradle 9.3.1 e a rede do ambiente não alcança `services.gradle.org`; o APK atualizado não foi gerado.
