package com.flota.menus;

public class menuItemsModelo {

    private final String textItem;
    private final int imgItemMenu;

    public menuItemsModelo(String textItem, int imgItemMenu) {
        this.textItem = textItem;
        this.imgItemMenu = imgItemMenu;
    }

    public String getTextoItem() {
        return textItem;
    }

    public int getImgItemMenu() {
        return imgItemMenu;
    }

}
