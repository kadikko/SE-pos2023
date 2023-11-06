package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAddingNewItem {

    private SalesSystemDAO dao;

    @BeforeEach
    void setup() {
        dao = new InMemorySalesSystemDAO();
    }

    @Disabled
    @Test
    @DisplayName("Should show if a new item is saved through the DAO")
    public void testAddingNewItem() {

        // initial number of stock items in dao
        int initialStockSize = dao.findStockItems().size();
        long barCodeForNewItem = initialStockSize + 1;
        StockItem newStockItem = new StockItem(barCodeForNewItem, "Jenkki", "Chewing gum", 1.00, 45);
        dao.saveStockItem(newStockItem);

        // updated number of stock items in dao
        int updatedStockSize = dao.findStockItems().size();
        assertEquals(updatedStockSize, initialStockSize + 1);
    }
}
