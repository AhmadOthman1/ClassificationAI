/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author HI-TECH
 */
public class FXMLClassificationMainController implements Initializable {

    @FXML
    private AnchorPane mainPagePane;
    @FXML
    private ComboBox<String> CategorieComboBox;
    @FXML
    private Button generateGridButton;
    @FXML
    private AnchorPane settingsPane;
    @FXML
    private ComboBox<?> classComboBox;
    @FXML
    private TextField learningRate;
    @FXML
    private TextField iterationsNum;
    @FXML
    private Label excelFilePath;
    @FXML
    private Label excelFileName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        CategorieComboBox.setItems(FXCollections.observableArrayList("Two","Three","Four"));
        CategorieComboBox.getSelectionModel().selectFirst();
    }    

    @FXML
    private void actionButton(ActionEvent event) {
        if(event.getSource()==generateGridButton){
            mainPagePane.setVisible(false);
        }
        else if(event.getSource()==generateGridButton){
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
            fileChooser.getExtensionFilters().addAll(extFilterJPG);

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);
            if(file!=null){
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                    if((int)image.getWidth()==(int)image.getHeight()){
                        excelFilePath.setText(file.getPath());
                    }
                } catch (IOException ex) {
                    
                } 
        }
        }
    }
    
}
