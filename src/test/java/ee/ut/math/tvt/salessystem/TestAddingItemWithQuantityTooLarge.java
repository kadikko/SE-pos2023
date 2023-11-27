package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAddingItemWithQuantityTooLarge {

    // - check that an exception is thrown if the quantity of
    // the added item is larger than the quantity in the warehouse

    private ShoppingCart shoppingCart;

    @Test
    public void testAddingItemWithQuantityTooLarge() {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
        int quantityInStock = dao.findStockItem(1L).getQuantity();
        int tooLargeQuantity = dao.findStockItem(1L).getQuantity() + 1;

        // verify that an exception is thrown when quantity requested is larger than the quantity in warehouse
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.validateQuantityNotTooLarge(tooLargeQuantity, quantityInStock);
        });
    }

}
