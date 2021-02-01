package com.jaygupta.mdpgroup10;

public class mazeCell {
    private String cellName;
    private int bgColor;

    public mazeCell(String cellName, int bgColor) {
        this.cellName = cellName;
        this.bgColor = bgColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }
}
