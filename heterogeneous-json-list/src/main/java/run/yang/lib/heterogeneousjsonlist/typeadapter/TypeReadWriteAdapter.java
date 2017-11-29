package run.yang.lib.heterogeneousjsonlist.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * 创建时间: 2017/09/01 10:50 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
interface TypeReadWriteAdapter<BaseTypeT> {
    BaseTypeT fromJsonTree(JsonElement element, JsonReader jsonReader);

    void write(JsonWriter writer, BaseTypeT value) throws IOException;
}
