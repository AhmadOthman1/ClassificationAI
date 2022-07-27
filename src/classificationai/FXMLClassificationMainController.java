/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

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
    }
    
}
