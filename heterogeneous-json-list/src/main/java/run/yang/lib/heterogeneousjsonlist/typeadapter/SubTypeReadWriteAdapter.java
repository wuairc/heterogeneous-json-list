package run.yang.lib.heterogeneousjsonlist.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import run.yang.lib.heterogeneousjsonlist.JsonSyntaxWithPathException;

import java.io.IOException;

/**
 * 创建时间: 2017/09/01 10:50 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
class SubTypeReadWriteAdapter<BaseTypeT, ImplTypeT extends BaseTypeT>
        implements TypeReadWriteAdapter<BaseTypeT> {

    private final TypeAdapter<ImplTypeT> mDelegateAdapter;

    SubTypeReadWriteAdapter(TypeAdapter<ImplTypeT> delegateAdapter) {
        this.mDelegateAdapter = delegateAdapter;
    }

    @Override
    public BaseTypeT fromJsonTree(JsonElement element, JsonReader jsonReader) {
        try {
            return mDelegateAdapter.fromJsonTree(element);
        } catch (JsonSyntaxWithPathException e) {
            e.appendJsonPath(jsonReader.getPath());
            throw e;
        }
    }

    @Override
    public void write(JsonWriter writer, BaseTypeT value) throws IOException {
        //noinspection unchecked
        mDelegateAdapter.write(writer, (ImplTypeT) value);
    }
}
