# Requisitos — baseline operacional de19/10/2026

IDs e texto relevante do catálogo histórico preservados. Decisões de implementação abaixo prevalecem sobre alternativas antigas; critérios não medidos continuam pendentes, não presumidos.

## 9. Requisitos funcionais

**Catálogo final: 19 RF.** Fonte PDF de 18/10 pp. 81-91, com atualização de RF-019 nas pp. 95-96; atualização de 19/10 relaciona RF-014 também ao UC-015, sem criar RF-020. As descrições e critérios seguem documentados; cartões numéricos ilustrativos do RF-016 não são dados reais. Prioridades: Alta RF-001 a RF-015, Média RF-016 a RF-018, Baixa RF-019; priorização proposta, não classificação confirmada pelo avaliador.

| RF | Caso de uso |
|---|---|
| RF-001 | UC-001 |
| RF-002, RF-003 | UC-002 |
| RF-004 | UC-003 |
| RF-005 | UC-004 |
| RF-006 | UC-005 |
| RF-007 | UC-006 |
| RF-008 | UC-007 |
| RF-009 | UC-008 |
| RF-010, RF-011 | UC-009 |
| RF-012, RF-013 | UC-010 |
| RF-014 | UC-011, UC-015 |
| RF-015 | UC-001, UC-007 |
| RF-016 | UC-012 |
| RF-017 | UC-013 |
| RF-018 | UC-014 |
| RF-019 | Transversal |

<a id="rf-001"></a>
### RF-001 — Cadastrar aluno

Descrição formal: O sistema deve permitir que um visitante realize seu cadastro, informando dados pessoais, credenciais de acesso e selecionando um curso disponível.

**Explicação funcional**

O próprio interessado informa seus dados e escolhe o curso que deseja frequentar, evitando que a administração precise preencher essas informações manualmente. Entretanto, o cadastro representa apenas uma solicitação de ingresso. O aluno ainda não está oficialmente matriculado.

**Critérios de aceitação**

- O formulário deve solicitar nome, e-mail, telefone, senha e curso desejado.

- O sistema deve validar os campos obrigatórios, o formato do e-mail e as regras de senha.

- Não deve permitir contas duplicadas com o mesmo e-mail.

- O curso selecionado deve existir e estar ativo.

- O novo usuário deve receber exclusivamente o perfil ALUNO.

- A solicitação deve ser registrada com situação PENDENTE.

- Nenhuma matrícula deve ser criada durante o cadastro inicial.

<a id="rf-002"></a>
### RF-002 — Autenticar usuários

Descrição formal: O sistema deve permitir autenticação por e-mail e senha.

**Explicação funcional**

A autenticação permite reconhecer quem está acessando a plataforma e garantir que suas informações não sejam disponibilizadas indevidamente.

**Critérios de aceitação**

- O sistema deve validar as credenciais.

- Deve rejeitar credenciais inválidas.

- Deve impedir a autenticação de contas desativadas.

- Após a autenticação, o usuário deve ser encaminhado ao painel correspondente ao seu perfil.

- Alunos pendentes ou rejeitados devem ter acesso restrito à consulta de sua situação.

<a id="rf-003"></a>
### RF-003 — Controlar acesso por perfil

Descrição formal: O sistema deve restringir o acesso às operações conforme o perfil do usuário autenticado.

**Explicação funcional**

Um aluno não deve conseguir aprovar a própria solicitação. Da mesma forma, um professor não deve ter acesso irrestrito à administração da instituição.

**Critérios de aceitação**

- Somente administradores podem aprovar ou rejeitar solicitações.

- Somente administradores podem cadastrar professores.

- Alunos só podem consultar e alterar suas próprias informações autorizadas.

- Professores podem consultar apenas os cursos e alunos aos quais possuem acesso.

- Tentativas de operações sem permissão devem retornar 403 Forbidden quando o usuário estiver autenticado.

<a id="rf-004"></a>
### RF-004 — Atualizar dados pessoais

Descrição formal: O sistema deve permitir que usuários autenticados atualizem seus dados pessoais autorizados.

**Explicação funcional**

Informações como telefone e nome podem precisar de correção. Permitir que os próprios usuários mantenham esses dados atualizados reduz o trabalho administrativo.

**Critérios de aceitação**

- O usuário deve conseguir consultar seus dados atuais.

- O sistema deve validar as alterações.

- O usuário não deve conseguir modificar seu papel de acesso.

- Alunos não devem alterar diretamente a situação de aprovação ou a matrícula.

- As alterações devem permanecer armazenadas após uma nova consulta.

<a id="rf-005"></a>
### RF-005 — Consultar situação da solicitação

Descrição formal: O sistema deve permitir que o aluno consulte a situação atual de sua solicitação de ingresso.

**Explicação funcional**

O aluno poderá acompanhar seu pedido sem precisar entrar em contato com a administração.

**Critérios de aceitação**

- A consulta deve apresentar PENDENTE, APROVADO ou REJEITADO.

- Deve identificar o curso solicitado.

- A consulta não deve modificar os dados.

- O aluno deve visualizar exclusivamente sua própria solicitação.

<a id="rf-006"></a>
### RF-006 — Consultar matrícula

Descrição formal: O sistema deve permitir que alunos aprovados consultem os dados de sua matrícula.

**Explicação funcional**

Após a aprovação, o aluno precisa confirmar em qual curso está matriculado.

**Critérios de aceitação**

- O sistema deve apresentar o curso e a data da matrícula.

- Somente o próprio aluno e os perfis autorizados devem acessar seus dados.

- Caso não exista matrícula, deve ser apresentada uma mensagem apropriada.

- O sistema não deve apresentar matrículas inexistentes ou dados fictícios como registros reais.

<a id="rf-007"></a>
### RF-007 — Listar solicitações pendentes

Descrição formal: O sistema deve permitir que administradores consultem e analisem solicitações de ingresso pendentes.

**Explicação funcional**

A administração precisa de um local centralizado para identificar quais alunos aguardam análise.

**Critérios de aceitação**

- O sistema deve listar as solicitações pendentes.

- Deve permitir consultar os dados e o curso solicitado.

- Somente administradores devem acessar essa funcionalidade.

- Solicitações já analisadas não devem permanecer na lista de pendentes.

<a id="rf-008"></a>
### RF-008 — Aprovar solicitação e gerar matrícula

Descrição formal: O sistema deve permitir que o administrador aprove uma solicitação pendente, criando automaticamente a matrícula no curso solicitado.

**Explicação funcional**

Como o aluno já escolheu um curso durante seu cadastro, o administrador não precisa repetir essa escolha. A aprovação efetiva a matrícula.

**Critérios de aceitação**

- Somente solicitações pendentes podem ser aprovadas.

- O curso deve existir e estar ativo.

- A operação deve criar uma única matrícula por aluno.

- O sistema deve alterar a situação para APROVADO.

- Aprovação e matrícula devem ser realizadas atomicamente.

- Uma tentativa repetida de aprovação não deve criar matrículas duplicadas.

<a id="rf-009"></a>
### RF-009 — Rejeitar solicitação

Descrição formal: O sistema deve permitir que o administrador rejeite solicitações pendentes.

**Explicação funcional**

Nem todas as solicitações poderão ser aprovadas. O sistema precisa registrar a decisão sem eliminar o cadastro realizado.

**Critérios de aceitação**

- Somente administradores podem rejeitar solicitações.

- A solicitação deve estar pendente.

- O sistema deve alterar a situação para REJEITADO.

- Nenhuma matrícula deve ser criada.

- Os dados do cadastro devem ser preservados.

<a id="rf-010"></a>
### RF-010 — Consultar e gerenciar alunos

Descrição formal: O sistema deve disponibilizar operações administrativas de consulta, atualização e exclusão lógica de registros de alunos.

**Explicação funcional**

A administração precisa localizar alunos, corrigir dados e administrar os registros existentes, mesmo depois que a matrícula foi realizada.

**Critérios de aceitação**

- Permitir listar e consultar alunos por identificador.

- Permitir atualizar os campos administrativos autorizados.

- Permitir localizar registros específicos.

- A exclusão lógica não deve remover fisicamente o histórico acadêmico.

- Um aluno inexistente deve resultar em 404 Not Found.

- O cadastro inicial do aluno é contemplado pelo RF-001; não será necessário duplicar esse fluxo na administração.

<a id="rf-011"></a>
### RF-011 — Ativar e desativar alunos

Descrição formal: O sistema deve permitir que administradores habilitem ou desabilitem o acesso de alunos.

**Explicação funcional**

Desativar uma conta significa bloquear seu acesso, sem necessariamente cancelar sua matrícula ou eliminar informações acadêmicas.

**Critérios de aceitação**

- Somente o administrador pode alterar esse estado.

- Alunos desativados não devem conseguir autenticar-se.

- A matrícula não deve ser excluída automaticamente.

- O administrador deve poder reativar a conta quando permitido.

<a id="rf-012"></a>
### RF-012 — Cadastrar e gerenciar professores

Descrição formal: O sistema deve permitir que administradores cadastrem, consultem e atualizem professores.

**Explicação funcional**

Como o professor terá acesso a informações de alunos, sua conta não deve ser criada livremente por visitantes. Esse cadastro será responsabilidade da administração.

**Critérios de aceitação**

- Somente administradores podem criar professores.

- O professor criado deve receber o perfil PROFESSOR.

- O sistema deve validar os dados obrigatórios.

- Deve permitir consultar e atualizar os registros.

- Não deve permitir duplicação de e-mail.

<a id="rf-013"></a>
### RF-013 — Ativar e desativar professores

Descrição formal: O sistema deve permitir que administradores controlem o estado de acesso das contas de professores.

**Explicação funcional**

Um professor que deixa de atuar na instituição não deve continuar acessando informações dos estudantes.

**Critérios de aceitação**

- O administrador deve conseguir ativar e desativar professores.

- Professores desativados não podem autenticar-se.

- Os vínculos acadêmicos devem ser preservados, evitando exclusão automática de dados relacionados.

<a id="rf-014"></a>
### RF-014 — Consultar e atualizar matrículas

Descrição formal: O sistema deve permitir que administradores consultem, alterem e cancelem matrículas.

**Explicação funcional**

O administrador poderá corrigir um curso registrado incorretamente ou cancelar uma matrícula quando necessário.

**Critérios de aceitação**

- Deve permitir consultar as matrículas existentes.

- Deve permitir atualizar o curso da matrícula.

- Deve impedir a associação com cursos inexistentes ou inativos.

- Deve permitir cancelar uma matrícula sem apagar seu registro.

- O aluno não poderá possuir mais de um registro de matrícula na versão beta.

- A rematrícula após cancelamento ficará fora do escopo inicial.

<a id="rf-015"></a>
### RF-015 — Consultar catálogo de cursos

Descrição formal: O sistema deve disponibilizar a consulta aos cursos previamente cadastrados.

**Explicação funcional**

Os cursos serão definidos antes da execução da aplicação. O aluno poderá escolher um desses cursos no cadastro, sem criar novos registros no catálogo.

**Critérios de aceitação**

- O sistema deve listar os cursos disponíveis.

- O visitante deve visualizar apenas os cursos habilitados para novas solicitações.

- O aluno não deve conseguir cadastrar cursos.

- O professor não deve conseguir cadastrar ou modificar cursos.

- A manutenção do catálogo ficará fora do escopo da versão beta.

<a id="rf-016"></a>
### RF-016 — Consultar indicadores administrativos

Descrição formal: O sistema deve apresentar um dashboard com indicadores quantitativos da instituição.

**Explicação funcional**

O administrador deve conseguir compreender rapidamente a quantidade de alunos, professores e matrículas, sem precisar consultar todos os registros individualmente.

**Critérios de aceitação**

- O dashboard deverá apresentar indicadores obtidos da base de dados, sem utilizar valores ilustrativos como resultados reais.

- Quantidade de alunos ativos e aprovados.

- Quantidade de professores ativos.

- Quantidade de solicitações pendentes.

- Quantidade de matrículas ativas.

- Quantidade de cursos disponíveis, como informação secundária.

- Lista de solicitações recentes.

- Os números devem refletir as alterações realizadas no banco de dados e ser recalculados a cada consulta ao endpoint correspondente.

<a id="rf-017"></a>
### RF-017 — Consultar cursos do professor

Descrição formal: O sistema deve permitir que professores consultem os cursos aos quais estão vinculados.

**Explicação funcional**

Um professor pode atuar em mais de um curso. O sistema precisa identificar essas associações para apresentar informações relevantes.

**Critérios de aceitação**

- O professor deve consultar apenas seus cursos.

- O sistema deve apresentar uma mensagem caso não exista vínculo.

- Os vínculos serão definidos previamente como dados de demonstração.

<a id="rf-018"></a>
### RF-018 — Consultar alunos dos cursos

Descrição formal: O sistema deve permitir que professores consultem os alunos matriculados nos cursos sob sua responsabilidade.

**Explicação funcional**

O professor precisa conhecer os alunos vinculados aos seus cursos, mas não necessita de acesso irrestrito aos dados de toda a instituição.

**Critérios de aceitação**

- O sistema deve listar os alunos de cada curso autorizado.

- Deve verificar a associação entre professor e curso.

- O professor não deve consultar dados de alunos de cursos não autorizados.

- Informações sensíveis ou desnecessárias não devem ser expostas.

<a id="rf-019"></a>
### RF-019 — Exibir informações da versão beta

Descrição formal: O sistema deve informar ao usuário quando uma funcionalidade não estiver disponível na versão demonstrativa.

**Explicação funcional**

Como a aplicação não será uma plataforma acadêmica completa, algumas áreas poderão apresentar informações ilustrativas sobre módulos futuros. O usuário precisa compreender essa limitação para não acreditar que uma operação foi executada quando, na realidade, não existe implementação.

**Critérios de aceitação**

- Funcionalidades indisponíveis devem ser identificadas.

- O sistema não deve simular confirmações de operações que não foram persistidas.

- As funcionalidades CRUD incluídas no escopo devem operar de verdade.

- Informações ilustrativas devem ser claramente diferenciadas de dados reais.

**Atualização posterior expressa (PDF pp. 95-96): RF-019 - Exibir informações da versão Beta e avisos legais.** O cadastro deve exibir aviso informativo sobre tratamento de dados pessoais e o caráter demonstrativo. Não haverá módulo de solicitações de titulares; não afirmar certificação ou conformidade integral com a LGPD. O texto sugerido do aviso está na seção 12. A atualização não cria um vigésimo RF.

<a id="rnf"></a>
## 10. Requisitos não funcionais

**15 RNF, versão 1.0 proposta para validação, com RNF-007 atualizado em 19/10.** Fonte inicial PDF de 18/10 pp. 96-107; alteração de RNF-007 no PDF de 19/10 pp. 210-214. P0: RNF-001 a RNF-011; P1: RNF-012 a RNF-014; P2: RNF-015. A condição histórica era **não verificados contra implementação**; a evidência de19/10 está registrada na atualização operacional abaixo. A exigência antiga de persistência após reinício foi substituída na conversa pela inicialização do H2 em memória e resemeadura de dados demo.

<a id="rnf-001"></a>
### RNF-001 — Arquitetura e organização do código

Prioridade: P0

Categoria: Manutenibilidade

Descrição formal: O backend deverá ser desenvolvido com Java e Spring Boot, adotando uma arquitetura em camadas, com separação das responsabilidades de apresentação, regras de negócio e persistência.

Explicação: Organizar o código em camadas facilita a identificação de problemas e a manutenção. Caso a forma de armazenar alunos seja alterada, por exemplo, não deverá ser necessário reescrever todo o sistema.

**Critérios de aceitação**

- A aplicação deverá separar Controllers, Services e Repositories.

- As regras de negócio deverão permanecer nos serviços ou componentes de domínio apropriados.

- Os controladores não deverão acessar diretamente os repositórios para executar regras de negócio.

- O código deverá ser organizado de forma coerente e compilável.

Verificação: inspeção da estrutura do projeto e execução do processo de compilação.

<a id="rnf-002"></a>
### RNF-002 — Padronização da API REST

Prioridade: P0

Categoria: Interoperabilidade

Descrição formal: A API deverá utilizar HTTP, com recursos organizados por endpoints, métodos HTTP apropriados e representações de dados em JSON.

Explicação: Uma API padronizada facilita sua compreensão por outros desenvolvedores e permite que diferentes interfaces utilizem o mesmo backend.

**Critérios de aceitação**

- Operações de consulta deverão utilizar GET.

- Operações de criação deverão utilizar POST.

- Operações de atualização deverão utilizar PUT ou PATCH, conforme o contrato.

- Operações de exclusão lógica deverão disponibilizar a semântica documentada de DELETE, quando aplicável.

- A API deverá retornar códigos HTTP coerentes, incluindo 200, 201, 204, 400, 401, 403, 404 e 409 conforme cada situação.

- Os formatos de requisição e resposta deverão ser documentados.

Verificação: testes de endpoints e inspeção dos contratos da API.

<a id="rnf-003"></a>
### RNF-003 — Autenticação e autorização

Prioridade: P0

Categoria: Segurança

Descrição formal: O sistema deverá proteger suas funcionalidades por mecanismos de autenticação e autorização implementados no backend, respeitando os perfis Administrador, Professor e Aluno.

Explicação: Ocultar uma opção no frontend não garante que uma pessoa esteja impedida de executar aquela operação. O próprio backend precisa verificar se o usuário possui a permissão necessária.

**Critérios de aceitação**

- Recursos protegidos deverão exigir autenticação.

- Usuários autenticados sem permissão deverão receber HTTP 403.

- Solicitações sem autenticação válida deverão receber HTTP 401 nos endpoints protegidos.

- Contas desativadas não poderão obter uma nova autenticação nem continuar utilizando credenciais previamente emitidas para acessar recursos protegidos.

- Um aluno não poderá consultar os dados privados de outro aluno.

- Um professor não poderá consultar alunos de cursos aos quais não esteja vinculado.

- O cadastro público não poderá conceder os perfis ADMIN ou PROFESSOR.

Verificação: testes de segurança e tentativas controladas de acesso não autorizado.

<a id="rnf-004"></a>
### RNF-004 — Proteção de credenciais

Prioridade: P0

Categoria: Segurança

Descrição formal: As senhas deverão ser armazenadas por meio de algoritmos adequados de hashing de senhas, nunca em texto simples.

Explicação: Se alguém tiver acesso indevido ao banco de dados, não deverá encontrar diretamente as senhas digitadas pelos usuários.

**Critérios de aceitação**

- Senhas não poderão ser armazenadas em texto simples.

- Deverá ser utilizado um algoritmo apropriado, como Argon 2 id ou outra alternativa tecnicamente justificada com configuração segura.

- Hashes e senhas não deverão aparecer nas respostas da API.

- Credenciais não deverão ser registradas em logs.

- Senhas de demonstração não deverão ser reutilizadas em ambientes reais.

Verificação: inspeção da implementação de autenticação, registros persistidos e respostas HTTP.

<a id="rnf-005"></a>
### RNF-005 — Privacidade e minimização dos dados

Prioridade: P0

Categoria: Privacidade

Descrição formal: O sistema deverá limitar a coleta, utilização e exposição de dados pessoais às informações necessárias para suas funcionalidades demonstrativas.

Explicação: Uma aplicação acadêmica deve evitar solicitar dados que não serão utilizados. Como o projeto é uma versão beta, utilizaremos dados fictícios e apresentaremos um aviso informativo sobre privacidade.

**Critérios de aceitação**

- Os formulários deverão solicitar somente informações justificadas pelo escopo.

- Informações pessoais deverão ser disponibilizadas apenas aos perfis autorizados.

- Dados sensíveis de autenticação não deverão aparecer nos logs nem nas respostas públicas.

- O frontend deverá apresentar o aviso de privacidade previsto no RF-019.

- O projeto não deverá declarar conformidade integral com a LGPD sem a devida avaliação.

Verificação: revisão de formulários, DTOs, logs e interfaces.

<a id="rnf-006"></a>
### RNF-006 — Integridade e consistência transacional

Prioridade: P0

Categoria: Confiabilidade

Descrição formal: O sistema deverá preservar a integridade dos relacionamentos e garantir consistência nas operações que envolvam múltiplas alterações de dados.

Explicação: Quando o administrador aprova um aluno, o sistema também precisa criar sua matrícula. Não podemos permitir que apenas uma dessas alterações seja confirmada.

**Critérios de aceitação**

- A aprovação e a criação da matrícula deverão ocorrer na mesma transação.

- Falhas durante a operação deverão provocar a reversão das alterações não confirmadas.

- O banco de dados deverá impedir a existência de matrículas duplicadas para o mesmo aluno.

- Duas tentativas simultâneas de aprovação não poderão gerar duas matrículas.

- As chaves estrangeiras deverão preservar os relacionamentos válidos.

Verificação: testes de integração, rollback, concorrência e restrições de banco de dados.

<a id="rnf-007"></a>
### RNF-007 — Persistência dos dados

Prioridade: P0

Categoria: Confiabilidade

Descrição formal atualizada em 19/10: O sistema deverá utilizar H2 Database em memória, garantindo a persistência e a integridade durante a execução; após reinicialização, o banco e os dados de demonstração deverão ser recriados.

Explicação: A decisão posterior priorizou o requisito relatado da prova técnica. Alterações feitas durante uma execução não sobrevivem ao encerramento; isso deve ser informado no README, e não descrito como defeito da persistência em memória.

**Critérios de aceitação**

- H2 deverá iniciar automaticamente com a aplicação e JPA/Hibernate deverá gerar o esquema conforme configuração.

- Operações CRUD deverão persistir corretamente durante a execução e respeitar chaves, relações e restrições.

- O sistema deverá preservar os vínculos entre as entidades.

- Os dados demonstrativos deverão ser recriados após reinicialização; README deverá informar que alterações em execução não são permanentes.

Verificação futura: confirmar inicialização H2, CRUD e relações durante execução, reinicialização e re-semeadura demo. Nenhuma dessas verificações foi executada nesta atualização.

<a id="rnf-008"></a>
### RNF-008 — Validação de entradas e tratamento de erros

Prioridade: P0

Categoria: Robustez

Descrição formal: O backend deverá validar os dados recebidos e apresentar respostas de erro padronizadas, sem expor informações internas desnecessárias.

Explicação: O sistema não pode confiar exclusivamente nos formulários. Mesmo que a interface impeça um cadastro inválido, alguém poderá enviar diretamente uma requisição à API.

**Critérios de aceitação**

- Dados obrigatórios deverão ser validados no backend.

- E-mails duplicados deverão ser rejeitados.

- Identificadores inexistentes deverão produzir respostas apropriadas.

- Violações de regras de negócio deverão resultar em respostas compreensíveis.

- Erros de validação não deverão expor stack traces ou informações internas da aplicação.

Verificação: testes com entradas inválidas, duplicadas e requisições inconsistentes.

<a id="rnf-009"></a>
### RNF-009 — Testabilidade e testes automatizados

Prioridade: P0

Categoria: Qualidade

Descrição formal: O projeto deverá possuir testes automatizados capazes de verificar as principais funcionalidades e regras de negócio implementadas.

Explicação: Testar somente pela interface não é suficiente. Os testes automatizados permitem confirmar que o sistema continua funcionando após alterações no código.

**Critérios de aceitação**

- Os serviços deverão possuir testes unitários para suas principais regras de negócio.

- As operações CRUD deverão possuir cenários de sucesso e de falha.

- As permissões dos perfis deverão ser testadas.

- A aprovação e matrícula deverão possuir testes de consistência transacional.

- A suíte deverá ser executável por um comando documentado, como ./mvnw test .

- Todos os testes obrigatórios deverão passar antes da entrega.

Verificação: execução da suíte e análise dos resultados.

<a id="rnf-010"></a>
### RNF-010 — Configuração e execução reproduzível

Prioridade: P0

Categoria: Portabilidade

Descrição formal: A aplicação deverá possuir configuração documentada que permita sua execução em um ambiente de desenvolvimento compatível, sem depender de configurações privadas do autor.

Explicação: O avaliador precisa conseguir executar o sistema em seu próprio computador sem descobrir manualmente quais dependências ou configurações são necessárias.

**Critérios de aceitação**

- O projeto deverá informar a versão de Java necessária.

- As dependências deverão ser gerenciadas pelo Maven.

- As configurações variáveis deverão ser externalizadas.

- Segredos reais não poderão ser incluídos no repositório.

- O projeto deverá oferecer instruções para inicialização dos dados demonstrativos.

- O processo de inicialização deverá ser verificável.

Verificação: instalação e execução conforme as instruções fornecidas.

<a id="rnf-011"></a>
### RNF-011 — Documentação técnica

Prioridade: P0

Categoria: Manutenibilidade

Descrição formal: O projeto deverá disponibilizar documentação suficiente para compreender sua arquitetura, executar a aplicação, utilizar a API e verificar suas funcionalidades.

Explicação: O avaliador não deverá precisar ler todo o código-fonte para entender como o projeto funciona.

**Critérios de aceitação**

- O README deverá apresentar a descrição do sistema e as tecnologias utilizadas.

- Deverá explicar como executar o backend e o frontend demonstrativo.

- Deverá documentar a execução dos testes.

- Deverá apresentar os endpoints e exemplos básicos de requisição.

- Deverá disponibilizar acesso aos requisitos, casos de uso, diagramas e dicionário de dados.

- Deverá identificar explicitamente as limitações da versão beta.

Verificação: revisão documental e reprodução das instruções.

<a id="rnf-012"></a>
### RNF-012 — Usabilidade e acessibilidade básica

Prioridade: P1

Categoria: Usabilidade

Descrição formal: O frontend deverá disponibilizar uma interface simples, compreensível e adaptável às dimensões de tela previstas para a demonstração.

Explicação: O avaliador precisa identificar rapidamente como entrar no sistema, consultar informações e executar operações de cadastro e gerenciamento.

**Critérios de aceitação**

- As telas deverão apresentar títulos e identificação clara das funcionalidades.

- Os formulários deverão conter rótulos associados aos campos.

- Os botões deverão possuir descrições compreensíveis.

- As mensagens de erro e sucesso deverão ser visíveis e inteligíveis.

- Os fluxos principais deverão permitir utilização por teclado.

- As telas principais deverão ser verificadas em uma largura de 360 pixels sem perda de controles essenciais ou rolagem horizontal desnecessária.

Verificação: testes manuais de navegação, formulários e responsividade.

<a id="rnf-013"></a>
### RNF-013 — Desempenho básico

Prioridade: P1

Categoria: Eficiência

Descrição formal: A API deverá apresentar tempo de resposta compatível com a utilização demonstrativa e o volume de registros definido para os testes.

Explicação: Mesmo sendo uma versão beta, as consultas de alunos e professores não devem apresentar lentidão excessiva para um conjunto pequeno de dados.

**Critério de aceitação proposto**

- Em ambiente local de teste, com a aplicação previamente inicializada e uma base contendo aproximadamente 300 alunos, 30 professores e 10 cursos, pelo menos 95% de 100 consultas sequenciais de listagem deverão responder em até 2 segundos.

- O ambiente, as condições de execução e os resultados deverão ser registrados.

Verificação: execução de um teste simples de desempenho em ambiente controlado.

Observação: esse limite é uma meta de engenharia proposta para o MVP, não uma exigência identificada no enunciado da prova técnica.

<a id="rnf-014"></a>
### RNF-014 — Registros técnicos e diagnóstico

Prioridade: P1

Categoria: Observabilidade

Descrição formal: O backend deverá produzir registros técnicos suficientes para identificar falhas de execução, sem divulgar informações confidenciais.

Explicação: Se uma operação falhar, o desenvolvedor deverá conseguir investigar o problema sem precisar expor senhas ou dados pessoais nos registros.

**Critérios de aceitação**

- Falhas inesperadas deverão gerar registros técnicos apropriados.

- Os logs não deverão conter senhas ou tokens de autenticação.

- Erros internos deverão produzir respostas controladas para o usuário.

- Informações técnicas detalhadas não deverão ser disponibilizadas publicamente.

Verificação: simulação de falhas e inspeção de logs e respostas.

<a id="rnf-015"></a>
### RNF-015 — Execução em ambiente Docker

Prioridade: P2 — Diferencial

Categoria: Portabilidade

Descrição formal: O projeto deverá, preferencialmente, disponibilizar configuração para executar a aplicação em um ambiente containerizado utilizando Docker.

Explicação: O Docker facilita a reprodução do ambiente de execução e reduz diferenças entre o computador do desenvolvedor e o do avaliador.

**Critérios de aceitação**

- A aplicação deverá possuir um Dockerfile funcional.

- As instruções de construção e execução deverão estar documentadas.

- A aplicação deverá iniciar e atender às requisições esperadas dentro do contêiner.

- A persistência deverá ser preservada entre reinicializações quando configurada com armazenamento persistente.

- Credenciais sensíveis não deverão ser incorporadas à imagem.

Verificação: construir e executar a imagem, realizar operações CRUD e confirmar o funcionamento após reinicialização.

### Auditoria documental histórica dos RNF

A conversa inicial concluiu que os 15 RNF têm propósito e critérios, alinhados aos 19 RF e então 14 UC. Em 19/10, UC-015 passa a integrar a baseline. Permanecem: diferenciar exclusão lógica/desativação; garantir unicidade da matrícula; conciliar persistência com H2 em memória; isolar credenciais fictícias; medir desempenho antes de afirmar atendimento. As referências técnicas são suporte à proposta, não resultado do sistema. (PDF de 18/10 pp. 105-107; PDF de 19/10 pp. 189-214.)

<a id="api"></a>

## Resoluções operacionais e estado de prova

- RF-004: aluno pendente/rejeitado/cancelado consulta, mas só aprovado com matrícula ATIVA e conta habilitada altera perfil. Professores/admin habilitados alteram seus dados.
- RF-014: cancelamento preserva APROVADO, preenche canceladoEm e não permite rematrícula.
- RF-012/013: administrador atribui um ou mais cursos; novos inativos proibidos, antigo inativo pode permanecer.
- RNF-004: BCrypt custo 12 e senha máxima 72 bytesUTF8; Argon 2 é alternativa histórica não adotada.
- RNF-007: H2 em memória/create-drop; encerramento descarta CRUD e reinício restaura demo. PostgreSQL/Flyway foraescopo.
- RNF-009:52 testes backend aprovados; cobertura 95,80%linhas/79,76%ramos. Veja [evidências](evidencias-backend.md). UI tem evidência própria; cobertura não comprova segurança integral.
- RNF-012: labels, foco, tabelas, diálogos e layout responsivo implementados; automação/inspeção registradas em evidencias-frontend.md. Não é auditoriaWCAG completa.
- RNF-013: não há benchmark formal/carga nem garantia de SLA; tempos de ambiente não provam desempenho de produção.
- RNF-015: Dockerfile criado; Docker indisponível, build/run NÃO executados.
- Requisitos documentais têm [índice](README.md); publicação/repositório/vídeos permanecem não executados.
