package com.llt.swaggershowdoc.markdownbulider.constants;

public enum  MdLevel {
    ONE(1),

    TWO(2),

    THREE(3),

    FOUR(4),

    FIVE(5),

    SIX(6),

    SEVEN(7),

    EIGHT(8),

    NINE(9);

    int num;

    MdLevel(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
