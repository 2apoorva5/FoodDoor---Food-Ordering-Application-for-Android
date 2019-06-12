package com.hospitality.fooddoor.model;

public class HomeToday {
    String name, image, discount, mealType;

    public HomeToday() {
    }

    public HomeToday(String name, String image, String discount, String mealType) {
        this.name = name;
        this.image = image;
        this.discount = discount;
        this.mealType = mealType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
