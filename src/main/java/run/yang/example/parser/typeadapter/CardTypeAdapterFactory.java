package run.yang.example.parser.typeadapter;

import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import run.yang.lib.heterogeneousjsonlist.log.Logger;
import run.yang.lib.heterogeneousjsonlist.typeadapter.BaseGenericTypeAdapter;
import run.yang.lib.heterogeneousjsonlist.typeadapter.BaseTypeAdapterFactory;
import run.yang.example.model.card.*;


/**
 * 创建时间: 2017/08/31 17:17 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

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
