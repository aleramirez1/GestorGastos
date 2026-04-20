# GestorGastos

App Android para gestionar gastos compartidos entre amigos, familia o trabajo.

## Funcionalidades

- Splash Screen con verificación de autenticación
- Login y Registro de usuarios
- Crear grupos de gastos
- Registrar gastos indicando quien pagó y cuánto
- Ruleta para seleccionar quién paga extra
- Ver personas que deben dinero extra
- Notificaciones push con Firebase Cloud Messaging
- Captura de fotos de tickets
- Vibración y flash al crear grupos
- Base de datos local con Room (Offline-First)

## Tecnologías

- Kotlin
- Jetpack Compose
- MVVM + Clean Architecture
- Hilt (Dependency Injection)
- Room Database
- Retrofit para API REST
- Firebase Cloud Messaging
- CameraX
- StateFlow para gestión de estados

## Arquitectura

### Clean Architecture + MVVM

Cada feature tiene:
- **Domain**: Entities, Repositories (interfaces), Use Cases
- **Data**: Repositories (implementaciones), Data Sources
- **Presentation**: Screens (Composables), ViewModels
- **DI**: Módulos de Hilt
- **Navigation**: NavGraphs

### Features Implementadas

1. **Splash** - Pantalla de carga inicial
2. **Login** - Autenticación de usuarios
3. **Registro** - Registro de nuevos usuarios
4. **Grupos** - Gestión de grupos y gastos
5. **Ruleta** - Selección aleatoria de ganador
6. **Personas** - Lista de ganadores de ruleta

## Requisitos

- Android Studio Hedgehog o superior
- JDK 11
- Android SDK 26 (Android 8.0) o superior
- Dispositivo/Emulador con Google Play Services

## Configuración

### 1. Clonar el repositorio

```bash
git clone [URL_DEL_REPO]
cd GestorGastos
```

### 2. Configurar Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un proyecto o usa uno existente
3. Agrega una app Android con package: `com.example.gestorgastos`
4. Descarga `google-services.json`
5. Colócalo en: `app/google-services.json`

### 3. Sync Gradle

En Android Studio:
```
File > Sync Project with Gradle Files
```

## Generar APK

### Opción 1: Android Studio

1. **Build > Generate Signed Bundle / APK**
2. Selecciona **APK** > Next
3. Keystore: `app/keystore/release.keystore`
4. Password: `gestorgastos2024`
5. Alias: `gestorgastos`
6. Create

### Opción 2: Línea de Comandos

```bash
# Windows
.\gradlew assembleRelease

# Linux/Mac
./gradlew assembleRelease
```

El APK se generará en:
```
app/build/outputs/apk/release/app-release.apk
```

O cópialo a la carpeta `apk/` en la raíz del proyecto.

## Instalar APK

### Con ADB (Dispositivo conectado)

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### Transferir al dispositivo

1. Copia el APK al dispositivo
2. Abre el archivo en el dispositivo
3. Permite instalación desde fuentes desconocidas
4. Instala

## Probar la App

### Notificaciones Locales (Sin Internet)

1. Crea un grupo → Verás notificación "Grupo Creado"
2. Gira la ruleta → Verás notificación del ganador

### Notificaciones Remotas (Con Internet)

1. Inicia sesión
2. Obtén el FCM token de Logcat: `adb logcat | grep "FCM Token"`
3. Ve a Firebase Console > Messaging
4. Envía mensaje de prueba con el token

### Hardware

- **Vibración**: Al crear grupo
- **Flash**: Parpadea 3 veces al crear grupo
- **Cámara**: Captura foto del ticket
- **Rotación**: Habilitada en ruleta

## Estructura del Proyecto

```
app/
├── src/main/java/com/example/gestorgastos/
│   ├── core/
│   │   ├── database/          # Room Database
│   │   ├── di/                # Módulos Hilt
│   │   ├── hardware/          # Managers de hardware
│   │   ├── navigation/        # Navegación
│   │   ├── network/           # Retrofit API
│   │   └── notifications/     # Firebase FCM
│   └── features/
│       ├── splash/            # Pantalla de carga
│       ├── login/             # Autenticación
│       ├── registro/          # Registro
│       ├── grupos/            # Gestión de grupos
│       ├── ruleta/            # Ruleta
│       └── personas/          # Lista de ganadores
├── keystore/                  # Keystore para firma
└── google-services.json       # Credenciales Firebase
```

## Credenciales

### Keystore
- **Ubicación**: `app/keystore/release.keystore`
- **Store Password**: `gestorgastos2024`
- **Key Alias**: `gestorgastos`
- **Key Password**: `gestorgastos2024`

### API Backend
- **URL**: `http://54.80.212.46:8000/`

## Características Técnicas

### Inyección de Dependencias
- Hilt en todas las features
- Módulos: Database, Network, Hardware, Notifications

### Base de Datos
- Room con estrategia Offline-First
- Type Converters para tipos complejos
- OnConflictStrategy.REPLACE para upsert

### Gestión de Estados
- StateFlow en todos los ViewModels
- Patrón UDF (Unidirectional Data Flow)
- Single Source of Truth

### Hardware Utilizado
1. Cámara (CameraX)
2. Vibrador
3. Linterna/Flash
4. Rotación de pantalla
5. Activity Lifecycle

### Push Notifications
- Firebase Cloud Messaging
- Notificaciones locales y remotas
- 3 canales: default, grupos, ruleta
- Topics: user_{id}, all_users

## Licencia

Este proyecto es de uso educativo.
