package ee.ut.math.tvt.salessystem.dao;
import ee.ut.math.tvt.salessystem.dataobjects.PreviousCart;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;
    public HibernateSalesSystemDAO () {
// if you get ConnectException / JDBCConnectionException then you
// probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory ("pos");
        em = emf.createEntityManager ();
    }
    // TODO implement missing methods
    public void close () {
        em.close ();
        emf.close ();
    }

    @Override
    public List<StockItem> findStockItems() {
        return em.createQuery("from StockItem ", StockItem.class).getResultList();
    }

    @Override
    public StockItem findStockItem(long id) {
        return em.find(StockItem.class, id);
    }

    @Override
    public List<PreviousCart> findPreviousCartList() {
        return em.createQuery("from PreviousCart ", PreviousCart.class).getResultList();
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        em.merge(stockItem);
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        em.merge(item);
    }
    @Override
    public List<SoldItem> findSoldItems() {
        return em.createQuery("from SoldItem ", SoldItem.class).getResultList();
    }
    @Override
    public void savePreviousCart(PreviousCart previousCart) {
        em.merge(previousCart);
    }

    @Override
    public void beginTransaction () {
        em.getTransaction (). begin ();
    }
    @Override
    public void rollbackTransaction () {
        em.getTransaction (). rollback ();
    }
    @Override
    public void commitTransaction () {
        em.getTransaction (). commit ();
    }

}