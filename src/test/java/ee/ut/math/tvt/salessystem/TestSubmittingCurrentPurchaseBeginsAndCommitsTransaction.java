package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestSubmittingCurrentPurchaseBeginsAndCommitsTransaction {
    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;
    @BeforeEach
    void setup() {
        dao = mock(InMemorySalesSystemDAO.class);//mock dao to use methods like verify and class inOrder
        shoppingCart =new ShoppingCart(dao);
    }

    @Test
    @DisplayName("Submitting current purchase starts and commits transaction once and in correct order.")
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() {
        List<SoldItem> items = new ArrayList<>();
        shoppingCart.submitCurrentPurchase();
        verify(dao, times(1)).beginTransaction();//methods are called only once
        verify(dao, times(1)).commitTransaction();

        InOrder inOrder = inOrder(dao);
        inOrder.verify(dao).beginTransaction();//verifies that the first method called is beginTransaction() and second commitTransaction()
        inOrder.verify(dao).commitTransaction();
    }
}
