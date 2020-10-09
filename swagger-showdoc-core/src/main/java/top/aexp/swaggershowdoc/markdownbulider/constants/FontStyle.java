package top.aexp.swaggershowdoc.markdownbulider.constants;

public enum  FontStyle {
    /**
     * 加粗
     */
    BOLD("**"),
    /**
     * 斜体
     */
    ITALICS("***"),
    /**
     * 高亮
     */
    HIGHLIGHT("`"),
    /**
     * 普通
     */
    NORMAL("")
    ;
    private String sign;

    FontStyle(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
