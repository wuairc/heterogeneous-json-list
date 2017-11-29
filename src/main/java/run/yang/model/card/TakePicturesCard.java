package run.yang.model.card;

import com.google.gson.annotations.SerializedName;

/**
 * 创建时间: 2017/08/30 17:57 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

public class TakePicturesCard extends BaseCard {

    @SerializedName("max_pic_count")
    public final int maxPicCount;

    public TakePicturesCard(String cardId, String title, boolean required,
                            int maxPicCount) {
        super(CardType.TAKE_PICTURES, cardId, title, required);
        this.maxPicCount = maxPicCount;
    }
}
