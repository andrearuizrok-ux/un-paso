# Un passo alla volta · Personalización para Simone · Versión 4.0

## Qué contiene

- **Bienvenida en italiano**, con opción para cambiar a español antes de elegir preferencias.
- **Personalización opcional:** Simone decide si quiere utilizar su nombre y qué temas activar: casa, trabajo, confianza, distancia. No hay diagnóstico ni recuerdos personales automáticos en el diario.
- **Mi brújula / La mia bussola:** preguntas guiadas y espacio para reconocer habilidades demostradas, necesidades y próximos pasos profesionales. El usuario decide qué guardar.
- Se conservan tareas, journaling, seguimiento, análisis descriptivo de patrones y copias JSON.
- Si Simone activa **Casa** en la bienvenida, se añaden como propuestas los pendientes de la lista compartida: **son editables y se pueden borrar**. Si omite la personalización, empieza sin pendientes. Los datos previos de v3 no se borran. Las prioridades no están verificadas.

## Privacidad y consentimiento

La información que ha compartido otra persona solo es una propuesta. La app **no presupone que Simone acepte esos datos**. Los temas personales se activan solo cuando él los elige en la bienvenida y pueden modificarse en Mis datos / I miei dati. Las entradas del diario empiezan vacías. No sincroniza ni transmite datos a un servidor. Sin embargo, **los datos se guardan localmente y NO están cifrados**; una persona con acceso al teléfono desbloqueado podría consultarlos. Evitar repositorios públicos si preocupa exponer los nombres de pendientes iniciales, que están incluidos en el código.

## Android APK

Este ZIP es un **proyecto Android listo para compilar**, no un APK. No se ha ejecutado la compilación Android ni la instalación real. Requisitos: Android Studio o GitHub Actions con Android SDK. El workflow de GitHub Actions está en `.github/workflows/compilar-apk.yml`; usar repo **privado**. Descargar el artifact `Un_paso_a_la_vez_Simone_v4_APK` y extraer `app-debug.apk`. Los APK debug creados en distintas máquinas/ejecuciones pueden tener **firmas diferentes**: quizás no puedan actualizar un APK anterior sin desinstalarlo. **Exportar el diario antes de cambiar de instalación**.

El paquete `com.unpaso.alavez` es el mismo del proyecto 3.0. El versionCode aumenta a 4 y los datos locales deberían conservarse *solo si* se instala una actualización compatible firmada con la misma clave. **No se ha probado esta migración en un dispositivo**.

## PWA

Publicar los archivos de `Un_paso_a_la_vez_Simone_v4_PWA` en un alojamiento HTTPS. La PWA y el APK **no comparten** datos del diario automáticamente; exportar JSON e importar si se cambia de app/dispositivo. Mantener respaldos periódicos en un lugar privado.

## Limitaciones

No reemplaza terapia ni atención de salud mental. Las preguntas son ejercicios generales de reflexión, las categorías reflejan elecciones del usuario y los patrones son descriptivos, no inferencias clínicas. No hay motor de IA, sincronización, cifrado, ni notificaciones automáticas.
