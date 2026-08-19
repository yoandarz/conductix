
## 2.0.0-alpha.7 · Agenda y planificación restaurada

Esta revisión recupera la estructura del Conductix clásico en Agenda y planificación: subpestañas **Nuevo / editar concierto** y **Lista y resumen**, resumen calculado completo del concierto, contador de conciertos visibles, bloqueos nuevos con ámbito **Global** por defecto y calendario mensual con días adyacentes y prioridad visual original (regular amarillo, extra violeta, bloqueo rojo, concierto verde azulado). También corrige el formateador de horas utilizado por Agenda.


## 2.0.0-alpha.6 · Auditoría de equivalencia funcional

Esta revisión compara Conductix 2 contra la aplicación Python/Tkinter original y restaura funciones que habían quedado simplificadas: gestión avanzada de secciones y niveles, importación de integrantes entre agrupaciones, estadísticas de asistencia, agenda completa con calendario/bloqueos, repertorio multiagrupación, programas de concierto detallados y herramientas de respaldo/planificación. También corrige el cálculo de ensayos frente a bloqueos y prepara el WebView Android para seleccionar JSON y guardar exportaciones en Descargas/Conductix.

# Conductix 2.0
Migración de Conductix desde Python/Tkinter + SQLite a una aplicación sincronizada.

## Arquitectura
- PWA estática: HTML/CSS/JavaScript.
- IndexedDB local-first para trabajar sin conexión.
- Supabase (proyecto GigPlan) con tablas `conductix_*` y RLS por `user_id`.
- Windows: PWA instalada. No existe Bridge porque Conductix no necesita alarmas.
- Android: APK nativa ligera con WebView de la misma PWA. Sin AlarmManager ni permisos de alarma.
- Identificadores UUID generados en cliente para crear relaciones también offline.

## Módulos
Panel general, instituciones, agrupaciones, secciones, niveles/grupos, integrantes, asistencia, agenda/planificación, repertorio, programas, exportación y configuración.

## Datos antiguos
La migración se entrega en un JSON privado separado; no forma parte del repositorio público.

## Publicación
Subir el contenido de esta carpeta a la raíz del repositorio `conductix`, activar GitHub Pages sobre `main`, y después compilar Android mediante `.github/workflows/build-android.yml`.


## 2.0.0-alpha.2 · Paleta visual
Se restauró la paleta violeta/morada del Conductix original (claro y oscuro), incluida la barra superior, controles, estados, PWA y colores nativos Android. No cambia el modelo de datos ni la lógica funcional.


## 2.0.0-alpha.4 · Orden de integrantes
Se restaura el selector original **Ordenar por: Apellido / Sección / Nivel o grupo** tanto en Integrantes como en Asistencia. Sección y nivel respetan `display_order` y usan apellido/nombre como desempate.


## Alpha 5 · Exportación restaurada
Se restaura el módulo de exportación de Conductix clásico: Integrantes, Asistencia, Agenda y planificación, Bloqueos de planificación y Repertorio, cada uno en CSV y Word. La asistencia Word conserva el formato matricial mensual con A/X/R. Se mantienen además la copia completa JSON y el resumen global Word.


## Alpha 8 · corrección móvil Android
- La barra de estado Android usa un scrim nativo de altura exacta al inset del sistema.
- El WebView comienza debajo de la barra de estado, sin duplicar safe-area en el encabezado.
- En móvil, el encabezado tiene altura automática; institución, agrupación y sincronización ya no desbordan sobre el contenido.
- Android queda alineado a versionName 2.0.0-alpha.8 / versionCode 20008.
