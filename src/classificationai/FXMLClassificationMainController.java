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
import static org.apache.poi.hssf.usermodel.HeaderFooter.file;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
    Text t=new Text();
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
      
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        drawPnael.getChildren().add(t);
        epicNum.setText("5");
        learningRate.setText("0.002");
      //  minMSETF.setText("0.02");
        // TODO        
        
        bestW=new float[3];
    }    

    @FXML
    private void actionButton(ActionEvent event) {
        if(event.getSource()==startButton){
            if(!CategorieNum.getText().trim().equals("")     &&      numaric(CategorieNum.getText().trim())     &&      Integer.parseInt(CategorieNum.getText().trim())>1   &&      Integer.parseInt(CategorieNum.getText().trim())<5){//if its a number
                cNum=Integer.parseInt(CategorieNum.getText().trim());//num of classes
                for(int i=0;i<cNum;i++)
                    classComboBox.getItems().add(i);//add classes to the combo box
                for(int i=0;i<cNum;i++){//add colors to point classes
                    Random random = new Random();
                    // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
                    int nextInt = random.nextInt(0xffffff + 1);
                    // format it as hexadecimal string (with hashtag and leading zeros)
                    String colorCode = String.format("#%06x", nextInt);
                    classesColors.add(colorCode);//specify color to each class
                }
                classComboBox.getSelectionModel().select(0);//select firs element as defult
                
                
                mainPagePane.setVisible(false);
                settingsPane.setVisible(true);
                drawPnael.setVisible(true);
                CategorieNumError.setVisible(false);
            }
            else
                CategorieNumError.setVisible(true);
        }
        else if(event.getSource()==openExcleButton){
                mainPagePane.setVisible(false);
                settingsPane.setVisible(true);
                drawPnael.setVisible(true);
                CategorieNumError.setVisible(false);
                drawPnael.getChildren().clear();
                points.clear();
                tPoints.clear();
                isLearn=false;
                isTest=false;
                mse=0;
                minMSE=2;
            try{
                File file = new File("C:\\Users\\hp\\Desktop\\ai project\\ClassificationAI\\src\\classificationai\\points.xlsx");   //creating a new file instance  
                FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
                //creating Workbook instance that refers to .xlsx file  
                XSSFWorkbook wb = new XSSFWorkbook(fis);   
                XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
                Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
                while (itr.hasNext())                 
                {  
                Row row = itr.next();  
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  
               Arc arc=new Arc(0,0, 5, 5, 0, 360);
                while (cellIterator.hasNext())   
                {  
                Cell cell = cellIterator.next(); 
                int nclass;
                float xp,yp,y2;
                       xp= (float)cell.getNumericCellValue();
                       cell = cellIterator.next(); 
                       yp= (float)cell.getNumericCellValue();
                       cell = cellIterator.next(); 
                       nclass=(int)cell.getNumericCellValue();
                       cell = cellIterator.next(); 
                       arc.setFill(Paint.valueOf(cell.getStringCellValue()));
                       y2= map(xp,-1,1,(float)drawPnael.getWidth(),0); 
                        
                       arc.setCenterX(y2);
                       y2= map(yp,-1,1,(float)drawPnael.getHeight(),0); 
                       arc.setCenterY(y2);
                       points.add(new Point(xp,yp,nclass,arc));
                       drawPnael.getChildren().add(arc);
                }
            }
            }
            catch(Exception e){
                
            }
        }
        
    }

    @FXML
    private void mouseClickd(MouseEvent event) {
        Arc arc = new Arc(event.getX(),event.getY(), 5, 5, 0, 360);
        arc.setFill(Color.web(classesColors.get(classComboBox.getValue())));
        points.add(new Point(map((float)event.getX(),0,(float)drawPnael.getWidth(),-1,1),map((float)event.getY(),(float)drawPnael.getHeight(),0,-1,1),classComboBox.getValue(),arc));
        drawPnael.getChildren().add(arc);
 
    }

    private boolean numaric(String str) {//if string is num
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)>'9'||str.charAt(i)<'0'){
                return false;
            }
        }
        return true;
    }
    private float map(float value,float  minA,float  maxA, float minB,float  maxB) {
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
             if(!isLearn){
                splitData(30);
             }
            try{
                learningR=Float.parseFloat(learningRate.getText().trim());
                if(isLearn){//to remove the lines after relearn
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
             if(isLearn){
             FileInputStream fis = null;
            try {
                File file = new File("C:\\Users\\hp\\Desktop\\ai project\\ClassificationAI\\src\\classificationai\\points.xlsx");   //creating a new file instance  
                
                fis = new FileInputStream(file); //obtaining bytes from the file
                //creating Workbook instance that refers to .xlsx file
                XSSFWorkbook wb = new XSSFWorkbook(fis);
                wb.removeSheetAt(0);
                wb.createSheet();
                XSSFSheet sheet = wb.getSheetAt(0);
                
                Map<String, Object[]> data = new HashMap<String, Object[]>();
                for(int i=0;i<points.size();i++){
                    data.put(""+i, new Object[] {points.get(i).x,points.get(i).y,points.get(i).classNum,points.get(i).arc.getFill().toString()});                    
                }
                for(int i=0;i<tPoints.size();i++){
                    data.put("2"+i, new Object[] {tPoints.get(i).x,tPoints.get(i).y,tPoints.get(i).classNum,tPoints.get(i).arc.getFill().toString()});
                    System.out.println(tPoints.get(i).arc.getFill().toString());
                }
                Set<String> newRows = data.keySet(); // get the last row number to append new data 
                int rownum = 0;
                for (String key : newRows) { // Creating a new Row in existing XLSX sheet
                    Row row1 = sheet.createRow(rownum++);
                    Object [] objArr = data.get(key);
                    int cellnum = 0;
                    for (Object obj : objArr) {
                        Cell cell = row1.createCell(cellnum++);
                        if (obj instanceof String) {
                            cell.setCellValue((String) obj);
                        } 
                        else if (obj instanceof Float) {
                            cell.setCellValue((Float) obj); }
                        else if (obj instanceof Integer) {
                            cell.setCellValue((Integer) obj); }
                    } 
                } // open an OutputStream to save written data into XLSX file 
                FileOutputStream os = new FileOutputStream(file);
                wb.write(os);
                System.out.println("Writing on XLSX file Finished ...");
            } catch (Exception ex) {
                Logger.getLogger(FXMLClassificationMainController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLClassificationMainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
         }
         }
    }

    private void learn(int classIndex,Preceptron prec) {
        
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("mse: "+prec.mse);
        System.out.println("--------------------------------------------------------------------------------------------------");
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
        System.out.println(prec.iteration);
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
            System.out.println("y0L2 = " +y1L2+"  y1L2 = " + y2L2);    
        }
        else if(mseCheckBox.isSelected()){
            mseLabel.setText("Mse= "+String.valueOf(minMSE));
            System.out.println(minMSE+"+++++++++++++++++++++++++++");
            prec.w[0]=bestW[0];
            prec.w[1]=bestW[1];
            prec.w[2]= bestW[2];
            System.out.println("Best w0= "+String.valueOf(prec.w[0]));
            System.out.println("Best w1= "+String.valueOf(prec.w[1]));
            System.out.println("Best w2= "+String.valueOf(prec.w[2]));
            float x1= map(0,0,(float)drawPnael.getWidth(),-1,1);
            System.out.println(x1);
            System.out.println(prec.findLine(x1));
            float y1= map(prec.findLine(x1),-1,1,(float)drawPnael.getHeight(),0);
            float x2= map((float)drawPnael.getWidth(),0,(float)drawPnael.getWidth(),-1,1);
            System.out.println(x2);
            System.out.println(prec.findLine(x2));
            float y2= map(prec.findLine(x2),-1,1,(float)drawPnael.getHeight(),0);
            line=new Line(0,y1,drawPnael.getWidth(),y2);
            line.setStroke(Color.web(classesColors.get(classIndex)));
            drawPnael.getChildren().add(line);
            System.out.println("y0 = " +y1+"  y1 = " + y2);
        }
    }

    private void test(int classIndex,Preceptron prec) {
        
        for(int pointI = 0 ;pointI <tPoints.size();pointI++){//loop on the testing points
            float []inputs={tPoints.get(pointI).x,tPoints.get(pointI).y,tPoints.get(pointI).bias};
            if(tPoints.get(pointI).classNum==classIndex){
                prec.test(inputs, 1);
                //System.out.println(pointI+"-result: "+prec.test(inputs, 1)+" class: "+tPoints.get(pointI).classNum );
            }
            else {
                prec.test(inputs, -1);
                //System.out.println(pointI+"-Not result: "+prec.test(inputs, -1)+" class: "+tPoints.get(pointI).classNum );
            }
        }
        
        mse= 1/(float)tPoints.size() * (prec.mse);
        if(minMSE>mse){
            System.out.println("prec MES: "+prec.mse);
            System.out.println("Best MES: "+mse);
            System.out.println("=======");
            minMSE=mse;
            bestW[0]=prec.w[0];
            bestW[1]=prec.w[1];
            bestW[2]=prec.w[2];
            System.out.println("Best w0= "+bestW[0]);
            System.out.println("Best w1= "+bestW[1]);
            System.out.println("Best w2= "+bestW[2]);
        }
        prec.mse=0;
        
    }
}

