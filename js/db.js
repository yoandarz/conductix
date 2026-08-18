const NAME='conductix-v2',VER=1;
export function openDb(){return new Promise((ok,fail)=>{const r=indexedDB.open(NAME,VER);r.onupgradeneeded=()=>{const d=r.result;if(!d.objectStoreNames.contains('records')){const s=d.createObjectStore('records',{keyPath:'_key'});s.createIndex('table','_table');}if(!d.objectStoreNames.contains('settings'))d.createObjectStore('settings',{keyPath:'key'});if(!d.objectStoreNames.contains('auth'))d.createObjectStore('auth',{keyPath:'key'});};r.onsuccess=()=>ok(r.result);r.onerror=()=>fail(r.error);});}
function req(r){return new Promise((ok,fail)=>{r.onsuccess=()=>ok(r.result);r.onerror=()=>fail(r.error);});}
async function tx(store,mode,fn){const d=await openDb(),t=d.transaction(store,mode),s=t.objectStore(store),out=fn(s);if(out instanceof IDBRequest)return req(out);return new Promise((ok,fail)=>{t.oncomplete=()=>ok(out);t.onerror=()=>fail(t.error);});}
export const key=(table,id)=>`${table}:${id}`;
export async function rows(table,{deleted=false}={}){const d=await openDb(),t=d.transaction('records','readonly'),idx=t.objectStore('records').index('table'),a=await req(idx.getAll(table));return deleted?a:a.filter(x=>!x._sync?.deleted);}
export async function allRows(){const d=await openDb(),t=d.transaction('records','readonly');return req(t.objectStore('records').getAll());}
export async function getRow(table,id){return tx('records','readonly',s=>s.get(key(table,id)));}
export async function putRow(table,row){const x={...row,_table:table,_key:key(table,row.id)};await tx('records','readwrite',s=>s.put(x));return x;}
export async function putRows(table,a){for(const r of a)await putRow(table,r);}
export async function hardDelete(table,id){return tx('records','readwrite',s=>s.delete(key(table,id)));}
export async function clearAll(){const d=await openDb();for(const name of ['records','settings'])await new Promise((ok,fail)=>{const t=d.transaction(name,'readwrite');t.objectStore(name).clear();t.oncomplete=ok;t.onerror=()=>fail(t.error);});}
export const setting=async(k,f=null)=>(await tx('settings','readonly',s=>s.get(k)))?.value??f;
export const setSetting=(k,v)=>tx('settings','readwrite',s=>s.put({key:k,value:v}));
export const getSession=async()=> (await tx('auth','readonly',s=>s.get('session')))?.value||null;
export const setSession=v=>tx('auth','readwrite',s=>s.put({key:'session',value:v}));
export const clearSession=()=>tx('auth','readwrite',s=>s.delete('session'));
export async function dirty(table,row){const now=new Date().toISOString();const x={...row,id:row.id||crypto.randomUUID(),updated_at:now,client_updated_at:now,_sync:{...(row._sync||{}),dirty:true,deleted:false}};return putRow(table,x);}
export async function tombstone(table,row){const now=new Date().toISOString();return putRow(table,{...row,deleted_at:now,client_updated_at:now,_sync:{...(row._sync||{}),dirty:true,deleted:true}});}
