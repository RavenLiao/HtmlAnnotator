# HtmlAnnotator

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]

Translation: [中文](./README_zh.md)

HtmlAnnotator is an HTML rendering library for Android that supports parsing CSS styles. It currently supports Jetpack Compose and will support Views in the future.


## Features

* Supports custom tags and CSS parsers
* Freely replaceable default parsers
* Supports multiple sources of CSS styles: inline, internal stylesheets, and even external stylesheets
* Supports caching of conversion results
* Supports Jetpack Compose
* Written in Kotlin and utilizes Kotlin coroutines

## Import

`Published on mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (excluding 'v')

```kotlin
dependencies {
    // Jetpack Compose extension support, providing out-of-the-box display components and caching mechanisms
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-compose-ext:${LAST_VERSION}")
    // Basic support for Jetpack Compose
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-compose:${LAST_VERSION}")

}
```

#### R8 / Proguard

This library doesn't require any specific Proguard rules to be configured, but you may need to add Proguard configurations for indirect dependencies like [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro) and [Jsoup](https://github.com/jhy/jsoup/)

## Quickly Started

Include `htmlAnnotator-compose-ext` and `coil-compose`
```kotlin
// Implement parsing of HTML documents with images
BasicHtmlImageText(
    html = srcHtml,
    imageContent = { imgUrl ->
        AsyncImage(
            imgUrl,
            null,
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentScale = ContentScale.FillWidth
        )
    }
)
```

## Special Thanks

* [NightWhistler/HtmlSpanner](https://github.com/NightWhistler/HtmlSpanner): HtmlAnnotator referenced some code from HtmlSpanner, including parsing, transformation, and the parser components
* [jhy/jsoup](https://github.com/jhy/jsoup/): HtmlAnnotator leverages Jsoup to parse HTML and select the final CSS
* [panpf/sketch](https://github.com/panpf/sketch): Referenced the build and release configurations of this project

## License

Apache 2.0. For more details, see the [LICENSE](LICENSE) file.

[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.ravenliao.htmlannotator/htmlAnnotator-core

[version_link]: https://repo1.maven.org/maven2/io/github/ravenliao/htmlannotator/

[min_api_image]: https://img.shields.io/badge/API-16%2B-orange.svg

[min_api_link]: https://android-arsenal.com/api?level=16
