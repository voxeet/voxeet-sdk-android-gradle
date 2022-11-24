# Gradle Configurations

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fvoxeet%2Fvoxeet-sdk-android-gradle%2Fbadge%3Fref%3Dmain&style=flat)](https://actions-badge.atrox.dev/voxeet/voxeet-sdk-android-gradle/goto?ref=main)

[![GitHub release](https://img.shields.io/github/release/voxeet/voxeet-sdk-android-gradle.svg)](https://gitHub.com/voxeet/voxeet-sdk-android-gradle/releases/)


Open Source configurations used on Android projects

## Install

In your root's settings.gradle, copy paste the following at the top :
Note : change the version to the specific tag, sha or branch you want to use

```groovy
def version = 'main'

apply from: "https://raw.githubusercontent.com/voxeet/voxeet-sdk-android-gradle/${version}/dolbyio.gradle"

dependencyResolutionManagement {
    versionCatalogs {
        dolbyio {
            from(files(gradle.dolbyio.files.catalog))
        }
    }
}
```

Note that if you already use a versionCatalogs, you'll need to copy the first line at the top of your gradle file and the `dolbyio{...}` block inside the **versionCatalogs**

## Use the configurations

Once the settings.gradle configuration is done, you can then use the result in two ways :

### Catalog / Dependencies / Plugins

The dependencies will be available in your project just like the Gradle documentation is giving you the ability to use dependencies, plugins aliases or even gradle namespaces. For more information about catalogs, please check the [Gradle documentation](https://docs.gradle.org/current/userguide/platforms.html)

For instance :

```groovy
dependencies {
  implementation dolbyio.androidx.appcompat
}
```

or for plugins :
```groovy
plugins {
  alias(dolbyio.plugins.dokka)
}
```

### Configuration files

In order for our projects to share most of the configuration like publishing, managing coverage and having lint validation, we are also providing those files. After your settings.gradle update, you then can use the following snippet anywhere required to use those files 

### gradle.dolbyio.files.SomeName

Where SomeName can be one of the following :

In the root project :

- **modules**  *(buildScript)* apply overall project configuration
- **versions**  *(buildScript)* apply some version-related variable like min sdk
- **jacocoProject** *(apply from)* apply jacoco main rules (rootProject)
- **dokka** *(apply from)* apply dokka rules
- **dependencyUpdates** *(apply from)* used to prevent non releases from our dependencies

For submodules :

- **moduleSetup** *(buildScript)* apply module-specific default configuration (submodule's buildScript)
- **publishing** *(apply from)* apply publication configuration (submodule)

**What to use in the main build.gradle**

Those are example of what can be included

```groovy
buildscript {
  apply from: gradle.dolbyio.files.modules
}

...

plugins {
  alias(dolbyio.plugins.dokka)
  alias(dolbyio.plugins.publish.nexus)
}

...


subprojects {
  ...
  if (isSourcesModule(it)) {
    apply from: gradle.dolbyio.files.moduleSetup
  }
}
...

apply from: gradle.dolbyio.files.jacocoProject
apply from: gradle.dolbyio.files.dependencyUpdates

//EOF
```

**What to use in the submodule's build.gradle**

```groovy
...

dependencies {
    androidTestImplementation dolbyio.androidx.espresso.core
    implementation dolbyio.eventbus
}

// if the project need publication. Note that it will then need to configure pom information
apply from: gradle.dolbyio.files.publishing

//EOF
```

## Try sample

inside the sample/ folder, run `./gradlew tasks`, you will notice that the **sample/gradle/dolbyio** folder will populate itself, those are files and configuration files you then can use in your own project

## Override in sample

On top of using the sample, you can also change the default one used, this is for instance used internally here to test that local & remote configuration is working (see our github actions for more)

## Configuration

### Publishing

Mandatory rootProject ext variables, for instance here are some values for one of our library:

```
ext {
  ...
  pom = [
    description: "Dolby.io Communications APIs library module",
    inceptionYear: "${new Date().format("YYYY")}",
    url: "https://github.com/voxeet/sdk-android-lib-promise",
    license: [
      name: 'Apache License',
      url: 'https://github.com/voxeet/sdk-android-lib-promise/blob/main/LICENSE'
    ],
    developer: [
      id: 'dolbyio',
      name: 'Dolby.io',
      email: 'support@dolby.io'
    ],
    scm: [
      connection: 'scm:git:github.com/voxeet/sdk-android-lib-promise.git',
      developerConnection: 'scm:git:ssh://github.com/voxeet/sdk-android-lib-promise.git',
      url: 'https://github.com/voxeet/sdk-android-lib-promise/tree/main'
    ]
  ]
}
```

## TODO

Some project files still need to be changed so that their predefined values can be changed a bit more easily (specifically for non dolby.io project integration)

- **publishing** The script should make the various optionals
- **sonarqube** make the prefix as a variable from rootProject as well

