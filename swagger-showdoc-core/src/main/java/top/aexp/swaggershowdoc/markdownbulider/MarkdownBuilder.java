package top.aexp.swaggershowdoc.markdownbulider;

import top.aexp.swaggershowdoc.markdownbulider.constants.FontStyle;
import top.aexp.swaggershowdoc.markdownbulider.constants.MdLevel;
import org.apache.commons.beanutils.BeanUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import static top.aexp.swaggershowdoc.markdownbulider.constants.MarkdownSign.*;

public class MarkdownBuilder {

    private static final Pattern ANCHOR_UNIGNORABLE_PATTERN = Pattern.compile("[^0-9a-zA-Z-_]+");
    private static final Pattern ANCHOR_IGNORABLE_PATTERN = Pattern.compile("[\\s@#&(){}\\[\\]!$*%+=/:.;,?\\\\<>|]+");
    private static final String ANCHOR_SEPARATION_CHARACTERS = "_-";
    private static final String ANCHOR_PREFIX = "-";

    private static final int MAX_TITLE_LEVEL = 5;
    private static final String NEW_LINE = "\r\n";
    private static final String SPACE = " ";

    private StringBuilder stringBuilder;

    private int quoteLevel = 1;

    private MarkdownBuilder() {
        stringBuilder = new StringBuilder();
    }

    public static MarkdownBuilder start() {
        return new MarkdownBuilder();
    }

    public MarkdownBuilder writeln(String line) {
        return writeln(line, FontStyle.NORMAL);
    }

    public MarkdownBuilder writeln(String line, FontStyle fontStyle) {
        appendWithStyle(line, fontStyle);
        stringBuilder.append(NEW_LINE);
        return this;
    }

    private StringBuilder appendWithStyle(String text, FontStyle fontStyle) {
        return stringBuilder.append(fontStyle.getSign()).append(text).append(fontStyle.getSign());
    }

    public MarkdownBuilder write(String line) {
        stringBuilder.append(line);
        return this;
    }

    public MarkdownBuilder write(String line, FontStyle fontStyle) {
        appendWithStyle(line, fontStyle);
        return this;
    }

    public MarkdownBuilder writeTitle(String title, MdLevel level) {
        return writeTitle(title, level, FontStyle.NORMAL);
    }

    public MarkdownBuilder writeTitle(String title, MdLevel level, FontStyle fontStyle) {
        for (int i = 0; i < level.getNum(); i++) {
            stringBuilder.append(TITLE);
        }
        appendWithStyle(title, fontStyle);
        stringBuilder.append(NEW_LINE);
        return this;
    }

    public MarkdownBuilder writeQuote(String quote) {
        return writeQuote(quote, FontStyle.NORMAL);
    }

    public MarkdownBuilder writeQuote(String quote, FontStyle fontStyle) {
        for (int i = 0; i < quoteLevel; i++) {
            stringBuilder.append(QUOTE);
        }
        appendWithStyle(quote, fontStyle);
        stringBuilder.append(NEW_LINE);
        return this;
    }


    public MarkdownBuilder writeNextQuote(String quote) {
        quoteLevel++;
        return writeQuote(quote);
    }

    public MarkdownBuilder writeNextQuote(String quote, FontStyle fontStyle) {
        quoteLevel++;
        return writeQuote(quote, fontStyle);
    }


    /**
     * @param list 可嵌套list
     * @return this
     */
    public MarkdownBuilder writeUnorderedList(List list) {
        appendList(null, list, UNORDERED_LIST);
        stringBuilder.append(NEW_LINE);
        return this;
    }

    public MarkdownBuilder writeUnorderedListItem(String item) {
        listItem(UNORDERED_LIST, item, FontStyle.NORMAL);
        return this;
    }

    public MarkdownBuilder writeUnorderedListItem(String item, FontStyle style) {
        listItem(UNORDERED_LIST, item, style);
        return this;
    }

    private void listItem(String markup, String item, FontStyle style) {
        //Validate.notBlank(item, "item must not be blank");
        stringBuilder.append(markup);
        appendWithStyle(item, style).append(NEW_LINE);
    }

    /**
     * @param list 可嵌套list
     * @return this
     */
    public MarkdownBuilder writeOrderedList(List list) {
        appendList(null, list, ORDERED_LIST);
        stringBuilder.append(NEW_LINE);
        return this;
    }

    /**
     * @return this
     */
    public MarkdownBuilder writeOrderedListItem(String item) {
        listItem(ORDERED_LIST, item, FontStyle.NORMAL);
        return this;
    }

    /**
     * @return this
     */
    public MarkdownBuilder writeOrderedListItem(String item, FontStyle style) {
        listItem(ORDERED_LIST, item, style);
        return this;
    }


    /**
     * @return this
     */
    public MarkdownBuilder writeHighlight(String text, String language) {
        stringBuilder.append(HIGHLIGHT);
        stringBuilder.append(language);
        stringBuilder.append(NEW_LINE);
        stringBuilder.append(text);
        stringBuilder.append(NEW_LINE);
        stringBuilder.append(HIGHLIGHT);
        stringBuilder.append(NEW_LINE);
        return this;
    }

    /**
     * @return this
     */
    public MarkdownBuilder writeTable(List list, LinkedHashMap<String, String> titleMap) {
        StringBuilder style = new StringBuilder();
        newLine();
        titleMap.forEach((k, v) -> {
            this.stringBuilder.append(TABLE_LINE);
            style.append(TABLE_LINE);
            this.stringBuilder.append(v);
            style.append(TABLE_STYLE_LEFT);
        });

        this.stringBuilder.append(TABLE_LINE).append(NEW_LINE).append(style).append(TABLE_LINE).append(NEW_LINE);
        list.forEach(item -> {
            for (String title : titleMap.keySet()) {
                this.stringBuilder.append(TABLE_LINE);
                try {
                    String property = BeanUtils.getProperty(item, title);
                    this.stringBuilder.append(property == null || "null".equals(property) ? ANCHOR_PREFIX : property);
                } catch (Exception ignore) {
                }
            }
            this.stringBuilder.append(TABLE_LINE).append(NEW_LINE);
        });


        return this;
    }


    private void appendList(String sign, List list, String type) {
        String thisSign;
        if (sign == null) {
            thisSign = type;
        } else {
            thisSign = SPACE + SPACE + SPACE + sign;
        }
        for (Object item : list) {
            if (item instanceof List) {
                appendList(sign, list, type);
            } else {
                if (item != null) {
                    stringBuilder.append(thisSign).append(item.toString()).append(NEW_LINE);
                }
            }
        }
    }

    public MarkdownBuilder crossReferenceRaw(String document, String anchor, String text) {
        getRef(document, anchor, text, stringBuilder);
        return this;
    }

    public static String getReference(String document, String anchor, String text) {
        StringBuilder stringBuilder = new StringBuilder();
        getRef(document, anchor, text, stringBuilder);
        return stringBuilder.toString();
    }

    private static void getRef(String document, String anchor, String text, StringBuilder stringBuilder) {
        if (text == null) {
            text = anchor.trim();
        }
        stringBuilder.append("[").append(text).append("]").append("(");
        if (document != null) {
            stringBuilder.append(document);
        }
        stringBuilder.append("#").append(anchor).append(")");
    }


    public MarkdownBuilder writeAnchor(String anchor) {
        stringBuilder.append("<a name=\"").append(anchor).append("\"></a>");
        return this;
    }

    public MarkdownBuilder newLine() {
        stringBuilder.append(NEW_LINE);
        return this;
    }


    public void reSetLevel() {
        quoteLevel = 1;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
