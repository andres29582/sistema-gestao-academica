# Testes e capturas do frontend

## Reprodução

Em `frontend/`, com JDK 21 configurado em `JAVA_HOME` e o JAR do backend compilado:

```sh
npm ci
npm run build
npx playwright install chromium
npm test
npm audit --json
```

Deixe livres as portas usadas pelos servidores de teste. Consulte o [README](../README.md) para configuração e uso de um navegador já instalado.

## Resultado registrado

A execução de 19/10/2026 registrou sete jornadas aprovadas, sem falhas, ignorados, instabilidades ou exceções JavaScript. Os testes usam a API real. Instalação, build e auditoria npm também foram registrados como aprovados. [Resumo](resultados-frontend.json).

| Jornada | Comportamento verificado | Tempo |
|---|---|---|
|01 | LoginADMIN, indicadores reais, captura desktop/mobile, storage vazio e reload encerra sessão |31,0 s |
|02 | Cadastro com curso→PENDENTE sem edição→aprovaçãoADMIN→APROVADO/ATIVA e edição pessoal |36,9 s |
|03 | RejeiçãoADMIN e consulta própria REJEITADO sem edição |16,4 s |
|04 | Criar professor com dois cursos, editar conjunto/nome e desativar |18,7 s |
|05 | Exclusão de aluno ativo recebe erro de matrícula, preserva registro; troca curso, cancela, aluno consulta sem edição, exclusão posterior permitida |27,9 s |
|06 | Professor consulta próprios cursos/alunos e acessa perfil, sem menuADMIN |11,5 s |
|07 | ADMIN desativa conta em outra sessão; token anterior não permite salvar e portal explica encerramento |27,3 s |


As jornadas são sequenciais e compartilham dados: a jornada 02 cria o aluno usado em 07; a 05 cancela e exclui o aluno inicial. Execute a suíte inteira com a demonstração recém-iniciada.

## Capturas

- [Login](imagens/login.png)
- [Painel administrativo](imagens/dashboard.png)
- [Painel em tela móvel](imagens/dashboard-mobile.png)

As capturas contêm dados demonstrativos. A inspeção visual não equivale a auditoria completa de acessibilidade.

## Limites

Não há medição de cobertura frontend. A jornada 04 não confirma o conjunto final de IDs de cursos; a 05 não confere o curso após a atualização; a 06 não testa diretamente a resposta 403 para curso alheio, coberta no backend. Sete jornadas não cobrem todas as combinações de uso.

O [registro npm audit](e7-npm-audit.json) informa zero vulnerabilidades conhecidas naquela execução; não certifica segurança integral ou versões futuras. Docker não foi executado. O [smoke do JAR](resultado-smoke.json) registra a verificação complementar de reinício.
