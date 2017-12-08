使用 Gson 反序列化异构 json 列表
Deserialize heterogeneous list from json with Gson.
===

<!-- TOC -->

- [1. 背景](#1-背景)
- [2. 使用](#2-使用)
    - [2.1. 数据结构要求](#21-数据结构要求)
    - [2.2. 接入](#22-接入)
    - [2.3. 使用](#23-使用)
- [设计与实现](#设计与实现)

<!-- /TOC -->

# 1. 背景

有时会遇到这样的 json 数组：数组中的每个元素是某个基类的子类。这样的数组在 Java 里通常表示为 `List<BaseItem>`，使用 `Gson` 库可以很方便的序列化它，反过来就不容易了。

典型的场景：IM 消息，通常用 `List<Msg>` 表示，`List` 中是 `Msg` 的各种子类，如 `TextMsg`, `ImageMsg`, `EmojiMsg`, `VoiceMsg` 等。

再比如，模板化的场景，信息流的卡片列表，或者一个采集信息的 App，卡片是待填写的字段，卡片用 `List<Card>`表示，`List` 中是 `Card` 的各种子类，如 `TextInputCard`，`TakePicturesCard`，`GetLocationCard` 等。

这个库就是要解决像这样的异构 json 列表的反序列化问题。

# 2. 使用

参考 [`example/src/main/java/run/yang/example/Main.java`](../example/src/main/java/run/yang/example/Main.java)。以下是要点总结。

## 2.1. 数据结构要求

1. 以 `List<Card>` 为例，[`Card`](../example/src/main/java/run/yang/example/model/card/Card.java) 的子类有 [`TakePicturesCard`](../example/src/main/java/run/yang/example/model/card/TakePicturesCard.java)，[`GetLocationCard`](../example/src/main/java/run/yang/example/model/card/GetLocationCard.java)，[`TextInputCard`](../example/src/main/java/run/yang/example/model/card/TextInputCard.java)。
2. 每个卡片都有 `cardType` 字段，表示卡片的类型（类型可以是 `String`, `int`, `long` 类型），这是使用本库的关键。对于 IM 消息，可以用 `msgType` 作为这个标识字段。

## 2.2. 接入

本库已上传到 Maven Central Repository。[以 Gradle 为例](../example/build.gradle)：

```gradle
repositories {
    // use google maven repo, required if used in non-android project
    // to download com.android.support:support-annotations
    google()
    // if your gradle version is lower than 4.0, use the following syntax
    // maven { url "https://maven.google.com" }

    jcenter()
}

dependencies {
    compile "run.yang.lib:heterogeneous-json-list:1.0.1"
}

```

## 2.3. 使用

以上面的 `Card` 类型为例：

1. 实现 [CardTypeAdapterFactory](../example/src/main/java/run/yang/example/parser/typeadapter/CardTypeAdapterFactory.java)

2. [将 CardTypeAdapterFactory 注册到 gson 实例中](../example/src/main/java/run/yang/example/parser/TemplateParser.java)

3. [使用 TemplateParser](../example/src/main/java/run/yang/example/Main.java)

# 设计与实现

见 [这篇文档](design-and-implemention-zh_CN.md)
