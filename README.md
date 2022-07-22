atlas-build-unity-ios
============

[![Gradle Plugin ID](https://img.shields.io/badge/gradle-net.wooga.build--unity--ios-brightgreen.svg?style=flat-square)](https://plugins.gradle.org/plugin/net.wooga.build-unity-ios)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](https://raw.githubusercontent.com/wooga/atlas-build-unity-ios/master/LICENSE)
[![GitHub tag](https://img.shields.io/github/tag/wooga/atlas-build-unity-ios.svg?style=flat-square)]()
[![GitHub release](https://img.shields.io/github/release/wooga/atlas-build-unity-ios.svg?style=flat-square)]()

ios build plugin for unity exported Xcode projects

# Applying the plugin

**build.gradle**
```groovy
plugins {
    id 'net.wooga.build-unity-ios' version '0.1.0'
}
```

Documentation
=============

- [API docs](https://wooga.github.io/atlas-build-unity-ios/docs/api/)

Gradle and Java Compatibility
=============================

| Gradle Version  | Works  |
| :-------------: | :----: |
| < 5.1           | ![no]  |
| 5.1             | ![yes] |
| 5.2             | ![yes] |
| 5.3             | ![yes] |
| 5.4             | ![yes] |
| 5.5             | ![yes] |
| 5.6             | ![yes] |
| 5.6             | ![yes] |
| 6.0             | ![yes] |
| 6.1             | ![yes] |
| 6.2             | ![yes] |
| 6.3             | ![yes] |
| 6.4             | ![yes] |
| 6.5             | ![yes] |
| 6.6             | ![yes] |
| 6.6             | ![yes] |
| 6.7             | ![yes] |
| 6.8             | ![yes] |
| 6.9             | ![yes] |
| 7.0             | ![yes] |


Development
===========

[Code of Conduct](docs/Code-of-conduct.md)

LICENSE
=======

Copyright 2022 Wooga GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

<!-- Links -->
[gradle]:               https://gradle.org/ "Gradle"
[gradle_finalizedBy]:   https://docs.gradle.org/3.5/dsl/org.gradle.api.Task.html#org.gradle.api.Task:finalizedBy
[gradle_dependsOn]:     https://docs.gradle.org/3.5/dsl/org.gradle.api.Task.html#org.gradle.api.Task:dependsOn

[yes]:                  https://resources.atlas.wooga.com/icons/icon_check.svg "yes"
[no]:                   https://resources.atlas.wooga.com/icons/icon_uncheck.svg "no"
