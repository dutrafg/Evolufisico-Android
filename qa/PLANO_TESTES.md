> Estado 0.5.0: build Android, testes JVM e Lint executados. Veja RESULTADOS_0.5.0.md. Os casos manuais abaixo continuam pendentes em aparelho.

> Estado 0.5.0: build Android, testes JVM e Lint executados. Veja RESULTADOS_0.5.0.md. Os casos manuais abaixo continuam pendentes em aparelho.

# Plano de testes funcionais, dados e QA para Android Studio

Rodar em **Android 10, 13 e 17**, telas **320dp, 360dp, tablet 800dp**, claro/escuro, portrait/landscape. Conferir mensagens reais e PDFs gerados.

| ID | Ação | Resultado esperado |
|---|---|---|
| INI-01 | Criar avaliação e retornar à tela Início | Contadores e última avaliação atualizados |
| INI-02 | Agendar atendimento e editar data/hora | Mostrar apenas na data correta, sem duplicação |
| INI-03 | Agendar por 3 semanas | Criar 3 registros com IDs diferentes |
| INI-04 | Concluir, reabrir e excluir agendamento | Estados persistem após reiniciar |
| INI-05 | Criar, marcar e remover tarefa | Persistência em SQLite |
| AV-01 | Criar aluno e nova avaliação do mesmo aluno | Mesmo studentId, outro evaluation.id |
| AV-02 | Sem treino, sem dieta | Aderência N/A, não 0%; nenhum score artificial |
| AV-03 | Sem treino, com dieta 80% | Aderência aplicável 80% |
| AV-04 | Treino 60%, dieta 100% | Aderência aplicável 80% |
| AV-05 | Salvar 7 dobras, sexo, idade e peso | Resultado comparável com cálculo de referência |
| AV-06 | Dobras incompletas ou inválidas | Resultado em branco, sem NaN/Infinity |
| AV-07 | Inserir 5 fotos > 1 MB | Compressão / tamanho limitado e backup possível |
| AV-08 | Excluir avaliação com confirmação | Só um registro removido |
| EV-01 | Duas avaliações do mesmo ID | Gráfico, variações e datas certas |
| EV-02 | Faltou peso em uma avaliação | Não interpolar um valor inexistente |
| EV-03 | Dois alunos homônimos com IDs diferentes | Históricos não misturados |
| CAL-01 | Peso 100kg, altura 200cm | IMC 25,0 |
| CAL-02 | RCQ cintura 80, quadril 100 | RCQ 0,80 |
| CAL-03 | TMB peso/altura/idade/sexo | Fórmula Mifflin validada |
| FOR-01 | Criar cada nível de anamnese | Perguntas específicas daquele nível |
| FOR-02 | Vincular, editar e salvar respostas | StudentId preservado e respostas persistentes |
| ANA-01 | Aderência com N/A | Indicador não penalizado |
| ANA-02 | Abrir '?', em 320dp | Ícone quadrado 44dp, texto legível, sem esticar |
| COB-01 | Diária | Preço R$25,00 |
| COB-02 | Valor R$100, desconto R$10 | Total R$90 |
| COB-03 | R$10,01 em 3 parcelas | R$3,34 + R$3,34 + R$3,33 |
| COB-04 | Mudar status pago / pendente | Totais corretos |
| COB-05 | Dois alunos com mesmo nome | Conferir vínculo por ID |
| PDF-01 | Exportar 3 relatórios | Gerar PDFs legíveis, sem painel Android na página |
| PDF-02 | Nome longo/fotos/reavaliação extensa | Paginação sem sobreposição |
| DB-01 | Fechar/abrir app com registros | Dados persistentes e legíveis |
| DB-02 | Backup portátil, app diferente | Restaurar com a senha correta |
| DB-03 | Senha errada e arquivo corrompido | Não modificar dados |
| DB-04 | Importar duas vezes o mesmo backup | Segunda importação retorna 0 registros novos, sem duplicação |
| DB-05 | Escolher pasta SAF e ativar 22:00 | Backup aparece na pasta com execução aproximada |
| DB-06 | Desinstalar/reinstalar e abrir .efdevice | Falha esperada (chave Keystore perdida); usar .efbackup portátil |
| LEG-01 | JSON exportado do APK original | Verificar checksum e contagens, preservar todos campos |
| LEG-02 | .efx com kdf e senha correta | Decriptar, verificar checksum, restaurar sem substituir |
| LEG-03 | .efx sem kdf ou senha errada | Recusar com mensagem explícita |
| NOT-01 | Autorizar notificações e criar compromisso futuro | Lembrete aproximadamente 1h antes |
| NAV-01 | Trocar entre as 8 telas | Sem crash ou sobreposição da barra inferior |
| THEME-01 | Alternar 5 tons e tema | Contraste e persistência após reiniciar |

**Critérios de aprovação antes de instalar sobre app antigo:** build Android sem erros, testes instrumentados e manuais aprovados, importação e restauração conferidas por contagem/IDs/checksum e assinatura/release controlada.

## Casos adicionais 0.3.0

| ID | Ação | Resultado esperado |
|---|---|---|
| CAL-04 | Selecionar 4 equações metabólicas e alterar sexo/idade | Recalcular resultado ou informar ausência de entradas |
| CAL-05 | RCE e BRI com altura ausente ou não positiva | Não exibir valor artificial ou NaN |
| CAL-06 | Simular percentual alvo sem gordura atual válida | Não inventar peso alvo |
| CAL-07 | FC máxima menor ou igual FC repouso | Não calcular Karvonen |
| FOR-03 | Resposta positiva em qualquer PAR-Q | Alerta de avaliação profissional; não registrar aptidão automática |
| AV-09 | Criar nova avaliação de aluno existente pelo seletor | Mesmo studentId, ID de avaliação diferente |
| ANA-03 | Peso de BIA diferente 5 kg do peso de cadastro | Exibir alerta de conferência sem diagnóstico |
| ANA-04 | Reavaliação 9 kg acima da semana anterior | Alerta de possível digitação/medição |
| COB-06 | Marcar cobrança como cortesia | Não somar em recebido nem em pendente |
| LEG-04 | Importar JSON com dois homônimos mas `cliente_id` distinto | Históricos não são mesclados |
| LEG-05 | Importar cobrança com `status=pendente`, `subtotal` e `valorDesconto` | Preservar desconto e receita líquida, sem marcar como pago |
| LEG-06 | Importar backup com data brasileira válida dd/MM/yyyy | Normalizar ISO sem assumir data de hoje |
| SEC-01 | Selecionar arquivo > limite de tamanho | Recusar sem carregar tudo na memória |
| SEC-02 | `.efdevice` adulterado, com tipo não suportado | Importação inteira cancelada, sem alterações |
| UI-02 | Ajuda `?` em 320dp, em cada módulo | Área de toque quadrada de 44dp e diálogo legível |

**Estado:** casos de teste em dispositivo ainda pendentes, não marcados como aprovados.

## Casos adicionais 0.4.0

| ID | Ação | Resultado esperado |
|---|---|---|
| AUD-01 | Rodar auditoria em base limpa | Contagem correta, sem inconsistências, sem modificar dados |
| AUD-02 | Dados financeiros com desconto maior que valor, parcelas fora da faixa | Alertas separados, nenhum ajuste automático |
| AUD-03 | Compromisso com data/hora inválidas | Alerta local em Configurações |
| AUD-04 | Duas avaliações do mesmo aluno com mudança abrupta de peso | Alerta de conferência, sem diagnóstico |
| DB-07 | Importar novamente backup portátil idêntico | 0 itens novos, dados preexistentes inalterados |
| DB-08 | Backup com ID já existente porém conteúdo divergente | Nenhum item importado; conflito apresentado |
| DB-09 | Backup com 1 item repetido e 1 novo | Item novo importado, repetido idêntico ignorado |
| NAV-02 | Alterar campos e usar Voltar gestual | Pergunta se quer descartar alterações, permite continuar edição |
| NAV-03 | Fechar diálogo e salvar | Alterações não se perdem antes de salvar |

**Estado:** testes puramente matemáticos/de validação concluídos; casos que exigem Android/Keystore/PDF ainda não executados.
