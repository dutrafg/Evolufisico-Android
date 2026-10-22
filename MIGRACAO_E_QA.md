# Atualização de código Beta 33

A Beta 33 mantém o pacote e o formato dos registros/backups. Edição de contato modifica somente a avaliação mais recente do cliente, preservando ID e medidas; não renomeia registros históricos. PDF de dossiê é um resumo, não um backup. CSV de notas omite conteúdo por padrão; a inclusão exige seleção explícita. A versão está sem compilação/homologação Android neste ambiente; veja `qa/ENTREGA_BETA33.md`.

---

# Migração e QA — Beta 2 nativa

Versão `0.1.0-native-beta.2`, versionCode 602, Android 10+. A Beta 1 anterior usava versionCode 601. Não houve alteração de applicationId nem do formato da tabela local. Os registros adicionais usam novos valores de tipo na mesma tabela criptografada.

## Campos adicionados

Séries e modelos pertencem ao tipo `training`; prioridade/categoria/cliente pertencem a tarefas; categoria/arquivo pertencem a notas. A leitura usa padrões para backups anteriores, sem apagar campos antigos. Evite downgrade: versões antigas podem ignorar os campos adicionais e perdê-los ao regravar.

## Compatibilidade

- Backups das versões nativas anteriores permanecem importáveis.
- Novos backups incluem `training`, `habit` e `note`. Restaure-os nesta Beta ou em versão posterior que suporte esses tipos.
- Nenhuma restauração sobrescreve silenciosamente uma versão conflitante de um registro.
- Treinos, hábitos e notas são validados antes da gravação e da restauração.
- A publicação do estado das oito coleções ocorre após a leitura completa, em um snapshot Compose.
- Importação de JSON/.efx legado continua disponível, mas sua homologação em dispositivo permanece pendente.

## Validado automaticamente

- Suíte JUnit: regras existentes, testes focados de workspace/recursos profissionais e duas matrizes de 10.000 cenários parametrizados.
- Auditoria estática de módulos, navegação, versionamento e integração dos novos registros à criptografia e restauração.
- Compilação Kotlin, Android Lint e montagem do APK, conforme registro final em `qa/ENTREGA_BETA2.md`.

## Homologação manual ainda necessária

1. Atualizar a Beta 1 com o novo APK e conferir preservação dos registros.
2. Testar todos os 16 módulos em aparelho Android 10 e versão atual, fonte ampliada, rotação, teclado, modo claro e escuro.
3. Conferir menu flutuante, drawer, busca global e proteção de edição não salva.
4. Criar, editar, concluir, duplicar, excluir e restaurar registros dos três novos tipos.
5. Testar backups portátil e `.efdevice`, permissões SAF, cancelamentos, interrupções e colisões.
6. Inspecionar PDFs longos, fotos e acentos; homologar importação de backups reais anonimizados.
7. Validar notificações e WorkManager sob economia de bateria.
8. Exportar os seis CSVs pelo seletor, confirmar acentos/aspas e conferir os dados em uma planilha.
9. Confirmar paginação com base local grande e uso de memória em aparelho.

A infraestrutura deste trabalho não tem emulador ou aparelho Android conectado. Não há evidência de execução visual ou teste de instalação em dispositivo nesta entrega.
