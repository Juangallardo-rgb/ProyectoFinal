package com.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List products = new ArrayList<>();
    private List quantities = new ArrayList<>();
    private List prices = new ArrayList<>();

    public void addProduct(String p, int q, double price) {
        products.add(p);
        quantities.add(q);
        prices.add(price);
        System.out.println("Product added.");
    }

    public void printInventory() {
        for (int i = 0; i < products.size(); i++) {
            System.out.println(
                "Product: " + products.get(i) +
                ", Quantity: " + quantities.get(i) +
                ", Price: $" + prices.get(i)
            );
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.addProduct("Laptop", 5, 1000.0);
        inventory.printInventory();
    }
}
