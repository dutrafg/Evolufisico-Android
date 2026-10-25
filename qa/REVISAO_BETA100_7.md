# Beta 100 — revisão 7

Referência exclusiva e imutável: reference/app.html. versionCode 706. Android nativo Kotlin/Compose, sem WebView e sem permissão INTERNET.

## Implementações

- Organização exporta JSON no envelope do HTML: notas, metas, tarefas e agenda. Seletor de arquivo Android, escrita fora da interface, retorno de erro/cancelamento, limpeza do buffer e limites de 3 MB/10000 registros. Mantém backup nativo criptografado. JSON web não preserva metadados exclusivos do app nativo e não é criptografado.
- Detalhes de tarefas aceitam 10000 caracteres do HTML sem truncamento.
- Calculadora EF77 ampliada: Navy, RFM, ABSI, FFMI/normalizado, água corporal total Watson, FC máxima Tanaka e comparador. Ignora valores fora dos limites, preserva ausência de dados, bloqueia equações corporais adultas para menores e bloqueia referência composta com amplitude ≥8 p.p.
- Análises EF68: eventos de melhora/mudança, consistência, oscilação, platô aparente, reversão e intervalo longo. Transições só com intervalo de sete dias e método de gordura compatível.
- Gráficos nativos usam datas proporcionais, não conectam gordura de métodos distintos nem preenchem medidas ausentes. Valores e deltas estão disponíveis em texto e semântica acessível. Não há projeção futura.
- Testes JVM usam JSON real. Vetores de referência são gerados do JavaScript do próprio HTML: EF68 (36), EF70 (48), EF77 (16).

## Validação

O resultado executado e a verificação do APK estão em BUILD_BETA100_REVISAO7.json. Logs da compilação e Lint acompanham o fonte. Não há emulador nem aparelho neste ambiente: instalação, aparência real, fontes ampliadas, rotação, teclado, leitores PDF Android, permissões, SAF e Keystore permanecem sem homologação em dispositivo.

Não representa paridade integral ou contagem de etapas. Auditoria exaustiva de protocolos/serviços, organização visual completa dos painéis web e reprodução dos PDFs web continuam pendentes em PARIDADE_HTML.md.
