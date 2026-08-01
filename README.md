[![Android CI](https://github.com/arburk/vscp/actions/workflows/android_ci.yml/badge.svg)](https://github.com/arburk/vscp/actions/workflows/android_ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=arburk_vscp&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=arburk_vscp)

#  [VSCP](http://vscp.ch/)
Pokertimer and sit'n'go tournament as native Android app to learn this technology

## Rational
Project was launched in order to learn more about native Android application development using kotlin.


## Installation

The app is not published in the Play Store. Signed APKs are attached to every
[GitHub Release](https://github.com/arburk/vscp/releases) and installed by sideloading.

Requirement: Android 7.1 (API 25) or newer.

1. Open the [latest release](https://github.com/arburk/vscp/releases/latest) and download
   `vscp-pokertimer-<version>.apk`.
2. Open the downloaded file. Android asks to allow "Install unknown apps" for the app you
   downloaded with (browser or file manager) - the permission is granted per source app
   since Android 8.
3. Optional integrity check against `SHA256SUMS.txt` from the same release:
   ```bash
   sha256sum -c SHA256SUMS.txt
   ```
4. Play Protect may warn on first start because the app does not come from the store.

Updates install straight over the existing app - same signing key, higher version code.
To get notified about new releases without a store, subscribe to the repository with
[Obtainium](https://github.com/ImranR98/Obtainium).

## Settings
### Common

| Setting         | Default Value | Description                                                                      |
|-----------------|---------------|----------------------------------------------------------------------------------|
| Round Minutes   | 12            | Defines the timer in minutes per blind level                                     |
| Warning Minutes | 1             | Plays a warning sound (if enabled) prior to next level. 0 Disables the function. |

## Credits

### Icons & Graphics

Icons are downloaded by [flaticon](https://www.flaticon.com/) and created by

- gungyoga04
- kliwir art
- Fathema Khanom
- justicon

### Sounds

Sounds are downloaded by [freesound.org](https://freesound.org/) with credits to

- [elliottdj](https://freesound.org/people/elliottdj/sounds/685903/) published
  under [Creative Commons 0 License](https://creativecommons.org/publicdomain/zero/1.0/).
