package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.PreviousCart;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();
    private final List<PreviousCart> carts;


    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
        this.carts = dao.findPreviousCartList();

    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        // TODO In case such stockItem already exists increase the quantity of the existing stock
        int quantityToBePurchased = item.getQuantity();
        boolean alreadyInCart = false;
        for (SoldItem itemInCart :
                items) {
            if (Objects.equals(itemInCart.getName(), item.getName())) {
                quantityToBePurchased += itemInCart.getQuantity();
                alreadyInCart = true;
                item = itemInCart;
            }
        }

        // TODO verify that warehouse items' quantity remains at least zero or throw an exception
        int quantityInWarehouse = dao.findStockItem(item.getStockItem().getId()).getQuantity();
        try {
            if (quantityToBePurchased > 0 && quantityToBePurchased <= quantityInWarehouse) {
                if(alreadyInCart){
                    item.setQuantity(quantityToBePurchased);
                }
                else{
                    items.add(item);}
            } else if (quantityToBePurchased < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative. ");
            } else {
                throw new SalesSystemException("Quantity exceeds stock in warehouse. ");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Quantity cannot be negative");
            //new Alert(Alert.AlertType.WARNING, "Quantity cannot be negative. Try again.").show();
        } catch (SalesSystemException e) {
            System.out.println("Quantity exceeds stock in warehouse");
            //new Alert(Alert.AlertType.WARNING, "Quantity exceeds stock in warehouse. Only " + item.getQuantity() + " left in stock. Try again.").show();
        }

        //log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public double totalOfCart() {//counts total sum of shopping cart for history.
        double totalSum = 0;
        for (SoldItem item : items) {
            totalSum += item.getSum();
        }
        return totalSum;
    }

    public void submitCurrentPurchase() {
        // TODO decrease quantities of the warehouse stock
        for (SoldItem item : items) {
            StockItem stockItem = dao.findStockItem(item.getStockItem().getId());
            int previousQuantity = stockItem.getQuantity();
            int updatedQuantity = previousQuantity - item.getQuantity();
            stockItem.setQuantity(updatedQuantity);
        }
        List<SoldItem> currentCopy = new ArrayList<>(items);
        PreviousCart current = new PreviousCart(currentCopy);
        dao.savePreviousCart(current);


        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        dao.beginTransaction();
        try {
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
            }
            dao.commitTransaction();
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}
