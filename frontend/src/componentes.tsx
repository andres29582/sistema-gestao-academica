import { useEffect,useRef,useState, type ReactNode } from 'react';
import { api } from './api';
export function useDados<T>(caminho:string){
 const [dados,setDados]=useState<T>();const [erro,setErro]=useState('');const [versao,setVersao]=useState(0);
 useEffect(()=>{let atual=true;setDados(undefined);setErro('');api<T>(caminho).then(d=>{if(atual)setDados(d);}).catch(e=>{if(atual)setErro(e.message);});return()=>{atual=false;};},[caminho,versao]);
 return {dados,erro,recarregar:()=>setVersao(v=>v+1)};
}
export function Aviso({texto,tipo='erro'}:{texto:string;tipo?:string}){return texto?<div className={`aviso ${tipo}`} role={tipo==='erro'?'alert':'status'}>{texto}</div>:null;}
export function Estado({erro,carregando}:{erro:string;carregando:boolean}){return <>{erro&&<Aviso texto={erro}/>} {carregando&&!erro&&<p role="status" className="vazio">Carregando dados…</p>}</>;}
export function Selo({valor}:{valor:string}){return <span className={`selo ${valor.toLowerCase()}`}>{valor.replaceAll('_',' ')}</span>;}
export function Modal({titulo,fechar,children}:{titulo:string;fechar:()=>void;children:ReactNode}){const ref=useRef<HTMLDialogElement>(null);useEffect(()=>{ref.current?.showModal();},[]);return <dialog ref={ref} aria-label={titulo} onCancel={fechar}><div className="modal-topo"><h2>{titulo}</h2><button type="button" className="secundario" onClick={fechar} aria-label="Fechar">×</button></div>{children}</dialog>;}
export function Campo({nome,rotulo,tipo='text',valor='',obrigatorio=true}:{nome:string;rotulo:string;tipo?:string;valor?:string;obrigatorio?:boolean}){return <label>{rotulo}<input name={nome} type={tipo} defaultValue={valor} required={obrigatorio} maxLength={nome==='nome'?120:nome==='email'?254:nome==='telefone'?30:undefined} minLength={tipo==='password'?8:undefined} autoComplete={tipo==='password'?'current-password':nome==='email'?'email':undefined}/></label>;}
export function dadosForm(form:HTMLFormElement){return Object.fromEntries(new FormData(form).entries());}
export function Tabela({titulos,children}:{titulos:string[];children:ReactNode}){return <div className="tabela-scroll"><table><thead><tr>{titulos.map(t=><th scope="col" key={t}>{t}</th>)}</tr></thead><tbody>{children}</tbody></table></div>;}
export function data(valor:string|null){return valor?new Intl.DateTimeFormat('pt-BR',{dateStyle:'short',timeStyle:'short'}).format(new Date(valor)):'—';}
