# Conductix 2.0.0-alpha.7 · Restauración de Agenda y planificación

Comparación dirigida contra `AgendaView` del Conductix Python/Tkinter original.

## Restaurado en esta revisión
- Subpestañas de Conciertos: **Nuevo / editar concierto** y **Lista y resumen**.
- Resumen calculado completo: agrupación, concierto/actividad, fecha, hora, lugar, días regulares, inicio, fin, duración por ensayo y desglose regular/extra/total.
- Contador: **Conciertos o actividades visibles: N** después de filtros.
- Nuevo bloqueo con ámbito **Global / Todas las agrupaciones** por defecto.
- Calendario mensual con días adyacentes del mes anterior/siguiente.
- Color del día completo por prioridad original: regular `#FFF3CD`, extra `#7C4DFF`, bloqueo `#B3261E`, concierto `#1B998B`.
- Prioridad cuando coinciden entradas: concierto > bloqueo > extra > regular.
- Formateador de horas de Agenda definido de forma explícita.

No modifica el esquema de Supabase ni los datos del usuario.
