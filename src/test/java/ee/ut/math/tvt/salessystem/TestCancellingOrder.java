package ee.ut.math.tvt.salessystem;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.PreviousCart;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCancellingOrder {
    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }


    @Test
    @DisplayName("Checks that canceling an order (with some items) and then submitting a new order (with some different items) only saves the items from the new order (with canceled items are discarded)")
    public void testCancellingOrder() {
        //Make the first cart
        shoppingCart.addItem(new SoldItem(new StockItem(1L, "name1", "desc1", 3, 20), 2));
        shoppingCart.addItem(new SoldItem(new StockItem(2L, "name2", "desc2", 9, 10), 3));

        //Cancel order
        shoppingCart.cancelCurrentPurchase();

        //Make the second cart
        shoppingCart.addItem(new SoldItem(new StockItem(3L, "name3", "desc3", 10.0, 10), 1));
        shoppingCart.addItem(new SoldItem(new StockItem(4L, "name4", "desc4", 20.0, 20), 2));

        //Submit the order
        shoppingCart.submitCurrentPurchase();

        //Get last submited cart
        List<PreviousCart> previousCarts = dao.findPreviousCartList();
        assertTrue(previousCarts.size() > 0, "There are no previous carts");
        PreviousCart lastCart = previousCarts.get(previousCarts.size() - 1);
        List<SoldItem> lastSubmittedItems = lastCart.getCart();

        //Asserts
        assertEquals(2, lastSubmittedItems.size(), "The submitted cart does not have 2 items");
        assertTrue(lastSubmittedItems.stream().anyMatch(item -> item.getStockItem().getId() == 3L), "Item id = 3 should be in the submitted cart.");
        assertTrue(lastSubmittedItems.stream().anyMatch(item -> item.getStockItem().getId() == 4L), "Item id = 4 should be in the submitted cart.");
    }
}
