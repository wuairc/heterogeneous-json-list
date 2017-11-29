package run.yang;

import com.google.gson.JsonParseException;
import run.yang.log.LoggerImpl;
import run.yang.model.TemplateDocument;
import run.yang.parser.TemplateParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 创建时间: 2017/11/29 11:30 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public class Main {

    public static void main(String[] args) throws IOException {
        final TemplateParser parser = new TemplateParser(new LoggerImpl());

        TemplateDocument document;
        try {
            document = readTemplateDocument(parser);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return;
        }

        String json = parser.toJson(document);
        TemplateDocument documentClone = parser.parse(json);

        String jsonClone = parser.toJson(documentClone);
        System.out.println(Objects.equals(json, jsonClone));

        List<String> elementTypeList = getElementTypeList(document.mCardList);
        List<String> elementTypeListClone = getElementTypeList(documentClone.mCardList);
        System.out.println(Objects.equals(elementTypeList, elementTypeListClone));
        System.out.println(elementTypeListClone);
    }

    private static <E> List<String> getElementTypeList(List<E> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>(list.size());
        for (E e : list) {
            if (e == null) {
                result.add(null);
            } else {
                result.add(e.getClass().getSimpleName());
            }
        }
        return result;
    }

    private static TemplateDocument readTemplateDocument(TemplateParser parser) throws IOException {
        final ClassLoader classLoader = TemplateDocument.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("card_document_template.json")) {
            return parser.parse(inputStream);
        }
    }
}
