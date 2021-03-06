package run.yang.example.parser;

import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import run.yang.example.model.TemplateDocument;
import run.yang.example.parser.typeadapter.CardTypeAdapterFactory;
import run.yang.lib.heterogeneousjsonlist.log.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 创建时间: 2017/11/29 14:06 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public class TemplateParser {

    private final Gson mGson;

    public TemplateParser(@Nullable Logger logger) {
        mGson = buildGson(logger);
    }

    public TemplateDocument parse(String json) throws JsonParseException {
        return mGson.fromJson(json, TemplateDocument.class);
    }

    public TemplateDocument parse(InputStream inputStream) throws JsonParseException {
        return mGson.fromJson(new InputStreamReader(inputStream), TemplateDocument.class);
    }

    public String toJson(TemplateDocument document) {
        return mGson.toJson(document);
    }

    private static Gson buildGson(@Nullable Logger logger) {
        final GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();
        builder.registerTypeAdapterFactory(new CardTypeAdapterFactory(logger));

        return builder.create();
    }
}
