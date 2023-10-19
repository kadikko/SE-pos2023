package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private final SalesSystemDAO dao;
    private static final Logger log = LogManager.getLogger(StockController.class);
    @FXML
    private Button addItem;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TableView<StockItem> warehouseTableView;

    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        addItem.setDisable(true);

        priceField.textProperty().addListener((observable, oldValue, newValue) -> allowAddItem());//allows to keep track of inserted values
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> allowAddItem());//and manage when item is ready to be added to the warehouse.
        nameField.textProperty().addListener((observable, oldValue, newValue) -> allowAddItem());
        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {//adding already existing item
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                }
            }
        });

    }

    private void allowAddItem() {
        String price = priceField.getText();
        String quantity = quantityField.getText();
        String name = nameField.getText();

        if (!price.isEmpty() && !name.isEmpty()) {
            barCodeField.setDisable(true);//does not allow to insert barcode if new name and price are inserted.
            if (!quantity.isEmpty()){//quantity has to be inserted to add item to warehouse.
                addItem.setDisable(false);}
        } else {
            barCodeField.setDisable(false);
            addItem.setDisable(true);
        }
    }
    // TODO refresh view after adding new items

    @FXML
    public void addStockItemEventHandler() {
        // add chosen item to the warehouse
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());//inserted quantity must be a number.
                stockItem.setQuantity(stockItem.getQuantity() + quantity);
            } catch (NumberFormatException e) {
                resetProductField();
            }
        }
        else {
            String name = nameField.getText();
            String description = "";
            long id = dao.findStockItems().size() + 1;
            double price;
            int quantity;

            try{
                price = Double.parseDouble(priceField.getText());//inserted price has to be number.
                quantity = Integer.parseInt(quantityField.getText());
                StockItem newItem = new StockItem(id, name, description, price, quantity);
                dao.saveStockItem(newItem);
            }
            catch (NumberFormatException e) {
                resetProductField();
            }

        }
        resetProductField();//after adding to the warehouse.
        refreshStockItems();
    }
    private void fillInputsBySelectedStockItem() {
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            nameField.setText(stockItem.getName());
            priceField.setText(String.valueOf(stockItem.getPrice()));
            barCodeField.setDisable(false);
        } else {
            resetProductField();
        }
    }
    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("");
        nameField.setText("");
        priceField.setText("");
    }

    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }
}
