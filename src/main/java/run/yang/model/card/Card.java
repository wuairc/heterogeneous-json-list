package run.yang.model.card;

/**
 * 创建时间: 2017/11/29 11:54 <br>
 * 作者: ty <br>
 * 描述:
 */
public interface Card {
    String getId();

    @CardType
    String getType();
}
