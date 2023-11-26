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
    @ManyToMany
   // @JoinTable(
   //         name = "PREVIOUSCART_TO_SOLDITEM",
   //         joinColumns = @JoinColumn(name = "PREVIOUSCART_ID", referencedColumnName = "ID"),
   //         inverseJoinColumns = @JoinColumn(name = "SOLDITEM_ID", referencedColumnName = "ID")
   // )
    private List<SoldItem> soldItems;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "time")
    private LocalTime time;

    public PreviousCart() {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }
    public PreviousCart(List<SoldItem> soldItems) {
        this.soldItems = soldItems;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }




    public List<SoldItem> getCart() {
        return soldItems;
    }

    public void setCart(List<SoldItem> cart) {
        this.soldItems = cart;
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
        return soldItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    @Override
    public String toString() {
        return "Cart"+soldItems+ ";Date: "+date + "; Time: "+time;
    }
}
