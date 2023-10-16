package ee.ut.math.tvt.salessystem.ui.controllers;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

public class TeamController implements Initializable {
    private final SalesSystemDAO dao;

    @FXML
    private Text teamName;

    @FXML
    private Text teamContactPerson;

    @FXML
    private Text teamMembers;

    @FXML
    private Text teamLogo;

    public TeamController(SalesSystemDAO dao) throws IOException {
        this.dao = dao;
    }
    private Properties loeProperties(String path) throws IOException {
        Properties pros = new Properties();
        //File file = new File(path);
        //String pathh = file.getAbsolutePath();
        FileInputStream ip = new FileInputStream(path);
        pros.load(ip);
        return pros;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamName.setText("not hah");
        try {
            Properties pros = loeProperties("src/main/resources/application.properties"); //siin tuleb enda arvutis olev application.properties path panna , muidu ei toimi
            String tName = pros.getProperty("teamName");
            String tLogo = pros.getProperty("teamLogo");
            String tMembers = pros.getProperty("teamMembers");
            String tCP = pros.getProperty("teamContactPerson");
            teamName.setText(tName);
            teamMembers.setText(tMembers);
            teamContactPerson.setText(tCP);
            teamLogo.setText(tLogo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

