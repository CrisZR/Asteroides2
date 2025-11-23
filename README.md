# Proyecto Integrador de Aprendizaje (PIA): Asteroides

## Descripción del proyecto

"Asteroides" es una adaptación para Android del clásico juego de arcade. Este proyecto fue desarrollado como un Proyecto Integrador de Aprendizaje (PIA) para aplicar y demostrar los conocimientos en desarrollo de aplicaciones móviles.

### Problema y propósito

El propósito principal es construir una aplicación de juego funcional para Android que no solo sea entretenida, sino que también sirva como un caso de estudio práctico de los componentes clave del desarrollo en Android. El proyecto resuelve el desafío de implementar un bucle de juego, renderizado de gráficos, manejo de interacciones del usuario y persistencia de datos en la plataforma.

### Alcance y contexto

El alcance del proyecto incluye:
- Un menú principal para la navegación.
- Una pantalla de juego con una nave controlable, asteroides y misiles.
- Un sistema de puntuación.
- Múltiples métodos para almacenar las puntuaciones más altas.
- Una pantalla de preferencias para configurar el tipo de almacenamiento y otras opciones del juego.
- Una sección informativa "Acerca de".

## Arquitectura interna

El proyecto sigue una arquitectura de aplicación Android estándar, con una separación clara entre la interfaz de usuario (gestionada por Activities y XML layouts) y la lógica del juego (encapsulada en una vista personalizada).

### Estructura de carpetas

La estructura del proyecto se organiza de la siguiente manera para separar el código fuente, los recursos y los scripts de construcción.

```
Asteroides2/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/asteroides/  # Código fuente principal
│   │   │   │   ├── VistaJuego.java           # Lógica y renderizado del juego
│   │   │   │   ├── MainActivity.java         # Actividad del menú principal
│   │   │   │   ├── AlmacenPuntuaciones.java  # Interfaz para persistencia de datos
│   │   │   │   └── ... (otras clases y activities)
│   │   │   ├── res/                          # Recursos de la aplicación
│   │   │   │   ├── drawable/                 # Gráficos (VectorDrawable)
│   │   │   │   ├── layout/                   # Diseños de UI (XML)
│   │   │   │   ├── anim/                     # Animaciones
│   │   │   │   ├── values/                   # Strings, colores, estilos
│   │   │   │   └── xml/                      # Preferencias
│   │   │   └── AndroidManifest.xml           # Manifiesto de la aplicación
│   │   ├── test/                             # Tests unitarios
│   │   └── androidTest/                      # Tests de instrumentación
│   └── build.gradle.kts                      # Configuración de build del módulo 'app'
├── gradle/                                   # Scripts y dependencias de Gradle
└── build.gradle.kts                          # Configuración de build del proyecto raíz
```

### Tecnologías

- **Lenguaje**: Java
- **Plataforma**: Android SDK
- **Build System**: Gradle
- **Gráficos**: Se utilizan `VectorDrawable` para los elementos del juego (nave, asteroides), lo que garantiza un escalado nítido en cualquier densidad de pantalla. El renderizado se realiza sobre un `Canvas`.
- **Persistencia de Datos**: Se exploran múltiples enfoques a través de una interfaz común:
    - `SharedPreferences`
    - Ficheros en almacenamiento interno y externo.
    - Serialización a JSON con `Gson`.
    - `Firebase Realtime Database`.
    - Recursos `raw` y `assets`.

### Comunicación entre módulos

- **Activities y Navegación**: La navegación entre pantallas (del menú al juego, a las puntuaciones, etc.) se gestiona mediante `Intents`.
- **Lógica de Juego y UI**: La `Activity` que aloja el juego se comunica con `VistaJuego` (la vista personalizada) a través de métodos públicos para pausar, reanudar o destruir el hilo de juego según el ciclo de vida de la `Activity`.
- **Persistencia de Datos**: La lógica de la aplicación no interactúa directamente con un método de almacenamiento específico, sino con la interfaz `AlmacenPuntuaciones`. Una clase "fábrica" o el menú de preferencias determina qué implementación concreta se utilizará en tiempo de ejecución.

### Decisiones técnicas

1.  **Vista de Juego Personalizada (`VistaJuego`)**: Se decidió encapsular toda la lógica del juego (bucle principal, física, renderizado) en una clase que hereda de `View`. Esto proporciona un control total sobre el `Canvas` y permite gestionar un hilo de ejecución (`Thread`) independiente para el bucle del juego, separándolo del hilo principal de la UI.
2.  **Interfaz de Persistencia (`AlmacenPuntuaciones`)**: Para demostrar y desacoplar los diferentes métodos de guardado, se creó una interfaz. Esto permite cambiar el sistema de almacenamiento (Firebase, SharedPreferences, JSON, etc.) de forma dinámica desde las preferencias de la aplicación sin alterar el resto del código.
3.  **Gráficos Vectoriales**: El uso de `VectorDrawable` en lugar de imágenes de mapa de bits (PNG, JPG) es una decisión clave para mantener la calidad visual en una amplia gama de dispositivos Android y para reducir el tamaño del APK.

## Instalación y Ejecución

### Instalación

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/CrisZR/Asteroides2.git
    ```
2.  **Abrir en Android Studio**:
    - Abrir el Android Studio.
    - Selecciona "Open an existing project" y elige la carpeta del repositorio.
    - Android Studio se encargará de sincronizar el proyecto con Gradle.

### Ejecución

- Conecta un dispositivo físico o inicia un Android Virtual Device (AVD).
- Selecciona el dispositivo en la barra de herramientas.
- Haz clic en el botón **Run 'app'** (▶️) o usa el atajo `Shift + F10`.

### Tests

El proyecto incluye tests de instrumentación básicos. Para ejecutarlos:
1.  En la vista de proyecto de Android Studio, haz clic derecho sobre la carpeta `app/src/androidTest/java`.
2.  Selecciona **Run 'Tests in com.example.asteroides'**.
3.  Los tests se ejecutarán en el emulador o dispositivo conectado.

## Contribución

Este es un proyecto académico, pero si deseas contribuir o proponer mejoras, puedes hacerlo siguiendo el flujo estándar de GitHub:
1.  Haz un **Fork** del repositorio.
2.  Crea una nueva rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3.  Realiza tus cambios y haz **commit** (`git commit -m 'Añade nueva funcionalidad'`).
4.  Haz **push** a tu rama (`git push origin feature/nueva-funcionalidad`).
5.  Abre un **Pull Request**.

## Diagramas

### Diagrama de Arquitectura
+-----------------------------------------------------------------------+
|                       CAPA DE PRESENTACIÓN (UI)                       |
|                                                                       |
|   [MainActivity] +--------+                                           |
|          |                |                                           |
|          +--------------->+-----> [PuntuacionesActivity]              |
|          |                |                |                          |
|          +--------------->+-----> [PreferenciasActivity]              |
|          |                                                            |
|          v                                                            |
|   [JuegoActivity]                                                     |
|          |                                                            |
|          +---(Contiene)---> [VistaJuego] <-------+                    |
|                                    |             |                    |
+------------------------------------|-------------|--------------------+
                                     |             |
                                 (Usa Hilo)    (Renderiza)
                                     |             |
+------------------------------------v-------------|--------------------+
|                         CAPA LÓGICA DE JUEGO     |                    |
|                                                  |                    |
|                            [ThreadJuego] --------+                    |
|                                  |                                    |
|                             (Actualiza)                               |
|                                  v                                    |
|                   [Modelos: Nave, Asteroide, Misil]                   |
|                                  |                                    |
+----------------------------------|------------------------------------+
                                   |
                               (Escribe)
                                   v
+-----------------------------------------------------------------------+
|                            CAPA DE DATOS                              |
|                                                                       |
|   [AlmacenPuntuaciones] <----(Lee)------- [PuntuacionesActivity]      |
|      (Interfaz)                                                       |
|          ^                                                            |
|          |                                                            |
|          +-------+---------------------+---------------------+        |
|                  |                     |                     |        |
|    [AlmacenPuntuaciones   [AlmacenPuntuaciones   [AlmacenPuntuaciones |
|       Preferencias]            Firebase]                 Gson]        |
|                                                                       |
+-----------------------------------------------------------------------+

### Diagrama de Secuencia
Usuario      MainActivity    JuegoActivity     VistaJuego     ThreadJuego   AlmacenPunt.
   |              |               |                |               |             |
   |---("Jugar")--|               |                |               |             |
   |----(Click)-->|               |                |               |             |
   |              |--startActiv.->|                |               |             |
   |              |               |----(Crea)----->|               |             |
   |              |               |---onResume()-->|               |             |
   |              |               |                |--(new Thread)>|             |
   |              |               |                |---(start)---->|             |
   |              |               |                |               |             |
   |              |               |                |   [== BUCLE DE JUEGO ==]    |
   |              |               |                |   |           |             |
   |              |               |                |   |<-(Update)-|             |
   |              |               |                |   |-(Redibuja)|             |
   |              |               |                |   [====================]    |
   |              |               |                |               |             |
   |              |               |           (Colisión / Fin)     |             |
   |              |               |                |               |             |
   |              |               |                |<--(Fin Hilo)--|             |
   |              |               |<---(finish)----|               |             |
   |              |               |                |               |             |
   |              |               |--guardarPunt.------------------------------->|
   |              |               |                                              |
   |              |               |<--------------------(OK)---------------------|
   |              |               |                |               |             |
   |              |<--(Retorno)---|                |               |             |
   |              |               |                |               |             |

## Resumen técnico

El sistema se centra en `VistaJuego`, que gestiona un hilo (`ThreadJuego`) para el bucle principal. En cada iteración, este hilo llama a `updateFisica()` para mover los objetos y detectar colisiones, y luego a `dibujaGraficos()` para pintar el estado actual en el `Canvas`. Las interacciones del usuario (sensores, pantalla táctil) son capturadas por la `Activity` y delegadas a `VistaJuego`. Las puntuaciones se guardan a través de la interfaz `AlmacenPuntuaciones`, cuya implementación se selecciona dinámicamente.

## FAQ

**¿Necesito un fichero `google-services.json` para ejecutar el proyecto?**
No es estrictamente necesario. Si no provees este fichero, la opción de persistencia con Firebase no funcionará. Sin embargo, puedes ir a `Preferencias` dentro de la app y seleccionar otro método de almacenamiento (como "Preferencias" o "Fichero interno") para que la app sea completamente funcional.

**¿Qué versión de Android es compatible?**
El proyecto está configurado para una versión mínima de API 24 (Android 7.0 Nougat), lo que cubre a la gran mayoría de dispositivos activos hoy en día.
