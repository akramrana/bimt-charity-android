package com.akramhossain.bimtcharity.models;

public class DashboardCard {

    private int icon;
    private String title;
    private String value;
    private int color;

    public DashboardCard(int icon, String title, String value, int color) {
        this.icon = icon;
        this.title = title;
        this.value = value;
        this.color = color;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }
}