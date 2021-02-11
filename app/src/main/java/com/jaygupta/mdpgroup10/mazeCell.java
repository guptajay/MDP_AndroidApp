package com.jaygupta.mdpgroup10;

public class mazeCell {
    private String cellName;
    private int bgColor;
    private String displayName;
    private int textColor;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public mazeCell(String cellName, int bgColor, String displayName, int textColor) {
        this.cellName = cellName;
        this.bgColor = bgColor;
        this.displayName = displayName;
        this.textColor = textColor;
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
