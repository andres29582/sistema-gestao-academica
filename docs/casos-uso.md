# 15 casos de uso — fluxos vigentes

Fluxogramas verticais (`flowchart TD`). Erros seguem [ProblemDetail e códigos REST](contratos-api.md). Conta bloqueada recebe 401; perfil/propriedade incorretos recebem 403. A interface orienta, mas a API é a autoridade das permissões.

## UC-001 — Solicitar cadastro de aluno
Visitante informa nome, e-mail, telefone, senha e curso ativo. A transação cria Usuario ALUNO e Aluno PENDENTE, sem matrícula. Campos inválidos 400, e-mail duplicado 409, sem efeitos parciais.
```mermaid
flowchart TD
 A[Preencher cadastro e escolher curso] --> B{Dados e curso válidos?}
 B -- Não --> C[Retornar400]
 B -- Sim --> D{E-mail disponível?}
 D -- Não --> E[Retornar409]
 D -- Sim --> F[Criar Usuario e Aluno PENDENTE na transação]
 F --> G[Retornar201 sem matrícula]
```
## UC-002 — Autenticar usuário
Qualquer usuário com credenciais válidas e conta habilitada recebe JWT de 15 min. Credenciais incorretas, conta excluída/desativada401; formato inválido 400. Papel não vem do cliente. Requisições posteriores revalidam conta e papel no banco.
```mermaid
flowchart TD
 A[Enviar e-mail e senha] --> B{Formato válido?}
 B -- Não --> C[400]
 B -- Sim --> D{Credenciais corretas e conta habilitada?}
 D -- Não --> E[401 sem revelar conta]
 D -- Sim --> F[Emitir JWT de15min]
 F --> G[Guardar somente na memória do portal]
```
## UC-003 — Atualizar dados pessoais
Usuário altera nome, e-mail e telefone próprios. ADMIN/PROFESSOR habilitados podem editar; ALUNO somente APROVADO com matrícula ATIVA. Papel/situação não são campos aceitos. E-mail duplicado 409.
```mermaid
flowchart TD
 A[Solicitar edição própria] --> B{Conta e estado autorizados?}
 B -- Não --> C[401 ou403]
 B -- Sim --> D{Dados válidos e e-mail único?}
 D -- Não --> E[400 ou409 sem alteração]
 D -- Sim --> F[Salvar dados pessoais e retornar200]
```
## UC-004 — Consultar situação da solicitação
ALUNO habilitado lê sua própria solicitação, inclusive PENDENTE/REJEITADO ou aprovado com matrícula cancelada. A consulta não muda estado nem cria matrícula.
```mermaid
flowchart TD
 A[Aluno consulta situação] --> B{Conta habilitada e papel ALUNO?}
 B -- Não --> C[401 ou403]
 B -- Sim --> D[Buscar solicitação do usuário autenticado]
 D --> E[Retornar200 com situação e curso solicitado]
```
## UC-005 — Consultar matrícula
ALUNO consulta somente sua matrícula. Ausente 404; ativa/cancelada200 com datas. Cancelamento não bloqueia essa leitura se a conta continuar habilitada.
```mermaid
flowchart TD
 A[Aluno consulta matrícula própria] --> B{Conta e perfil autorizados?}
 B -- Não --> C[401 ou403]
 B -- Sim --> D{Existe matrícula?}
 D -- Não --> E[404 sem criar registro]
 D -- Sim --> F[200 com matrícula e datas]
```
## UC-006 — Analisar solicitações
ADMIN lista pendentes não excluídos e consulta detalhe pelo ID do Aluno. Inatividade da conta não apaga a solicitação. Consultar não aprova/rejeita implicitamente.
```mermaid
flowchart TD
 A[ADMIN abre solicitações] --> B[Consultar pendentes não excluídos]
 B --> C[Selecionar solicitação]
 C --> D{ID existe?}
 D -- Não --> E[404]
 D -- Sim --> F[Exibir detalhe para análise]
```
## UC-007 — Aprovar solicitação e gerar matrícula
ADMIN aprova somente PENDENTE. Mesma transação/lock no Aluno muda APROVADO e insere matrícula ATIVA no curso solicitado ativo. UNIQUE vitalício impede duplicidade; falha causa rollback integral.
```mermaid
flowchart TD
 A[ADMIN confirma aprovação] --> B[Iniciar transação e bloquear Aluno]
 B --> C{PENDENTE sem matrícula e curso ativo?}
 C -- Não --> D[409 sem alteração]
 C -- Sim --> E[Marcar APROVADO e inserir matrícula ATIVA]
 E --> F{Persistência concluiu?}
 F -- Não --> G[Rollback integral]
 F -- Sim --> H[Commit e201 com matrícula]
```
## UC-008 — Rejeitar solicitação
ADMIN rejeita PENDENTE na transação com lock do Aluno. Define REJEITADO sem matrícula. Repetição ou solicitação já decidida 409; ID desconhecido 404.
```mermaid
flowchart TD
 A[ADMIN confirma rejeição] --> B[Transação e lock do Aluno]
 B --> C{Solicitação PENDENTE?}
 C -- Não --> D[409]
 C -- Sim --> E[Definir REJEITADO sem matrícula]
 E --> F[Commit e200]
```
## UC-009 — Gerenciar alunos
ADMIN lê, altera dados pessoais, ativa/desativa e exclui logicamente. Cadastro inicial é UC-001, não outro formulário administrativo. Matrícula ATIVA bloqueia exclusão 409; cancelada/ausente permite preservar registros, marcar exclusão e bloquear conta 204.
```mermaid
flowchart TD
 A[ADMIN seleciona aluno] --> B{Operação?}
 B -- Consultar --> C[200 com DTO]
 B -- Editar ou ativar --> D[Validar e salvar na transação]
 B -- Excluir --> E[Transação e lock do Aluno]
 E --> F{Matrícula ATIVA?}
 F -- Sim --> G[409 e orientar cancelamento prévio]
 F -- Não --> H[Exclusão lógica e conta bloqueada]
 H --> I[204 preservando relacionamentos]
```
## UC-010 — Gerenciar professores
ADMIN cria/consulta/edita/ativa/desativa/exclui logicamente. Cursos mínimos 1, IDs únicos e existentes. Novos vínculos inativos são inválidos; vínculo antigo inativo pode permanecer. PUT com cursoIds define conjunto final; omissão preserva. Professor e associações são atômicos.
```mermaid
flowchart TD
 A[ADMIN envia dados e cursos] --> B{Dados e conjunto de cursos válidos?}
 B -- Não --> C[400 ou409 sem efeito parcial]
 B -- Sim --> D[Persistir professor e vínculos na transação]
 D --> E{Banco concluiu?}
 E -- Não --> F[Rollback]
 E -- Sim --> G[201 ao criar ou200 ao editar]
 G --> H[Exclusão lógica preserva cursos e alunos]
```
## UC-011 — Gerenciar matrículas
ADMIN lista/consulta e altera curso de matrícula ATIVA para outro curso existente/ativo. Preserva ID, curso solicitado e registro único. Matrícula cancelada não pode ser reativada/editada. Cancelamento detalhado em UC-015.
```mermaid
flowchart TD
 A[ADMIN seleciona matrícula] --> B[Consultar DTO]
 B --> C[Enviar novo curso]
 C --> D[Transação e lock do mesmo Aluno]
 D --> E{Matrícula ATIVA e curso válido?}
 E -- Não --> F[400 ou409]
 E -- Sim --> G[Alterar curso sem nova matrícula]
 G --> H[200 com mesmo ID]
```
## UC-012 — Consultar indicadores administrativos
ADMIN consulta contagens reais do banco e últimos 5 cadastros, ordenados por criadoEm/id descendentes. As fórmulas distinguem matrícula ativa de conta habilitada; não há números simulados.
```mermaid
flowchart TD
 A[ADMIN abre dashboard] --> B[Consultar contagens no banco]
 B --> C[Consultar cinco solicitações recentes]
 C --> D[Retornar200 e apresentar cartões e tabela]
```
## UC-013 — Consultar cursos atribuídos ao professor
PROFESSOR habilitado consulta seus próprios vínculos, inclusive curso antigo inativo se ainda atribuído. Não recebe operação de criação/alteração do catálogo ou dos vínculos.
```mermaid
flowchart TD
 A[Professor abre portal] --> B[Identificar professor autenticado]
 B --> C[Consultar vínculos próprios]
 C --> D[200 com cursos atribuídos]
```
## UC-014 — Consultar alunos dos cursos
PROFESSOR consulta curso próprio; alheio 403. Resultado contém apenas alunos habilitados/não excluídos com matrícula ATIVA naquele curso e campos mínimos alunoId/nome/matriculaId.
```mermaid
flowchart TD
 A[Professor seleciona curso] --> B{Curso atribuído ao professor?}
 B -- Não --> C[403]
 B -- Sim --> D[Filtrar matrícula ATIVA e conta habilitada]
 D --> E[200 com dados mínimos dos alunos]
```
## UC-015 — Cancelar matrícula
ADMIN confirma cancelamento de matrícula ATIVA. Mesmo lock/transação do Aluno; preenche canceladoEmUTC, define CANCELADA e preserva APROVADO e registros. Repetição 409; sucesso 204. Consulta própria continua, edição acadêmica/pessoal fica restrita; rematrícula fora do escopo.
```mermaid
flowchart TD
 A[ADMIN confirma cancelamento] --> B[Transação e lock do Aluno]
 B --> C{Matrícula ATIVA?}
 C -- Não --> D[409]
 C -- Sim --> E[Definir CANCELADA e canceladoEm]
 E --> F[Preservar APROVADO e relacionamentos]
 F --> G[Commit e204]
 G --> H[Consulta permanece e edição fica bloqueada]
```
