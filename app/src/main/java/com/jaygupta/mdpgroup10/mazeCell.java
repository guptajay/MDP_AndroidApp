package com.jaygupta.mdpgroup10;

public class mazeCell {
    private String cellName;
    private int bgColor;
    private String displayName;
    private int textColor;
    private boolean explored;
    private boolean obstacle;

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

    public mazeCell(String cellName, int bgColor, String displayName, int textColor, boolean explored, boolean obstacle) {
        this.cellName = cellName;
        this.bgColor = bgColor;
        this.displayName = displayName;
        this.textColor = textColor;
        this.explored = explored;
        this.obstacle = obstacle;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public boolean getExplored() {
        return explored;
    }

    public void setExplored(boolean val) {
        this.explored = val;
    }

    public boolean getObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean val) {
        this.obstacle = val;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }
}
