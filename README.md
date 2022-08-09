# Description

It is a machine learning desktop application that contains a simple neural network consisting of a precipitone that solves the linear classification problem by dividing the data using linear line.
The algorithm classify the data that contains more than 2 classes (Multi-class) by using One-Vs-All Classification.

<img src="https://user-images.githubusercontent.com/103585755/183695881-49cba57c-86cb-4341-b336-c3df3f2b990e.png" width="600" height="300">

# GUI

* **Main Page**

  In this page, You can enter number of classes/categories `[2-4]` and click start to open an empty panel to enter the data manually, Or you can open a Excel sheet that contains the data in specific format.
  
  ![image](https://user-images.githubusercontent.com/103585755/183696738-b6219563-7664-499f-87e5-a72acf643cd4.png)


* **Settings Page**

  In this page you can manage the settings for the algorithm
  
  * Class Name:
  
    A combo box that you can choose the the class name for the point you want to add to rhe dowing pane.
  * Learning rate: 
  
    a float number between 0 and 1.
  * Number of Epic:
  
    Number of maximum Epics.
  * Testing data percentage:
  
    The percentage that split data between training data and testing/validation data `[0-100]`.
  * Use Classification Error:
  
    A CheckBox that Take into account some percentage of Error that get checked evrey Epic, if the `MSE error` reached the input Max Error `[0-1]`, the algorithm will stop learning and drow the line for the current class, if the algorithm cannot reach that input it will take the best Epic equasition that has the nearest error percentage to the input Max Error.
    ```
    MSE = number of wrong expected testing points / number of all testing points.
    ```
  Buttons:
  
  * Clear:
    
    Clear the drowing pane from points/lines.
  * Undo:
  
    Delete last point added to the drowing pane.
  * Save:
  
    Saves the pints in Excel sheet with specific format.
  * Back: 
    
    Back to the main page.
  * Learn:
    
    Start learning process.
  * performance:
    
    Show system performance.
  

