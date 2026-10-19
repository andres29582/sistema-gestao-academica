# Rastreabilidade — requisitos, implementação e testes

Os 24 cenários exigidos estão detalhados, com nomes completos, em [evidências backend](evidencias-backend.md#rastreabilidade-dos-24-cenários-exigidos). Não há testes omitidos para obter aprovação. Classes executáveis:

- `backend/src/test/java/br/com/academico/FluxosApiTest.java`: c01–c23 e integrações adicionais, MockMvc/banco real.
- `backend/src/test/java/br/com/academico/ReinicioH2Test.java`: c24, dois contextos reais.
- `backend/src/test/java/br/com/academico/servico/AlunoServicoTest.java`, `ProfessorServicoTest.java`, `MatriculaServicoTest.java`:18 unitários Mockito.
- `frontend/tests/fluxos.spec.ts`: jornadas 01–07 em navegador, API real, sem mocks. Resultados em [evidências UI](evidencias-frontend.md).

| RF | UC | Código principal | Cenários backend / jornadaUI |
|---|---|---|---|
| RF-001 |001 | AlunoServico, Cadastro, Acesso | c01–c03 /02 |
| RF-002 |002 | AutenticacaoServico, seguranca, api.ts | c20,c22 e login adicional /01,06,07 |
| RF-003 |002 | SegurancaConfig, PerfilServico | c19–c22 /02,03,06,07 |
| RF-004 |003 | PerfilServico, PerfilPessoal | c06,c19,c20 /02,03,05,07 |
| RF-005 |004 | PerfilServico, PortalAluno | c19,c20 /02,03 |
| RF-006 |005 | MatriculaServico, PortalAluno | c13,c19 /02,05 |
| RF-007 |006 | AlunoServico, Pessoas | c04,c05,c17 /02,03 |
| RF-008 |007 | AlunoServico.aprovar | c13–c16 /02 |
| RF-009 |008 | AlunoServico.rejeitar | c17 /03 |
| RF-010 |009 | AlunoServico, Pessoas | c04–c08 /05 |
| RF-011 |009 | AlunoServico, Usuario | c22 /07 |
| RF-012 |010 | ProfessorServico, EditarPessoa | c09–c12 /04 |
| RF-013 |010 | ProfessorServico | c20,c22 e unitários /04 |
| RF-014 |011,015 | MatriculaServico, Matriculas | c08,c15,c18,c19 e troca de curso adicional /05 |
| RF-015 |001,007 | CursoRepositorio, Acesso | c01,c11,c13 /02,04,05 |
| RF-016 |012 | DashboardServico, Indicadores | c23 /01 e mutações 02–07 |
| RF-017 |013 | ProfessorServico, PortalProfessor | c21 /06 |
| RF-018 |014 | ProfessorServico, AlunosCurso | c21 /06 |
| RF-019 | transversal | App.tsx, README | Inspeção de aviso + capturas 01 |

## RNF e alcance da evidência

RNF-001/002: camadas e contratos inspecionados;29 operações. RNF-003/004/005: autorização e minimização testadas, BCrypt12/UTF8 e JWT atual no banco; sem auditoria de segurança completa. RNF-006: CHECK real força rollback e threads exercitam concorrência de aprovação. RNF-007: c24 e smoke registrado separadamente. RNF-008/009: validação/ProblemDetail e 52 testes com percentuais medidos. RNF-010/011: Wrapper, lockfile e documentação de reprodução; somente ambiente local desta entrega executado. RNF-012: labels, foco, diálogos, tabelas e larguras desktop/mobile; sem auditoria WCAG completa. RNF-013: benchmark não executado. RNF-014: logs técnicos sem credenciais nos artefatos de evidência; não é observabilidade de produção. RNF-015: Dockerfile escrito, **Docker não executado**.

Testes não cobrem exaustivamente todas as combinações: não há regressão específica para JWT expirado, dois alunos isolados ou corridas cancelamento×edição/exclusão. A auditoria independente encontrou a autorização/lock corretos estaticamente, mas isso não equivale a prova dinâmica dessas combinações.
