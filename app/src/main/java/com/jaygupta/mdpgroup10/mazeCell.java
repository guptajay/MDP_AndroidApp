package com.jaygupta.mdpgroup10;

public class mazeCell {
    private String cellName;
    private String bgColor;

    public mazeCell(String cellName, String bgColor) {
        this.cellName = cellName;
        this.bgColor = bgColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }
}
