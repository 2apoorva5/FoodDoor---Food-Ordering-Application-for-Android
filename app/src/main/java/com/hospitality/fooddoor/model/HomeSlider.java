package com.hospitality.fooddoor.model;

public class HomeSlider {
    private String Name, Image;

    public HomeSlider() {
    }

    public HomeSlider(String Name, String Image) {
        this.Name = Name;
        this.Image = Image;
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
}
