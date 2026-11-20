# Un passo alla volta — v5 (source Android)

Esta edición conserva el paquete Android `com.unpaso.alavez` y el almacenamiento de la versión 4.
Novedades: inicio simplificado, nota rápida, ejemplos neutros, actualizaciones en cada tarea, edición de hitos y copia de seguridad compatible.

**No incluye APK compilado ni recordatorios nativos:** requiere compilar y probar en un teléfono.
Para publicar una nueva versión, sube las carpetas `app` y los archivos Gradle al repositorio existente y conserva el `main.yml` de GitHub Actions que ya ha funcionado. No sustituyas ni elimines el workflow existente.

**Importante:** haz una copia de seguridad JSON desde la versión instalada antes de actualizar. Los APK debug generados por GitHub pueden usar claves de firma distintas entre ejecuciones y no permitir una actualización directa. En ese caso, importar el JSON tras instalar de nuevo protege el progreso.
