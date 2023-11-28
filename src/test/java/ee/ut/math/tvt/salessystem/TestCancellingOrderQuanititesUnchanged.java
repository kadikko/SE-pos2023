package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCancellingOrderQuanititesUnchanged {
    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        //Mock DAO
        dao = mock(InMemorySalesSystemDAO.class);
        shoppingCart = new ShoppingCart(dao);
        when(dao.findStockItem(1L)).thenReturn(new StockItem(1L, "name1", "desc1", 20.00, 10));
        when(dao.findStockItem(2L)).thenReturn(new StockItem(2L, "name2", "desc2", 15.50, 20));
    }

    @Test
    @DisplayName("Checks that after canceling an order the quantities of the related StockItems are not changed")
    public void testCancellingOrderQuantitiesUnchanged() {
        //StockItems
        StockItem si1 = new StockItem(1L, "name1", "desc1", 20.00, 10);
        StockItem si2 = new StockItem(2L, "name2", "desc2", 15.50, 20);

        //Add items to the cart
        shoppingCart.addItem(new SoldItem(si1, 2));
        shoppingCart.addItem(new SoldItem(si2, 3));

        //Cancel purchase
        shoppingCart.cancelCurrentPurchase();

        //Asserts
        assertEquals(10, si1.getQuantity(), "Quantity of the item id=1 should stay the same");
        assertEquals(20, si2.getQuantity(), "Quantity of the item id=2 should stay the same");
    }
}
