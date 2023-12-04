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
import org.mockito.InOrder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class Tests {
    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;
    private SalesSystemDAO mockDao;
    private ShoppingCart mockShoppingCart;




    @BeforeEach
    public void setUp() {
        dao = new InMemorySalesSystemDAO();
        mockDao = mock(InMemorySalesSystemDAO.class);
        shoppingCart = new ShoppingCart(dao);
        mockShoppingCart = new ShoppingCart(mockDao);
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


    @Test
    public void testAddingItemWithQuantitySumTooLarge() {

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

    @Test
    @DisplayName("Checks that after canceling an order the quantities of the related StockItems are not changed")
    public void testCancellingOrderQuantitiesUnchanged() {

        //StockItems
        StockItem si1 = new StockItem(1L, "name1", "desc1", 20.00, 10);
        StockItem si2 = new StockItem(2L, "name2", "desc2", 15.50, 20);
        when(mockDao.findStockItem(1L)).thenReturn(new StockItem(1L, "name1", "desc1", 20.00, 10));
        when(mockDao.findStockItem(2L)).thenReturn(new StockItem(2L, "name2", "desc2", 15.50, 20));

        //Add items to the cart
        mockShoppingCart.addItem(new SoldItem(si1, 2));
        mockShoppingCart.addItem(new SoldItem(si2, 3));

        //Cancel purchase
        mockShoppingCart.cancelCurrentPurchase();


        //Asserts
        assertEquals(10, si1.getQuantity(), "Quantity of the item id=1 should stay the same");
        assertEquals(20, si2.getQuantity(), "Quantity of the item id=2 should stay the same");
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


    @Test
    @DisplayName("Should test if submitting a purchase makes a previousCart have accurate dates")
    public void testSubmittingCurrentOrderSavesCorrectTime() {
        LocalTime aeg = LocalTime.now();
        shoppingCart.submitCurrentPurchase();
        PreviousCart cart = dao.findPreviousCartList().get(dao.findPreviousCartList().size()-1);
        assertEquals(aeg.getSecond(), cart.getTime().getSecond());

    }

    @Test
    @DisplayName("Submitting current purchase starts and commits transaction once and in correct order.")
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() {
        List<SoldItem> items = new ArrayList<>();
        mockShoppingCart.submitCurrentPurchase();
        verify(mockDao, times(1)).beginTransaction();//methods are called only once
        verify(mockDao, times(1)).commitTransaction();

        InOrder inOrder = inOrder(mockDao);
        inOrder.verify(mockDao).beginTransaction();//verifies that the first method called is beginTransaction() and second commitTransaction()
        inOrder.verify(mockDao).commitTransaction();
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
