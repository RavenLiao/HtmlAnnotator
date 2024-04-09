# HtmlAnnotator

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![License][license_image]][license_link]

翻译：[English](README.md)

HtmlAnnotator 是 Android 上的一个HTML渲染库，而且支持解析CSS样式，目前支持 Jetpack Compose Compose，后续会支持View。


## 特点

* 支持自定义标签和CSS解析器
* 可自由替换默认解析器
* 支持多种CSS样式来源：内联、内部样式表、甚至是外部样式表
* 支持转换结果缓存
* 支持 Jetpack Compose
* 基于 Kotlin 及 Kotlin 协程编写

## 导入

`已发布到 mavenCentral`

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
dependencies {
    //Jetpack Compose 扩展支持, 提供开箱即用的显示组件与缓存机制
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-compose-ext:${LAST_VERSION}")
    // Jetpack Compose 基础支持
    implementation("io.github.ravenliao.htmlannotator:htmlAnnotator-compose:${LAST_VERSION}")

}
```

#### R8 / Proguard

该库不需要配置任何混淆规则，但你可能需要为间接依赖的 [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro)和[Jsoup](https://github.com/jhy/jsoup/)添加混淆配置

## 快速上手

引入`htmlAnnotator-compose-ext`与`coil-compose`

```kotlin
//实现对带图片的HTML文档的解析
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

## 特别感谢

* [NightWhistler/HtmlSpanner](https://github.com/NightWhistler/HtmlSpanner): HtmlAnnotator参考了来自HtmlSpanner的部分代码，包括解析转换、解析器部分
* [jhy/jsoup](https://github.com/jhy/jsoup/): HtmlAnnotator借助Jsoup解析HTML,以及选出最终的CSS
* [panpf/sketch](https://github.com/panpf/sketch): 参考了该项目的构建发布部分配置

## License

Apache 2.0. 有关详细信息，请参阅 [LICENSE](LICENSE) 文件.

[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg

[license_image]: https://img.shields.io/badge/License-Apache%202-blue.svg

[license_link]: https://www.apache.org/licenses/LICENSE-2.0

[version_icon]: https://img.shields.io/maven-central/v/io.github.ravenliao.htmlannotator/htmlAnnotator-core

[version_link]: https://repo1.maven.org/maven2/io/github/ravenliao/htmlannotator/

[min_api_image]: https://img.shields.io/badge/API-16%2B-orange.svg

[min_api_link]: https://android-arsenal.com/api?level=16
