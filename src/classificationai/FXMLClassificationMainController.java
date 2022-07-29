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
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

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
    private AnchorPane settingsPane;
    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private TextField learningRate;
    @FXML
    private TextField iterationsNum;
    @FXML
    private Label excelFilePath;
    @FXML
    private Button browseButton;
    @FXML
    private AnchorPane drawPnael;

    ArrayList <String> classApoints=new ArrayList(); 
    ArrayList <String> classBpoints=new ArrayList();
    ArrayList <String> classCpoints=new ArrayList(); 
    ArrayList <String> classDpoints=new ArrayList(); 
    ArrayList <String> classpoints=new ArrayList(); 
    
    float w1,w2,threshold,alfa;
    boolean isLearn=false;
    Text t=new Text();
    float mse;
    Line line=new Line();
    
      //Setting the properties of the rectangle 
    @FXML
    private Button startButton;
    @FXML
    private Button learnButton;
    @FXML
    private Label testLabel;
    @FXML
    private Button UndoButton;
      
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        drawPnael.getChildren().add(t);
        iterationsNum.setText("9");
        learningRate.setText("0.1");
        // TODO
        CategorieComboBox.setItems(FXCollections.observableArrayList("Two -Binary","Three -multi class","Four -multi class"));
        CategorieComboBox.getSelectionModel().selectFirst();
        
        drawPnael.getChildren().add(line);
    }    

    @FXML
    private void actionButton(ActionEvent event) {
        if(event.getSource()==startButton){
            if(CategorieComboBox.getValue().equals("Two -Binary")){
                classComboBox.setItems(FXCollections.observableArrayList("Class A","Class B"));
                classComboBox.getSelectionModel().selectFirst();
            }
            else if(CategorieComboBox.getValue().equals("Three -multi class")){
                classComboBox.setItems(FXCollections.observableArrayList("Class A","Class B","Class C"));
                classComboBox.getSelectionModel().selectFirst();
            }
            else{
                classComboBox.setItems(FXCollections.observableArrayList("Class A","Class B","Class C","Class D"));
                classComboBox.getSelectionModel().selectFirst();
            }
            
            mainPagePane.setVisible(false);
            settingsPane.setVisible(true);
            drawPnael.setVisible(true);
        }
        
        if(event.getSource()==browseButton){
            drawPnael.getChildren().clear();
            classpoints.clear();
            isLearn=false;
        }
         if(event.getSource()==learnButton){
             learn();
         }
         if(event.getSource()==UndoButton){
             if(!classpoints.isEmpty() && !isLearn){
             drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
             classpoints.remove(classpoints.size()-1);
             }
         }
    }

    @FXML
    private void mouseClickd(MouseEvent event) {
        if(!isLearn){
        
            if(classComboBox.getValue().equals("Class A")){
                Arc arc = new Arc(event.getX(), event.getY(), 10, 10, 0, 360);
                arc.setFill(Color.TRANSPARENT);
                arc.setStroke(Color.GREEN);
                classpoints.add(event.getX()+","+event.getY()+",A");
                drawPnael.getChildren().add(arc);
            }
            else if(classComboBox.getValue().equals("Class B")){
                Rectangle rectangle = new Rectangle();  
                rectangle.setX(event.getX()); 
                rectangle.setY(event.getY());
                classpoints.add(event.getX()+","+event.getY()+",B");
                rectangle.setWidth(15.0); 
                rectangle.setHeight(15.0);
                rectangle.setStroke(Color.RED);
                rectangle.setFill(Color.TRANSPARENT);
                drawPnael.getChildren().add(rectangle);
            }
        if(CategorieComboBox.getValue().equals("Three -multi class")){
            if(classComboBox.getValue().equals("Class C")){
                Polygon fovTriangle = new Polygon(
                (10 * Math.tan(0)), -5,
               (10 * Math.tan(70)), 10,
               -(10 * Math.tan(70)), 10  
                );
               classpoints.add(event.getX()+","+event.getY()+",C");
               fovTriangle.setLayoutX(event.getX());
               fovTriangle.setLayoutY(event.getY());
               fovTriangle.setStroke(Color.BLUE);
               fovTriangle.setFill(Color.TRANSPARENT);
               drawPnael.getChildren().add(fovTriangle);
            }
        }
        else if(CategorieComboBox.getValue().equals("Four -multi class")){
            if(classComboBox.getValue().equals("Class C")){
                Polygon fovTriangle = new Polygon(
                (10 * Math.tan(0)), -5,
               (10 * Math.tan(70)), 10,
               -(10 * Math.tan(70)), 10  
                );
               classpoints.add(event.getX()+","+event.getY()+",C");     
               fovTriangle.setLayoutX(event.getX());
               fovTriangle.setLayoutY(event.getY());
               fovTriangle.setStroke(Color.BLUE);
               fovTriangle.setFill(Color.TRANSPARENT);
               drawPnael.getChildren().add(fovTriangle);
            }
            
            if(classComboBox.getValue().equals("Class D")){
                Arc arc = new Arc(event.getX(), event.getY(), 10, 10, 90, 270);
                arc.setType(ArcType.ROUND);
                arc.setFill(Color.TRANSPARENT);
                arc.setStroke(Color.YELLOW);
                classpoints.add(event.getX()+","+event.getY()+",D");
                drawPnael.getChildren().add(arc);
            }
            
        }
        
        }
        else{
            drawPnael.getChildren().remove(t);
            t.setText("x");
            t.setX(event.getX());
            t.setY(event.getY());
            t.setStroke(Color.BROWN);
            drawPnael.getChildren().add(t);
            
            test(event.getX(),event.getY());
        }
    }

    
    private void learn(){
        isLearn=true;
//        classpoints.add("0,0,0");
//        classpoints.add("0,1,0");
//        classpoints.add("1,0,0");
//        classpoints.add("1,1,1");
        Random rand = new Random();
        w1=rand.nextFloat(-0.5f, 0.5f);
        w2=rand.nextFloat(-0.5f, 0.5f);
        threshold=rand.nextFloat(-0.5f, 0.5f);
//        w1=0.3f;
//        w2=-0.1f;
//        threshold=0.2f;
        
        float x1,x2,delta_w1,delta_w2,bigX,delta_threshold;
        int yd,ya,error;
        
        alfa=Float.parseFloat(learningRate.getText());
        for(int i=0;i<Integer.parseInt(iterationsNum.getText());i++){
           int ta=0,tb=0,na=0,nb=0;
           int actualA=0,actualB=0;
           int predectedA=0,predectedB=0;
            mse=0;
            for(int j=0;j<classpoints.size();j++){
           String [] Spoint =classpoints.get(j).split(",");
           x1=Float.parseFloat(Spoint[0]);
           x2=Float.parseFloat(Spoint[1]);
           if(Spoint[2].equals("A")){
               yd=1;
               actualA++;
           }
           else{ yd=0;
           actualB++;
           }
          //  yd=Integer.parseInt(Spoint[2]);
            System.out.println("x1="+x1);
            System.out.println("x2="+x2);
            System.out.println("yd="+yd);
           bigX=(x1*w1)+(x2*w2)+(threshold*-1);
           System.out.println("bigX="+bigX);
           if(bigX>=0){
               ya=1;
               predectedA++;
           }
           
            else {
               ya=0;
               predectedB++;
           }
           if(yd==1 && ya==1){
               ta++;
           }
           else if(yd==1 && ya==0){
               na++;
           }
           else if(yd==0 && ya==0){
               tb++;
           }
           else if(yd==0 && ya==1){
               nb++;
           }
           error=yd-ya;
           System.out.println("ya="+ya);
           System.out.println("error="+error);
           
          
           delta_w1=alfa*x1*error;
               w1=w1+delta_w1;    
           delta_w2=alfa*x2*error;
               w2=w2+delta_w2;
           delta_threshold=alfa*-1*error;    
           threshold=threshold+delta_threshold;
           System.out.println("delta W1="+delta_w1);
           System.out.println("delta W2="+delta_w2);
           System.out.println(" W1="+w1);
           System.out.println(" W2="+w2);
           System.out.println("delta delta_threshold="+delta_threshold);
           System.out.println(" threshold="+threshold);
           mse=Math.abs(yd-ya)+mse;
            }
             System.out.println("msr="+mse);
             mse=(1/(float)classpoints.size())*mse;
             System.out.println("msr="+mse);
            testLabel.setText("mse="+mse); 
        System.out.println("            predected A     predected B");
        System.out.println("Actual A    "+ta+"               "+na+"               "+actualA);
        System.out.println("Actual B    "+nb+"               "+tb+"               "+actualB);
        System.out.println("            "+predectedA+"               "+predectedB+"                          ");
        System.out.println("############################################################");       
        }
        System.out.println("************");
        System.out.println("final W1="+w1);
        System.out.println("final W2="+w2+"\n\n");
        drawPnael.getChildren().remove(line);
        line=new Line(0,-(threshold/w2),drawPnael.getWidth(),((-threshold-(w1*drawPnael.getWidth()))/w2));
        drawPnael.getChildren().add(line);
        System.out.println("startY="+(threshold/w2));
        System.out.println("endY="+((-threshold-(w1*drawPnael.getWidth()))/w2));

    }
    private void test(double x,double y){
        float bigx=((float)x*w1)+((float)y*w2)+(-1*threshold);
        if(bigx>=0)testLabel.setText("Tested point is: class A");
            else testLabel.setText("Tested point is: class B");
        
    }
    
}
