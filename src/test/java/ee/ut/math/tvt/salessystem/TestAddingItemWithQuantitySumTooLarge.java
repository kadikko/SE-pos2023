package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAddingItemWithQuantitySumTooLarge {

    // check that an exception is thrown if the sum of the quantity
    // of the added item and the quantity already in the shopping cart
    // is larger than the quantity in the warehouse

    private ShoppingCart shoppingCart;

    @Test
    public void testAddingItemWithQuantitySumTooLarge() {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
        StockItem stockItem = dao.findStockItem(1L);
        int quantityInStock = dao.findStockItem(1L).getQuantity();

        // let's add 2 pieces of stockitem with ID 1 into cart items
        SoldItem soldItem = new SoldItem(stockItem, 2);
        List<SoldItem> cartItems = new ArrayList<>();
        cartItems.add(soldItem);

        // let's buy another 100 pieces of same stockitem
        int quantityToBePurchased = 100;

        for (SoldItem itemInCart :
                cartItems) {
            if (Objects.equals(itemInCart.getName(), soldItem.getName())) {
                quantityToBePurchased += itemInCart.getQuantity();
                soldItem = itemInCart;
            }
        }
        int finalQuantityToBePurchased = quantityToBePurchased;

        // verifying that the quantityToBePurchased gets correctly updated and the amount in cart already is added to
        // the amount the customer is now attempting to add to cart
        assertEquals(finalQuantityToBePurchased, 100 + 2);

        // verifying that an exception is thrown in case after the update the quantityToBePurchased is higher than
        // quantity of said item in stock
        assertThrows(SalesSystemException.class, () -> {
            shoppingCart.validateQuantityNotTooLarge(finalQuantityToBePurchased, quantityInStock);
        });
    }

}
