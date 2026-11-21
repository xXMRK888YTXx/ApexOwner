# ApexOwner Agent Notes

## Project conventions

- The project is a multi-module Android/Kotlin application using Jetpack Compose.
- Keep user-visible strings in both `values/strings.xml` and `values-en/strings.xml`.
- Build from the repository root with `./gradlew.bat` on Windows.
- Do not modify unrelated user changes in the working tree.

## Application permission policies

Per-application permission policy is represented by `AppPermissionPolicy` in both the core and feature layers:

1. `DEFAULT` — follow the global permission policy. If the permission is globally disabled, the app receives a denied grant state.
2. `USER_CONTROLLED` — apply `PERMISSION_GRANT_STATE_DEFAULT` for this app, so the user/system controls the permission and the global deny rule is not reapplied to this package.
3. `FORCE_DENIED` — always apply `PERMISSION_GRANT_STATE_DENIED`.
4. `FORCE_GRANTED` — always apply `PERMISSION_GRANT_STATE_GRANTED`, subject to Android and `Profile Owner` limitations.

When changing the global policy, preserve `USER_CONTROLLED`, `FORCE_DENIED`, and `FORCE_GRANTED` exceptions. Do not treat `DEFAULT` and `USER_CONTROLLED` as the same state.

Sensitive permissions such as camera, microphone, and location cannot be force-granted by Android in `Profile Owner` mode. The UI must keep `FORCE_GRANTED` unavailable in that mode while allowing `USER_CONTROLLED`.

## Validation checklist

- Run `./gradlew.bat :app:assembleDebug --no-daemon`.
- Run `./gradlew.bat :core:device-policy:test --no-daemon` for the permission-policy unit tests.
- Verify all four policy modes are rendered and persisted for an app.
- In `Device Owner` mode, disable a permission globally, set one app to `FORCE_GRANTED`, and verify the exception remains granted after reapplying policies.
- Set another app to `USER_CONTROLLED` and verify its grant state returns to `PERMISSION_GRANT_STATE_DEFAULT` despite the global deny rule.
- In `Profile Owner` mode, verify sensitive `FORCE_GRANTED` is blocked with an explanation, while the other three modes remain available.
- Record device-specific limitations in the final task report.

## Latest device verification

- Debug APK was installed on device `df4062bb` for personal user `0`; the app starts successfully.
- The permission screen renders `Default`, `User`, `Deny`, and `Allow`.
- On the tested Profile Owner setup (work-profile user `13`), `Allow` is disabled for camera and microphone while `User` remains available. This is an Android platform restriction, not an application error.
