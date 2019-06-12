package com.hospitality.fooddoor.model;

public class HomePopular {
    public String image, name, mealType;

    public HomePopular() {
    }

    public HomePopular(String image, String name, String mealType) {
        this.image = image;
        this.name = name;
        this.mealType = mealType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
