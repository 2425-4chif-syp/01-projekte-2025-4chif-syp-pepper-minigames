# Repository Guidelines

## Project Structure & Module Organization
This repository is a monorepo for Pepper-based retirement-home apps. Main areas:

- `pep-caretaker/frontend`: Angular caretaker UI (`src/app`, `src/assets`, `*.spec.ts`).
- `pep-caretaker/person-entry-app`: Angular app for person entry.
- `pep-caretaker/PepperKotlin_Frontend`: Android/Kotlin Pepper clients such as `MemoryGamePepper`, `mmg`, `TicTacToe`, and `smallTalk`.
- `pep-mealplan/backend/pep-mealplan-backend`: Quarkus backend with `entity`, `repository`, `service`, `resource`, and `resource/dto` packages.
- `pep-mealplan/frontend`: Angular meal-plan frontend.
- `docs` and `asciidocs`: project documentation, diagrams, and wireframes.

Keep changes scoped to the affected subproject; this repo does not use one global build.

## Build, Test, and Development Commands
Run commands from the relevant module directory:

- `npm install && npm start` in an Angular app: starts the dev server.
- `npm run build`: production or default Angular build.
- `npm test`: runs Karma/Jasmine unit tests.
- `./mvnw quarkus:dev` in `pep-mealplan/backend/pep-mealplan-backend`: starts the Quarkus API with live reload.
- `./mvnw test`: runs Quarkus JUnit 5 and RestAssured tests.
- `./gradlew assembleDebug` in an Android app: builds a debug APK.
- `./gradlew test` or `./gradlew connectedAndroidTest`: runs JVM or device tests.

## Coding Style & Naming Conventions
Use the style already present in each module:

- TypeScript/HTML/CSS: 2-space indentation, Angular standalone components, kebab-case filenames like `tagalongstory.component.ts`.
- Java: 4-space indentation, PascalCase classes, package names under `com.pep...`, DTOs suffixed with `DTO`.
- Kotlin: 4-space indentation, PascalCase composables/classes, keep screen files under `ui/screens`.

Prefer descriptive component/service names and keep REST resources thin by pushing logic into services.

## Testing Guidelines
Place Angular tests beside the unit under test as `*.spec.ts`. Quarkus tests belong in `src/test/java`; integration-style tests follow the `*IT.java` suffix already present. Android tests use `app/src/test` for JVM tests and `app/src/androidTest` for device tests. Add or update tests for behavior changes; no repository-wide coverage gate is configured.

## Commit & Pull Request Guidelines
Recent history follows Conventional Commit style such as `feat: ...`, `fix: ...`, and scoped forms like `fix(frontend): ...`. Keep subjects short and imperative. PRs should include:

- a brief summary of the changed module(s),
- linked issue or task ID when available,
- test evidence (`npm test`, `./mvnw test`, Gradle test output),
- screenshots for Angular or Pepper UI changes.
