# Testes do backend

## Execução

Em `backend/`, com JDK 21:

```powershell
.\mvnw.cmd verify
```

No Linux ou macOS: `sh mvnw verify`. Relatórios Surefire em `backend/target/surefire-reports/` e JaCoCo em `backend/target/site/jacoco/`.

## Resultado registrado

A execução de 19/10/2026 registrou 52 testes aprovados, sem falhas, erros ou ignorados: 33 testes API/banco, um teste de reinício de contextos H2 e 18 unitários Mockito. [Resumo](resultados-backend.json).

| Medida JaCoCo | Coberto / total | Percentual medido |
|---|---|---|
| Linhas | 365 / 381 | **95,80%** |
| Instruções | 2.510 / 2.607 | **96,28%** |
| Ramificações | 67 / 84 | **79,76%** |
| Métodos | 205 / 214 | **95,79%** |

Não há alegação de 100% de cobertura. As 45 classes analisadas incluem todo o código de produção do projeto, sem excluir DTOs, configuração ou entidades do relatório. Relatório HTML reproduzível: `backend/target/site/jacoco/index.html`; dados: `backend/target/site/jacoco/jacoco.xml`, `jacoco.csv` e `backend/target/surefire-reports/TEST-*.xml`.

JAR executável final: `backend/target/gestao-academica-1.0.0-beta.1.jar`. Os arquivos de `target/` são gerados e não precisam ser versionados.

## Rastreabilidade dos 24 cenários exigidos

Os métodos `c01` a `c23` estão em `FluxosApiTest`; `c24` está em `ReinicioH2Test`. A suíte também inclui cenários adicionais e 18 unitários de Services com Mockito.

| Nº | Cenário | Método executável |
|---|---|---|
| 1 | Cadastro válido e sem matrícula | c01CadastroValidoCriaSomenteAlunoPendente |
| 2 | Dados inválidos e tentativa de enviar papel | c02RejeitaDadosInvalidosECamposPrivilegiados |
| 3 | E-mail único inclusive excluído | c03EmailDuplicadoInclusiveAposExclusao |
| 4 | Consulta de aluno | c04ConsultaAlunoExistente |
| 5 | ID inexistente e formato inválido | c05IdInexistenteRetorna 404 |
| 6 | Atualização autorizada de aluno | c06AtualizaAlunoSemAlterarCursoOuPapel |
| 7 | Exclusão lógica preserva e omite | c07ExclusaoLogicaPreservaEOmite |
| 8 | Exclusão bloqueada com matrícula ativa | c08ExclusaoBloqueadaComMatriculaAtiva |
| 9 | Professor com vários cursos | c09ProfessorComVariosCursos |
| 10 | Edição do conjunto completo de cursos | c10AtualizaConjuntoCompletoCursosProfessor |
| 11 | Cursos inválidos/duplicados/inativos | c11RejeitaCursosInvalidosDuplicadosInativosSemEfeitoParcial |
| 12 | Professor sem cursos | c12ProfessorSemCursosRetorna 400 |
| 13 | Aprovação e matrícula atômica | c13AprovacaoCriaMatriculaAtomicaNoCursoSolicitado |
| 14 | Falha real na inserção e rollback | c14FalhaRealDeBancoDesfazAprovacao |
| 15 | Segunda matrícula e rematrícula impedidas | c15SegundaAprovacaoOuRematriculaImpossivel |
| 16 | Duas transações simultâneas | c16AprovacoesConcorrentesCriamUmaUnicaMatricula |
| 17 | Rejeição sem matrícula | c17RejeicaoSemMatriculaESemRepeticao |
| 18 | Cancelamento preserva aprovação | c18CancelamentoPreservaAprovadoEPermiteExclusaoPosterior |
| 19 | Cancelado consulta, mas não altera perfil | c19CanceladoConsultaEstadoMasNaoAtualizaPerfil |
| 20 | Perfis e propriedade do recurso | c20ProtecaoDePerfisEPropriedade |
| 21 | Professor não acessa curso alheio | c21ProfessorNaoConsultaCursoAlheioENaoVeDadosExcessivos |
| 22 | Conta desativada e token anterior | c22ContaDesativadaInvalidaTokenAnteriorMasNaoExclui |
| 23 | Dashboard após mutações reais | c23DashboardRefleteMutacoesReais |
| 24 | Dois contextos reais, fechamento e nova carga | c24FecharContextoDescartaDadosEReinicioRecarregaDemonstracao |

## Consistência e limites

Os testes incluem falha real de banco para verificar rollback, aprovação concorrente com duas threads e reinício de contextos H2. O script `backend/scripts/verificar-execucao.ps1` complementa a suíte com execução do JAR e reinício de processo. O [resultado registrado](resultado-smoke.json) apresenta duas rodadas aprovadas, validação de requisições e restauração dos dados demonstrativos.

Não há regressões específicas para JWT expirado, isolamento entre dois alunos ou concorrência entre cancelamento e edição/exclusão. Cobertura não comprova segurança integral. Docker e benchmark de carga não foram validados. Os resultados são registros de uma execução; devem ser reproduzidos após alterações relevantes.
