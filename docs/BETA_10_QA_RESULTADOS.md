# Beta 10 — QA executado (Android)

**Versão:** `96.0.0-beta.10` · **versionCode:** `203` · **debug package:** `br.com.evolufisico.android.beta10`.

- [x] Build APK debug e testes unitários — [GitHub Actions CI](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39575393903)
- [x] Compilação dos testes instrumentados e Android Lint — mesma execução, aprovada
- [x] Verificação Python de IDs: 50.000 por Beta 7, 8, 9, 10; 200.000 checkpoints de **planejamento** sem duplicatas
- [x] Testes instrumentados no Android 10 — [GitHub Actions emulador](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39575393961), aprovado
- [x] Artefato APK `12334132004` extraído e integridade ZIP verificada; 60,76 MB
- [x] SHA-256 do APK: `bd5f80a38b92c543897ff13f51de38752f6c637c6ddc45d0648058b9eb6c0470`
- [ ] QA em smartphone e tablet físicos, fontes grandes, rotação, TalkBack
- [ ] Avaliar migração e restauração de backup antigo em uma instalação real
- [ ] Paridade completa com EvoluFísico2 (consulte `BETA_10_PARIDADE_EVOLUFISICO2.md`)

**Escopo:** Beta 7 — Início/Módulos; Beta 8 — Clientes/Agenda/Cobranças; Beta 9 — Avaliações/Formulários/Evolução/Configurações; Beta 10 — análise de paridade, testes e APK. Os 10 mockups são direção visual, não telas reproduzidas pixel a pixel.

**Aviso:** APK DEBUG é instalação paralela com assinatura temporária e base isolada; não é APK oficial atualizável da versão anterior.
