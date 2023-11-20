package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "PREVIOUSCART")
public class PreviousCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany
    @JoinColumn(name = "SOLDITEM_ID", nullable = false)
    private List<SoldItem> cart;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "time")
    private LocalTime time;


    public PreviousCart(List<SoldItem> cart) {
        this.cart = cart;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public PreviousCart() {

    }


    public List<SoldItem> getCart() {
        return cart;
    }

    public void setCart(List<SoldItem> cart) {
        this.cart = cart;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public double getTotal() {
        return cart.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    @Override
    public String toString() {
        return "Cart"+cart+ ";Date: "+date + "; Time: "+time;
    }
}
