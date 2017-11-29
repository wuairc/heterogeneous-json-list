package run.yang.lib.heterogeneousjsonlist;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 创建时间: 2017/09/01 16:33 <br>
 * 作者: Yang Tianmei <br>
 * 描述: 包含 Json Path 的 {@link JsonSyntaxException}，方便定位语法错误发生在 json 字符串的哪个位置 <br />
 * <p>
 * 输出示例：<code>run.yang.lib.heterogeneousjsonlist.JsonSyntaxWithPathException: action_type
 * expected to be a string, but JsonObject found, context: $.tasks[1]$.ui.pages[2]$.levels[1].cards[2]$.top_right_button.action
 * </code>
 */

public class JsonSyntaxWithPathException extends JsonParseException {

    private final List<String> mJsonPath = new ArrayList<>();

    public JsonSyntaxWithPathException(String msg) {
        super(msg);
    }

    public JsonSyntaxWithPathException(String msg, String jsonPath) {
        super(msg);
        mJsonPath.add(jsonPath);
    }

    public void appendJsonPath(String jsonPath) {
        mJsonPath.add(jsonPath);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (!mJsonPath.isEmpty()) {
            ListIterator<String> iterator = mJsonPath.listIterator(mJsonPath.size());
            sb.append(iterator.previous());
            while (iterator.hasPrevious()) {
                sb.append(iterator.previous());
            }
        }
        return super.getMessage() + ", context: " + sb.toString();
    }
}
