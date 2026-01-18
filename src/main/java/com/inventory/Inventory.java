package com.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public void printInventory() {
        for (Product product : products) {
            System.out.println(
                "Product: " + product.getName() +
                ", Quantity: " + product.getQuantity() +
                ", Price: $" + product.getPrice()
            );
        }
    }
}
