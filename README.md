# JUBA Android Apps

JUBA is a three-application Android platform composed of:

- **UserApp** — customer-facing application.
- **StoreApp** — merchant/store application.
- **ProviderApp** — service/provider application.

## Project structure

```text
JUBA_android_app/
├── UserApp/
├── StoreApp/
└── ProviderApp/
```

Each application is maintained as an independent Gradle Android project so it can be opened and built directly in Android Studio.

## Build

From PowerShell on Windows:

```powershell
cd UserApp
.\gradlew.bat assembleDevDebug

cd ..\StoreApp
.\gradlew.bat assembleDevDebug

cd ..\ProviderApp
.\gradlew.bat assembleDevDebug
```

The repository also contains a GitHub Actions matrix that builds all three applications on every push and pull request to `main`.

## Release APKs

Production APKs are kept under each app's `app/prod/release/` directory. Generated Gradle build directories remain ignored to prevent committing build intermediates and caches.

## Development direction

The modernization work focuses on a consistent JUBA visual language, safer build/release practices, shared UX principles, performance, reliability, and preserving the existing business flows while incrementally replacing fragile legacy implementation details.
