/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import javafx.scene.shape.Arc;

/**
 *
 * @author HI-TECH
 */
public class Point {
    float x;
    float y;
    int classNum;
    float bias;
    Arc arc;
    Point(float x,float y,int classNum){
        this.x=x;
        this.y=y;
        this.classNum=classNum;
        bias=1;
    }
    Point(float x,float y,int classNum,Arc arc){
        this.x=x;
        this.y=y;
        this.classNum=classNum;
        bias=1;
        this.arc=arc;
    }
}
