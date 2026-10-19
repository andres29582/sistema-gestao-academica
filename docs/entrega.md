# Entrega pública — status atual

**Repositório público criado e enviado para https://github.com/andres29582/sistema-gestao-academica.** Nenhum vídeo foi publicado.

## Checklist humano antes de publicar

1. Revisar README, licença desejada e limites. A imagem de modelagem da prova foi referência; confirmar com o avaliador se o modelo acadêmico é aceito.
2. Executar o Dockerfile em máquina com Docker, registrar build/run e corrigir divergências antes de marcar esse critério concluído.
3. Conferir resultados reais de backend/UI e smoke com o código que será enviado. Não transformar pendência em “100% pronto”.
4. Remover dados pessoais/segredos de arquivos e capturas. O projeto usa apenas credenciais demo explícitas, mas o ambiente local pode ter arquivos particulares.
5. **Não enviar** `.ferramentas/`, `.m2/`, `node_modules/`, `target/`, `.env`, traces privados ou caches. `package-lock.json`, Wrapper e código-fonte devem acompanhar a entrega.
6. Separar este diretório de implementação do espelho privado do projeto ChatGPT. Não publicar PDFs/transcrições/histórico pessoais automaticamente. Os links operacionais da aplicação apontam somente para arquivos deste pacote; o histórico externo é mencionado apenas como nota local e permanece preservado, sem ser copiado ou exigido pelo repositório público.
7. Publicação inicial concluída em um commit convencional, sem atribuição de IA ou `Co-Authored-By`.
8. Gravar os dois roteiros de 2–5 min e revisar a tela antes de enviar como vídeos não listados. Adicionar os links reais ao README; não usar links fictícios.

## Portabilidade documental verificada

Os dois links que saíam da aplicação para o histórico privado foram substituídos por referências internas de decisões e documentação operacional. A checagem estrutural também rejeita links locais de documentos do pacote que escapem da pasta da aplicação. [Relatório atualizado](verificacao-documental.json). Esse ajuste não alterou backend, frontend ou testes, não iniciou servidores e não repetiu build ou testes funcionais.

## O que ainda impede afirmar conclusão integral

Docker não foi executado neste ambiente. Desempenho formal, conformidade LGPD/WCAG e segurança integral não foram certificados. Relatórios de cobertura e auditoria têm alcance delimitado; consulte [qualidade](qualidade.md). Vídeos seguem preparados, não gravados nem publicados.
