package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */
@Entity
@Table(name = "SOLDITEM")
public class SoldItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "STOCKITEM_ID",nullable = false, referencedColumnName = "ID")
    private StockItem stockItem;
    @Column(name = "name")
    private String name;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price")
    private double price;
    @ManyToMany(mappedBy = "soldItems")
    private List<PreviousCart> previousCarts;

    public SoldItem() {
    }

    public SoldItem(StockItem stockItem, int quantity) {

        this.stockItem = stockItem;
        this.name = stockItem.getName();
        this.price = stockItem.getPrice();
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = stockItem.getPrice();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getSum() {
        return price * ((double) quantity);
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public List<PreviousCart> getPreviousCarts() {
        return previousCarts;
    }
    public void setPreviousCarts(List<PreviousCart> previousCarts) {
        this.previousCarts = previousCarts;
    }

    @Override
    public String toString() {
        return String.format("SoldItem with id=%d, name='%s' and quantity=%d", id, name, quantity);
    }


}
