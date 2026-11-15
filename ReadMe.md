# ApexOwner

> **Alpha version:** the application is still under active development and may be unstable. Some features may be incomplete, change without notice, or not work on every device.

ApexOwner is an Android application for managing apps, permissions, and Android Enterprise policies across personal and work profiles.

The application supports two operating modes:

- `Device Owner` — manages the entire device and personal profile;
- `Profile Owner` — manages the work profile only.

> This project is intended for testing and personal use. The `Device Owner` role gives the application extensive control over the device. Back up important data before setup.

## Features

### Profiles and apps

- create and remove a work profile;
- browse apps in personal and work profiles;
- install, launch, freeze, and unfreeze apps;
- clone apps between profiles;
- search apps and refresh lists with pull-to-refresh;
- manage system apps and unavailable features.

### Policies and automation

- `One-shot` — temporarily launch an app and freeze it afterward;
- `Auto-freeze` — automatically freeze an app after inactivity;
- wipe app data after a period of inactivity;
- configure separate freeze and data-reset timers;
- configure behavior on screen lock;
- restore timers after a device reboot.

### Permissions and restrictions

- manage application permissions;
- configure cross-profile interaction;
- restrict camera, microphone, USB, external storage, and other functions;
- explain whether a restriction is available on the current device and owner mode.

The availability of individual policies depends on the Android version, device manufacturer, firmware, and whether the app is running as `Device Owner` or `Profile Owner`.

## Build

Open the project in Android Studio and wait for Gradle synchronization. From the project root, run:

```powershell
.\gradlew.bat assembleDebug
```

The APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Useful commands:

```powershell
# Run unit tests
.\gradlew.bat test

# Install the debug APK
.\gradlew.bat installDebug
```

The current release build uses the debug signing configuration. Configure a private signing key and a separate release configuration before publishing.

## Install with ADB

Check the connected devices:

```powershell
adb devices
```

If multiple devices are connected, use `-s <DEVICE_ID>`:

```powershell
adb -s <DEVICE_ID> install -r -g app/build/outputs/apk/debug/app-debug.apk
adb -s <DEVICE_ID> shell am start -n com.xxmrk888ytxx.apexowner/.view.mainActivity.MainActivity
```

Stop the application with:

```powershell
adb -s <DEVICE_ID> shell am force-stop com.xxmrk888ytxx.apexowner
```

## Initial Device Owner setup

Assigning `Device Owner` is normally performed on a new device or after a factory reset. The device must not contain users, work profiles, or accounts that block provisioning.

1. Enable USB debugging in Developer options.
2. Install the debug APK.
3. Verify the connection with `adb devices`.
4. Assign the device owner:

   ```powershell
   adb shell dpm set-device-owner com.xxmrk888ytxx.apexowner/com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver
   ```

5. Launch ApexOwner and configure the work profile from the app.

List users and profiles with:

```powershell
adb shell pm list users
```

If provisioning fails, check for guest users, secondary users, an existing work profile, and manufacturer accounts. Do not remove the primary user `0`.

## Remove Device Owner and the work profile

Disabling `Device Owner` is available in the app Settings screen when supported by the current mode:

```powershell
adb shell dpm remove-active-admin com.xxmrk888ytxx.apexowner/com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver
```

The work profile can be removed from the Settings screen. This deletes all apps and data inside the work profile.

> Removing an owner or profile is destructive for the affected profile. Verify the target user and back up any required data first.

## Diagnostics

```powershell
# List users
adb shell pm list users

# Device and profile owner state
adb shell dumpsys device_policy

# Application and policy service logs
adb logcat -s ApexPolicyService:V OneShotAppManagerOwnerComponent:V

# Clear the logcat buffer
adb logcat -c

# Capture a screenshot
adb exec-out screencap -p > screen.png
```

Inspect the UI tree with:

```powershell
adb shell uiautomator dump /sdcard/window.xml
adb pull /sdcard/window.xml .\window.xml
```

## Project structure

The project uses a multi-module Gradle structure:

| Module | Purpose |
| --- | --- |
| `app` | Application entry point, navigation, settings, policy service, and bridge components |
| `core:device-policy` | `DeviceAdminReceiver` and Android Device Policy API integration |
| `core:preferences-storage` | DataStore-based settings storage |
| `core:base:android` | Shared Android utilities, ViewModels, and logging |
| `core:base:compose` | Shared Compose components and theme |
| `feature:main` | Main screen and profile state |
| `feature:onboarding` | Onboarding components; display is temporarily disabled |
| `feature:management-module:app-restriction` | Device, profile, and application permission restrictions |
| `feature:management-module:work-profile` | Work profile and application management |
| `common` | Shared project components |

## Technology stack

- Kotlin 2.2;
- Jetpack Compose and Material 3;
- Android Gradle Plugin 9.2;
- Gradle Version Catalog;
- Hilt;
- Kotlin Serialization;
- AndroidX DataStore;
- AndroidX Navigation 3;
- Kotlin Coroutines;
- Timber.

---

# Русская версия

> **Альфа-версия:** приложение всё ещё находится в активной разработке и может работать нестабильно. Некоторые функции могут быть неполными, меняться без предупреждения или не работать на отдельных устройствах.

ApexOwner — Android-приложение для управления приложениями, разрешениями и политиками Android Enterprise в личном и рабочем профилях.

Приложение поддерживает два режима работы:

- `Device Owner` — управление всем устройством и личным профилем;
- `Profile Owner` — управление только рабочим профилем.

> Проект предназначен для тестирования и личного использования. Роль `Device Owner` даёт приложению расширенный контроль над устройством. Перед настройкой сделайте резервную копию важных данных.

## Возможности

### Управление профилями и приложениями

- создание и удаление рабочего профиля;
- просмотр приложений личного и рабочего профиля;
- установка, запуск, заморозка и разморозка приложений;
- клонирование приложений между профилями;
- поиск приложений и обновление списков свайпом вниз;
- управление системными приложениями и недоступными функциями.

### Политики и автоматизация

- `One-shot` — временный запуск приложения с последующей заморозкой;
- `Auto-freeze` — автоматическая заморозка после неиспользования;
- сброс данных приложения после периода неактивности;
- отдельные таймеры заморозки и сброса данных;
- настройка поведения при блокировке экрана;
- восстановление таймеров после перезагрузки устройства.

### Разрешения и ограничения

- управление разрешениями приложений;
- настройка взаимодействия между профилями;
- ограничения камеры, микрофона, USB, внешних носителей и других функций;
- проверка доступности ограничений для конкретного устройства и роли владельца.

Доступность отдельных политик зависит от версии Android, производителя устройства и режима `Device Owner` или `Profile Owner`.

## Сборка

Откройте проект в Android Studio и дождитесь синхронизации Gradle. Из корня проекта выполните:

```powershell
.\gradlew.bat assembleDebug
```

APK будет создан здесь:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Полезные команды:

```powershell
# Запуск unit-тестов
.\gradlew.bat test

# Установка debug APK
.\gradlew.bat installDebug
```

Для release-сборки сейчас используется debug signing config. Перед публикацией настройте собственный ключ подписи и отдельную release-конфигурацию.

## Установка через ADB

Проверьте подключение устройства:

```powershell
adb devices
```

Если подключено несколько устройств, используйте `-s <DEVICE_ID>`:

```powershell
adb -s <DEVICE_ID> install -r -g app/build/outputs/apk/debug/app-debug.apk
adb -s <DEVICE_ID> shell am start -n com.xxmrk888ytxx.apexowner/.view.mainActivity.MainActivity
```

Для остановки приложения:

```powershell
adb -s <DEVICE_ID> shell am force-stop com.xxmrk888ytxx.apexowner
```

## Первоначальная настройка Device Owner

Назначение `Device Owner` обычно выполняется на новом устройстве или после сброса к заводским настройкам. На устройстве не должны оставаться пользователи, рабочие профили или аккаунты, блокирующие provisioning.

1. Включите «Отладку по USB» в настройках разработчика.
2. Установите debug APK.
3. Проверьте подключение командой `adb devices`.
4. Назначьте владельца устройства:

   ```powershell
   adb shell dpm set-device-owner com.xxmrk888ytxx.apexowner/com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver
   ```

5. Запустите ApexOwner и настройте рабочий профиль из интерфейса приложения.

Проверить пользователей и профили можно так:

```powershell
adb shell pm list users
```

Если provisioning не выполняется, проверьте наличие гостевых пользователей, второго пользователя, рабочего профиля и аккаунтов производителя. Не удаляйте основного пользователя `0`.

## Удаление Device Owner и рабочего профиля

Отключение `Device Owner` доступно во вкладке настроек приложения, если это поддерживается текущим режимом:

```powershell
adb shell dpm remove-active-admin com.xxmrk888ytxx.apexowner/com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver
```

Удаление рабочего профиля доступно во вкладке настроек. Оно удаляет все приложения и данные внутри рабочего профиля.

> Команды удаления и снятия владельца устройства необратимы для соответствующего профиля. Перед выполнением проверьте целевого пользователя и сохраните нужные данные.

## Диагностика

```powershell
# Список пользователей
adb shell pm list users

# Состояние владельцев устройства и профилей
adb shell dumpsys device_policy

# Логи приложения и сервисов политик
adb logcat -s ApexPolicyService:V OneShotAppManagerOwnerComponent:V

# Очистка буфера logcat
adb logcat -c

# Снимок экрана
adb exec-out screencap -p > screen.png
```

Для диагностики интерфейса:

```powershell
adb shell uiautomator dump /sdcard/window.xml
adb pull /sdcard/window.xml .\window.xml
```

## Структура проекта

Проект использует multi-module Gradle-структуру:

| Модуль | Назначение |
| --- | --- |
| `app` | Точка входа, навигация, настройки, сервис политик и bridge-компоненты |
| `core:device-policy` | `DeviceAdminReceiver` и Android Device Policy API |
| `core:preferences-storage` | Хранение настроек через DataStore |
| `core:base:android` | Общие Android-утилиты, ViewModel и логирование |
| `core:base:compose` | Общие Compose-компоненты и тема |
| `feature:main` | Главный экран и состояние профиля |
| `feature:onboarding` | Компоненты onboarding; показ временно отключён |
| `feature:management-module:app-restriction` | Ограничения устройства, профиля и разрешений |
| `feature:management-module:work-profile` | Рабочий профиль и управление приложениями |
| `common` | Общие компоненты проекта |

## Технологический стек

- Kotlin 2.2;
- Jetpack Compose и Material 3;
- Android Gradle Plugin 9.2;
- Gradle Version Catalog;
- Hilt;
- Kotlin Serialization;
- AndroidX DataStore;
- AndroidX Navigation 3;
- Kotlin Coroutines;
- Timber.