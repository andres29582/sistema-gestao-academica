# Sistema de Gestão Acadêmica — Beta 1.0

Demonstração CRUD acadêmica para prova técnica, com Java 21, Spring Boot 3.5.16, React, TypeScript e Vite. O cadastro público solicita ingresso em um curso; a administração aprova a solicitação e cria uma matrícula única. Este projeto é para execução local e não está pronto para produção.

## Executar localmente

**Docker não é necessário.** Você precisa de JDK 21 disponível no `PATH` (ou `JAVA_HOME` configurado), Node.js 24 com npm 11 e acesso à internet no primeiro uso do Maven Wrapper e na instalação das dependências do frontend. Não é preciso instalar Maven globalmente.

### 1. Inicie o backend

Abra um terminal PowerShell na pasta do projeto e execute:

```powershell
Set-Location .\backend
.\mvnw.cmd spring-boot:run
```

Mantenha esse terminal aberto. Na primeira execução, o Wrapper pode baixar o Maven e as dependências do backend.

### 2. Inicie o frontend

Abra um segundo terminal PowerShell na pasta do projeto. Na primeira execução, ou quando `package-lock.json` mudar, instale as dependências uma vez:

```powershell
Set-Location .\frontend
npm ci
```

Em seguida — e em cada inicialização futura — inicie o frontend:

```powershell
Set-Location .\frontend
npm run dev
```

Mantenha esse terminal aberto também.

### 3. Acesse a aplicação

- Portal: [http://127.0.0.1:5173](http://127.0.0.1:5173)
- API: [http://127.0.0.1:8080](http://127.0.0.1:8080)
- Swagger: [http://127.0.0.1:8080/swagger-ui/index.html](http://127.0.0.1:8080/swagger-ui/index.html)

O Vite encaminha `/api` ao backend local; a aplicação não abre CORS para outras origens. Entre com a conta demonstrativa de administração: **admin@demo.local** — senha **Demo@12345**. As contas e os dados são fictícios; não use dados reais nem exponha essas credenciais na internet.

Para encerrar, pressione `Ctrl+C` em cada terminal. O H2 é um banco em memória: ao parar o backend, as alterações são descartadas; ao iniciá-lo novamente, os dados demonstrativos são recriados.

### Linux/macOS

No terminal do backend, use `sh mvnw spring-boot:run` dentro de `backend/`. No terminal do frontend, use `npm ci` e `npm run dev` dentro de `frontend/`.

## Contas demonstrativas

Todas usam a senha `Demo@12345` e são destinadas somente à demonstração local.

| E-mail | Estado |
|---|---|
| admin@demo.local | Administração |
| professor@demo.local | Professor habilitado, Programação e Matemática |
| professor.inativo@demo.local | Conta desativada; login bloqueado |
| pendente@demo.local | Solicitação pendente |
| aluno@demo.local | Aprovado, matrícula ativa |
| cancelado@demo.local | Aprovado, matrícula cancelada; consulta sem edição |
| rejeitado@demo.local | Solicitação rejeitada; consulta sem edição |
| inativo@demo.local | Aluno desativado; login bloqueado |

## Funcionalidades e regras

- **ADMIN** gerencia solicitações, alunos, professores, vínculos e matrículas.
- **PROFESSOR** consulta somente os cursos e alunos autorizados.
- **ALUNO** acompanha sua situação e edita os próprios dados apenas quando aprovado e com matrícula ativa.
- O cancelamento mantém a situação de aprovado e impede uma nova matrícula. A exclusão é lógica e é bloqueada enquanto houver matrícula ativa.
- JWT dura 15 minutos, fica somente na memória do navegador e exige novo login após recarregar a página.

## Testes e verificações

- **Backend:** dentro de `backend/`, execute `.\mvnw.cmd verify` no Windows ou `sh mvnw verify` no Linux/macOS. Os relatórios Surefire e JaCoCo ficam em `target/`. [Resultados registrados: 52 testes](docs/evidencias-backend.md).
- **Frontend com API real:** libere as portas 8080, 5173 e 18081; compile o JAR antes e execute `npm test` dentro de `frontend/`. O cenário inicia o JAR e o Vite. `npm run build` verifica o TypeScript e gera `frontend/dist/`; `vite preview` apenas pré-visualiza os arquivos estáticos. Para indicar um navegador já instalado, defina `PLAYWRIGHT_CHROMIUM_EXECUTABLE` com o caminho completo do executável. [Evidências da interface](docs/evidencias-frontend.md).
- **Smoke do JAR:** com PowerShell 7, execute `backend/scripts/verificar-execucao.ps1 -JavaHome <JDK21>`. O roteiro inicia o backend duas vezes na porta 18080 e encerra os processos que iniciou. [Resultado do smoke](docs/resultado-smoke.json).
- Nos testes de backend desta entrega, Mockito precisou de execução autorizada no host porque o sandbox Windows impedia o attach do ByteBuddy. Um teste interrompido não deve ser interpretado como aprovado.

## Docker — preparado, ainda não verificado

O Dockerfile está preparado, mas o build e a execução do container não foram verificados. Docker não é necessário para executar a aplicação localmente. O container cobre somente backend e Swagger; o frontend continua separado.

```sh
docker build -t academico-beta ./backend
docker run --rm --name academico-beta -p 127.0.0.1:8080:8080 academico-beta
docker stop academico-beta
```

O H2 continua volátil; montar um volume não torna esses dados persistentes. `APP_DEMO=false` desliga os dados demonstrativos. `APP_JWT_SEGREDO`, quando configurado, deve conter Base64 de pelo menos 32 bytes aleatórios; nunca o grave no código. Sem esse segredo, uma chave aleatória é gerada a cada execução. O backend escuta em `0.0.0.0` dentro do container; o comando acima limita a porta publicada ao host local.

## Limites conhecidos

Não há notas, frequência, calendário, materiais, CRUD de cursos, rematrícula, recuperação de senha ou módulo de exclusão LGPD. O aviso simples não certifica conformidade. Spring Boot 3.5 está fora do suporte OSS, um risco para evolução ou exposição do sistema. Desempenho, auditoria completa de acessibilidade WCAG e Docker não foram comprovados.

## Documentação

[Índice técnico e de produto](docs/README.md) · [Decisões e limites](docs/decisoes.md) · [Status da entrega pública](docs/entrega.md)
