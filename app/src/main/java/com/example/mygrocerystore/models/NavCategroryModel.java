package com.example.mygrocerystore.models;

public class NavCategroryModel {
    String description;
    String name;
    String discount;
    String img_url;
    String type;

    public NavCategroryModel() {
    }

    public NavCategroryModel(String description, String name, String discount, String img_url, String type) {
        this.description = description;
        this.name = name;
        this.discount = discount;
        this.img_url = img_url;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
