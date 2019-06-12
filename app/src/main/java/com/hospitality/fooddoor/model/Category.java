package com.hospitality.fooddoor.model;

public class Category {

    private String Name, Image, Discount;

    public Category() {
    }

    public Category(String name, String image, String discount) {
        Name = name;
        Image = image;
        Discount = discount;
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

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
