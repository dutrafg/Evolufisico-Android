# EvoluFísico Android — transição de base e preservação (2026-10-21)

## Estado preservado
- **Desenvolvimento anterior interrompido por solicitação do proprietário.**
- Código integral da Beta 11 preservado de maneira imutável na branch de referência
  [`archive/beta11-before-desktop-2026-10-21`](https://github.com/dutrafg/Evolufisico-Android/tree/archive/beta11-before-desktop-2026-10-21).
- SHA congelado: `04971c33ca3ae666803130b54df9f9c645e6ae89`.
- Último CI e emulador desse SHA: **success** (Android CI run `39614091720`; Android Emulator QA run `39614091478`).
- Aplicativo originalmente identificado como `97.0.0-beta.11`, `versionCode=204`; preservar esta nomenclatura durante o estágio Beta 11.
- A branch `main` do Android não deve ser sobrescrita nem misturada sem revisão.

## Nova fonte funcional e visual obrigatória
- **Repositório de referência:** https://github.com/dutrafg/EvoluFisicoDesktop
- **Branch upstream:** `main`
- **Desktop HEAD verificado na transição:** `7a7729e52e0ce6a8c8134651d4117c2a32cfc121`.
- **Branch Android para futuros portes:** `desktop-parity-beta11`.
- `EvoluFisicoDesktop-v2` é outro projeto; **não** é o upstream primário.
- Não copiar HTML/JavaScript diretamente para Kotlin quando o equivalente exigir UI nativa, banco local Android ou serviços do SO. Adaptar a semântica, as regras, a experiência de uso e a identidade visual.
- Alterações de infraestrutura específicas do Windows, instalador Inno Setup, WebView2, cache de PWA ou caminhos NTFS **não** devem ser implantadas literalmente no APK.

## Novidades Desktop identificadas na data de transição (ainda não declaradas portadas)
Commit `7a7729e`:
1. indicadores vivos de agenda no painel inicial, incluindo retornos pendentes;
2. formulários que aproveitam a largura de trabalho no desktop;
3. botão de agendamento rápido proporcional nas diferentes larguras;
4. testes de regressão para contadores, botão e formulário.

Considerar diferenças de densidade de interface entre smartphones, tablets e desktops. Sempre adaptar responsivamente.

## Política permanente de sincronização — até homologação do APK estável
1. Conferir periodicamente o HEAD de `EvoluFisicoDesktop/main` e seus commits desde a última revisão.
2. Avaliar funcionalidades, regras de negócio, UI, acessibilidade, responsividade, bugs e fluxos de dados; registrar diferenças no Android.
3. Portar apenas mudanças aplicáveis, compatibilizando armazenamento e segurança; preservar dados/backup/restauração da Beta 11. Não publicar APK de atualização sem **mesmo applicationId**, **mesma chave de assinatura** e **versionCode crescente**.
4. Exigir testes unitários, `lintDebug`, build Android e testes instrumentados, incluindo modo claro/escuro e layouts compactos; corrigir regressões.
5. Fazer commits exclusivamente na branch de porte; manter cópia preservada e `main` intocadas até homologação.
6. Quando houver uma release estável assinada e homologada, encerrar o acompanhamento recorrente e documentar último commit Desktop integrado.

**Atenção:** esta configuração define a política e os próximos passos, **não** significa que todo o código Desktop foi migrado nem que as novidades foram implementadas no APK.

## Pontos de controle
- `desktop_initial_reference_sha`: `7a7729e52e0ce6a8c8134651d4117c2a32cfc121`
- `android_preserved_sha`: `04971c33ca3ae666803130b54df9f9c645e6ae89`
- `last_fully_ported_desktop_sha`: **não estabelecido**
- `stable_android_release`: **pendente**
