/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    boolean isLearn=false;//is the model learned
    boolean stopLearningFlag;// stop learinig if we reached the desired MES
    int cNum;// classes number (2 - 4)
    int numOfTestPoints;//number of testing points added by user after training ends
    int epicNumber;// input number of epics
    int testingDataPNumber;//persantage of testing points from all points to split
    float []bestW;//best waight after testing in every epic
    float [][]finalLineY;// final equations after trainig and testing
    int [][]ConfusionMatrix;
    float mse=0;
    float minMSE=2;// min mse for all epics
    float learningR; //learning rate
    float maxErrorNumber;
    String errorType;//for catching Errors
    Preceptron prec;
    Line line=new Line();
    ArrayList <Point> tPoints=new ArrayList(); // testing points arraylist
    ArrayList <Point> points=new ArrayList();//training points arraylist
    ArrayList <String> classesColors =new ArrayList(); // array lest of point classes colors
    
    
    
    
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
    private CheckBox mseCheckBox;
    private TextField minMSETF;
    @FXML
    private Button saveButton;
    @FXML
    private Button openExcleButton;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField testingDataPNum;
    @FXML
    private TextField maxError;
    @FXML
    private Label MEErrorLabel;
    @FXML
    private Label LRErrorLabel;
    @FXML
    private Label NOEErrorLabel;
    @FXML
    private Label TDErrorLabel;
    @FXML
    private Label settingErrorLabel;
      
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        epicNum.setText("10");
        learningRate.setText("0.2");
        errorType="";
      //  minMSETF.setText("0.02");
        // TODO        
        
        bestW=new float[3];
    }    

    @FXML
    private void actionButton(ActionEvent event) {
        //if CategorieNum is a number
        if(!CategorieNum.getText().trim().equals("")     &&      numaric(CategorieNum.getText().trim())     &&      Integer.parseInt(CategorieNum.getText().trim())>1   &&      Integer.parseInt(CategorieNum.getText().trim())<5){
            cNum=Integer.parseInt(CategorieNum.getText().trim());//num of classes
            for(int i=0;i<cNum;i++)
                classComboBox.getItems().add(i);//add classes to the combo box
            classComboBox.getSelectionModel().select(0);//select firs element as defult
            //clear old data
            drawPnael.getChildren().clear();
            points.clear();
            tPoints.clear();
            isLearn=false;
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
            else{//its unknown class
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
    

    @FXML
    private void settingsButtonAction(ActionEvent event) {
        if(event.getSource()==clearButton){
            drawPnael.getChildren().clear();
            points.clear();
            tPoints.clear();
            isLearn=false;
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
            classComboBox.getItems().clear();
            drawPnael.setVisible(false);
            settingsPane.setVisible(false);
            mainPagePane.setVisible(true);
            
        }
         else if(event.getSource()==UndoButton){
             if(!points.isEmpty() && !isLearn){
             drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
             points.remove(points.size()-1);
             }
         }
         else if(event.getSource()==saveButton){//save to Excel File 
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
    @FXML
    private void learnButtonAction(ActionEvent event) {
        if(event.getSource()==learnButton){
            testLabel.setText("");
            errorType="";//Error String to catch spacific Errors
            stopLearningFlag=false;
            ConfusionMatrix=new int [cNum][cNum];
            finalLineY=new float[cNum][3];
            try{
                settingErrorLabel.setVisible(false);
                learningR=Float.parseFloat(learningRate.getText().trim());
                epicNumber=Integer.parseInt(epicNum.getText().trim());
                testingDataPNumber=Integer.parseInt(testingDataPNum.getText().trim());
                if (mseCheckBox.isSelected()) maxErrorNumber =Float.parseFloat(maxError.getText().trim()) ;
                if (mseCheckBox.isSelected() && (maxErrorNumber<0 || maxErrorNumber>1)) {
                    errorType="maxErrorNumber";
                    throw new NullPointerException();
                }
                 mse=0;
                 minMSE=2;

                if(!isLearn)//split data for one time 
                    if( testingDataPNumber>=0 && testingDataPNumber<=100){
                        TDErrorLabel.setVisible(false);
                        splitData(testingDataPNumber);
                    }
                    else{
                        errorType="testingDataPNumber";
                        throw new NullPointerException();
                    }
                if(isLearn){
                    for(int i=0;i<numOfTestPoints;i++){//to remove the test Points after relearn
                        drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
                    }
                    numOfTestPoints=0;
                    for(int classIndex=0; classIndex<cNum;classIndex++){//to remove the lines after relearn
                        drawPnael.getChildren().remove(drawPnael.getChildren().size()-1);
                        if(cNum==2)
                            break;
                    }

                }
                //check for correct inputs
                LRErrorLabel.setVisible(false);
                NOEErrorLabel.setVisible(false);
                if(learningR <=0 || learningR >1){
                    errorType="learningR";
                    throw new NullPointerException();
                }
                if(epicNumber<1){
                    errorType="epicNumber";
                    throw new NullPointerException();
                }
                for(int classIndex=0; classIndex<cNum;classIndex++){//start learning class by class
                    mse=0;
                    minMSE=2;
                    stopLearningFlag=false;
                    prec = new Preceptron(learningR);//create preceptron
                    learn(classIndex,prec);
                    drowLine(classIndex,prec);
                    if(cNum==2)// to not drow two lines when its binary
                        break;
                }
                isLearn=true;
                fillConfusionMatrix();
                System.out.println(" \t0\t1");
                for(int i=0 ; i<cNum;i++){
                    System.out.print("{{"+i+"}}");
                    for(int j=0 ; j<cNum;j++){
                        System.out.print("\t"+ConfusionMatrix[i][j]+"\t");
                    }
                    System.out.println("");
                }
                
            }catch(NumberFormatException e){
                settingErrorLabel.setVisible(true);
            }catch(NullPointerException e){
                if(errorType.equals("testingDataPNumber"))
                    TDErrorLabel.setVisible(true);
                else if(errorType.equals("learningR"))
                    LRErrorLabel.setVisible(true);
                else if(errorType.equals("epicNumber"))
                    NOEErrorLabel.setVisible(true);
                else if(errorType.equals("maxErrorNumber"))
                    MEErrorLabel.setVisible(true);
            }
            
         }
    }
    private void learn(int classIndex,Preceptron prec) {
        try{
            for(int epic=0; (epic< epicNumber);epic++){//loop in epic
                for(int pointI = 0 ;pointI <points.size();pointI++){//loop in points
                    float []inputs={points.get(pointI).x,points.get(pointI).y,points.get(pointI).bias};
                    if(points.get(pointI).classNum==classIndex)
                        prec.train(inputs, 1);// if the point in the class send it with Yexpected=1
                    else 
                        prec.train(inputs, -1);
                }

                    test(classIndex,prec);//after every Epic test the splited data
                    prec.mse=0;
                    if(stopLearningFlag)
                        break;
            }
        }catch(Exception e){
            
        }
    }

    private void drowLine(int classIndex,Preceptron prec) {
        if(!mseCheckBox.isSelected()){//drow line based on final epic waights
            float x1L2= map(0,0,(float)drawPnael.getWidth(),-1,1);
            float y1L2= map(prec.findLine(x1L2),-1,1,(float)drawPnael.getHeight(),0);
            float x2L2= map((float)drawPnael.getWidth(),0,(float)drawPnael.getWidth(),-1,1);
            float y2L2= map(prec.findLine(x2L2),-1,1,(float)drawPnael.getHeight(),0);
            line=new Line(0,y1L2,drawPnael.getWidth(),y2L2);
            line.setStroke(Color.web(classesColors.get(classIndex)));
            drawPnael.getChildren().add(line);
            for(int i=0;i<3;i++){
               finalLineY[classIndex][i]= prec.w[i];
            }
        }
        else if(mseCheckBox.isSelected()){//drow line based on best Error epic waights
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
        if(mseCheckBox.isSelected()){//if MSE checkBox selected test the data
            mse= 1/(float)tPoints.size() * (prec.mse);
            if(mse<=maxErrorNumber){
                bestW[0]=prec.w[0];
                bestW[1]=prec.w[1];
                bestW[2]=prec.w[2];
                stopLearningFlag=true;
            }
            else if(minMSE>mse){
                System.out.println(mse);
                minMSE=mse;
                bestW[0]=prec.w[0];
                bestW[1]=prec.w[1];
                bestW[2]=prec.w[2];
            }
        }
        prec.mse=0;
        
    }

    private int testAPoint(float []x) {//test a point in which class based on final equations after learning and testing
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
    private void fillConfusionMatrix() {
        for(int pClass=0; pClass < cNum;pClass++){
            for(int conMatrix=0 ; conMatrix<tPoints.size();conMatrix++){
                if(tPoints.get(conMatrix).classNum==pClass){
                    float []point= {tPoints.get(conMatrix).x,tPoints.get(conMatrix).y,1};
                    ConfusionMatrix[pClass][testAPoint(point)]++;
                }
            }
        }
        ShowConfusionMatrix();
    }
    @FXML
    private void checkBoxAction(ActionEvent event) {
        if(event.getSource()==mseCheckBox){//   Disable/Enable maxError TextField
                maxError.setDisable(!mseCheckBox.isSelected());
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

    private void splitData(float p) {//split data to training and testing
        int testingDataSize=  (int) (points.size()*p/100);
        for(int i=0;i<testingDataSize;i++){
              int tIndex= new Random().nextInt(points.size()-1);
              tPoints.add(points.get(tIndex));
              tPoints.get(i).arc.setStroke(Color.RED);
              points.remove(tIndex);
        }
        System.out.println(tPoints.size());
    }

    private void ShowConfusionMatrix() {
         Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("path/to/other/view.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Confusion Matrix");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
}

