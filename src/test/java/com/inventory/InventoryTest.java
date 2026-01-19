package com.inventory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class InventoryTest {

    @Test
    void testAddProductDoesNotThrowException() {
        Inventory inventory = new Inventory();
        Product product = new Product("Laptop", 5, 1000.0);

        assertDoesNotThrow(() -> inventory.addProduct(product));
    }
}
