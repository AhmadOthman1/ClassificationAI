/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import static org.apache.poi.hssf.usermodel.HeaderFooter.file;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author HI-TECH
 */
public class FXMLClassificationMainController implements Initializable {
    ArrayList <Point> tPoints=new ArrayList(); 
    ArrayList <Point> points=new ArrayList();
    boolean isLearn=false;
    boolean isTest=false;
    Line line=new Line();
    Line line2=new Line();
    float mse=0;
    
    int cNum;
    ArrayList <String> classesColors =new ArrayList(); 
    float learningR; 
    Preceptron prec;
    float []bestW;
    float minMSE=2;
    String cnumb="";
    float [][]finalLineY;
    int numOfTestPoints;
    
    @FXML
    private AnchorPane mainPagePane;
    @FXML
    private AnchorPane settingsPane;
    @FXML
    private ComboBox<Integer> classComboBox;
    @FXML
    private TextField learningRate;
    @FXML
    private TextField epicNum;
    @FXML
    private Label excelFilePath;
    @FXML
    private Button clearButton;
    @FXML
    private AnchorPane drawPnael;
    @FXML
    private Button startButton;
    @FXML
    private Button learnButton;
    @FXML
    private Label testLabel;
    @FXML
    private Button UndoButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField CategorieNum;
    @FXML
    private Label CategorieNumError;
    @FXML
    private Label mseLabel;
    @FXML
    private CheckBox mseCheckBox;
    private TextField minMSETF;
    @FXML
    private Button saveButton;
    @FXML
    private Button openExcleButton;
    @FXML
    private AnchorPane mainPane;
      
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        epicNum.setText("10");
        learningRate.setText("0.2");

      //  minMSETF.setText("0.02");
        // TODO        
        
        bestW=new float[3];
    }    

    @FXML
    private void actionButton(ActionEvent event) {
        //if CategorieNum is a number
        if(!CategorieNum.getText().trim().equals("")     &&      numaric(CategorieNum.getText().trim())     &&      Integer.parseInt(CategorieNum.getText().trim())>1   &&      Integer.parseInt(CategorieNum.getText().trim())<5){
            cNum=Integer.parseInt(CategorieNum.getText().trim());//num of classes
            finalLineY=new float[cNum][3];
            for(int i=0;i<cNum;i++)
                classComboBox.getItems().add(i);//add classes to the combo box
            classComboBox.getSelectionModel().select(0);//select firs element as defult
            //clear old data
            drawPnael.getChildren().clear();
            points.clear();
            tPoints.clear();
            isLearn=false;
            isTest=false;
            mse=0;
            minMSE=2;
            classesColors.clear();
            numOfTestPoints=0;
            
            if(event.getSource()==startButton){
                for(int i=0;i<cNum;i++){//add colors to point classes
                    Random random = new Random();
                    // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
                    int nextInt = random.nextInt(0xffffff + 1);
                    // format it as hexadecimal string (with hashtag and leading zeros)
                    String colorCode = String.format("#%06x", nextInt);
                    classesColors.add(colorCode);//specify color to each class
                }
                mainPagePane.setVisible(false);
                settingsPane.setVisible(true);
                drawPnael.setVisible(true);
                CategorieNumError.setVisible(false);

            }
            else if(event.getSource()==openExcleButton){
                try{
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilterXLSX = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.XLSX");//Set extension filter
                    fileChooser.getExtensionFilters().addAll(extFilterXLSX);
                    File file = fileChooser.showOpenDialog(null);
                    if(file!=null){
                        FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
                        //creating Workbook instance that refers to .xlsx file  
                        XSSFSheet sheet = new XSSFWorkbook(fis).getSheetAt(0);     //creating a Sheet object to retrieve object  
                        Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
                        while (itr.hasNext()){
                            Row row = itr.next();  
                            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  
                            Arc arc=new Arc(0,0, 5, 5, 0, 360);
                            while (cellIterator.hasNext()){  
                                Cell cell = cellIterator.next(); 
                                int nclass;
                                float xp,yp;
                                xp= (float)cell.getNumericCellValue();
                                cell = cellIterator.next(); 
                                yp= (float)cell.getNumericCellValue();
                                cell = cellIterator.next(); 
                                nclass=(int)cell.getNumericCellValue();
                                cell = cellIterator.next();
                                boolean colorNotExists=true;
                                //add colors without repetition
                                if(classesColors.isEmpty())
                                    classesColors.add(nclass,cell.getStringCellValue());
                                else
                                    for(int i=0;i<classesColors.size();i++){
                                        if(classesColors.get(i).equals(cell.getStringCellValue())){
                                            colorNotExists=false;
                                            break;
                                        }
                                    }
                                if(colorNotExists){
                                    classesColors.add(nclass,cell.getStringCellValue());
                                }
                                //add the arc to screen 
                                arc.setFill(Paint.valueOf(cell.getStringCellValue()));
                                arc.setCenterX(xp);
                                arc.setCenterY(yp);
                                //store the x,y and map them from screen dimensions to -1,1
                                xp=map((float)xp,0,(float)drawPnael.getWidth(),-1,1);
                                yp=map((float)yp,(float)drawPnael.getHeight(),0,-1,1);
                                points.add(new Point(xp,yp,nclass,arc));
                                drawPnael.getChildren().add(arc);
                            }
                        }
                    }
                    mainPagePane.setVisible(false);
                    settingsPane.setVisible(true);
                    drawPnael.setVisible(true);
                    CategorieNumError.setVisible(false);
                }catch(Exception e){
                        System.out.println("file Error");
                }
            }
        }
        else
            CategorieNumError.setVisible(true);
        System.out.println("w2:" +mainPane.getWidth() + "h2: "+mainPane.getHeight());
    }

    @FXML
    private void mouseClickd(MouseEvent event) {
        if(!isLearn){//if still not learnd add the points to learning points array
            Arc arc = new Arc(event.getX(),event.getY(), 5, 5, 0, 360);
            arc.setFill(Color.web(classesColors.get(classComboBox.getValue())));
            points.add(new Point(map((float)event.getX(),0,(float)drawPnael.getWidth(),-1,1),map((float)event.getY(),(float)drawPnael.getHeight(),0,-1,1),classComboBox.getValue(),arc));
            drawPnael.getChildren().add(arc);
        }
        else{//if its learned the new points for testing
            float xp,yp;//map the points from -1,1 to screen dimensions 
            xp=map((float)event.getX(),0,(float)drawPnael.getWidth(),-1,1);
            yp=map((float)event.getY(),(float)drawPnael.getHeight(),0,-1,1);
            float []x = {xp,yp,1};
            int tpClass= testAPoint(x);//test the point based on the stored equation for each line
            if(tpClass>=0 && tpClass<=3){//if it has a class
                Arc arc = new Arc(event.getX(),event.getY(), 5, 5, 0, 360);
                arc.setFill(Color.web(classesColors.get(tpClass)));
                arc.setStroke(Color.web(classesColors.get(tpClass)).invert());
                arc.setStrokeWidth(2.0);
                drawPnael.getChildren().add(arc);
                testLabel.setText("class: "+ tpClass);
                numOfTestPoints++;
            }
            else{
                numOfTestPoints++;
                Text t=new Text();
                t.setText("x");
                t.setX(event.getX());
                t.setY(event.getY());
                t.setStroke(Color.BROWN);
                drawPnael.getChildren().add(t);
                testLabel.setText("class: none");
            }
        }
    }
    private boolean numaric(String str) {//if string is num
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)>'9'||str.charAt(i)<'0'){
                return false;
            }
        }
        return true;
    }
    private float map(float value,float  minA,float  maxA, float minB,float  maxB) {//scale data (value , minOldDataRange , maxOldDataRange , minNewDataRange , maxNewDataRange)
        return (1 - ((value - minA) / (maxA - minA))) * minB + ((value - minA) / (maxA - minA)) * maxB;
    }

    private void splitData(float p) {
        int testingDataSize=  (int) (points.size()*p/100);
        for(int i=0;i<testingDataSize;i++){
              int tIndex= new Random().nextInt(points.size()-1);
              tPoints.add(points.get(tIndex));
              tPoints.get(i).arc.setStroke(Color.RED);
              points.remove(tIndex);
        }
        System.out.println(tPoints.size());
    }

    @FXML
    private void settingsButtonAction(ActionEvent event) {
        if(event.getSource()==clearButton){
            drawPnael.getChildren().clear();
            points.clear();
            tPoints.clear();
            isLearn=false;
            isTest=false;
            mse=0;
            minMSE=2;
        }
        else if(event.getSource()==backButton){
            drawPnael.getChildren().clear();
            points.clear();
            tPoints.clear();
            mse=0;
            minMSE=2;
            isLearn=false;
            isTest=false;
            classComboBox.getItems().clear();
            drawPnael.setVisible(false);
            settingsPane.setVisible(false);
            mainPagePane.setVisible(true);
            
        }
        else if(event.getSource()==learnButton){
             mse=0;
             minMSE=2;
             if(!isLearn)
                splitData(30);
            try{
                learningR=Float.parseFloat(learningRate.getText().trim());
                if(isLearn){//to remove the lines after relearn
                    for(int i=0;i<numOfTestPoints;i++){
                        drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
                    }
                    numOfTestPoints=0;
                    for(int classIndex=0; classIndex<cNum;classIndex++){
                        drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
                        if(cNum==2)
                            break;
                    }
                    
                }
                for(int classIndex=0; classIndex<cNum;classIndex++){//start learning class by class
                    mse=0;
                    minMSE=2;
                    prec = new Preceptron(learningR);//create preceptron
                    learn(classIndex,prec);
                    drowLine(classIndex,prec);
                    System.out.println(cnumb);
                    
                    if(cNum==2)// to not drow two lines when its binary
                        break;
                }
                isLearn=true;
                
            }catch(NumberFormatException e){
                //not float Error
            }
         }
         else if(event.getSource()==UndoButton){
             if(!points.isEmpty() && !isLearn){
             drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
             points.remove(points.size()-1);
             }
         }
         else if(event.getSource()==saveButton){
             
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                workbook.createSheet("data");
                XSSFSheet sheet = workbook.getSheetAt(0);
                int ipoints=0;
                for(int i=0;i<points.size();i++){
                    XSSFRow row = sheet.createRow((short)i); 
                    ipoints=i;
                    float x,y;
                    x=map(points.get(i).x,-1,1,0,(float)drawPnael.getWidth());
                    y=map(points.get(i).y,-1,1,(float)drawPnael.getHeight(),0);
                    row.createCell(0).setCellValue(x);  
                    row.createCell(1).setCellValue(y);  
                    row.createCell(2).setCellValue(points.get(i).classNum);  
                    row.createCell(3).setCellValue(points.get(i).arc.getFill().toString());  
                }
                for(int i=0;i<tPoints.size();i++){
                    XSSFRow row = sheet.createRow((short)ipoints+1+i); 
                    float x,y;
                    x=map(tPoints.get(i).x,-1,1,0,(float)drawPnael.getWidth());
                    y=map(tPoints.get(i).y,-1,1,(float)drawPnael.getHeight(),0);
                    row.createCell(0).setCellValue(x);  
                    row.createCell(1).setCellValue(y);  
                    row.createCell(2).setCellValue(tPoints.get(i).classNum);  
                    row.createCell(3).setCellValue(tPoints.get(i).arc.getFill().toString());  
                }
                LocalDateTime DTNow = LocalDateTime.now();
                String formattedDate = DTNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss"));
                FileOutputStream fileOut = new FileOutputStream("src\\classificationai\\savedExcel\\points "+formattedDate+ " ("+cNum+")"+".xlsx");  
                workbook.write(fileOut);  
                fileOut.close();  
                //closing the workbook  
                workbook.close();  
                System.out.println("Writing on XLSX file Finished ...");
            } catch (Exception ex) {
                Logger.getLogger(FXMLClassificationMainController.class.getName()).log(Level.SEVERE, null, ex);
            } 
         
         }
    }

    private void learn(int classIndex,Preceptron prec) {
        for(int epic=0; (epic< Integer.parseInt(epicNum.getText().trim()));epic++){//loop in epic
            for(int pointI = 0 ;pointI <points.size();pointI++){//loop in points
                float []inputs={points.get(pointI).x,points.get(pointI).y,points.get(pointI).bias};
                if(points.get(pointI).classNum==classIndex)
                    prec.train(inputs, 1);// if the point in the class send it with Yexpected=1
                else 
                    prec.train(inputs, -1);
            }
            if(mseCheckBox.isSelected()){//if MSE checkBox selected test the data
                test(classIndex,prec);
                prec.mse=0;
            }
        }
    }

    private void drowLine(int classIndex,Preceptron prec) {
        if(!mseCheckBox.isSelected()){
            float x1L2= map(0,0,(float)drawPnael.getWidth(),-1,1);
            float y1L2= map(prec.findLine(x1L2),-1,1,(float)drawPnael.getHeight(),0);
            float x2L2= map((float)drawPnael.getWidth(),0,(float)drawPnael.getWidth(),-1,1);
            float y2L2= map(prec.findLine(x2L2),-1,1,(float)drawPnael.getHeight(),0);
            line2=new Line(0,y1L2,drawPnael.getWidth(),y2L2);
            line2.setStroke(Color.web(classesColors.get(classIndex)));
            drawPnael.getChildren().add(line2);
            for(int i=0;i<3;i++){
               finalLineY[classIndex][i]= prec.w[i];
            }
        }
        else if(mseCheckBox.isSelected()){
            for(int i=0;i<3;i++){
               prec.w[i]=bestW[i]; 
               finalLineY[classIndex][i]= prec.w[i];
            }
            float x1= map(0,0,(float)drawPnael.getWidth(),-1,1);
            float y1= map(prec.findLine(x1),-1,1,(float)drawPnael.getHeight(),0);
            float x2= map((float)drawPnael.getWidth(),0,(float)drawPnael.getWidth(),-1,1);
            float y2= map(prec.findLine(x2),-1,1,(float)drawPnael.getHeight(),0);
            line=new Line(0,y1,drawPnael.getWidth(),y2);
            line.setStroke(Color.web(classesColors.get(classIndex)));
            drawPnael.getChildren().add(line);
        }
    }

    private void test(int classIndex,Preceptron prec) {
        
        for(int pointI = 0 ;pointI <tPoints.size();pointI++){//loop on the testing points
            float []inputs={tPoints.get(pointI).x,tPoints.get(pointI).y,tPoints.get(pointI).bias};
            if(tPoints.get(pointI).classNum==classIndex){
                prec.test(inputs, 1);
            }
            else {
                prec.test(inputs, -1);
            }
        }
        
        mse= 1/(float)tPoints.size() * (prec.mse);
        if(minMSE>mse){
            minMSE=mse;
            bestW[0]=prec.w[0];
            bestW[1]=prec.w[1];
            bestW[2]=prec.w[2];
        }
        prec.mse=0;
        
    }

    private int testAPoint(float []x) {
        for(int i=0 ; i<cNum ; i++){
            float sum=0;
            for(int w=0;w<3;w++){
                sum+=finalLineY[i][w]*x[w];
            }
            if(sum >=0)
                return i;
        }
        return -1;
    }
}

