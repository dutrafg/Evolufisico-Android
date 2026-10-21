# Progresso de paridade Desktop → Android (2026-10-21)

## Referência Desktop verificada
- Fonte: `dutrafg/EvoluFisicoDesktop`, `main`.
- SHA mais recente analisado: `9940d942afcfaeeacd468bc1e483094a8d5706f5`.
- O SHA de referência **não é necessariamente o último integralmente portado**.
- Preservado Android original: `archive/beta11-before-desktop-2026-10-21` @ `04971c33ca3ae666803130b54df9f9c645e6ae89`.
- Desenvolvimento: `desktop-parity-beta11` com `97.0.0-beta.11`.

## Alterações recentes no Android

### Pré-cadastro
- Fila de pré-cadastros, edição, exclusão confirmada, conversão idempotente em cliente e integração no início.
- Validação, armazenamento separado e conservação em backup foram implementados na branch, com testes adicionais.
- **Estado:** aguardar CI e emulador do último commit de integração para homologar.

### Contraste de navegação Desktop beta.4.1
- `NavigationContrastTokens` define cor própria para fundo de navegação, itens selecionados e inativos.
- Aplicação na barra inferior e no rail lateral (Jetpack Compose).
- Testes verificam WCAG AA >= 4,5:1 em **15 paletas e dois temas**.
- O acento laranja original tem exceção de contraste, resolvida com fallback no texto em vez de modificar cores salvas.
- **Estado:** implementação enviada ao GitHub; homologação do novo SHA depende do Android CI/Emulator QA.

## Itens ainda pendentes de revisão e adaptação
- Ordenação interativa de análises do Desktop: examinar equivalência com os painéis de análises nativos e regras de persistência por perfil.
- Ajustes de formulários para áreas de trabalho grandes: validar em tablet e telas amplas sem prejudicar smartphones.
- Indicadores de agenda, retornos e agendamento rápido: revisar equivalência dos dados e interação das telas, apesar dos indicadores já existentes no Android.
- Ciclo completo de backup/restauração, assinatura permanente para APK e homologação em aparelho físico.
- A integração de commits posteriores deve ocorrer somente com validação, sem sobrescrever a `main` nem a branch preservada.

**Não declarar paridade total nem APK estável a partir deste registro.**
