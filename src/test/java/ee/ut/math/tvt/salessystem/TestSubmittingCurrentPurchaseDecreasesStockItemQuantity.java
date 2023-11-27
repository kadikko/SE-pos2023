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

public class TestSubmittingCurrentPurchaseDecreasesStockItemQuantity {

    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Test
    @DisplayName("Should detect if submitting the purchase decreases StockItem quantity. Testing submitCurrentPurchase() method in class ShoppingCart.")
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity() {

        int initialStockSize = dao.findStockItems().size();
        long barCodeForNewItem = initialStockSize + 1;
        StockItem stockitem1 = new StockItem(barCodeForNewItem, "Water", "sparkling", 1.20, 20);
        StockItem stockitem2 = new StockItem(barCodeForNewItem+1, "Milk", "Alma", 1.55, 45);
        dao.saveStockItem(stockitem1);
        dao.saveStockItem(stockitem2);
        shoppingCart.addItem(new SoldItem(stockitem1, 2));
        shoppingCart.addItem(new SoldItem(stockitem2, 10));
        shoppingCart.submitCurrentPurchase();


        StockItem updatedItem1 = dao.findStockItem(stockitem1.getId());
        assertEquals(18, updatedItem1.getQuantity());

        StockItem updatedItem2 = dao.findStockItem(stockitem2.getId());
        assertEquals(35, updatedItem2.getQuantity());

        assertEquals(1, dao.findPreviousCartList().size());
    }
}