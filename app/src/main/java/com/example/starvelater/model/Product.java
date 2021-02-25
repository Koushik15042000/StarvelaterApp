package com.example.starvelater.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private int unitPrice;
    private int itemTotalPrice;
    private int quantity;

    private String type;
    private String titles;

    private String images;
    private String itemCategory;

    public Product() {
    }

    public Product(int unitPrice, int itemTotalPrice, int quantity, String type, String titles, String images, String itemCategory) {
        this.unitPrice = unitPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.quantity = quantity;
        this.type = type;
        this.titles = titles;
        this.images = images;
        this.itemCategory = itemCategory;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(int itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }
}
