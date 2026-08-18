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
