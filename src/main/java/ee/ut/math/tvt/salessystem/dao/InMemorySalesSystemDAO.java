package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.PreviousCart;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.ArrayList;
import java.util.List;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;
    private final List<PreviousCart> previousCartList; // all carts before
    private long nextSaleId = 1;



    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<StockItem>();
        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();


//        UNCOMMENT TO SEE HISTORY TAB WITH MADEUP CARTS
//        Random random = new Random();
//        List<PreviousCart> previousCartsItems = new ArrayList<PreviousCart>();
//        for(int n = 0; n<15; n++){
//            List<SoldItem> soldItems = new ArrayList<SoldItem>();
//            int itemsNum = random.nextInt(5)+1;
//            for (int i=0; i<itemsNum; i++){
//                int whatItem = random.nextInt(4);
//                int whatQuantity = random.nextInt(10)+1;
//                soldItems.add(new SoldItem(items.get(whatItem), whatQuantity));
//            }
//            PreviousCart temp = new PreviousCart(soldItems);
//            LocalDate date = LocalDate.now().minusDays(random.nextInt(1000));
//            LocalTime time = LocalTime.now().minusHours(random.nextInt(24)).minusMinutes(random.nextInt(60));
//            temp.setDate(date);
//            temp.setTime(time);
//            previousCartsItems.add(temp);
//
//        }
//        this.previousCartList = previousCartsItems;
        this.previousCartList = new ArrayList<>(); //COMMENT THIS OUT, IF YOU WANT TO USE MADE UP CARTS
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public List<PreviousCart>  findPreviousCartList() {
        return previousCartList;
    }

    @Override
    public List<PreviousCart> findLast10Carts() {
        return null;
    }

    @Override
    public void saveSoldItem(SoldItem item) {//as we cannot use hibernate for creating an id, this method mocks database by addinf id to SoldItem manually
        if (item.getId() == null){
            item.setId(nextSaleId++);
        }
        soldItemList.add(item);
    }

    @Override
    public List<SoldItem> findSoldItems() {
        return soldItemList;
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
    }

    @Override
    public void savePreviousCart(PreviousCart previousCart) {
        previousCartList.add(previousCart);
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
    }
}
