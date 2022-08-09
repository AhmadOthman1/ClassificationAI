# Description

It is a machine learning desktop application that contains a simple neural network consisting of a precipitone that solves the linear classification problem by dividing the data using linear line.
The algorithm classify the data that contains more than 2 classes (Multi-class) by using One-Vs-All Classification.

<img src="https://user-images.githubusercontent.com/103585755/183695881-49cba57c-86cb-4341-b336-c3df3f2b990e.png" width="600" height="300">

# GUI

### Main Page

  In this page, You can enter number of classes/categories `[2-4]` and click start to open an empty panel to enter the data manually, Or you can open a Excel sheet that contains the data in [specific format](https://github.com/AhmadOthman1/ClassificationAI/blob/master/README.md#excel-file-format).
  
  ![image](https://user-images.githubusercontent.com/103585755/183696738-b6219563-7664-499f-87e5-a72acf643cd4.png)


### Settings Page

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
  
  * Clear: Clear the drowing pane from points/lines.
  * Undo: Delete last point added to the drowing pane.
  * Save: Saves the pints in Excel sheet with [specific format](https://github.com/AhmadOthman1/ClassificationAI/blob/master/README.md#excel-file-format).
  * Back: Back to the main page.
  * Learn: Start learning process.
  * performance: Show system performance.

![image](https://user-images.githubusercontent.com/103585755/183707911-f962e7ba-ecef-4d0b-8cbc-fe063459de2c.png)

After click learn button, you can add test points and see the class based on the line equation.

![image](https://user-images.githubusercontent.com/103585755/183708694-899f0739-49bc-40a9-905f-fa6c69aba388.png)

  * The colored points: the point with a specific class.
  * Red stroke: testing/validation points that got splitted based on the inpout `testing data percentage`.
  * colored points with inverted stroke color: test points add manualy by the user after learning.

### performance Page
  
  The performance button shows the performance page that contains: The confusion matrix, Accuracy, Misclassification rate, MSE for all Lines
  
![image](https://user-images.githubusercontent.com/103585755/183710310-cf10b773-973d-4306-ab0d-4ab8c060fcdb.png)
![image](https://user-images.githubusercontent.com/103585755/183710683-c2eaa1fa-2482-4475-a074-8ff044b97687.png)



# Excel File Format
  
  The save button saves the points in an Excel file that contains 4 columns (x, y, class Name, class color in HEX), The file name indicates the date and time it was saved (the number of classes).
  
  ![image](https://user-images.githubusercontent.com/103585755/183712191-2484f913-d090-4508-9213-5521434ae123.png)
  
  ![image](https://user-images.githubusercontent.com/103585755/183712786-316c8ff5-27cf-4f21-bf7c-fc860f0bad5d.png)

  ***if you want to create/fill the Excel sheet manually You must stick to the order of the columns, the names of the classes `[0-3]`, number of classes `[2-4]`***
  






