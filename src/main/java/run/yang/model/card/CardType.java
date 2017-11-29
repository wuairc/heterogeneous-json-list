package run.yang.model.card;

import android.support.annotation.StringDef;

/**
 * 创建时间: 2017/11/29 12:11 <br>
 * 作者: Yang Tianmei <br>
 * 描述: enum of card type
 */

@StringDef({
        CardType.TEXT_INPUT, CardType.GET_LOCATION, CardType.TAKE_PICTURES
})
public @interface CardType {
    String TEXT_INPUT = "text_input";

    String GET_LOCATION = "get_location";

    String TAKE_PICTURES = "take_pictures";
}
