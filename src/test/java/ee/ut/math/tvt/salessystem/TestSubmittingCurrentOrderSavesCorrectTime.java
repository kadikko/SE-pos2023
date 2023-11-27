package ee.ut.math.tvt.salessystem;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.PreviousCart;

import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;


import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestSubmittingCurrentOrderSavesCorrectTime {
    private SalesSystemDAO dao;
    private ShoppingCart shoppingCart;


    @BeforeEach
    void setup() {
        dao = new InMemorySalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Test
    @DisplayName("Should test if submitting a purchase makes a previousCart have accurate dates")
    public void testSubmittingCurrentOrderSavesCorrectTime() {
        LocalTime aeg = LocalTime.now();
        shoppingCart.submitCurrentPurchase();
        PreviousCart cart = dao.findPreviousCartList().get(dao.findPreviousCartList().size()-1);
        assertEquals(aeg.getSecond(), cart.getTime().getSecond());

    }
}