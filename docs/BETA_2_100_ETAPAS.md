# EvoluFísico Android — Beta 2: 100 etapas de desenvolvimento e homologação

Este documento é um **plano operacional** de dez frentes com dez verificações cada, não um registro de 100 Betas já publicadas. A Beta 2 permanece candidata experimental, não apta a dados pessoais reais sem criptografia e homologação. Uma etapa deve ser marcada após evidência do teste correspondente.

## Etapas 1–10 — Assinatura e identidade

Verificar pacote, assinatura original e versão ascendente.

- [ ] 001. Especificar comportamento verificável
- [ ] 002. Inspecionar código existente
- [x] 003. Implementar regra funcional
- [ ] 004. Testar base vazia
- [ ] 005. Testar casos normais
- [ ] 006. Testar falhas e entradas inválidas
- [ ] 007. Verificar compatibilidade de dados
- [x] 008. Automatizar regressão
- [ ] 009. Executar emulador Android
- [ ] 010. Homologar em dispositivo real

## Etapas 11–20 — Atualização APK

Validar arquivo local, release oficial e mensagens de erro.

- [ ] 011. Especificar comportamento verificável
- [ ] 012. Inspecionar código existente
- [x] 013. Implementar regra funcional
- [ ] 014. Testar base vazia
- [ ] 015. Testar casos normais
- [ ] 016. Testar falhas e entradas inválidas
- [ ] 017. Verificar compatibilidade de dados
- [ ] 018. Automatizar regressão
- [ ] 019. Executar emulador Android
- [ ] 020. Homologar em dispositivo real

## Etapas 21–30 — Backup e restauração

Verificar cópia externa, integridade e teste de retorno.

- [ ] 021. Especificar comportamento verificável
- [ ] 022. Inspecionar código existente
- [ ] 023. Implementar regra funcional
- [ ] 024. Testar base vazia
- [ ] 025. Testar casos normais
- [ ] 026. Testar falhas e entradas inválidas
- [ ] 027. Verificar compatibilidade de dados
- [ ] 028. Automatizar regressão
- [ ] 029. Executar emulador Android
- [ ] 030. Homologar em dispositivo real

## Etapas 31–40 — Armazenamento

Proteger registros locais e manter compatibilidade de schema.

- [ ] 031. Especificar comportamento verificável
- [ ] 032. Inspecionar código existente
- [ ] 033. Implementar regra funcional
- [ ] 034. Testar base vazia
- [ ] 035. Testar casos normais
- [ ] 036. Testar falhas e entradas inválidas
- [ ] 037. Verificar compatibilidade de dados
- [ ] 038. Automatizar regressão
- [ ] 039. Executar emulador Android
- [ ] 040. Homologar em dispositivo real

## Etapas 41–50 — Clientes e avaliações

Validar registros, relações e relatórios.

- [ ] 041. Especificar comportamento verificável
- [ ] 042. Inspecionar código existente
- [ ] 043. Implementar regra funcional
- [ ] 044. Testar base vazia
- [ ] 045. Testar casos normais
- [ ] 046. Testar falhas e entradas inválidas
- [ ] 047. Verificar compatibilidade de dados
- [ ] 048. Automatizar regressão
- [ ] 049. Executar emulador Android
- [ ] 050. Homologar em dispositivo real

## Etapas 51–60 — Formulários

Revisar anamnese, modelos e exportações.

- [ ] 051. Especificar comportamento verificável
- [ ] 052. Inspecionar código existente
- [ ] 053. Implementar regra funcional
- [ ] 054. Testar base vazia
- [ ] 055. Testar casos normais
- [ ] 056. Testar falhas e entradas inválidas
- [ ] 057. Verificar compatibilidade de dados
- [ ] 058. Automatizar regressão
- [ ] 059. Executar emulador Android
- [ ] 060. Homologar em dispositivo real

## Etapas 61–70 — Cobranças

Testar totais, recebimentos, atrasos e filtros.

- [ ] 061. Especificar comportamento verificável
- [ ] 062. Inspecionar código existente
- [ ] 063. Implementar regra funcional
- [ ] 064. Testar base vazia
- [ ] 065. Testar casos normais
- [ ] 066. Testar falhas e entradas inválidas
- [ ] 067. Verificar compatibilidade de dados
- [ ] 068. Automatizar regressão
- [ ] 069. Executar emulador Android
- [ ] 070. Homologar em dispositivo real

## Etapas 71–80 — Agenda

Testar criação, conflito de horários e calendário.

- [ ] 071. Especificar comportamento verificável
- [ ] 072. Inspecionar código existente
- [ ] 073. Implementar regra funcional
- [ ] 074. Testar base vazia
- [ ] 075. Testar casos normais
- [ ] 076. Testar falhas e entradas inválidas
- [ ] 077. Verificar compatibilidade de dados
- [ ] 078. Automatizar regressão
- [ ] 079. Executar emulador Android
- [ ] 080. Homologar em dispositivo real

## Etapas 81–90 — UI e acessibilidade

Verificar celular, tablet, fontes e temas.

- [ ] 081. Especificar comportamento verificável
- [ ] 082. Inspecionar código existente
- [ ] 083. Implementar regra funcional
- [ ] 084. Testar base vazia
- [ ] 085. Testar casos normais
- [ ] 086. Testar falhas e entradas inválidas
- [ ] 087. Verificar compatibilidade de dados
- [ ] 088. Automatizar regressão
- [ ] 089. Executar emulador Android
- [ ] 090. Homologar em dispositivo real

## Etapas 91–100 — Homologação

Rodar CI, instrumentação, regressão e upgrade físico.

- [ ] 091. Especificar comportamento verificável
- [ ] 092. Inspecionar código existente
- [ ] 093. Implementar regra funcional
- [ ] 094. Testar base vazia
- [ ] 095. Testar casos normais
- [ ] 096. Testar falhas e entradas inválidas
- [ ] 097. Verificar compatibilidade de dados
- [ ] 098. Automatizar regressão
- [ ] 099. Executar emulador Android
- [ ] 100. Homologar em dispositivo real

## Portão de Beta

A publicação com assinatura persistente fica bloqueada enquanto não houver (1) certificado original conservado e configurado em GitHub Secrets; (2) atualização testada sobre app previamente instalado; (3) banco e backups protegidos; (4) restore round-trip e QA físico completos. CI verde não comprova automaticamente esses itens.
