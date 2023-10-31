package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TestAddingExistingItem {

    private SalesSystemDAO mockDao;

    @BeforeEach
    void setup() {
        mockDao = mock(InMemorySalesSystemDAO.class);
    }

    @Test
    @DisplayName("Should show if adding an exiting item increases the stock quantity and if the saveStockItem method of the DAO is not called")
    public void testAddingExistingItem() {

        // setting up mock dao
        List<StockItem> items = new ArrayList<StockItem>();
        StockItem initialStockItem = new StockItem(1L, "Domino", "Cookies", 2.50, 300);
        items.add(initialStockItem);
        when(mockDao.findStockItem(1L)).thenReturn(items.get(0));

        // new batch of items with bar code 1 comes into warehouse, quantity 444
        int additionalQuantity = 444;
        StockItem stockItemAdditional = mockDao.findStockItem(1L);
        int quantityBeforeUpdate = mockDao.findStockItem(1L).getQuantity();
        stockItemAdditional.setQuantity(quantityBeforeUpdate + additionalQuantity);

        assertTrue(mockDao.findStockItem(1L).getQuantity() > quantityBeforeUpdate);
        assertEquals(mockDao.findStockItem(1L).getQuantity(), quantityBeforeUpdate + additionalQuantity);
        verify(mockDao, times(0)).saveStockItem(stockItemAdditional);
    }

}
