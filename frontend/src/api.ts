let token:string|null=null;
export function definirToken(valor:string|null){token=valor;}
export class ErroApi extends Error { constructor(public status:number,public codigo:string,mensagem:string){super(mensagem);} }
export async function api<T>(caminho:string,metodo='GET',dados?:unknown):Promise<T>{
  let resposta:Response;
  try { resposta=await fetch('/api'+caminho,{method:metodo,headers:{...(token?{Authorization:`Bearer ${token}`} : {}),...(dados!==undefined?{'Content-Type':'application/json'}:{})},body:dados===undefined?undefined:JSON.stringify(dados)}); }
  catch {throw new Error('Não foi possível conectar à API. Confira se o backend está em execução.');}
  if(!resposta.ok){
    const problema=await resposta.json().catch(()=>({}));
    if(resposta.status===401 && token){token=null;window.dispatchEvent(new Event('sessao-encerrada'));}
    const campos=problema.campos?Object.values(problema.campos).join(' '):'';
    throw new ErroApi(resposta.status,problema.codigo??'ERRO_API',`${problema.detail??'Não foi possível concluir a operação.'}${campos?' '+campos:''}`);
  }
  return resposta.status===204?undefined as T:resposta.json();
}
