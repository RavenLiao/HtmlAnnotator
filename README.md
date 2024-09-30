# HtmlAnnotator

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)![badge-linux](http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat)
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]

Translation: [中文](./README_zh.md)

**HtmlAnnotator** is an HTML rendering library based on Kotlin Multiplatform and Compose Multiplatform, with support for parsing CSS styles. It also supports the Android View system.


## Features

* Supports custom tags and CSS parsers
* Freely replaceable default parsers
* Supports multiple sources of CSS styles: inline, internal stylesheets, and even external stylesheets
* Supports caching of conversion results
* Supports Jetpack Compose
* Written in Kotlin Multiplatform and utilizes Kotlin coroutines



## Default parsers

###  Jetpack Compose

#### Html Tag

- i
- em
- cite
- dfn
- b
- strong
- blockquote
- ul
- ol
- li
- br
- p
- div
- h1
- h2
- h3
- h4
- h5
- h6
- tt
- pre
- big
- small
- sub
- sup
- center
- a
- img
- span

#### CSS Rule

- text-align

- font-size

- font-weight

- font-style

- color

- background-color

- text-indent

- text-decoration

  

###  View

#### Html Tag

- i
- em
- cite
- dfn
- b
- strong
- blockquote
- ul
- ol
- li
- br
- p
- div
- h1
- h2
- h3
- h4
- h5
- h6
- tt
- pre
- big
- small
- sub
- sup
- center
- a
- span

#### CSS Rule

- text-align
- font-size
- font-style
- color
- background-color
- text-indent
- text-decoration



## Import

`Published on mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (excluding 'v')

```kotlin
dependencies {
    // Jetpack Compose extension support, providing out-of-the-box display components and caching mechanisms
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-compose-ext:${LAST_VERSION}")
    // Basic support for Jetpack Compose
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-compose:${LAST_VERSION}")
    // View
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-view:${LAST_VERSION}")
}
```

#### R8 / Proguard

This library doesn't require any specific Proguard rules to be configured, but you may need to add Proguard configurations for indirect dependencies like [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro) and [ksoup](https://github.com/fleeksoft/ksoup)

## Quickly Started

Include `htmlAnnotator-compose-ext` and `sketch`
```kotlin
// Implement parsing of HTML documents with images
BasicHtmlImageText(
    html = srcHtml,
    imageContent = { imgUrl ->
        AsyncImage(
            uri = it,
            contentDescription = "photo",
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentScale = ContentScale.FillWidth
        )
    }
)
```

For more details, see [DemoScreen](./composeApp/src/commonMain/kotlin/DemoScreen.kt#L293).

For the Android View implementation, refer to [MainActivity](./composeApp/src/androidMain/kotlin/com/ravenl/htmlannotator/MainActivity.kt#L59).

## Special Thanks

* [NightWhistler/HtmlSpanner](https://github.com/NightWhistler/HtmlSpanner): HtmlAnnotator referenced some code from HtmlSpanner, including parsing, transformation, and the parser components
* [ksoup](https://github.com/fleeksoft/ksoup): HtmlAnnotator leverages ksoup to parse HTML and select the final CSS
* [panpf/sketch](https://github.com/panpf/sketch): Referenced the build and release configurations of this project

## License

Apache 2.0. For more details, see the [LICENSE](LICENSE) file.


[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.ravenliao.htmlannotator/htmlAnnotator-core

[version_link]: https://repo1.maven.org/maven2/io/github/ravenliao/htmlannotator/

[min_api_image]: https://img.shields.io/badge/API-21%2B-orange.svg

[min_api_link]: https://android-arsenal.com/api?level=21
