package run.yang.lib.heterogeneousjsonlist.typeadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import run.yang.lib.heterogeneousjsonlist.JsonSyntaxWithPathException;
import run.yang.lib.heterogeneousjsonlist.TypeAware;
import run.yang.lib.heterogeneousjsonlist.log.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间: 2017/09/01 10:48 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public abstract class BaseGenericTypeAdapter<BaseTypeT extends TypeAware>
        extends TypeAdapter<BaseTypeT> {

    private final String mTypeFieldName;
    private final TypeAdapter<JsonObject> mJsonObjectTypeAdapter;
    private final Map<String, TypeReadWriteAdapter<BaseTypeT>> mAdapterMap = new HashMap<>();
    private final Logger mLogger;

    public BaseGenericTypeAdapter(@NonNull Gson gson, @NonNull String typeFieldName, @Nullable Logger logger) {
        mJsonObjectTypeAdapter = gson.getAdapter(JsonObject.class);
        mTypeFieldName = typeFieldName;
        mLogger = logger;
    }

    protected <ImplTypeT extends BaseTypeT> void addSubTypeAdapter(TypeAdapterFactory factory,
                                                                   Gson gson, String typeName, Class<ImplTypeT> subTypeClass) {
        mAdapterMap.put(typeName, new SubTypeReadWriteAdapter<BaseTypeT, ImplTypeT>(
                gson.getDelegateAdapter(factory, TypeToken.get(subTypeClass))));
    }

    @Override
    public void write(JsonWriter out, BaseTypeT value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        getReadWriteAdapter(value.getType(), null).write(out, value);
    }

    @Override
    public BaseTypeT read(JsonReader in) throws IOException {
        final JsonToken firstToken = in.peek();
        if (firstToken == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if (firstToken != JsonToken.BEGIN_OBJECT) {
            throw new JsonSyntaxWithPathException("expect {, but " + firstToken + " found", in.getPath());
        }

        final JsonObject jsonObject = mJsonObjectTypeAdapter.read(in);
        final JsonElement jsonElement = jsonObject.get(mTypeFieldName);

        if (jsonElement == null) {
            if (mLogger != null) {
                mLogger.log(Logger.WARNING, "type field: " + mTypeFieldName + " not found, skip");
            }
            return null;
        } else if (jsonElement.isJsonPrimitive()) {
            String typeName = jsonElement.getAsString();
            return getReadWriteAdapter(typeName, in).fromJsonTree(jsonObject, in);
        } else if (jsonElement.isJsonNull()) {
            return null;
        } else {
            String msg = mTypeFieldName
                    + " expected to be a string, but "
                    + jsonElement.getClass().getSimpleName()
                    + " found";
            throw new JsonSyntaxWithPathException(msg, in.getPath());
        }
    }

    @NonNull
    private TypeReadWriteAdapter<BaseTypeT> getReadWriteAdapter(String typeName,
                                                                JsonReader jsonReader) {
        TypeReadWriteAdapter<BaseTypeT> taskAdapter = mAdapterMap.get(typeName);
        if (taskAdapter == null) {
            final String msg = "unknown " + mTypeFieldName + " type = " + typeName;
            if (jsonReader == null) {
                throw new JsonParseException(msg);
            } else {
                throw new JsonSyntaxWithPathException(msg, jsonReader.getPath());
            }
        }
        return taskAdapter;
    }
}