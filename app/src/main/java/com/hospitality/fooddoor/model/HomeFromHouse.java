package com.hospitality.fooddoor.model;

public class HomeFromHouse {
    String name, image, mealType;

    public HomeFromHouse() {
    }

    public HomeFromHouse(String name, String image, String mealType) {
        this.name = name;
        this.image = image;
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

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
