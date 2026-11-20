# UN PASO A LA VEZ — Aplicación Android v3.0

## Qué contiene

Proyecto de Android Studio para convertir el prototipo web v3.0 en una aplicación Android real mediante una **WebView de contenido empaquetado**.

- Interfaz y datos locales de la versión 3.0, en español e italiano.
- Diario, seguimientos, identificación de patrones y tareas tal como estaban en la PWA.
- Todos los archivos de la interfaz incluidos dentro de la app (funciona sin servidor web).
- Importar y exportar copias JSON a través del selector nativo de archivos Android.
- Origen web interno HTTPS interceptado por una lista cerrada de recursos locales (sin conexiones de la app a servicios externos).
- Los enlaces externos se abren en el navegador, fuera de la aplicación.
- Los datos se almacenan sin cifrado en el espacio privado de WebView; desactivadas las copias de seguridad automáticas Android.

**¡Importante! ESTE ZIP NO ES UN APK COMPILADO.** Es el proyecto fuente que permite obtener el archivo `app-debug.apk` cuando se compile en un equipo con Android SDK. No se ha compilado ni probado en un dispositivo en este entorno.

## Opción A: obtener el APK con GitHub Actions (sin instalar Android Studio)

1. Crea un repositorio **PRIVADO** nuevo en GitHub, por ejemplo `un-paso-android`.
2. Extrae este ZIP y sube **todos** los archivos y carpetas a la raíz del repositorio, incluyendo `.github/workflows/compilar-apk.yml` (carpeta oculta; verifica que se suba).
3. Abre `Actions` > `Compilar APK Android` > `Run workflow` (o haz un push a `main`).
4. Si la compilación pasa, abre la ejecución y descarga el artefacto llamado `Un_paso_a_la_vez_v3_APK`.
5. Descomprime el artefacto descargado: contiene `app-debug.apk`.
6. Pasa el APK a un teléfono Android propio y ábrelo para instalar. Android puede pedir habilitar el permiso de instalar desde esa fuente. Revisa que la procedencia sea de confianza.

No publiques el repositorio salvo que quieras compartir el código; el código contiene **ejemplos de preocupaciones personales** precargados (no tus diarios reales).

**La automatización GitHub Actions aún no se ha ejecutado ni verificado.** Requiere acceso a Internet en el runner para descargar Android SDK y Gradle y puede requerir ajustes si los servicios cambian.

## Opción B: compilar localmente en Android Studio

1. Instala Android Studio con Android SDK API 35 y Java 17.
2. Abre la carpeta extraída como proyecto Gradle existente.
3. Configura Gradle versión **8.9** (Android Gradle Plugin **8.7.3**). Si Android Studio te solicita generar un Gradle Wrapper, hazlo con Gradle 8.9. El ZIP no contiene `gradle-wrapper.jar` porque el entorno donde se generó no tiene Gradle instalado.
4. Usa `Build` > `Build Bundle(s) / APK(s)` > `Build APK(s)`.
5. Archivo generado: `app/build/outputs/apk/debug/app-debug.apk`.

`app-debug.apk` es una compilación de desarrollo firmada con la clave de depuración creada en el ordenador de compilación. Para distribuir públicamente, genera una compilación release con una clave de firma propia y consérvala de forma segura.

## Datos y compatibilidad

- Si antes usabas la PWA en Chrome, **los datos no se transfieren automáticamente al APK**, porque son almacenamientos distintos.
- Primero exporta el `.json` desde la PWA, luego utiliza `Mis datos` > `Importar` dentro de la app Android.
- Los datos nuevos se guardarán en el almacenamiento del WebView de la aplicación en ese teléfono.
- Desinstalar la app o borrar sus datos destruye los registros si no tienes copia exportada.
- Al actualizar APK, deben conservarse **applicationId, firma y almacenamiento**. No se puede actualizar directamente si cambia la clave de firma.
- La app es personal: no incluye diagnóstico, terapia, sincronización entre dispositivos ni cifrado de diario.

## Comprobaciones locales realizadas

- Preservación de la interfaz y funcionalidades JavaScript v3 (mediante comparación contra el HTML original).
- Validación sintáctica del JavaScript, estructura de archivos, iconos y XML.
- Pruebas funcionales web originales de la versión 3.0 ejecutadas sobre la versión de navegador.
- Sin compilación de APK ni prueba en teléfono: **siguen pendientes**.

## Seguridad

No introduzcas diarios personales en un repositorio. El código es independiente de los registros locales de la persona. Android `allowBackup=false` evita el backup cloud de datos del app, de modo que la exportación manual es importante. Los datos `localStorage` de WebView no están cifrados con una clave de aplicación adicional. Los enlaces externos se abren en el navegador. La app solicita permiso `INTERNET` para permitir WebView HTTPS interceptada, pero la interfaz usa recursos locales y está configurada para no acceder a servicios externos.
