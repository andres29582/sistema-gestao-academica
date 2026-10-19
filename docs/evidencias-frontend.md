# Evidências executadas — frontend E7 e integração final

**Sete jornadas reais aprovadas, zero falhas, ignorados ou instáveis.** Instalação reproduzível, build e auditoria npm também concluídos. Nenhum endpoint foi simulado. Docker permanece não executado; não declarar o projeto 100% pronto.

## Ambiente e comandos

Windows x 64; Node 24.14.0, npm 11.9.0. Versões consultadas no registro oficial npm e fixadas no `package.json`/`package-lock.json`: React/ReactDOM19.3.1, Vite 8.3.4, TypeScript 7.0.2, plugin React 6.1.2, tipos React 19.3.0, Playwright 1.64.1. Apenas 27 pacotes instalados nesta plataforma; nenhum framework de estado/design foi acrescentado.

Comandos em `frontend/`:

```sh
npm ci --no-audit --no-fund
npm run build
npm test
npm audit --json
```

O primeiro build detectou falta da referência de tipos do Vite para o importCSS; `src/vite-env.d.ts` corrigiu a causa. O build final TypeScript+Vite aprovou 20 módulos em 2,59 s; JavaScript 243,04 kB (gzip 75,32 kB), CSS11,17 kB. Instalação limpa pelo lockfile foi repetida com `npm ci`, concluída em 59 s e seguida de build aprovado. Não se confundem tempos de build com benchmark da aplicação.

Logs: `docs/e7-npm-ci.log`, `e7-build-final.log`, `e7-testes-ui.log`; resumo persistido em [resultados-frontend.json](resultados-frontend.json). Logs são arquivos locais ignorados pelo Git; os resumos JSON e capturas preservam a evidência essencial.

## Jornadas em navegador contra API real

`frontend/tests/fluxos.spec.ts`, Chromium já existente no cache; nenhum navegador foi instalado. Playwright iniciou o JAR final Java 21 em 8080 e Vite em 5173. Um helper esperou catálogo demonstrativo completo e login administrativo antes de liberar a suíte. Foram usadas portas livres e dados exclusivamente fictícios.

| Jornada | Comportamento verificado | Tempo |
|---|---|---|
|01 | LoginADMIN, indicadores reais, captura desktop/mobile, storage vazio e reload encerra sessão |31,0 s |
|02 | Cadastro com curso→PENDENTE sem edição→aprovaçãoADMIN→APROVADO/ATIVA e edição pessoal |36,9 s |
|03 | RejeiçãoADMIN e consulta própria REJEITADO sem edição |16,4 s |
|04 | Criar professor com dois cursos, editar conjunto/nome e desativar |18,7 s |
|05 | Exclusão de aluno ativo recebe erro de matrícula, preserva registro; troca curso, cancela, aluno consulta sem edição, exclusão posterior permitida |27,9 s |
|06 | Professor consulta próprios cursos/alunos e acessa perfil, sem menuADMIN |11,5 s |
|07 | ADMIN desativa conta em outra sessão; token anterior não permite salvar e portal explica encerramento |27,3 s |

Resultado final do runner: **7 passed,0 unexpected,0 skipped,0 flaky**, sem erros de execução registrados; duração total 463,176 s (7,7 min), incluindo preparação/encerramento de servidores. Todas as jornadas verificaram lista vazia de exceções JavaScript `pageerror`. RespostasHTTP inválidas esperadas fazem parte dos testes, não são tratadas como sucesso. Aviso do runner sobre `NO_COLOR/FORCE_COLOR` foi apenas de apresentação de log.

As jornadas são sequenciais e compartilham a demonstração:02 cria o aluno usado em 07;05cancela/exclui o aluno inicial. Execute a suíte inteira em banco recém-iniciado, não um teste isolado sem preparar seus dados. Relatório completo gerado em `frontend/playwright-report/index.html` e JSON em `frontend/test-results/resultados.json`; resumos compactos persistidos em docs. Não há medição de cobertura frontend nem alegação de equivalência entre sete jornadas e todas as combinações de uso.

## Inspeção visual realizada

As três imagens foram abertas com `view_image` e inspecionadas nesta entrega:

- [Login1440px](imagens/login.png): formulário legível, hierarquia clara e aviso de privacidade; sem corte de campos.
- [Dashboard1440px](imagens/dashboard.png): sidebar, cartões reais1/1/1/2/3 e tabela coerentes com seed; sem sobreposição.
- [Dashboard390px](imagens/dashboard-mobile.png): cartões em duas colunas, navegação e tabela com rolagem horizontal deliberada; não há corte de conteúdo no corpo principal.

Labels, foco visível, cabeçalhos de tabela, diálogos nativos, confirmações de exclusão/cancelamento e estados de erro/carregamento/desabilitado estão implementados. Isso é QA visual e acessibilidade básica, não auditoria WCAG completa. A captura contém apenas dados demo. Não foram criadas imagens de mockup para simular dados persistidos.

## Segurança de dependências e limites

`npm audit --json` em19/10/2026 retornou exit 0 e **zero vulnerabilidades conhecidas** em todas as severidades no lockfile atual. [Resposta preservada](e7-npm-audit.json). Nenhuma atualização invasiva foi feita para alterar o resultado. Auditoria de dependências não certifica o código, bibliotecas futuras ou segurança integral.

JWT é mantido só em variável de memória do módulo `api.ts`, não localStorage/sessionStorage/cookie. Resposta 401 protegida limpa sessão e avisa; permissão real permanece na API. A edição de professor mescla catálogo ativo com cursos antigos atribuídos, inclusive inativos, sem oferecer novos vínculos inativos. CatálogoCRUD, rematrícula e áreas acadêmicas extras não foram acrescentados.

## Smoke complementar do JAR

A única nova execução robusta do helper terminou em19/10/2026 às 19:22:04 UTC com **duas rodadas aprovadas**. Confirmou login, dashboard protegido, Swagger/Bearer, validação400/`DADOS_INVALIDOS`, cadastro 201 e perda desse cadastro após encerrar/reiniciar o processo. A segunda rodada voltou a cinco alunos demonstrativos. O helper captura status/código/campos sem tokens em `backend/target/execucao/validacao-1.json` e `validacao-2.json`. [Resumo persistido](resultado-smoke.json).

O smoke parcial anterior não foi apagado do relato: foi corrigida apenas a observação do helper, sem alterar fontes de produção. Ele agora valida contrato estável, não regex de texto de resposta. Nenhuma suíte Java foi repetida por mudanças de frontend/docs/helper. Os processos iniciados para UI/smoke foram encerrados; não foi mantida demonstração em execução.
