# Qualidade e relatório E0–E9

**Não declarar 100% pronto:** Docker build/run continua pendente. Este relatório distingue implementação, execução e inspeção estática.

## Auditoria independente do backend

Auditor separado, somente leitura, examinou o backend congelado após a suíte final. Resultado comunicado em19/10/2026: **nenhum defeito objetivo bloqueante**. Confirmou 29 operações, seis tabelas e os 24 cenários substantivos entre 52 testes: rollback por CHECK real, duas threads com barreira e reinício de contextos H2. XML conferido:365/381linhas e67/84ramificações.

Limites não bloqueantes identificados: sem regressão específica de JWT expirado, isolamento entre dois alunos e corridas cancelamento×edição/exclusão. A propriedade de `/me` e o lock compartilhado foram considerados corretos por inspeção estática, não por esses testes dinâmicos ausentes. Percentuais de cobertura não significam prova de segurança completa. A auditoria não alterou fontes nem repetiu a suíte.

## Relatório por incremento

### Auditoria independente do frontend e Dockerfile

Auditor separado, somente leitura, conferiu as fontes congeladas de UI/Dockerfile e o relatório Playwright:7 expected,0 unexpected,0 skipped,0 flaky. Nenhum defeito objetivo bloqueante foi identificado. A inspeção não executou Docker e não equivale a aprovação de build/run da imagem.

Lacunas não bloqueantes preservadas: a jornada 04 não confirma o conjunto final de IDs de cursos retornado;05 não confere o curso após PUT;06 não testa diretamente 403 para curso alheio (backend c21 cobre essa negativa). São limites da prova dinâmica de UI, não funcionalidades declaradas defeituosas. Não se alteraram fontes/testes para ocultar essas lacunas. O responsável principal também abriu as capturas dashboarddesktop/mobile e não identificou defeito de layout.

| Etapa | Implementação / decisão | Verificação e limite |
|---|---|---|
| E0 | Java 21, Wrapper e Boot 3.5.16, projeto novo | Compile real; JAR iniciado. Ferramentas portáteis oficiais com checksum, sem instalação global. |
| E1 | Entidades, H2, seis tabelas e seed controlado | Constraints/UNIQUE e reinício de contextos reais; H2 é volátil. |
| E2 | CRUD aluno/professor, DTOs e exclusão lógica | Integrações e 18 unitários Services; sem interface/abstração extra. |
| E3 | JWT15 min, estado atual da conta, cadastro e aprovação | Falha de banco causa rollback; aprovação concorrente não duplica. |
| E4 | Conjunto de cursos atômico e acesso por vínculo | Mínimo 1, IDs inválidos/duplicados/inativos e professor em curso alheio testados. |
| E5 | Matrícula única, alteração/cancelamento e dashboard | EstadoAPROVADO preservado; dados reais após mutações. |
| E6 | ProblemDetail, Swagger e JaCoCo |52 aprovados,0falhas/erros/ignorados;95,80%linhas/79,76%ramos. [Evidências](evidencias-backend.md). |
| E7 | React/TS/Vite com API real, portais e CSS responsivo | `npm ci` e build aprovados. Jornadas, imagens e contagens finais em [evidênciasfrontend](evidencias-frontend.md), não presumidas aqui. |
| E8 | Dockerfile multi-stage, README e documentação | Docker indisponível: **não executado**. Smoke/helper com resultado separado em [registro](resultado-smoke.json). |
| E9 | Auditoria independente backend, rastreabilidade e roteiros | Publicação/repositório/vídeos **não realizados**. Roteiros preparados e próximos critérios explícitos. |

## Reprodução e fronteira de rollback

Comandos, logs e percentuais backend: [evidênciasbackend](evidencias-backend.md). Instalação/build/teste/audit npm e inspeção visual: [evidênciasfrontend](evidencias-frontend.md). O incremento frontend pode ser removido como unidade `frontend/` sem alterar o backend; docs/README/Dockerfile são a unidade de documentação/empacotamento correspondente. O histórico recebeu apenas atualização datada e apontamentos vigentes, sem apagar alternativas anteriores. Nada em `sources/` foi alterado.

Não há commits ou publicação nesta entrega. Os testes automatizados não substituem validação do avaliador, testes Docker nem revisão de requisitos de produção.
