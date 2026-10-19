export interface Curso { id:number; nome:string; descricao:string; ativo:boolean }
export interface Perfil { usuarioId:number; nome:string; email:string; telefone:string; papel:'ADMIN'|'PROFESSOR'|'ALUNO'; ativo:boolean }
export interface Aluno extends Perfil { id:number; cursoSolicitado:Curso; situacaoSolicitacao:'PENDENTE'|'APROVADO'|'REJEITADO'; criadoEm:string }
export interface Professor extends Perfil { id:number; cursos:Curso[] }
export interface Matricula { id:number; alunoId:number; alunoNome:string; curso:Curso; situacao:'ATIVA'|'CANCELADA'; matriculadoEm:string; canceladoEm:string|null }
export interface Dashboard { alunosAtivos:number; professoresAtivos:number; solicitacoesPendentes:number; matriculasAtivas:number; cursosDisponiveis:number; solicitacoesRecentes:Aluno[] }
export interface Sessao { token:string; usuario:Perfil }
