package run.yang.model.card;

/**
 * 创建时间: 2017/08/30 18:24 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

public class GetLocationCard extends BaseCard {

    public GetLocationCard(String cardId, String title, boolean required) {
        super(CardType.GET_LOCATION, cardId, title, required);
    }
}
