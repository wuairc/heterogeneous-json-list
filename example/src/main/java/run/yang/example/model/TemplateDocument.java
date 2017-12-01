package run.yang.example.model;

import com.google.gson.annotations.SerializedName;
import run.yang.example.model.card.Card;

import java.util.List;

/**
 * 创建时间: 2017/11/29 14:05 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public class TemplateDocument {
    @SerializedName("cards")
    public final List<Card> mCardList;

    public TemplateDocument(List<Card> cardList) {
        mCardList = cardList;
    }
}
