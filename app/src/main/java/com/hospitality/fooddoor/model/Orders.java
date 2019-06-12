package com.hospitality.fooddoor.model;

public class Orders {
    private String UserEmail, FoodId, FoodName, Quantity, Price, Discount, FoodImg, MealType;

    public Orders() {
    }

    public Orders(String userEmail, String foodId, String foodName, String quantity, String price, String discount, String foodImg, String mealType) {
        UserEmail = userEmail;
        FoodId = foodId;
        FoodName = foodName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        FoodImg = foodImg;
        MealType = mealType;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getFoodImg() {
        return FoodImg;
    }

    public void setFoodImg(String foodImg) {
        FoodImg = foodImg;
    }

    public String getMealType() {
        return MealType;
    }

    public void setMealType(String mealType) {
        MealType = mealType;
    }
}
