/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import java.util.Random;

/**
 *
 * @author HI-TECH
 */
public class Preceptron {
    //we can use the Preceptron for more than 2 inputs (x,y) by increasing the array size
    float []w = new float[3];//w0  ,  w1   ,threshold 
    float learningRate;
    float mse;//number of error points
    Preceptron(float learningR){
        for(int i=0;i<this.w.length;i++){
            w[i] = -0.5f + new Random().nextFloat() * (0.5f - (-0.5f)); // random num from -0.5 to 0.5 for waights
        }
        learningRate=learningR;
        mse=0;
    }
    public int findY(float []in){//Y actual
        float sum=0;
        for(int i=0;i<in.length;i++){//segma
            sum+=in[i]*w[i];//x0w0 + x1w1 + w2 --> 
        }
        // sign activation function
        if(sum >=0)//sign function
            return 1;
        else
            return -1;
    }
    public void train(float []in, int Yexpected){
        int Yactual=findY(in);
        int error= Yexpected- Yactual;
        //mse+=Math.abs(error)/2;
        for(int i=0;i<this.w.length;i++){//waight correction
            w[i]+=learningRate*in[i]*error;
        }
        
    }
    public float findLine(float x){// test node in a line
        return -(w[2]/w[1]) - (w[0]/w[1])* x;
    }
    public int test(float []in, int Yexpected){//find error in tested data
        int Yactual=findY(in);
        int error= Yexpected- Yactual;
        mse+=Math.abs(error)/2;
        return error;
    }
    
}
