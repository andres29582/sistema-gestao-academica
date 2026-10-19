# Decisões e limites —19/10/2026

## Definitivas no pedido atual

- Java 21, Boot 3.5.16, Maven Wrapper, JPA/H2 em memória; nãoPostgreSQL/Flyway.
- Monólito em camadas controller→service→repository, DTOs separados; seminterfaces espelho/CRUDgenérico/microserviços.
- Seis tabelas exatas, enumsSTRING, IDENTITY, ADMINcomo papel.
- Cadastro comcurso→PENDENTE→aprovaçãoADMIN+matrículaautomática; matrículaúnica vitalícia. Cancelar preservaAPROVADO.
- Exclusãológica distinta de desativação; aluno com matrícula ATIVA não pode ser excluído.
- React/TS/Vite+CSS próprio, semframeworks deestado/design; JWTsomente memória,401 encerra sessão; proxyAPIlocal.
- LGPDcomo aviso simples; semmódulodeexclusão. Dadosfictícios e sem publicação/autorização implícita.

## Resoluções técnicas desta implementação

| Escolha | Motivo e compromisso |
|---|---|
| Lockpessimista noAluno em todasmutação/transição | Simplifica serialização; consultas de IDs escalares antes do lock evitam entidade obsoleta no contexto JPA. Não promete throughput distribuído. |
| UNIQUE no banco por aluno em matrícula | Proteção definitiva mesmo diante de concorrência; testeSQLdireto e duasthreads reais. |
| BCrypt 12 e validaçãoUTF8≤72 bytes | Custo explícito e limite real do algoritmo; testesfixture não alteram encoderprodução. |
| JWT15min/estado atual no banco | Desativação invalida token anterior; recarregar perde sessão. Semrefresh/recovery/rate limit distribuído. |
| Perfilsó aprovado+ATIVA para aluno | Fecha conservadoramente a ambiguidade histórica; leitura própria permanece disponível para pendente/rejeitado/cancelado. |
| Cursos de professor conjuntofinal naUI | Mescla ativos+antigosatribuídos; preserva antigo inativo sem oferecer novo inativo. |
| Listas sempaginação | Demonstração de pequeno volume; rever antes de escala. |
| Dependências frontend fixadas e lockfile | React19.3.1/Vite8.3.4/TS7.0.2/Playwright1.64.1; registry npm consultado e build executado conforme evidências. |

## Riscos e sugestões, não funcionalidades entregues

1. Boot 3.5 encerrou suporte OSS: manter pedido da prova não torna a aplicação adequada para exposição pública. Migração de linha depende de nova decisão e regressão, não feita aqui.
2. Imagem de modelagem do formulário foi referência, não modelo obrigatório. Avaliador pode interpretar diferente; conferir enunciado original, não disponível separadamente.
3. Dockerfile preparado, semDocker neste ambiente: **build/runpendentes**. Tags oficiais verificadas, mas sempin de digest imutável; recomenda-se fixar após executar em plataforma-alvo.
4. Sembenchmark formal/carga, pentest, certificação LGPD ou auditoria WCAG completa. Cobertura alta não prova ausência de falhas.
5. H2 e credenciaisdemonstrativas sãointencionais. Não adaptar para dados reais sem novo escopo de persistência/segurança/privacidade.
6. Publicar repositório e vídeos exige revisão de conteúdo e autorização humana; não foi realizado.

Fontes de ferramentas: [Viteproxy oficial](https://vite.dev/config/server-options#server-proxy), [imagens Maven](https://hub.docker.com/_/maven), [TemurinJRE21](https://hub.docker.com/_/eclipse-temurin/tags?name=21-jre). FontesJava/Maven/Boot específicas estão em [evidênciasbackend](evidencias-backend.md).
