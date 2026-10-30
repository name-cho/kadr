<div align="center">

# Kadr

### Material 3 Expressive fork of Mihon

A full-featured manga, webtoon and comic reader for Android, forked from
[Mihon](https://github.com/mihonapp/mihon) and restyled with Material 3
Expressive — dynamic theming, floating navigation bar, expressive backdrop
shapes and a redesigned settings screen.

</div>

## What differs from Mihon

This fork is a UI/UX restyle, not a new feature set. Most changes live in the
presentation layer:

- **Material 3 Expressive theme** — expressive color scheme, updated
  typography and shapes (`app/src/main/java/eu/kanade/presentation/theme`).
- **Floating navigation bar** — a pill-shaped bar with an animated indicator
  on the active tab only, instead of a static bottom bar
  (`ExpressiveNavigationBar.kt`). It floats over the content instead of
  reserving space, collapses while you scroll down and comes back on the first
  upward drag, driven by a `NestedScrollConnection` in `HomeScreen.kt`.
- **Redesigned settings** — preferences are grouped into rounded cards with
  section labels and thin dividers, the way Telegram-style clients do it
  (`presentation/more/settings/widget`).
- **Hero backdrop shapes** — decorative shapes drawn behind the header of the
  "More" screen via `drawWithCache` (computed once per size, no per-frame
  cost).
- **Predictive back** — gesture-driven back animation using
  `androidx.navigationevent`, reads animated values in the draw phase so a
  gesture never recomposes the screen.
- **Rebranded to Kadr** — application id `app.kadr`, new adaptive launcher
  icon, notifications and splash use the Kadr "K" mark.

The reader, library, sources and all back-end logic are upstream Mihon code.

## Building

Requires JDK 17+ and the Android SDK. Set `JAVA_HOME` to a JDK (on Windows,
Android Studio's bundled JBR works) and:

```bash
# Debug
./gradlew :app:assembleDebug

# Release (minified, resources shrunk)
./gradlew :app:assembleRelease

# Release with the in-app updater enabled (checks GitHub releases)
./gradlew :app:assembleRelease -Pdist=github
```

The debug build id is `app.kadr.dev`, release is `app.kadr`.
A debug keystore is configured through `keystore.properties` at the project
root (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`) — see
`app/build.gradle.kts`.

## Continuous integration

- **GitHub Actions** (`.github/workflows/`): `release.yml` builds a release
  APK and creates a GitHub release on tags `v*`; `build.yml` builds and tests
  on pushes to `expressive-ui`.
- **GitLab CI** (`.gitlab-ci.yml`): builds a release APK on tags, kept as a
  CI artifact (for gitgud.io).

## Performance notes

The project ships baseline profiles
(`app/src/main/baselineProfiles`), R8 minification for release builds, and a
Coil memory cache sized to the device. Grids and lists use stable keys and
`@Immutable` models where applicable; image grids skip crossfade on low-RAM
devices to keep scrolling smooth.

## License

<pre>
Copyright © 2015 Javier Tomás
Copyright © 2024 Mihon Open Source Project
Copyright © 2026 Kadr

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>