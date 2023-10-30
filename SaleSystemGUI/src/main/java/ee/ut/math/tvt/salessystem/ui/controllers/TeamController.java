package ee.ut.math.tvt.salessystem.ui.controllers;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class TeamController implements Initializable {

    private static final Logger log = LogManager.getLogger("TeamController");
    private final SalesSystemDAO dao;

    @FXML
    private Text teamName;

    @FXML
    private Text teamContactPerson;

    @FXML
    private Text teamMembers;

    @FXML
    private ImageView teamLogo;

    public TeamController(SalesSystemDAO dao) {
        this.dao = dao;
    }
    private Properties loeProperties(String path) throws IOException {
        Properties pros = new Properties();
        File file = new File(path);
        String absPath = file.getAbsolutePath();
        String[] pathid = absPath.split("lg10-lg10");
        absPath = pathid[0] + "lg10-lg10"+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+path;
        FileInputStream ip = new FileInputStream(absPath);
        pros.load(ip);
        return pros;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Properties pros = loeProperties("application.properties");
            String tName = pros.getProperty("teamName");
            String tLogo = pros.getProperty("teamLogo");
            String tMembers = pros.getProperty("teamMembers");
            String tCP = pros.getProperty("teamContactPerson");
            teamName.setText(tName);
            teamMembers.setText(tMembers);
            teamContactPerson.setText(tCP);
            teamLogo.setImage(new Image(tLogo));
            log.info("Team info loaded");
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}

