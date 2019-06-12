package com.hospitality.fooddoor.model;

public class Food {
    private String Name, Image, Type, Close, Price, PreviousPrice, Discount, Description, MenuId;

    public Food() {
    }

    public Food(String name, String image, String type, String close, String price, String previousPrice, String discount, String description, String menuId) {
        Name = name;
        Image = image;
        Type = type;
        Close = close;
        Price = price;
        PreviousPrice = previousPrice;
        Discount = discount;
        Description = description;
        MenuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getClose() {
        return Close;
    }

    public void setClose(String close) {
        Close = close;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPreviousPrice() {
        return PreviousPrice;
    }

    public void setPreviousPrice(String previousPrice) {
        PreviousPrice = previousPrice;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
