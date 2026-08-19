# Conductix 2.0 · Auditoría de equivalencia funcional

**Versión auditada:** 2.0.0-alpha.6  
**Base de comparación:** Conductix clásico Python/Tkinter entregado por el usuario  
**Fecha:** 18/08/2026

## Resultado ejecutivo

La Alpha 5 conservaba el núcleo de datos y varias operaciones principales, pero simplificaba módulos completos del Conductix clásico. La Alpha 6 restaura esas capacidades sobre la arquitectura nueva PWA + IndexedDB + Supabase + Android WebView, sin volver a introducir dependencias de Tkinter/SQLite como fuente principal.

## Equivalencia por módulo

| Módulo | Conductix clásico | Estado Alpha 6 |
|---|---|---|
| Panel general | Contexto y resumen operativo | Restaurado/adaptado |
| Instituciones | Buscar, mostrar inactivas, alta/edición, activar/desactivar, contexto, eliminar | Restaurado |
| Agrupaciones | Buscar, filtro institución, inactivas, alta/edición, activar/desactivar, contexto, eliminar | Restaurado |
| Secciones | Alta, sugerencias, plantillas, plantillas personalizadas, activar/desactivar, reordenar, eliminar | Restaurado |
| Niveles o grupos | Ámbito institución/agrupación, reutilizar niveles institucionales, activar/desactivar, reordenar | Restaurado |
| Integrantes | Selección de agrupación, orden, inactivos, ficha completa, importar desde otra agrupación | Restaurado |
| Asistencia | Sesiones, presente/tarde, marcar todos, guardar, eliminar sesión, estadísticas históricas, orden | Restaurado |
| Agenda y planificación | Conciertos, horarios regulares, filtros, métricas, ensayos extra, bloqueos, calendario mensual | Restaurado |
| Repertorio | Buscar, estado, orden, vista por agrupación, asociaciones múltiples, CRUD | Restaurado |
| Programas | Participantes, piezas internas/externas, subtítulos, orden, intérpretes, solistas, copia, vista amplia, Word | Restaurado |
| Exportación | Integrantes, asistencia, agenda, bloqueos y repertorio separados en CSV/Word | Restaurado desde Alpha 5 |
| Configuración | Contexto, verificar almacenamiento, importar planificación, plantilla JSON, respaldo/restauración | Restaurado |

## Corrección de planificación

Se corrigió la lógica para reproducir el comportamiento del original:

- los ensayos regulares se calculan desde la fecha actual hasta el día anterior al concierto;
- `full_day` cancela ensayos regulares y extras aplicables;
- `cancel_regular` cancela únicamente el ensayo regular;
- un ensayo extra puede existir el propio día del concierto;
- el calendario incluye ensayos regulares, extras, bloqueos y concierto.

Las pruebas automatizadas de estos casos pasan en Alpha 6.

## Android

El APK sigue usando la misma interfaz web dentro de WebView, sin alarmas ni Bridge local. En Alpha 6 se añadieron dos capacidades necesarias para mantener equivalencia práctica:

- selector nativo de archivos para importar JSON desde Configuración;
- puente nativo para guardar Word, CSV y JSON en `Descargas/Conductix`.

Se conserva el tratamiento de insets/barra de estado de Android.

## Diferencias deliberadas por plataforma

No se reproducen literalmente elementos propios de Tkinter/Windows cuando el navegador o Android ya proporcionan la función equivalente:

- identificador de ventana y geometría de Tkinter;
- botones específicos de copiar/cortar/pegar: los campos web usan edición estándar del sistema;
- selector persistente de carpeta de exportación de Windows: navegador gestiona descargas y Android usa `Descargas/Conductix`;
- SQLite deja de ser fuente principal: IndexedDB permite trabajo local y Supabase sincroniza.

Estas diferencias no representan pérdida de funcionalidad de gestión de Conductix.

## Validaciones realizadas

- comprobación de sintaxis de todos los módulos JavaScript;
- pruebas automatizadas de cálculo de planificación;
- generación de paquetes `.docx` y verificación de su estructura ZIP;
- revisión de metadatos Android, WebView, insets, selector de archivos y guardado de exportaciones;
- Service Worker incrementado a caché Alpha 6;
- versionado Android alineado a `2.0.0-alpha.6` / `versionCode 20006`.

## Validación que necesariamente queda en dispositivo real

El código Android está preparado para GitHub Actions, pero este entorno no contiene Android SDK/Gradle para producir y ejecutar localmente el APK. Tras subir Alpha 6 al repositorio, el workflow debe compilar `app-debug.apk`. La prueba final debe hacerse en el teléfono real: inicio de sesión, sincronización, importación JSON, exportación a Descargas y navegación/insets.
