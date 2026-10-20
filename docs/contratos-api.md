# Contratos REST — Beta 1.0

API local: `http://127.0.0.1:8080`. Swagger: `/swagger-ui/index.html`; especificação: `/v3/api-docs`.

## Autenticar e usar

1. `POST /api/auth/login` com `{"email":"admin@demo.local","senha":"Demo@12345"}`.
2. Usar `Authorization: Bearer <token>` nas operações protegidas.
3. O token expira em 15 minutos. A API verifica conta habilitada/não excluída e papel atual no banco em cada requisição. O segredo aleatório muda ao reiniciar; não há refresh token.

Essas credenciais são exclusivamente demonstrativas. O cadastro público nunca aceita papel ou situação enviados pelo cliente. JSON com campos desconhecidos é rejeitado com 400.

## Operações

| Perfil | Método e caminho | Corpo | Sucesso |
|---|---|---|---|
| Público | POST `/api/auth/register` | nome, email, telefone, senha, cursoId | 201: AlunoDto PENDENTE, sem matrícula |
| Público | POST `/api/auth/login` | email, senha | 200: token, tipo, expiraEm, usuario |
| Público | GET `/api/cursos` | — | 200: cursos ativos |
| Autenticado | GET `/api/me` | — | 200: PerfilDto |
| Autorizado¹ | PUT `/api/me` | nome, email, telefone | 200: PerfilDto |
| ALUNO | GET `/api/me/solicitacao` | — | 200: AlunoDto próprio |
| ALUNO | GET `/api/me/matricula` | — | 200: MatriculaDto própria; 404 se ainda não existe |
| ADMIN | GET `/api/admin/solicitacoes` | — | 200: alunos pendentes não excluídos |
| ADMIN | GET `/api/admin/solicitacoes/{alunoId}` | — | 200: AlunoDto |
| ADMIN | POST `/api/admin/solicitacoes/{alunoId}/aprovar` | — | 201: MatriculaDto |
| ADMIN | POST `/api/admin/solicitacoes/{alunoId}/rejeitar` | — | 200: AlunoDto |
| ADMIN | GET `/api/admin/dashboard` | — | 200: DashboardDto |
| ADMIN | GET `/api/alunos` | — | 200: AlunoDto[] |
| ADMIN | GET `/api/alunos/{id}` | — | 200: AlunoDto |
| ADMIN | PUT `/api/alunos/{id}` | nome, email, telefone | 200: AlunoDto |
| ADMIN | PATCH `/api/alunos/{id}/status` | ativo: boolean | 200: AlunoDto |
| ADMIN | DELETE `/api/alunos/{id}` | — | 204: exclusão lógica; matrícula ATIVA impede com 409 |
| ADMIN | POST `/api/professores` | nome, email, telefone, senha, cursoIds: number[] | 201: ProfessorDto |
| ADMIN | GET `/api/professores` | — | 200: ProfessorDto[] |
| ADMIN | GET `/api/professores/{id}` | — | 200: ProfessorDto |
| ADMIN | PUT `/api/professores/{id}` | nome, email, telefone; cursoIds opcional | 200: ProfessorDto |
| ADMIN | PATCH `/api/professores/{id}/status` | ativo: boolean | 200: ProfessorDto |
| ADMIN | DELETE `/api/professores/{id}` | — | 204: exclusão lógica |
| PROFESSOR | GET `/api/professores/me/cursos` | — | 200: CursoDto[] atribuídos |
| PROFESSOR | GET `/api/professores/me/cursos/{cursoId}/alunos` | — | 200: AlunoCursoDto[]; curso alheio 403 |
| ADMIN | GET `/api/matriculas` | — | 200: MatriculaDto[] |
| ADMIN | GET `/api/matriculas/{id}` | — | 200: MatriculaDto |
| ADMIN | PUT `/api/matriculas/{id}` | cursoId | 200: MatriculaDto com mesmo ID |
| ADMIN | DELETE `/api/matriculas/{id}` | — | 204: cancelamento, nunca exclusão física |

¹ ADMIN/PROFESSOR habilitados alteram dados pessoais. ALUNO somente se APROVADO e com matrícula ATIVA. PENDENTE, REJEITADO e aprovado com matrícula CANCELADA consultam o próprio perfil/solicitação/matrícula, mas não alteram dados pessoais. Conta desativada ou excluída recebe 401, inclusive com token emitido anteriormente.

## Identificadores e respostas

- `/api/alunos/{id}` usa o ID de Aluno; `/api/professores/{id}` usa o ID de Professor; `usuarioId` identifica a conta. Não são intercambiáveis.
- `CursoDto`: `id,nome,descricao,ativo`.
- `PerfilDto`: `usuarioId,nome,email,telefone,papel,ativo`.
- `AlunoDto`: `id,usuarioId,nome,email,telefone,ativo,cursoSolicitado,situacaoSolicitacao,criadoEm,atualizadoEm`.
- `ProfessorDto`: `id,usuarioId,nome,email,telefone,ativo,cursos,criadoEm`.
- `MatriculaDto`: `id,alunoId,alunoNome,curso,situacao,matriculadoEm,canceladoEm`.
- `AlunoCursoDto`: somente `alunoId,nome,matriculaId`; não expõe contato, conta ou solicitação ao professor.
- `DashboardDto`: `alunosAtivos,professoresAtivos,solicitacoesPendentes,matriculasAtivas,cursosDisponiveis,solicitacoesRecentes`.
- Datas de eventos são ISO 8601 UTC (`Instant`, sufixo `Z`). Nenhuma resposta de entidade ou hash de senha.
- Listas operacionais omitem excluídos. Contas inativas permanecem visíveis à administração. Professor vê alunos habilitados com matrícula ATIVA nos próprios cursos. Sem paginação nesta demonstração de pequena escala.

## Regras de escrita

- `email`: normalizado com trim/lowercase; UNIQUE global, incluindo contas excluídas. Nome obrigatório até 120 caracteres, e-mail válido até 254, telefone obrigatório até 30. Senha: mínimo 8 caracteres Unicode e máximo 72 bytes UTF-8; BCrypt custo 12.
- Cadastro: curso existente/ativo, criação transacional Usuario+Aluno. Curso solicitado não pode ser alterado pela edição pessoal.
- Aprovar/rejeitar: somente PENDENTE. Aprovar cria matrícula única vitalícia no curso solicitado (que deve continuar ativo). Repetições/estado incompatível: 409.
- `cursoIds` de professor: mínimo um, IDs únicos e existentes. Na edição representa o conjunto final; omitido/null preserva os vínculos. Curso inativo anteriormente atribuído pode permanecer; novo vínculo inativo é rejeitado. Nenhum curso/aluno é apagado ao remover associação.
- Alterar curso da matrícula: somente ATIVA, curso destino existente/ativo; mantém o ID e não altera `cursoSolicitado`.
- Cancelar: somente ATIVA, preenche `canceladoEm`, mantém solicitação APROVADO e conta habilitada como estava. Repetir: 409. Rematrícula não existe.
- Excluir aluno: bloqueado enquanto houver matrícula ATIVA. Depois de cancelar (ou sem matrícula), marca `usuario.excluidoEm` e `ativo=false`; dados e vínculos permanecem. Exclusão não é desativação de conta.
- Alterações acadêmicas que disputam um aluno usam a mesma transação e lock pessimista no Aluno; há também UNIQUE de banco em `matricula.aluno_id`.

## Dashboard

| Indicador | Critério no banco |
|---|---|
| alunosAtivos | APROVADO + matrícula ATIVA + conta ativa/não excluída |
| professoresAtivos | Conta ativa/não excluída |
| solicitacoesPendentes | PENDENTE e não excluído, mesmo se conta desativada |
| matriculasAtivas | Situação ATIVA, independentemente da desativação da conta |
| cursosDisponiveis | Curso ativo |
| solicitacoesRecentes | Últimos cinco alunos não excluídos, todas situações, criadoEm DESC e id DESC |

## Erros

`application/problem+json`, formato ProblemDetail com `type,title,status,detail,instance` quando aplicável, `codigo` estável e `ocorridoEm` UTC. Validação acrescenta `campos`, sem os valores rejeitados.

| HTTP | Exemplos de codigo |
|---|---|
| 400 | DADOS_INVALIDOS, REQUISICAO_INVALIDA, CURSO_INVALIDO, CURSO_INATIVO, CURSOS_DUPLICADOS, CURSOS_OBRIGATORIOS |
| 401 | CREDENCIAIS_INVALIDAS, AUTENTICACAO_NECESSARIA |
| 403 | ACESSO_NEGADO |
| 404 | RECURSO_NAO_ENCONTRADO |
| 409 | EMAIL_JA_CADASTRADO, SOLICITACAO_NAO_PENDENTE, MATRICULA_JA_EXISTE, MATRICULA_ATIVA, MATRICULA_CANCELADA, CURSO_INATIVO, CONFLITO_INTEGRIDADE |

É uma demonstração local: sem limite de tentativas distribuído, refresh token, recuperação de senha ou garantias de conformidade LGPD. Não expor diretamente à internet nem utilizar dados reais.
