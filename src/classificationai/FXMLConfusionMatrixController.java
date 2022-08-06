/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author HI-TECH
 */
public class FXMLConfusionMatrixController implements Initializable {
    
    int cNum=FXMLClassificationMainController.cNum;
    int [][]ConfusionMatrix=FXMLClassificationMainController.ConfusionMatrix;
    int n=0;
    int anchorSize=550/(cNum+2);
    int actualSum=0;
    int []predictedSum=new int [cNum+1];
    float accuracy=0;
    GridPane gridPane = new GridPane();
    String borderColor="#f4e04d"; 
    
    @FXML
    private AnchorPane conMatrixPane;
    @FXML
    private VBox performanceVBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        gridPane.setMinWidth(550);
        gridPane.setMinHeight(550);
        for(int i=0;i<cNum;i++)//count number of n
            for(int j=0;j<=cNum;j++)
                n+=ConfusionMatrix[i][j];
        
        addCell("n="+n,"#f4e04d","transparent","transparent",0,0);//print number of points
        for(int i=0;i<cNum;i++){// label Predicted Class
            addCell("Predicted Class "+i,"#f4e04d","transparent",borderColor,0,i+1);
        }
        addCell("no Class","#f4e04d","transparent",borderColor,0,cNum+1);// label no Class
        for(int i=0 ; i<cNum;i++){
            addCell("Actual Class "+i,"#f4e04d","transparent",borderColor,i+1,0);// label Actual Class
            for(int j=0 ; j<=cNum;j++){
                if(i==j){
                    addCell(""+ConfusionMatrix[i][j],"#000000","#8CF75B",borderColor,i+1,j+1);//correct Predicted
                    accuracy+=ConfusionMatrix[i][j];
                }else
                    addCell(""+ConfusionMatrix[i][j],"#000000","#F75B5B",borderColor,i+1,j+1);//wrong Predicted
                actualSum+=ConfusionMatrix[i][j];//sum of  actual data points
                predictedSum[j]+=ConfusionMatrix[i][j];//sum of Predicted data points
            }
            addCell(""+actualSum,"#ffffff","transparent",borderColor,i+1,cNum+2);
            actualSum=0;
            
        }
        for(int i=0 ; i<=cNum;i++){
            addCell(""+predictedSum[i],"#ffffff","transparent",borderColor,cNum+1,i+1);
        }
        conMatrixPane.getChildren().add(gridPane);
        
        
        accuracy/=n;
        Label L=new Label("Accuracy: "+accuracy);
        L.setWrapText(true);
        L.setStyle("-fx-text-fill:#f4e04d;-fx-font-size: 20px; -fx-font-weight: bold;");
        performanceVBox.getChildren().add(L);
        
        float MisclassificationRate=1-accuracy;
        L=new Label("Mis Rate: "+MisclassificationRate);
        L.setWrapText(true);
        L.setStyle("-fx-text-fill:#f4e04d;-fx-font-size: 20px; -fx-font-weight: bold;");
        performanceVBox.getChildren().add(L);
        
        if(FXMLClassificationMainController.mseCheckBoxIsSelected){
            for(int i=0;i<cNum;i++){
                L=new Label("MSE"+i+" : "+FXMLClassificationMainController.minMSE[i]);
                L.setWrapText(true);
                L.setStyle("-fx-text-fill:#f4e04d;-fx-font-size: 20px; -fx-font-weight: bold;");
                performanceVBox.getChildren().add(L);
            }
        }
    }

    private void addCell(String txt,String textColor,String cellColor,String borderColor,int i,int j) {
        StackPane cell=new StackPane();
        Label L=new Label(txt);
        L.setWrapText(true);
        L.setStyle("-fx-text-fill:"+textColor+";-fx-font-size: 16px; -fx-font-weight: bold;");
        cell.setPrefWidth(anchorSize);
        cell.setPrefHeight(anchorSize-5);
        cell.setMaxWidth(anchorSize);
        cell.setMaxHeight(anchorSize-5);
        Font font=new Font(16);
        L.setFont(font);
        cell.getChildren().add(L);
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-border-color:"+borderColor+";-fx-background-color:"+ cellColor +";");
        gridPane.add(cell, j, i);
        
    }
    
    
}
