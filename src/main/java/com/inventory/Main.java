package com.inventory;

public class Main {

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("Laptop", 5, 1000.0));
        inventory.printInventory();
    }
}
