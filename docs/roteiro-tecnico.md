# Vídeo técnico — roteiro de 3 min 45 s

Roteiro preparado, **não gravado nem publicado**. Reinicie a demonstração antes de gravar; use apenas dados fictícios. Grave a tela, sem segredos do ambiente nem tokens visíveis. Não é necessário mostrar a chave JWT, payload de login ou hashes.

| Tempo | Tela e ação | Fala sugerida |
|---|---|---|
|0:00–0:25 | README e estrutura `backend/src/main/java` | “Esta prova implementa um CRUD acadêmico em Java 21, Spring Boot e H2 em memória. O backend é um monólito em camadas; o frontend React consome a API real.” |
|0:25–1:00 | `dominio/`, DER e DTOs | “São seis tabelas. ADMIN é papel de Usuario. Aluno solicita um curso e recebe matrícula apenas após aprovação. DTOs impedem exposição direta das entidades.” |
|1:00–1:45 | `AlunoServico`, repositório de lock e UNIQUE em Matricula | “Aprovação e matrícula são atômicas. Todas as transições do mesmo aluno disputam o mesmo lock dentro da transação. A constraint única é uma proteção adicional, inclusive após cancelamento.” |
|1:45–2:15 | Segurança e contrato REST | “JWT dura 15 min e cada requisição verifica conta e papel atuais no banco. Desativar bloqueia inclusive um token anterior. Professores só veem alunos autorizados de seus cursos.” |
|2:15–2:55 | Resultado Surefire/JaCoCo e testes c14/c16/c24 | “Foram executados 52 testes backend, sem falhas. Há rollback com falha real de banco, duas transações concorrentes e reinício de contextos H2. Linhas 95,80% e ramificações 79,76%; isso não significa segurança integral.” |
|2:55–3:20 | Portal e evidências Playwright | Mostre o relatório real, citando somente a quantidade aprovada registrada após a execução. “As jornadas de navegador usam a API real, sem estatísticas simuladas.” |
|3:20–3:45 | Dockerfile e limitações | “Dockerfile multi-stage foi preparado, mas Docker não estava disponível para build/run. H2 descarta os dados ao encerrar. O projeto não oferece notas, frequência ou rematrícula e não é uma plataforma pronta para produção.” |

Antes de publicar, confira se o relatório exibido corresponde ao código gravado. Se qualquer etapa continuar pendente, diga isso; não edite o vídeo para sugerir teste não executado. O usuário decide e realiza o envio ao YouTube como não listado.
