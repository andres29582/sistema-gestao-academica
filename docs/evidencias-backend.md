# Evidências executadas — backend E0–E6

**Backend implementado e suíte final aprovada:52 testes, zero falhas, erros ou ignorados. Smoke complementar do JAR aprovado em duas rodadas.** A tentativa parcial anterior foi preservada abaixo como histórico, não resultado vigente. A integração posterior também aprovou sete jornadas de frontend; Docker continua não executado e o projeto completo ainda não deve ser considerado 100% pronto.

## Ambiente e reprodução

- Windows x 64; JDK Microsoft OpenJDK **21.0.12.1+1 LTS**, portátil no projeto. O Java do PATH original era um shim Oracle que não respondia; não foi reutilizado.
- Maven Wrapper **3.3.4**, distribuição **Maven 3.10.0**, scripts oficiais `backend/mvnw` e `backend/mvnw.cmd`.
- Spring Boot **3.5.16**, Spring MVC/Data JPA/Validation/Security, H2 **2.3.232**, springdoc **2.9.1**, JUnit 5, Mockito, MockMvc e JaCoCo **0.8.14**. Versões transitivas finais estão no POM efetivo/dependências e nos relatórios de teste.
- `.ferramentas/` e `.m2/` são locais, ignorados pelo Git. Nenhuma instalação global, Docker, publicação, commit ou push foi realizado.

Comandos usados, a partir de `backend/` (caminhos portáteis locais configurados em `JAVA_HOME` e `MAVEN_USER_HOME`):

```powershell
.\mvnw.cmd -B -ntp "-Dmaven.repo.local=<raiz-do-projeto>\.m2\repository" -DskipTests compile
.\mvnw.cmd -B -ntp "-Dmaven.repo.local=<raiz-do-projeto>\.m2\repository" verify
.\mvnw.cmd -B -ntp "-Dmaven.repo.local=<raiz-do-projeto>\.m2\repository" clean verify
.\mvnw.cmd -o -B -ntp "-Dmaven.repo.local=<raiz-do-projeto>\.m2\repository" verify
```

O primeiro comando de compilação concluiu com **BUILD SUCCESS**, compilando 36 arquivos Java com `release 21`. Log: [e0-compile.log](e0-compile.log). Dependências exigiram acesso de rede autorizado após falha DNS no sandbox.

## Resultado por etapa

| Etapa | Arquivos principais | Comportamento implementado e evidência |
|---|---|---|
| E0 | `backend/pom.xml`, Wrapper, `Aplicacao.java`, `application.yml` | Compilação real aprovada; JAR iniciou em Java 21 e respondeu catálogo, login, dashboard e Swagger. Smoke completo pendente; detalhes abaixo. |
| E1 | `dominio/`, `repositorio/`, `config/DadosDemonstrativos.java` | Seis tabelas, IDENTITY, enum STRING, N:N e UNIQUE real; testes de esquema, constraint e reinício H2 executados. |
| E2 | `AlunoServico`, `ProfessorServico`, controllers e DTOs | CRUD real; exclusão lógica e desativação distintas; unitários e integração correspondentes. |
| E3 | `seguranca/`, `AutenticacaoServico`, `PerfilServico`, aprovação/rejeição | JWT com conta/papel atual no banco; transação e lock por aluno; falhas reais de banco e concorrência verificadas. |
| E4 | `ProfessorServico`, `professor_curso` | Curso mínimo, conjunto completo, omissão preserva, validação prévia, transação e autorização por vínculo. |
| E5 | `MatriculaServico`, `DashboardServico` | Atualização preserva matrícula/curso solicitado; cancelamento preserva APROVADO; dashboard obtido do banco. |
| E6 | `src/test/`, configuração JaCoCo, tratamento ProblemDetail e Swagger | Suíte final: **52 aprovados, 0 falhas, 0 erros, 0 ignorados**, BUILD SUCCESS. |

## Resultado final de testes e cobertura

A execução final terminou em **19/10/2026 às 15:28:05 (America/Sao_Paulo)**, em **7 min 32 s**, com o candidato que inclui mensagens pt-BR e cronologia corrigida da carga demonstrativa. São **33 testes API/DB MockMvc + 1 reinício real de contextos + 18 unitários Mockito**. Log integral: [e6-testes-final.log](e6-testes-final.log). Resumo extraído dos XMLs Surefire/JaCoCo: [resultados-backend.json](resultados-backend.json).

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

### O que torna as provas reais

- **Rollback:** constraints CHECK temporárias no H2 fazem inserções reais falharem em matrícula, aluno e professor. As consultas posteriores conferem ausência de efeitos parciais; não se usa Mockito para alegar rollback de banco.
- **Concorrência:** duas tarefas em executor de duas threads, com barreira de início (`CountDownLatch`), chamam o Service transacional real. Esperam-se uma criação e um conflito, com exatamente uma linha de matrícula. UNIQUE também é exercitado por uma segunda inserção SQL direta.
- **Reinício:** encerra-se um contexto real Spring/Hikari/H2 e cria-se outro; os dados temporários desaparecem e a carga demonstrativa retorna. O smoke do JAR complementa com reinício de processo.
- **Cobertura:** o agente mede apenas `br.com.academico.*` para não instrumentar bibliotecas/frameworks. Todas as classes de produção continuam no relatório; nenhum teste/cenário foi removido e a medição não acumula execuções anteriores (`append=false`).
- **Criptografia:** cadastro real e carga demonstrativa usam BCrypt custo 12; o teste c01 verifica o prefixo do hash produzido. Somente as contas ADMIN/PROFESSOR criadas diretamente como fixture do teste usam custo 4, evitando repetir trabalho criptográfico caro que não é o objeto de cada cenário. Os testes não substituem o PasswordEncoder de produção.

## Execução real do JAR — histórico da tentativa parcial

Comando executado a partir de `backend/`, com PowerShell 7 e permissão de host autorizada:

```powershell
.\scripts\verificar-execucao.ps1 -JavaHome "<raiz-do-projeto>\.ferramentas\java\jdk-21.0.12.1+1" -Porta 18080
```

A repetição final de 19/10/2026 iniciou o JAR, não um contexto MockMvc. O log registra Java 21, Tomcat na porta 18080 e inicialização do Spring. O helper confirmou, em sequência:

| Verificação | Resultado observado |
|---|---|
| `GET /api/cursos` | Três cursos ativos da demonstração |
| `POST /api/auth/login` | HTTP200, token não vazio, conta admin demonstrativa |
| `GET /api/admin/dashboard` com token | Alunos ativos 1 e matrículas ativas 2 |
| `GET /v3/api-docs` | Documento com esquema Bearer |
| `GET /swagger-ui/index.html` | HTTP200 |
| Cadastro inválido `{}` | Asserção combinada de HTTP400 e texto pt-BR falhou; status/corpo não foram preservados pelo helper |
| Cadastro efêmero e reinício de processo | Não alcançados; **não verificados pelo smoke** |

A falha foi `Validação em português brasileiro não conferida.`, linha 52 do helper. Ela não permite distinguir uma divergência de status, texto ou leitura da resposta sem capturar o corpo; não se presume defeito de produção nem aprovação dessa checagem. O teste API/DB c02, executado na suíte final válida, conferiu HTTP400 e mensagem contendo `branco`; isso não substitui a verificação que faltou no processo empacotado. O reinício H2 de dois contextos reais está aprovado em c24, mas o reinício entre dois processos do JAR permanece pendente.

O primeiro smoke havia consultado antes de a transação do `ApplicationRunner` demonstrativo terminar. O helper passou a aguardar, por até 180 s após o primeiroHTTP, três cursos e login admin 200. A única repetição autorizada alcançou essa prontidão; não houve nova alteração de código de produção nem nova suíte após o resultado final. Evidências: `backend/target/execucao/aplicacao-1.log`, `backend/target/execucao/erro-1.log` (vazio), [registro do smoke](resultado-smoke.json). Não existe relatório `APROVADO` de duas rodadas para esta execução. O processo iniciado pelo helper foi encerrado no bloco `finally`; consulta posterior de processos Java do projeto e listeners 18080 não retornou nenhum. Nenhum servidor foi mantido para o frontend.

## Correções e limites observados

**Atualização posterior:** o helper foi corrigido para capturar a respostaJSON/status, sem credenciais, e verificar HTTP400/`DADOS_INVALIDOS`, em vez de regex frágil sobre texto. A única nova execução robusta aprovou duas rodadas em19/10/2026 às 19:22:04 UTC: cadastro 201, processo encerrado/reiniciado e dado efêmero ausente com seed restaurado. [Resultado vigente](resultado-smoke.json) e [evidências de integração](evidencias-frontend.md). Código de produção e suíte Java final permaneceram inalterados.

1. O comando inicial de geração do Wrapper usou um argumento PowerShell sem aspas e recebeu a fase inválida `.10.0`; os scripts oficiais foram gerados, e a URL foi corrigida para o Maven 3.10.0 com SHA-256 fixado. O Wrapper já executou a compilação real com sucesso.
2. A primeira suíte detectou HTTP 401, em vez de 400, para login com senha multibyte acima de 72 bytes. A validação do login foi alinhada à do cadastro, mantendo o teste e o HTTP 401 para credenciais bem formadas incorretas. Log sanitizado: [e6-primeira-execucao.log](e6-primeira-execucao.log).
3. A matriz conservadora permite consulta própria para aluno pendente/rejeitado/cancelado, mas edição pessoal só para aprovado com matrícula ativa. Está explicitada em [contratos-api.md](contratos-api.md).
4. Spring Boot 3.5.x foi mantido conforme o pedido; o fim de suporte OSS dessa linha é risco para exposição/uso além da prova técnica. A versão beta não equivale a plataforma pronta para produção.
5. H2 é volátil; não há PostgreSQL, Flyway, backup, rematrícula, catálogo editável, notas, frequência ou módulo de exclusão LGPD.
6. Foi corrigida a ordem do seed cancelado: primeiro persistir a matrícula, depois cancelar, garantindo `canceladoEm >= matriculadoEm`; o teste de reinício consulta e comprova essa relação no banco real.
7. A tentativa de revalidação no sandbox ficou parada no attach da JVM: foi observado um processo `net.bytebuddy.agent.Attacher` filho do Surefire, sem execução dos cenários. Foi interrompida e **não conta como aprovação**. A mesma suíte final, executada com as permissões de host autorizadas, concluiu com sucesso. Logs: [tentativa restrita](e6-sandbox-interrompido.log) e [execução final válida](e6-testes-final.log). Um bloqueio temporário do arquivo de log anterior também impediu um lançamento; a execução válida usou outro log, após confirmar ausência de Maven duplicado.

## Fontes das ferramentas e versões

- [Microsoft Build of OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download): ZIP21.0.12.1 Windows x 64 verificado com SHA-256 oficial `192441a9d27da813bada974bb88b4cf64d37a9589ed37f204374d411ca5ce07f`.
- [Apache Maven](https://maven.apache.org/download.cgi): distribuição 3.10.0 verificada com SHA-512 oficial `22d31676d5b92ed53308c19ecded3245d0efd0cddfec51ec7bab62f4f13ad70ddddceff8e62940e0e8e2421f3cde8106737f4becec0aa034ffeff2a754e88337`; SHA-256 fixado no Wrapper `1f6d9909266510f039f59aa0e13dcd2c66da85f043e56276e41f2918f8bddaff`.
- [Maven Wrapper oficial](https://maven.apache.org/tools/wrapper/index.html): scripts only-script 3.3.4, distribuição oficial no Maven Central.
- [Spring Boot 3.5.16](https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/) e [requisitos3.5](https://docs.spring.io/spring-boot/3.5/system-requirements.html).
- [springdoc2.9.1 POM oficial](https://github.com/springdoc/springdoc-openapi/blob/v2.9.1/pom.xml).
- [JaCoCo: opções do agente](https://www.jacoco.org/jacoco/trunk/doc/agent.html).

## Limite de rollback deste incremento

O backend foi criado em diretório novo, sem código anterior sobrescrito. O limite do incremento E0–E6 é `backend/` e os documentos de contratos/evidências criados em `docs/`; fontes sincronizadas e histórico documental não foram alterados. Nenhuma remoção foi executada. As funcionalidades dependem desse conjunto, não de commits separados por tipos de arquivo.

## Próxima etapa

E7 está implementado com sete jornadas reais aprovadas. Documentação, Dockerfile, auditoria independente backend e roteiros E8/E9 foram preparados. Resta executar Docker build/run em ambiente apropriado e, mediante autorização explícita, preparar a publicação e gravações. Benchmark formal e certificações de produção não fazem parte da evidência executada.
