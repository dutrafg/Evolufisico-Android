# Roadmap — EvoluFísico Android

## Fase 1 — Fundação
- [x] Projeto nativo em Jetpack Compose, Material Design 3, Android 10+.
- [x] Clientes com IDs estáveis, cadastro/edição/exclusão, tema.
- [x] AES-256-GCM/Android Keystore, gravação atômica local.
- [x] CI Gradle e SDK Android (confirmar resultado em Actions).
- [ ] Autenticação local por PIN/biometria e recuperação segura.
- [ ] Banco transacional e migração de esquema.

## Fase 2 — Avaliações (implementação parcial funcional na 0.2.0-alpha)
- [x] Anamnese inicial: objetivo, nível de atividade, frequência e limitações.
- [x] Medidas básicas e opcionais (peso, altura, cintura, quadril, braço, coxa).
- [x] Protocolos básicos: cálculo de IMC e RCQ, com validação de entrada.
- [x] Histórico individual, inclusão, edição, exclusão e datas.
- [x] Armazenamento criptografado, vínculo por ID e prevenção de órfãos em exclusão.
- [x] Testes unitários para validação e cálculos (execução automática via CI).
- [ ] Equivalência com os protocolos da versão Web: dobras cutâneas, Pollock, BIA e outros.
- [ ] Importação/migração de dados reais da versão Web com consentimento e backup.
- [ ] Testes instrumentados em Android real e acessibilidade.

## Fase 3 — Evolução e PDFs
- [ ] Gráficos e comparativos mensais/trimestrais.
- [ ] Relatórios PDF separados da interface, exportação SAF/FileProvider.

## Fase 4 — Cobranças e dados
- [ ] Cobranças, pendências e ação Receber.
- [ ] Pasta selecionada pelo usuário, SAF, backup cifrado, restauração íntegra.

## Fase 5 — QA e distribuição
- [ ] Segurança, testes instrumentados, benchmarks, assinatura de APK/AAB.
