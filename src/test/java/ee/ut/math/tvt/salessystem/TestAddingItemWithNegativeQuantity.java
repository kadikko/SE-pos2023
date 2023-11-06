package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAddingItemWithNegativeQuantity {

    // check that adding an item
    //with negative quantity results in an exception

    private ShoppingCart shoppingCart;

    @BeforeEach
    void setup() {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Test
    public void testAddingItemWithNegativeQuantity() {
        int negativeQuantityEx = -10;

        // throws an illegal argument exception when quantity is negative
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.validateQuantityNotNegative(negativeQuantityEx);
        });
    }

    @Test
    public void testAddingItemsWithPositiveQuantity() {
        int positiveQuantityEx = 234;

        // returns true when quantity is not negative
        assertTrue(shoppingCart.validateQuantityNotNegative(positiveQuantityEx));
    }

}
