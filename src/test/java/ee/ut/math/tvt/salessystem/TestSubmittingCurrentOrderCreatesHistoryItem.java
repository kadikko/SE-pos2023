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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestSubmittingCurrentOrderCreatesHistoryItem {
    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;


    @BeforeEach
    void setup() {
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Test
    @DisplayName("Should test if submitting a purchase makes a previousCart item in history")
    public void testSubmittingCurrentOrderCreatesHistoryItem() {
        List<PreviousCart> oldCartList = dao.findPreviousCartList();
        int oldCartListSize = oldCartList.size();
        int initialStockSize = dao.findStockItems().size();
        long barCodeForNewItem = initialStockSize + 1;
        StockItem stockitem1 = new StockItem(barCodeForNewItem, "Water", "sparkling", 1.20, 20);
        StockItem stockitem2 = new StockItem(barCodeForNewItem+1, "Milk", "Alma", 1.55, 45);
        dao.saveStockItem(stockitem1);
        dao.saveStockItem(stockitem2);
        SoldItem soldItem1 = new SoldItem(stockitem1, 2);
        SoldItem soldItem2 = new SoldItem(stockitem2, 10);
        List<SoldItem> soldItems = new ArrayList<>();
        soldItems.add(soldItem1);
        soldItems.add(soldItem2);
        shoppingCart.addItem(soldItem1);
        shoppingCart.addItem(soldItem2);
        shoppingCart.submitCurrentPurchase();

        List<PreviousCart> newCartList = dao.findPreviousCartList();
        List<SoldItem> cartNew = newCartList.get(dao.findPreviousCartList().size()-1).getCart();

        assertEquals(1,newCartList.size()-oldCartListSize);

        for (int i = 0; i < soldItems.size(); i++) {
            assertEquals(soldItems.get(i), cartNew.get(i));
        }

    }
}