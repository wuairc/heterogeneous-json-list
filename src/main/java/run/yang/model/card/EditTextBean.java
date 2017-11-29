package run.yang.model.card;

import android.support.annotation.StringDef;
import com.google.gson.annotations.SerializedName;

/**
 * 创建时间: 2017/08/30 16:40 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */

public class EditTextBean {
    @InputType
    @SerializedName("input_type")
    public final String inputType;

    /**
     * 未输入文字时输入框内的提示文字
     */
    @SerializedName("hint")
    public final String hint;

    @SerializedName("minLines")
    public final int minLines;

    @SerializedName("maxLines")
    public final int maxLines;

    @SerializedName("maxCharsCount")
    public final int maxCharsCount;

    public EditTextBean(String inputType, String hint, int minLines, int maxLines,
                        int maxCharsCount) {
        this.inputType = inputType;
        this.hint = hint;
        this.minLines = minLines;
        this.maxLines = maxLines;
        this.maxCharsCount = maxCharsCount;
    }

    @StringDef({InputType.TEXT, InputType.NUMBER})
    public @interface InputType {
        String TEXT = "text";
        String NUMBER = "number";
    }
}
