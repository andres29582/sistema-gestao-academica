# Sistema de Gestão Acadêmica — Beta 1.0

Prova técnica CRUD: Java21/Spring Boot3.5.16, Maven Wrapper, JPA/H2 em memória e frontend React/TypeScript/Vite. Cadastro público solicita curso; ADMIN aprova e gera uma matrícula única. O projeto é demonstração local, não um serviço pronto para produção.

## Executar

Requisitos: **JDK21**, `JAVA_HOME` válido, **Node24** com npm11 e acesso à internet no primeiro build. Maven global não é necessário. As ferramentas portáteis usadas nesta máquina estão em `.ferramentas/`, não fazem parte da entrega versionada.

Windows PowerShell, na raiz deste projeto:

```powershell
$env:JAVA_HOME='C:\caminho\para\jdk-21'
cd backend
.\mvnw.cmd verify
& "$env:JAVA_HOME\bin\java.exe" -jar target\gestao-academica-1.0.0-beta.1.jar
```

Linux/macOS:

```sh
export JAVA_HOME=/caminho/para/jdk-21
cd backend
sh mvnw verify
"$JAVA_HOME/bin/java" -jar target/gestao-academica-1.0.0-beta.1.jar
```

Em outro terminal, a partir da raiz:

```sh
cd frontend
npm ci
npm run dev
```

Portal: **http://127.0.0.1:5173**. API: http://127.0.0.1:8080. [Swagger](http://127.0.0.1:8080/swagger-ui/index.html). O Vite encaminha `/api` para o backend local; não há CORS aberto. `npm run build` verifica TypeScript e gera `frontend/dist/`. `vite preview` apenas pré-visualiza arquivos; para API integrada use `npm run dev` ou configure proxy de implantação. `Ctrl+C` encerra cada servidor.

**H2 é volátil:** encerrar o processo descarta alterações. A próxima execução recria esquema e dados demo. Não há banco externo, Flyway ou recuperação dos dados. JWT dura15min, fica somente na memória do navegador e recarregar a página exige novo login.

## Contas demonstrativas

Senha de todas: `Demo@12345`. Somente dados fictícios; não usar dados reais nem expor estas credenciais na internet.

| E-mail | Estado |
|---|---|
| admin@demo.local | Administração |
| professor@demo.local | Professor habilitado, Programação e Matemática |
| professor.inativo@demo.local | Conta desativada, login bloqueado |
| pendente@demo.local | Solicitação pendente |
| aluno@demo.local | Aprovado, matrícula ativa |
| cancelado@demo.local | Aprovado, matrícula cancelada, consulta sem edição |
| rejeitado@demo.local | Solicitação rejeitada, consulta sem edição |
| inativo@demo.local | Aluno desativado, login bloqueado |

ADMIN gerencia solicitações, alunos, professores, vínculos e matrículas. PROFESSOR consulta apenas seus cursos/alunos autorizados. ALUNO acompanha sua situação; edita dados apenas aprovado e com matrícula ativa. Cancelamento mantém APROVADO e impede rematrícula. Exclusão é lógica, bloqueada se a matrícula estiver ativa.

## Testar e verificar

- Backend: `cd backend` e `mvnw.cmd verify` (Windows) ou `sh mvnw verify` (Linux). Relatórios Surefire e JaCoCo em `target/`. [Resultados reais:52 testes](docs/evidencias-backend.md).
- UI real: com portas8080/5173/18081 livres, `cd frontend` e `npm test`. O teste inicia JAR e Vite e usa API real; compile o JAR antes. Navegador Chromium/Edge existente: configure `PLAYWRIGHT_CHROMIUM_EXECUTABLE` com caminho completo. O padrão é o cache Chromium desta máquina, ajustável no ambiente. Se não houver navegador compatível, instale o Chromium do Playwright por sua conta (`npx playwright install chromium`) e informe seu executável. [Evidências UI](docs/evidencias-frontend.md).
- Smoke JAR: PowerShell7, `backend/scripts/verificar-execucao.ps1 -JavaHome <JDK21>`. Ele inicia duas vezes em18080 e encerra seus processos. [Resultado de execução](docs/resultado-smoke.json).
- Durante esta entrega, Mockito precisou de execução autorizada no host: o sandbox Windows impedia o attach do ByteBuddy. Isso é limite do ambiente, não aprovação implícita de teste interrompido.

## Docker — preparado, NÃO testado

Docker não está disponível neste ambiente; o critério build/run continua **pendente**. Dockerfile multi-stage usa imagens oficiais Maven/JDK21 e TemurinJRE21; não-root no runtime. A etapa de imagem empacota sem repetir testes; execute `verify` antes.

```sh
docker build -t academico-beta ./backend
docker run --rm --name academico-beta -p 127.0.0.1:8080:8080 academico-beta
docker stop academico-beta
```

O container só serve backend/Swagger. Frontend segue separado. H2 continua efêmero; não montar volume como promessa de persistência. Variáveis opcionais: `APP_DEMO=false` desliga seed; `APP_JWT_SEGREDO` exige Base64 de pelo menos32bytes aleatórios, nunca no código. Sem segredo configurado, uma chave aleatória é gerada por execução. O container habilita bind0.0.0.0 internamente e o comando acima limita exposição ao host local.

## Limites e documentação

Sem notas, frequência, calendário, materiais, CRUD de cursos, rematrícula, recuperação de senha ou módulo de exclusão LGPD. O aviso simples não certifica conformidade. Spring Boot3.5 fora de suporte OSS é risco explícito para evolução/exposição. Desempenho, auditoria WCAG completa e Docker não foram comprovados.

[Índice técnico e produto](docs/README.md) · [Decisões e limites](docs/decisoes.md) · [Status da entrega pública](docs/entrega.md).

Nota: o histórico de desenvolvimento permanece fora deste pacote. Esta entrega contém o necessário para executar a aplicação e consultar sua documentação operacional.