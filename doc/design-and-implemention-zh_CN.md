
从 Json 到 List<? extends BaseItem>：使用 Gson 反序列化异构列表
===

本文要解决的问题：从 json 字符串反序列化形如 `List<BaseItem>` 的列表，做采集功能模板化时遇到的问题。

<!-- TOC -->

- [1. 需求](#1-需求)
- [2. 方案](#2-方案)
- [3. 实现](#3-实现)
    - [3.1. 实现 `TypeAdapterFactory`](#31-实现-typeadapterfactory)
    - [3.2 实现 `CardTypeAdapter`](#32-实现-cardtypeadapter)
    - [3.3 抽象为可复用的库](#33-抽象为可复用的库)
- [4. 总结](#4-总结)

<!-- /TOC -->

# 1. 需求

App 端需要采集小区、房屋等实体的信息。采集功能是模板化的，配置文件采用 json 格式。整个模板配置包含许多异构的 json 数组，解析成 Java 对象是 `List<>`。

例如，一个采集任务可能包括多个页面（`List<Page>`），每个页面有多个卡片（`List<Card>`）。`Page` 和 `Card` 都是接口，有多种类型的子类，这种列表是异构的列表。

以卡片为例，为了将不同类型的卡片放在同一个列表里，需要抽象出一个公共接口（或基类），卡片被抽象为接口 `Card`，不同类型卡片分别实现这个接口。以下面简化的配置文件为例，它对应 `List<Card>`（ `List<Card>` 作为 `TemplateDocument` 类的成员）

```json
{
  "cards": [
    {
      "card_type": "text_input",
      "card_id": "name",
      "title": "Name",
      "required": true,
      "edittext": {
        "input_type": "text",
        "hint": "your name",
        "minLines": 1,
        "maxLines": 1,
        "maxCharsCount": 50
      }
    },
    {
      "card_type": "get_location",
      "card_id": "house_location",
      "title": "House Location",
      "required": true
    },
    {
      "card_type": "take_pictures",
      "card_id": "house_pics",
      "title": "House Pictures",
      "required": true,
      "max_pic_count": 10
    },
    {
      "card_type": "take_pictures",
      "card_id": "gate_pics",
      "title": "House Gate Pictures",
      "required": true,
      "max_pic_count": 5
    }
  ]
}
```

反序列化后的 `List<Card>` 中的元素如下：

```
+------------------+
|    List<Card>    |
+------------------+
| TextInputCard    |
| GetLocationCard  |
| TakePicturesCard |
| TakePicturesCard |
+------------------+
```

`TemplateDocument` 的定义：

```java
public class TemplateDocument {
  
    @SerializedName("cards")
    public final List<Card> mCardList;

    public TemplateDocument(List<Card> cardList) {
        mCardList = cardList;
    }
}
```

那么问题来了，App 拿到这段 json 该如何还原为原来的 `List<Card>` 呢？

# 2. 方案

手工解析显然不是我们的追求，在我们的项目中目前有 9 种像 `List<Card>` 这样的异构列表，并且 `List<Card>` 通常是更大的数据结构的一部分，无法单独拎出来解析。所以必须找到简便的方案。

注意到每个卡片都有 `card_type` 字段标识卡片的类型。实际上，**这是定义配置文件格式时有意规定的**，通过这种方式，将卡片类型编码在 json 字符串里，反序列化时根据这个字段的值，就知道对应哪种实体 Bean 了。

我们的项目使用 [Gson](https://github.com/google/gson) 库作为 json 的序列化方案。Gson 支持自定义任意类型的序列化、反序列化。相关接口如下：

```java
public final class GsonBuilder{
    public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter);
    public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory);
    public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter);
}
```

这三个方法中，基础是 `registerTypeAdapterFactory`，另外两个是对它的封装。我们需要注册 `Card.class` 的 TypeAdapter。

`Object`类型的参数`typeAdapter`可以是 `TypeAdapter`, `JsonSerializer`, `JsonDeserializer` 或 `InstanceCreator`，前三个与我们的目的有关，`JsonSerializer` 和 `JsonDeserializer` 官方不建议使用，使用 `TypeAdapter` 更高效。我们使用基础的 `registerTypeAdapterFactory` 函数，另外两个方法的参数`TypeAdapter`拿不到我们需要的 `gson` 对象。

# 3. 实现

## 3.1. 实现 `TypeAdapterFactory`

`TypeAdapterFactory` 是生成 `TypeAdapter` 的工厂，这个接口只有一个方法，根据类型创建对应的 `TypeAdapter`，在这里 `create()` 的参数 `type` 就代表 `Card.class`:

```java
<T> TypeAdapter<T> create(Gson gson, TypeToken<T> type);
```

如果 `type` 是我们要处理的类型，则返回一个新的 `CardTypeAdapter` 实例，否则返回 `null` 表示不能处理这种类型。

## 3.2 实现 `CardTypeAdapter`

`CardTypeAdapter` 继承自 `TypeAdapter`，需要实现 `write()` 和 `read()` 方法。分别是 `Card` 类型（子类）的序列化和反序列化过程。

`write` 过程无需干预，代理给 Gson 内置的对应类型（`Card` 的子类）的 `TypeAdapter` 就可以了。如何获取 Gson 对某种类型的默认 `TypeAdapter` 呢，Gson 提供了接口：

```java
public class Gson {
    public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type);
}
```

`TypeToken` 使用 `TypeToken.get()` 获取。例如，获取 `TextInputCard.class` 的 `TypeAdapter`:

```java
TypeAdapter<TextInputCard> delegateAdapter = gson.getDelegateAdapter(factory, TypeToken.get(TextInputCard.class));
```

`read` 过程的处理需要一些技巧。`read()` 的函数原型如下：
```java
public abstract T read(JsonReader in) throws IOException;
```

参数 `JsonReader` 对应一个卡片序列化后的 json 信息，我们需要读取 `card_type` 字段，根据它的值交给对应子类的 `TypeAdapter`去处理。

`JsonReader` 提供的接口是流式(stream)的，无法直接根据字段名称(`card_type`)拿到对应的值。先转成万能类型 `JsonObject`(注意是 `com.google.code.gson` 包下的，不是 `org.json.JSONObject` )，然后就可以拿到 `card_type` 的值了，接着也可以方便的把 `JsonObject` 转成对应的子类对象。

```java
TypeAdapter<JsonObject> jsonObjectTypeAdapter = gson.getAdapter(JsonObject.class);
final JsonObject jsonObject = jsonObjectTypeAdapter.read(in);
final JsonElement jsonElement = jsonObject.get("card_type");
String cardTypeName = jsonElement.getAsString();
Card card = getSubtypeAdapter(cardTypeName).fromJsonTree(jsonObject)
```

拿到 cardTypeName 后，查找对应的子类的代理 TypeAdapter，调用其 `fromJsonTree` 方法就可以了。

查找子类时有一个小问题，由于 Java 的强类型特性，直接用 `Map<String, TypeAdapter<? extends Card>>` 保存对应关系将在某处出现类型不兼容问题，处理方法是增加一个适配层，具体方法见开源出来的代码中的 `TypeReadWriteAdapter` 类。

## 3.3 抽象为可复用的库

上面讲的是针对 `Card.class` 这个特例，使用 Java 泛型技术可以抽象为可复用的库。抽象后使用起来就方便多了。上面的例子现在只需要分别继承 `BaseTypeAdapterFactory` 和 `BaseGenericTypeAdapter`，调用 `registerSubtypeAdapter` 注册类型名称与实现类的映射就可以了。实现 CardTypeAdapterFactory 和 CardTypeAdapter 只需要 20 行代码。

以下是新的 `CardTypeAdapterFactory` 实现：

```java
public class CardTypeAdapterFactory extends BaseTypeAdapterFactory<Card> {
  
  public CardTypeAdapterFactory(@Nullable Logger logger) {
    super(Card.class, logger);
  }

  @Override
  public TypeAdapter<Card> createTypeAdapter(final Gson gson, @Nullable Logger logger) {
    // using anonymous class is not recommend, as it confuse crash stacktrace.
    return new CardTypeAdapter(gson, logger, this);
  }

  private static class CardTypeAdapter extends BaseGenericTypeAdapter<Card> {
    public CardTypeAdapter(Gson gson, Logger logger, TypeAdapterFactory factory) {
      super(gson, "card_type", logger);

      registerSubtypeAdapter(factory, gson, CardType.GET_LOCATION, GetLocationCard.class);
      registerSubtypeAdapter(factory, gson, CardType.TAKE_PICTURES, TakePicturesCard.class);
      registerSubtypeAdapter(factory, gson, CardType.TEXT_INPUT, TextInputCard.class);
    }
  }
}
```

使用

```java
final GsonBuilder builder = new GsonBuilder();
builder.registerTypeAdapterFactory(new CardTypeAdapterFactory(logger));
// 这里可以继续注册多个 TypeAdapterFactory 或 TypeAdapter
Gson gson = builder.create();

TemplateDocument document = gson.fromJson(json, TemplateDocument.class);
```

**注：** 考虑到实际使用场景，遇到未知的类型值，将会忽略（并输出 Error 级别的 log），以便旧版的 App 兼容新版本的配置。这时，`List<Card>` 将出现元素为 `null` 的情况，使用时需要注意。

# 4. 总结

借助 Gson 提供的扩展能力和 Java 泛型技术，我们实现了类型安全的反序列化异构列表的工具，让反序列化异构列表变得像使用 Gson 本身一样简单。

这个技巧在项目的模板化中得到了大量运用，目前共有 9 个这样的异构列表，模板配置文件有 597 行，解析模板不是一件痛苦的事情。对应的保存采集结果的 json 结构与此类似，但大小可达上万行（格式化后的 json）。曾经遇到过一个压缩后大小 294KB，13818 行的 json，使用这个方法也顺利的反序列化出来。

模板化能够快速响应新的采集需求，配以服务端动态下发模板配置，大大提高了产品的灵活性，缩短了需求的响应时间。其他有需要做模板化的产品可以参考这里提供的方案。

抽象出来的库和文中的例子已经开源在 <https://github.com/wuairc/heterogeneous-json-list>，欢迎使用和提出建议。

