package run.yang.lib.heterogeneousjsonlist.typeadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import run.yang.lib.heterogeneousjsonlist.JsonSyntaxWithPathException;
import run.yang.lib.heterogeneousjsonlist.log.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间: 2017/09/01 10:48 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public abstract class BaseGenericTypeAdapter<BaseTypeT>
        extends TypeAdapter<BaseTypeT> {

    private final String mTypeFieldName;
    private final TypeAdapter<JsonObject> mJsonObjectTypeAdapter;
    private final Map<String, TypeReadWriteAdapter<BaseTypeT>> mTypeNameToAdapterMap = new HashMap<>();
    private final Map<String, TypeReadWriteAdapter<BaseTypeT>> mClassToAdapterMap = new HashMap<>();
    private final Logger mLogger;

    public BaseGenericTypeAdapter(@NonNull Gson gson, @NonNull String typeFieldName, @Nullable Logger logger) {
        mJsonObjectTypeAdapter = gson.getAdapter(JsonObject.class);
        mTypeFieldName = typeFieldName;
        mLogger = logger;
    }

    protected <ImplTypeT extends BaseTypeT> void registerSubtypeAdapter(TypeAdapterFactory factory,
                                                                        Gson gson, String typeName, Class<ImplTypeT> subTypeClass) {
        SubTypeReadWriteAdapter<BaseTypeT, ImplTypeT> readWriteAdapter = new SubTypeReadWriteAdapter<>(
                gson.getDelegateAdapter(factory, TypeToken.get(subTypeClass)));
        mTypeNameToAdapterMap.put(typeName, readWriteAdapter);
        mClassToAdapterMap.put(subTypeClass.getName(), readWriteAdapter);
    }

    @Override
    public void write(JsonWriter out, BaseTypeT value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        getReadWriteAdapterByClassName(value.getClass().getName()).write(out, value);
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
            log(Logger.ERROR, "type field " + mTypeFieldName + " not found, skip, path: " + in.getPath());
            return null;
        } else if (jsonElement.isJsonPrimitive()) {
            // compatible with string, number type
            String typeName = jsonElement.getAsString();
            TypeReadWriteAdapter<BaseTypeT> readWriteAdapter = getReadWriteAdapterByTypeName(typeName);
            if (readWriteAdapter == null) {
                // skip new types
                log(Logger.ERROR, "unknown subtype " + mTypeFieldName + " = " + typeName + ", skip, path: " + in.getPath());
                return null;
            }
            return readWriteAdapter.fromJsonTree(jsonObject, in);
        } else if (jsonElement.isJsonNull()) {
            log(Logger.ERROR, mTypeFieldName + " is null, skip, path: " + in.getPath());
            return null;
        } else {
            String msg = mTypeFieldName
                    + " expected to be string or number, but "
                    + jsonElement.getClass().getSimpleName()
                    + " found";
            throw new JsonSyntaxWithPathException(msg, in.getPath());
        }
    }

    @Nullable
    private TypeReadWriteAdapter<BaseTypeT> getReadWriteAdapterByTypeName(String typeName) {
        return mTypeNameToAdapterMap.get(typeName);
    }

    @NonNull
    private TypeReadWriteAdapter<BaseTypeT> getReadWriteAdapterByClassName(String className) {
        TypeReadWriteAdapter<BaseTypeT> taskAdapter = mClassToAdapterMap.get(className);
        if (taskAdapter == null) {
            // should not happen when write
            throw new JsonParseException("unknown subtype = " + className);
        }
        return taskAdapter;
    }

    private void log(int logLevel, String msg) {
        Logger logger = mLogger;
        if (logger != null) {
            logger.log(logLevel, msg);
        }
    }
}
