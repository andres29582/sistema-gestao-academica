# Demonstração do produto — roteiro de 4 min

Roteiro preparado, **não gravado nem publicado**. Inicie backend e frontend com dados recém-carregados. Use três janelas de navegador, sem persistir tokens manualmente. Todas as pessoas são fictícias.

| Tempo | Ação real | Mensagem principal |
|---|---|---|
|0:00–0:25 | Abrir login, mostrar Beta e aviso de privacidade | “Este portal acompanha cadastro, análise e matrícula. É uma demonstração local com dados temporários, não use dados pessoais reais.” |
|0:25–1:00 | Solicitar cadastro com nome/e-mail fictícios, senha e curso | “O aluno escolhe um curso. A solicitação começa PENDENTE e ainda não há matrícula.” |
|1:00–1:35 | LoginADMIN, dashboard e solicitações; aprovar novo aluno | “Os cartões vêm do banco. Ao aprovar, o sistema cria a matrícula automaticamente na mesma operação.” |
|1:35–2:05 | Sair; entrar como novo aluno; consultar situação e matrícula | “Agora o aluno vê APROVADO, matrícula ATIVA e pode atualizar seus dados pessoais. Ele não escolhe papel nem altera a própria matrícula.” |
|2:05–2:35 | Entrar como professor; consultar cursos e alunos | “O professor enxerga só seus cursos e os alunos autorizados com matrícula ativa, com dados mínimos.” |
|2:35–3:10 | ADMIN cria professor com dois cursos e edita conjunto | “A administração controla vínculos sem apagar cursos ou alunos. Sempre há ao menos um curso atribuído.” |
|3:10–3:45 | ADMIN tenta excluir aluno ativo, vê conflito; cancela matrícula | “A exclusão exige cancelar a matrícula ativa. O cancelamento preserva a aprovação e o histórico; não existe rematrícula nesta Beta.” |
|3:45–4:00 | Voltar ao aluno cancelado ou mostrar README | “A consulta continua, mas a edição fica bloqueada. Ao reiniciar o backend, os dados criados são descartados e a demonstração é restaurada.” |

Não mostrar botão ou alegar operação fora do escopo: notas, frequência, calendário, materiais, criação de cursos e rematrícula não existem. A gravação não substitui teste automatizado. O envio ao YouTube como não listado é ação posterior do usuário.
