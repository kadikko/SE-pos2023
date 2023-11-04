package ee.ut.math.tvt.salessystem.dataobjects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PreviousCart {
    private List<SoldItem> cart;
    private LocalDate date;
    private LocalTime time;


    public PreviousCart(List<SoldItem> cart) {
        this.cart = cart;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
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

    @Override
    public String toString() {
        return "Cart"+cart+ ";Date: "+date + "; Time: "+time;
    }
}
