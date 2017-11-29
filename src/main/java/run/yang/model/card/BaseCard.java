package run.yang.model.card;

import com.google.gson.annotations.SerializedName;

/**
 * 创建时间: 2017/08/30 16:23 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

public abstract class BaseCard implements Card {

    @SerializedName("card_type")
    @CardType
    private final String cardType;

    @SerializedName("card_id")
    private final String cardId;

    @SerializedName("title")
    public final String title;

    @SerializedName("required")
    public final boolean required;


    public BaseCard(@CardType String cardType, String cardId, String title, boolean required) {
        this.cardType = cardType;
        this.cardId = cardId;
        this.title = title;
        this.required = required;
    }

    @Override
    public final String getType() {
        return cardType;
    }

    @Override
    public final String getId() {
        return cardId;
    }
}
