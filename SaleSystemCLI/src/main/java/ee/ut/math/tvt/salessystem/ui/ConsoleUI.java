package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger("ConsoleUI");

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
    }

    public static void main(String[] args) throws Exception {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        log.info("CLI initialized");
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
            System.out.println("Done. ");
        }
    }

    private void showStock() {
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getId() + " " + si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (stockItems.isEmpty()) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)" + " Sum: " + si.getSum() + "Euro");
        }
        if (cart.getAll().isEmpty()) {
            System.out.println("\tNothing");
        }
        System.out.println("Total: " + cart.totalOfCart() + "Euro");
        System.out.println("-------------------------");
    }

    private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\tShow this help");
        System.out.println("w\t\tShow warehouse contents");
        System.out.println("c\t\tShow cart contents");
        System.out.println("a IDX NR \tAdd NR of stock item with index IDX to the cart");
        System.out.println("aw\t\tAdd a stock item to the warehouse");
        System.out.println("rw\t\tRefresh warehouse");
        System.out.println("p\t\tPurchase the shopping cart");
        System.out.println("r\t\tReset the shopping cart");
        System.out.println("t\t\tShow team view");
        System.out.println("-------------------------");
    }

    public Properties loeProperties(String path) throws IOException {
        Properties pros = new Properties();
        File file = new File(path);
        String absPath = file.getAbsolutePath();
        String[] pathid = absPath.split("lg10-lg10");
        absPath = pathid[0] + "lg10-lg10"+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"application.properties";
        FileInputStream ip = new FileInputStream(absPath);
        pros.load(ip);
        return pros;
    }
    private void showTeam() throws IOException {
        Properties properties = loeProperties("application.properties");

        System.out.println("-------------------------");
        System.out.println("Team name: " + properties.getProperty("teamName"));
        System.out.println("Team contact person: " + properties.getProperty("teamContactPerson"));
        System.out.println("Team members: " + properties.getProperty("teamMembers"));
        System.out.println("-------------------------");
    }

    private void addWarehouseHelp() {
        System.out.println("-------------------------");
        System.out.println("Usage:\n");
        System.out.println("ae IDX NR\n\n- Adds an already existing item to the warehouse (essentialy changing the quantity)\n(example: 'ae 5 5')\n");
        System.out.println("an NR NAME PRC\n\n- Add a new item to the warehouse with it's details (split the name with undercases)\n(example: 'an 5 Vodka 9.99', 'an 8 Frosted_Flakes 2.99'");
        System.out.println("-------------------------");

    }
    private void addWarehouse() throws IOException {
        addWarehouseHelp();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] cmd = in.readLine().trim().split(" ");
        if (cmd[0].equalsIgnoreCase("q")) System.exit(0);
        else if (cmd[0].equalsIgnoreCase("ae") && cmd.length==3) {
            try {
                long idx = Long.parseLong(cmd[1]);
                int amount = Integer.parseInt(cmd[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    log.info("Updating existing item");
                    item.setQuantity(item.getQuantity()+amount);
                } else {
                    log.error("no stock item with id " + idx);
                    log.debug("Updating existing item failed");
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage());
            }
        }
        else if (cmd[0].equalsIgnoreCase("an") && cmd.length == 4) {
            try {
                long idx = dao.findStockItems().size() + 1;
                int amount = Integer.parseInt(cmd[1]);
                String[] names = cmd[2].split("_");
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < names.length; i++) {
                    if (i == names.length - 1) name.append(names[i]);
                    else name.append(names[i]).append(" ");
                }
                double price = Double.parseDouble(cmd[3]);
                log.info("Adding new item to warehouse");
                dao.saveStockItem(new StockItem(idx, name.toString(), "", price, amount));
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        } else log.info("Unknown command");
        printUsage();
    }
    private void processCommand(String command) throws IOException {
        String[] c = command.split(" ");

        if (c[0].equals("h")) {
            log.debug("Help requested");
            printUsage();
        }
        else if (c[0].equals("q")) {
            log.info("Bye!");
            System.exit(0);
        }
        else if (c[0].equals("w")) {
            log.debug("Warehouse contents displayed");
            showStock();
        }
        else if (c[0].equals("c")) {
            log.debug("Cart contents displayed");
            showCart();
        }
        else if (c[0].equals("t")) {
            log.debug("Team info displayed");
            showTeam();
        }
        else if (c[0].equals("p")) {
            log.info("Purchase submitted");
            cart.submitCurrentPurchase();
        }
        else if (c[0].equals("r")) {
            log.info("Purchase cancelled");
            cart.cancelCurrentPurchase();
        }
        else if (c[0].equals("a") && c.length == 3) {
            try {
                int amount = Integer.parseInt(c[2]);
                long idx = Long.parseLong(c[1]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    log.info("New item added to cart");
                    cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                } else {
                    log.error("no stock item with id " + idx);
                }
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
        else if (c[0].equals(("aw"))) {
            addWarehouse();
        }
        else if (c[0].equals(("rw"))) {
            log.debug("Warehouse updated");
            showStock();
        }
        else {
            log.info("Unknown command");
        }
    }

}
