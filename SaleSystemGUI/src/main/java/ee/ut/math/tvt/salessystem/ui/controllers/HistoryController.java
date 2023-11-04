package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.PreviousCart;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoryController implements Initializable {
    private  SalesSystemDAO dao;

    @FXML
    private Button betweenDates;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private TableView<PreviousCart> listPurchases;
    @FXML
    private TableView<SoldItem> purchaseItems;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //listens what row is selected.
        listPurchases.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                refreshPurchaseItems(newSelection);
            }
        });
    }


    public void refreshPurchaseItems(PreviousCart cart){
        purchaseItems.setItems(FXCollections.observableList(cart.getCart()));
        purchaseItems.refresh();
    }

    //sorts the List<PreviousCart> by date and time from newest to oldest.
    private List<PreviousCart> getSortedCarts() {
        List<PreviousCart> sortedCarts = new ArrayList<>(dao.findPreviousCartList());
        sortedCarts.sort((cart1, cart2) -> {
            int dateCompare = cart2.getDate().compareTo(cart1.getDate());
            if (dateCompare == 0) {
                return cart2.getTime().compareTo(cart1.getTime());
            }
            return dateCompare;
        });
        return sortedCarts;
    }

    //shows all carts
    public void showAll() {
        List<PreviousCart> sortedCarts = getSortedCarts();
        listPurchases.setItems(FXCollections.observableList(sortedCarts));
        listPurchases.refresh();
    }

    //shows last 10 carts
    public void show10() {
        List<PreviousCart> sortedCarts = getSortedCarts();
        int endIndex = Math.min(sortedCarts.size(), 10);
        List<PreviousCart> last10Carts = sortedCarts.subList(0, endIndex);

        listPurchases.setItems(FXCollections.observableList(last10Carts));
        listPurchases.refresh();
    }

    @FXML
    public void showCartsBetweenDates() {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        if (start == null || end == null) {
            // Alert or log that no date is selected if required.
            System.out.println("Please select both start and end dates.");
            new Alert(Alert.AlertType.WARNING, "Please select both start and end dates. Try again.").show();
            return;
        }

        if (start.isAfter(end)) {
            // Alert or log that the dates are invalid if required.
            System.out.println("Start date must be before end date.");
            new Alert(Alert.AlertType.WARNING, "Start date must be before end date. Try again.").show();
            return;
        }

        // Assuming findPreviousCartList() returns a live or unmodifiable list that you don't want to filter directly.
        List<PreviousCart> sortedCarts = getSortedCarts();
        List<PreviousCart> filteredCarts = sortedCarts.stream()
                .filter(cart -> !cart.getDate().isBefore(start) && !cart.getDate().isAfter(end))
                .collect(Collectors.toList());

        listPurchases.setItems(FXCollections.observableList(filteredCarts));
        listPurchases.refresh();
    }

}
