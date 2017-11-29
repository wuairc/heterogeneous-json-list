package run.yang.lib.heterogeneousjsonlist.typeadapter;

import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import run.yang.lib.heterogeneousjsonlist.log.Logger;

/**
 * 创建时间: 2017/09/01 15:15 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

public abstract class BaseTypeAdapterFactory<BaseTypeT> implements TypeAdapterFactory {

    private final Class<BaseTypeT> mBaseTypeClass;
    @Nullable
    private final Logger mLogger;

    protected BaseTypeAdapterFactory(Class<BaseTypeT> tClass, @Nullable Logger logger) {
        mBaseTypeClass = tClass;
        mLogger = logger;
    }

    public abstract TypeAdapter<BaseTypeT> createTypeAdapter(Gson gson, @Nullable Logger logger);

    @Override
    public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (mBaseTypeClass.isAssignableFrom(type.getRawType())) {
            //noinspection unchecked
            return (TypeAdapter<T>) createTypeAdapter(gson, mLogger);
        } else {
            return null;
        }
    }
}
