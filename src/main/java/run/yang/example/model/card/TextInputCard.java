package run.yang.example.model.card;

import com.google.gson.annotations.SerializedName;

/**
 * 创建时间: 2017/08/30 17:55 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

public class TextInputCard extends BaseCard {

    @SerializedName("edittext")
    public final EditTextBean editText;

    public TextInputCard(String cardId, String title, boolean required, EditTextBean editText) {
        super(CardType.TEXT_INPUT, cardId, title, required);
        this.editText = editText;
    }
}
