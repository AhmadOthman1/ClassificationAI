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
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
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
    private Label excelFileName;
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
    Text t=new Text();;
    
      //Setting the properties of the rectangle 
    @FXML
    private Button startButton;
    @FXML
    private Button learnButton;
    @FXML
    private Label testLabel;
      
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
           // mainPagePane.setVisible(false);
           
        }
//         if(event.getSource()==browseButton){
//            FileChooser fileChooser = new FileChooser();
//
//            //Set extension filter
//            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
//            fileChooser.getExtensionFilters().addAll(extFilterJPG);
//            
//            //ahmad 
//            //Show open file dialog
//            File file = fileChooser.showOpenDialog(null);
//            if(file!=null){
////                try {
////                    //BufferedImage bufferedImage = ImageIO.read(file);
////                   // WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
////                    
////                        excelFilePath.setText(file.getPath());
////                    
////                } catch (IOException ex) {
////                    
////                } 
//        }
//        }
         if(event.getSource()==learnButton){
             learn();
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
                System.out.println(event.getX()+","+event.getY()+",A");
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
                System.out.println(event.getX()+","+event.getY()+",B");
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
               System.out.println(event.getX()+","+event.getY()+",C");
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
               System.out.println(event.getX()+","+event.getY()+",C");
            }
            
            if(classComboBox.getValue().equals("Class D")){
                Arc arc = new Arc(event.getX(), event.getY(), 10, 10, 90, 270);
                arc.setType(ArcType.ROUND);
                arc.setFill(Color.TRANSPARENT);
                arc.setStroke(Color.YELLOW);
                classpoints.add(event.getX()+","+event.getY()+",D");
                System.out.println(event.getX()+","+event.getY()+",D");
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

    @FXML
    private void mouseEntered(MouseEvent event) {
      //  float a= (float)event.getX();
      //  System.out.println(a);
      //  System.out.println(event.getX());
        //if(event.) g.drawLine(xa, m*xa + c, xb, m*xb + c)
//        float b=-0.2;
//        float w1=-0.1;
//        float w2=0.1;
        
//      float  x_intercept = (0, -b / w2);
//       float y_intercept = (-b / w1, 0);
      //  Line line=new Line(0f,(b/w2), (b / w1),0f);
      //Line line=new Line(0,0+drawPnael.getHeight(),drawPnael.getWidth(),0);
       // drawPnael.getChildren().add(line);
//        System.out.println(drawPnael.getWidth());
//        System.out.println(drawPnael.getHeight());
    }
    
    private void learn(){
        isLearn=true;
//        classpoints.add("0,0,0");
//        classpoints.add("0,1,0");
//        classpoints.add("1,0,0");
//        classpoints.add("1,1,1");
        Random rand = new Random();
        Random rand2 = new Random();
        Random rand3 = new Random();
        w1=rand.nextFloat(-0.5f, 0.5f);
        System.out.println("w1="+w1);
        w2=rand2.nextFloat(-0.5f, 0.5f);
        System.out.println("w2="+w2);
        threshold=rand3.nextFloat(-0.5f, 0.5f);
        System.out.println("threshold="+threshold);
//        w1=0.3f;
//        w2=-0.1f;
//        threshold=0.2f;
        
        float x1=0;
        float x2;
        float delta_w1;
        float delta_w2;
        float bigX;
        int yd;
        int ya;
        int error;
        alfa=Float.parseFloat(learningRate.getText());
        for(int i=0;i<Integer.parseInt(iterationsNum.getText());i++){
            for(int j=0;j<classpoints.size();j++){
           String [] Spoint =classpoints.get(j).split(",");
           x1=Float.parseFloat(Spoint[0]);
           x2=Float.parseFloat(Spoint[1]);
           if(Spoint[2].equals("A"))yd=1; else yd=0;
          // yd=Integer.parseInt(Spoint[2]);
            System.out.println("x1="+x1);
            System.out.println("x2="+x2);
            System.out.println("yd="+yd);
           bigX=(x1*w1)+(x2*w2)+(threshold*-1);
           System.out.println("bigX="+bigX);
           if(bigX>=0)ya=1;
            else ya=0;
           
           error=yd-ya;
           System.out.println("ya="+ya);
           System.out.println("error="+error);
           
           delta_w1=alfa*(x1*error);
           w1=w1+delta_w1;
           
           delta_w2=alfa*(x2*error);
           w2=w2+delta_w2;
           System.out.println("delta W1="+delta_w1);
           System.out.println("delta W2="+delta_w2);
           System.out.println(" W1="+w1);
           System.out.println(" W2="+w2);
           
            }
            System.out.println("############################################################");
        }
        System.out.println("************");
        System.out.println("final W1="+w1);
        System.out.println("final W2="+w2);
        System.out.println("threshold/w2="+threshold/w2);
        System.out.println("point2="+drawPnael.getWidth()+","+((threshold-(w1*drawPnael.getWidth()))/w2));
        if((-w1/w2)>0){
            Text t=new Text("X");
            t.setX(0);
            t.setY((threshold/w2));
           // t.setY(((threshold-(w1*-drawPnael.getWidth()))/w2)-drawPnael.getHeight());
            t.setStroke(Color.RED);
            drawPnael.getChildren().add(t);
            Text t2=new Text("X");
            t2.setX(drawPnael.getWidth());
            t2.setY(((-threshold-(w1*drawPnael.getWidth()))/w2));
            t2.setStroke(Color.BLUE);
            drawPnael.getChildren().add(t2);
            Line line=new Line(0,-((threshold/w2)*drawPnael.getHeight())/(1/0.5f),drawPnael.getWidth(),((-threshold-(w1*drawPnael.getWidth()))/w2));
            drawPnael.getChildren().add(line);
        }
        else{
            Line line=new Line(0,(threshold/w2),drawPnael.getWidth(),((threshold-(w1*drawPnael.getWidth()))/w2));
            //drawPnael.getChildren().add(line);
        }
        
        
    }
    private void test(double x,double y){
        System.out.println("(float)x="+(float)x);
        System.out.println("(float)y="+(float)y);
        float bigx=((float)x*w1)+((float)y*w2)+(-1*threshold);
        System.out.println("bigx="+bigx);
        if(bigx>=0)testLabel.setText("class A");
            else testLabel.setText("class B");
        System.out.println("------------------------------------------------------------------------------------------");
    }
    
}
