
package democoursedbapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class Democoursedbapp extends Application {

    //Declare connection
    //NOTE - Requires the MySQL JDBC driver has been added to Libraries
    private Connection conn;
    
        //Declare textfields for form
    private TextField tfpid = new TextField();
    private TextField tffirstName = new TextField();
    private TextField tflastName = new TextField();
    private TextField tfTotalPoints = new TextField();
    private TextField tfjerseyNumber = new TextField();

        //Declare textArea for display
    private TextArea taShowRecords = new TextArea();
    
    private Label lbpid = new Label("PID:");
    private Label lbfirstName = new Label("First Name:");
    private Label lblastName = new Label("Last Name:");
    private Label lbTotalPoints = new Label("Total Points:");
    private Label lbjerseyNumber = new Label("Jersey Number:");
   

        //Declare buttons
    private Button btInsert = new Button("Insert");
    private Button btUpdate = new Button("Update");
    private Button btDelete = new Button("Delete");
    private Button btSearch = new Button("Search");
    private Button btCLS = new Button("CLS");
    
        //Declare JavaFX HBox to organize buttons
    private HBox hbButtons = new HBox();
    
        @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {

        //Call method defined below that creates connection to DB
        initializeDB();

        //Create UI
        //Personal placement choices of labels and textfields here.
        //Room for experimentation.
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));
        gridPane.add(lbpid, 0, 1, 1, 1);
        gridPane.add(tfpid, 1, 1, 1, 1);
        gridPane.add(lbfirstName, 0, 2, 1, 1);
        gridPane.add(tffirstName, 1, 2, 1, 1);
        gridPane.add(lblastName, 0, 3, 1, 1);
        gridPane.add(tflastName, 1, 3, 1, 1);
        gridPane.add(lbTotalPoints, 0, 4, 1, 1);
        gridPane.add(tfTotalPoints, 1, 4, 1, 1);
        gridPane.add(lbjerseyNumber, 0, 5, 1, 1);
        gridPane.add(tfjerseyNumber, 1, 5, 1, 1);
        hbButtons.getChildren().addAll(btSearch, btInsert, btUpdate, btDelete, btCLS);
        //btDelete.setBackground();
        hbButtons.setAlignment(Pos.CENTER);
        gridPane.add(hbButtons, 0, 6, 2, 1);
        gridPane.add(taShowRecords, 0, 7, 2, 4);
        gridPane.setAlignment(Pos.TOP_LEFT);

        // Create event handlers for buttons
        btInsert.setOnAction(e -> insertRecord());
        btCLS.setOnAction(e -> clearFields());
        btSearch.setOnAction(e -> searchRecord());
        btUpdate.setOnAction(e -> updateRecord());
        btDelete.setOnAction(e -> deleteRecord());

        // Create a scene and place it in the stage
        Scene scene = new Scene(gridPane, 400, 650);
        primaryStage.setTitle("Fantasy Football Annual Stat Tracker"); // Set title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        showRecords();  //showRecords called on init and any method that changes DB     
    }
        //This method is for the delete button. Run a prepared statement 
        //to delete active record.

            private void deleteRecord() {
        String queryString = "delete from Player where pid = ?;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(queryString);
            preparedStatement.setString(1, tfpid.getText());
            preparedStatement.executeUpdate();
            showAlert("Delete worked!");
            //After db change, update list at bottom
            showRecords();
        } catch (SQLException e2) {
            e2.printStackTrace();
            showAlert("Delete Failed");
            //clearFields(); //optional clear form on delete fail
        }
    }
                
    //This method is for the insert button. Run a prepared statement 
    //to insert values in form.
            
                private void insertRecord() {
        String queryString = "insert into Player (pid, firstName, lastName, TotalPoints, jerseyNumber) values (?,?,?,?,?);";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(queryString);
            preparedStatement.setString(1, tfpid.getText());
            preparedStatement.setString(2, tffirstName.getText());

            //Little extra work here, convert string for int DB field tfCourseNumber
            preparedStatement.setString(3, tflastName.getText());
            preparedStatement.setInt(4, Integer.parseInt(tfTotalPoints.getText()));
            //Little extra work here, convert string for int DB field tfNumberOfCredits
            preparedStatement.setInt(5, Integer.parseInt(tfjerseyNumber.getText()));
            preparedStatement.executeUpdate();
            showAlert("Insert worked!");
            //After db change, update list at bottom
            showRecords();
        } catch (SQLException e2) {
            e2.printStackTrace();
            showAlert("Insert Failed");
            //clearFields(); //optional clear form on delete fail
        }
    }
                private void updateRecord() {
        String queryString = "update Player set pid = ?, firstName = ?, lastName = ?, TotalPoints = ?, jerseyNumber = ? where pid = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(queryString);
            preparedStatement.setString(1, tfpid.getText());
            preparedStatement.setString(2, tffirstName.getText());

            //Little extra work here, convert string for int DB field tfCourseNumber
            preparedStatement.setString(3, tflastName.getText());
            preparedStatement.setString(4, tfTotalPoints.getText());
            //Little extra work here, convert string for int DB field tfNumberOfCredits
            preparedStatement.setInt(5, Integer.parseInt(tfjerseyNumber.getText()));
            preparedStatement.setInt(6, Integer.parseInt(tfpid.getText()));
            preparedStatement.executeUpdate();
            showAlert("Update worked!");
            //After db change, update list at bottom
            showRecords();
        } catch (SQLException e2) {
            e2.printStackTrace();
            showAlert("Update Failed");
            //clearFields(); //optional clear form on update fail
        }
    }
                private void searchRecord() {
        //type a courseID and search
        String queryString = "Select * from Player where pid = ?;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(queryString);
            preparedStatement.setString(1, tfpid.getText());
            ResultSet rset = preparedStatement.executeQuery();
            if (rset.next()) {
                tfpid.setText(rset.getString("pid"));
                tffirstName.setText(rset.getString("firstName"));
                tflastName.setText(rset.getString("lastName"));
                tfTotalPoints.setText(String.valueOf(rset.getInt("TotalPoints")));
                tfjerseyNumber.setText(String.valueOf(rset.getString("jerseyNumber")));
            } else {
                showAlert("No Record Found with ID of " + tfpid.getText());
                clearFields();
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
            showAlert("Search Failed");
        }
    }
              private void showRecords() {
        //Utility method - update list at bottom of form whenever any update 
        //is called, and on program load
        //TableView would be a good replacement for this logic, 
        //but was more complicated. Keeping this simple. Might
        //add an order by though.
        String queryString = "Select * from Player";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(queryString);
            ResultSet rset2 = preparedStatement.executeQuery();
            String output = new String("");
            //Counter here - loop through the retrieved records.
            //If we finish the loop without changing count,
            //DB table was empty
            int count = 0;
            taShowRecords.setText("");
            while (rset2.next()) {
                count += 1;
                output = output + rset2.getString("pid") + "    " + rset2.getString("FirstName")
                        + " " + rset2.getString("LastName") + "    " + "\n";
            }    
            taShowRecords.setText("Id   Name\n" + output);
            //If count remained 0, no records, so display an error. 
            if (count == 0) {
                taShowRecords.setText("No records found.");
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
            showAlert("Insert Failed");
            //clearFields();
        }
    }
                  private void clearFields() {
        //Utility method - want to clear the textFields from textfields
        tfpid.setText("");
        tffirstName.setText("");
        tflastName.setText("");
        tfTotalPoints.setText("");
        tfjerseyNumber.setText("");
    }

    private void showAlert(String message) {
        //Utility method - wanted easy way to trigger alert box
        //Warning - I set the message up in 3 different spots. 
        //Just pick one - this should be tweaked.
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(message);
        alert.setHeaderText(message);
        alert.setContentText(message);
        alert.showAndWait();

    }

    //Here's where we build the connection to the DB. 
    //Double check DB name, user name, and pass!
            private void initializeDB() {
        try {
            //creates a new instance of the class and hence causes the driver class to be
            //initialized. NOTE: application no longer needs to explicitly load JDBC
            //drivers using Class.forName()
            Class.forName("com.mysql.jdbc.Driver");
//            The getConnection(String url) method of Java DriverManager class attempts to 
//            establish a connection to the database by using the given database URL. 
//            The appropriate driver from the set of registered JDBC drivers is selected.(mysql)
            conn = DriverManager.getConnection("jdbc:mysql://localhost/projectdb", "root", "");
            System.out.println("Connected to database");
        } catch (Exception ex) {
            //pinpoints the exact line, prints throwable along with line number and class name
            ex.printStackTrace();
            showAlert("Connection failed - check DB is created");
        }
    }

    //This kicks off the JavaFX main form 
    public static void main(String[] args) {
        Application.launch(args);
    }
}





