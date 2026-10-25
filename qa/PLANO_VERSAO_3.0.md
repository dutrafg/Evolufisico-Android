# EvoluFísico Android — início da versão nativa 3.0

## Referência

A referência canônica foi atualizada para a publicação EvoluFísico2 3.0 aprovada pelo usuário. O HTML foi copiado sem alterações para `reference/app.html`; SHA-256: `03a433c85ef491ccce6a95f4dbd6d87ae5cb1ac2fbebf275dbce28f32edbd066`.

## Base técnica

- Kotlin + Jetpack Compose nativo; sem WebView.
- Preservar os fluxos de dados, registros e compatibilidade de backup da Beta 100 Rev. 14.
- Identidade da próxima candidata: `3.0.0-native-beta.1`, `versionCode 714`.
- A publicação de referência foi obtida do pacote EvoluFísico2 3.0 aprovado e corresponde à `main` observada no ciclo atual.

## Escopo de paridade 3.0

1. Separar claramente a Dashboard de trabalho (“secretária”) do Perfil Unificado do aluno.
2. Replicar navegação e atalhos da versão publicada, mantendo o botão flutuante “Módulo” e abertura inicial no painel principal.
3. Integrar cadastro, avaliações, histórico e ações do Perfil Unificado sem duplicar nem perder dados.
4. Conferir temas, layout responsivo, acessibilidade, estados vazios e privacidade financeira em celular e tablet.
5. Rodar testes de domínio, auditoria estática, Lint e testes visuais em dispositivo quando disponível.

## Estado deste ciclo

A referência, a identidade de versão e as instruções do projeto foram atualizadas. As diferenças funcionais e de interface ainda precisam ser portadas para Compose; não declarar paridade nem APK 3.0 até concluir os fluxos e validar a compilação.
