# EvoluFísico Android — Beta 5 | QA automatizado

**Versão:** 91.0.0-beta.5 (versionCode 198) · **Branch:** beta-5-development · **Código compilado:** `19fe3763d88d2dae3fe38bceec5772ef58cf9e37`.

## Evidência de execução

- [x] Build Android debug e testes unitários: [GitHub Actions #39569465412](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39569465412), concluído com sucesso.
- [x] Compilação dos testes instrumentados e Android Lint: mesma execução, sucesso.
- [x] Testes instrumentados Android 10: [GitHub Actions #39569465462](https://github.com/dutrafg/Evolufisico-Android/actions/runs/39569465462), concluído com sucesso.
- [x] Artefato APK debug gerado pelo GitHub: `Evolufisico-DEV-DEBUG-NAO-ATUALIZAVEL`, artefato `12331063945`.
- [x] APK extraído validado como arquivo ZIP íntegro, SHA-256 `be6db8ace005e33d1b8c3e821f10c7730f3201d6ee29ec41eca027d309a95964`.

## O que ainda não foi homologado

- [ ] Verificação manual em smartphone Android e tablet real, inclusive teclado, fontes grandes e modo claro/escuro.
- [ ] Restauração de backups antigos reais com campos de cliente e perimetria legados.
- [ ] Paridade funcional completa das rotinas do EvoluFísico2; ver `BETA_5_PARIDADE_EVOLUFISICO2.md`.
- [ ] Segurança para dados reais, armazenamento protegido e assinatura release permanente.
- [ ] Conclusão individual das 10.000 etapas planejadas.

**Nota:** Aprovação automática é evidência de compilação e dos testes existentes; não prova todos os fluxos de negócio. O APK `.beta5` instala um app de teste separado.
