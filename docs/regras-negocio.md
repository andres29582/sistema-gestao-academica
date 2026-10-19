# Regras, atores e estados — contrato vigente

O domínio tem seis tabelas; ADMIN é papel de Usuario, não entidade separada. [Contrato exato de campos/códigos](contratos-api.md).

| Ator/estado | Pode | Não pode |
|---|---|---|
| Público | Login, cadastroALUNO com curso ativo, catálogo ativo | Escolher papel/situação, criar matrícula |
| ADMIN habilitado | Gestão, aprovar/rejeitar, atribuir cursos, ativar/desativar, exclusão lógica, dashboard | Rematricular, cadastrar curso, alterar papel por payload |
| PROFESSOR habilitado | Perfilpessoal, cursos próprios, alunos habilitados com matrícula ATIVA nesses cursos | Gestãoacadêmica, curso alheio, contato desnecessário dos alunos |
| ALUNO pendente/rejeitado | Perfil/situação próprios; matrícula retorna 404 quando ausente | Editarperfil ou operações acadêmicas |
| ALUNO aprovado+matrícula ATIVA+conta habilitada | Consultar e editar dados pessoais próprios | Editar curso solicitado, matrícula ou situação |
| ALUNO aprovado+matrícula CANCELADA | Consultarpróprioestado/matrícula | Editarperfil, rematricular |
| Conta desativada/excluída | Nenhuma operaçãoautenticada | Login e token anterior recebem 401 |

## Invariantes

1. Cadastro transacional: nome/e-mail/telefone/senha/curso. E-mailtrim/lowercase, único global inclusive excluídos. Curso existente/ativo. UsuarioALUNO+AlunoPENDENTE, sem matrícula.
2. Senha mínimo 8 codepoints e máximo 72 bytesUTF8; BCryptcusto 12. Nunca logar credenciais, token ou hash.
3. Só PENDENTE pode ser aprovado/rejeitado por ADMIN. Aprovar cria matrícula ATIVA no curso solicitado e muda paraAPROVADO atomicamente. Rejeitar geraREJEITADO sem matrícula.
4. MatrículaUNIQUE(aluno_id) durante toda a beta, mesmoCANCELADA. Concorrentes disputam o mesmo lockPESSIMISTIC_WRITE noAluno dentro de transação; constraintsDB são segunda proteção.
5. PUTmatrícula altera apenas curso de matrícula ATIVA, preservaID e curso solicitado. Não cria nova linha.
6. Cancelar matrícula ATIVA mantémAPROVADO, gravaInstantUTCcanceladoEm e preserva histórico. Repetir 409; não há rematrícula.
7. ExcluirAluno com matrícula ATIVA→409. Sem matrícula ou cancelada: usuario.excluidoEm+ativo=false; preserva registros e omite das listas. Desativaçãoativo=false é distinta e reversível pela gestão enquanto não excluído.
8. Professor tem ao menos um curso. IDs únicos/existentes; novos inativos proibidos. PUTcursoIds representa conjuntofinal; omitido/null preserva. Vínculo antigo inativo pode permanecer. Tudo validado antes de persistir atomicamente; retirar vínculo não apaga curso/aluno.
9. Listas omitem excluídos e mostram inativos aoADMIN. Professor recebe sóalunoId/nome/matriculaId, nãoe-mail/telefone.
10. JWT15 min, semrefresh; segredoaleatório por execução (ou externo forte). Toda requisição protegida consulta conta/papel atualDB.401 autenticação/bloqueio;403perfil/propriedade;404ausente;400inválido;409duplicidade/estadoincompatível.

## Dashboard obtido do banco

Alunosativos = APROVADO+matrícula ATIVA+conta ativa/não excluída. Professoresativos = conta ativa/não excluída. Pendentes = PENDENTE/não excluídos, mesmo conta desativada. Matrículasativas contamATIVA independentemente da conta. Cursosdisponíveis sãoativos. Recentes:5 últimosalunosnão excluídos, criadoEmDESC,idDESC. Números da interface nunca são mockados.

## Limites do produto

H2 em memória perde CRUD ao encerrar; seed é recriado. Catálogo sóleitura. Semáreas acadêmicas adicionais, módulo de exclusãoLGPD ou certificação jurídica. Cadastro exibe aviso simples de demonstração/dados fictícios.
