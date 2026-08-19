export function minutesBetween(a,b){
 if(!a||!b)return 0;
 const[h1,m1]=String(a).split(':').map(Number),[h2,m2]=String(b).split(':').map(Number);
 return Math.max(0,h2*60+m2-h1*60-m1);
}
function isoDate(d){return d.toISOString().slice(0,10)}
function addDays(iso,n){const d=new Date(`${iso}T12:00:00`);d.setDate(d.getDate()+n);return isoDate(d)}
function pyWeekday(iso){const d=new Date(`${iso}T12:00:00`);return(d.getDay()+6)%7}
function applies(block,ensembleId){return !block.deleted_at&&(!block.ensemble_id||block.ensemble_id===ensembleId)}
function hasBlock(blocks,date,ensembleId,type){return blocks.some(b=>applies(b,ensembleId)&&b.block_date===date&&b.block_type===type)}
export function concertEntries(concert,extras=[],blocks=[],fromDate=new Date().toISOString().slice(0,10)){
 const end=concert.concert_date;
 if(!end)return{regular:[],extra:[],calendar:[]};
 const sched=concert.weekly_schedule||{};
 const days=Array.isArray(sched.weekdays)?sched.weekdays:String(concert.regular_weekdays||'').split(',').filter(Boolean).map(Number);
 const start=sched.start||concert.regular_start_time||'',finish=sched.end||concert.regular_end_time||'';
 const regular=[];
 for(let iso=fromDate;iso<end;iso=addDays(iso,1)){
  if(!days.includes(pyWeekday(iso)))continue;
  if(hasBlock(blocks,iso,concert.ensemble_id,'full_day')||hasBlock(blocks,iso,concert.ensemble_id,'cancel_regular'))continue;
  regular.push({date:iso,kind:'regular',label:'Ensayo regular',start_time:start,end_time:finish,duration_minutes:minutesBetween(start,finish),notes:''});
 }
 const extra=extras.filter(x=>!x.deleted_at&&x.concert_id===concert.id&&x.rehearsal_date>=fromDate&&!hasBlock(blocks,x.rehearsal_date,concert.ensemble_id,'full_day')).sort((a,b)=>String(a.rehearsal_date).localeCompare(String(b.rehearsal_date))||String(a.start_time||'').localeCompare(String(b.start_time||''))).map(x=>({date:x.rehearsal_date,kind:'extra',label:x.rehearsal_date===end?'Ensayo extra el día del concierto':'Ensayo extra',start_time:x.start_time||'',end_time:x.end_time||'',duration_minutes:Number(x.duration_minutes)||minutesBetween(x.start_time,x.end_time),notes:x.notes||''}));
 const calendar=[...regular,...extra];
 for(let iso=fromDate;iso<=end;iso=addDays(iso,1)){
  for(const b of blocks.filter(x=>applies(x,concert.ensemble_id)&&x.block_date===iso))calendar.push({date:iso,kind:'block',label:b.block_type==='full_day'?'Día inhábil total':'Anulación de ensayo regular',start_time:'',end_time:'',duration_minutes:0,notes:b.notes||''});
 }
 calendar.push({date:end,kind:'concert',label:'Concierto',start_time:concert.start_time||concert.concert_time||'',end_time:'',duration_minutes:0,notes:concert.title||''});
 const rank={concert:0,block:1,regular:2,extra:3};
 calendar.sort((a,b)=>a.date.localeCompare(b.date)||(rank[a.kind]??9)-(rank[b.kind]??9)||String(a.start_time).localeCompare(String(b.start_time)));
 return{regular,extra,calendar};
}
export function concertMetrics(concert,extras=[],blocks=[],fromDate=new Date().toISOString().slice(0,10)){
 if(!concert?.concert_date||fromDate>concert.concert_date)return{regularCount:0,extraCount:0,totalCount:0,regularHours:0,extraHours:0,totalHours:0,calendarEntries:[]};
 const e=concertEntries(concert,extras,blocks,fromDate),regularMin=e.regular.reduce((n,x)=>n+x.duration_minutes,0),extraMin=e.extra.reduce((n,x)=>n+x.duration_minutes,0);
 return{regularCount:e.regular.length,extraCount:e.extra.length,totalCount:e.regular.length+e.extra.length,regularHours:regularMin/60,extraHours:extraMin/60,totalHours:(regularMin+extraMin)/60,regularEntries:e.regular,extraEntries:e.extra,calendarEntries:e.calendar};
}
