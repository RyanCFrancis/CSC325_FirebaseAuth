package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.model.Student;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainMenuController {

    @FXML
    ImageView profilePic;

    private boolean key;
    @FXML
    private TableView<Student> tableViewStudents;
    @FXML
    private TableColumn<Student,Integer> idCol;

    @FXML
    private TableColumn<Student,String> firstCol;

    @FXML
    private TableColumn<Student,String> lastCol;

    @FXML
    private TableColumn<Student,String> deptCol;

    @FXML
    private TableColumn<Student,String> emailCol;
    @FXML
    private TableColumn<Student,String> majorCol;

    @FXML
    private TextField fnameTF;

    @FXML
    private TextField lnameTF;

    @FXML
    private TextField majorTF;

    @FXML
    private TextField idTF;


    @FXML
    private TextField emailTF;

    @FXML
    private TextField deptTF;

    private ObservableList<Student> studentList;

    @FXML
    private Button editbtn;
    boolean editing;


    @FXML
    private void initialize(){
        editing = false;
        idCol.setCellValueFactory(new PropertyValueFactory<Student,Integer>("id"));
        firstCol.setCellValueFactory(new PropertyValueFactory<Student,String>("FirstName"));
        lastCol.setCellValueFactory(new PropertyValueFactory<Student,String>("LastName"));
        deptCol.setCellValueFactory(new PropertyValueFactory<Student,String>("Department"));
        majorCol.setCellValueFactory(new PropertyValueFactory<Student,String>("Major"));
        emailCol.setCellValueFactory(new PropertyValueFactory<Student,String>("Email"));

        studentList = tableViewStudents.getItems();

    }

    @FXML
    private void addStudent(){
        int id = (Integer) Integer.parseInt(idTF.getText());
        studentList.add(getTypedStudentData());
        clearTextFields();
    }

    @FXML
    private void deleteStudent(){
        Student toBeDeleted = tableViewStudents.getSelectionModel().getSelectedItem();
        studentList.remove((Student) toBeDeleted );
    }

    @FXML
    private void clearStudents(){
        studentList.clear();
    }

    @FXML
    private void editStudent(){
        if (!editing){
            Student stud = tableViewStudents.getSelectionModel().getSelectedItem();
            //fill in fields with content to change
            idTF.setText(stud.getId().toString());
            fnameTF.setText(stud.getFirstName());
            lnameTF.setText(stud.getLastName());
            deptTF.setText(stud.getDepartment());
            majorTF.setText(stud.getMajor());
            emailTF.setText(stud.getEmail());

            editbtn.setText("Update Student");
            editing = true;
        }
        else{
            Student oldStud = tableViewStudents.getSelectionModel().getSelectedItem();
            int i = tableViewStudents.getSelectionModel().getSelectedIndex();


//            Student newStud = new Student(id,fname,lname,dept,major,email);
////            studentList.remove(oldStud);
////            studentList.add(newStud);
//            studentList.add(i,newStud);
//            studentList.
            Student temp = getTypedStudentData();

            //change data in the student object to the newly entered/edited data
            oldStud.setFirstName(temp.getFirstName());
            oldStud.setId(temp.getId());
            oldStud.setLastName(temp.getLastName());
            oldStud.setMajor(temp.getMajor());
            oldStud.setDepartment(temp.getDepartment());
            oldStud.setEmail(temp.getEmail());
            tableViewStudents.refresh();

            editbtn.setText("Edit");
            editing = false;

            clearTextFields();
        }
    }

    public void addData() {
        Student temp = getTypedStudentData();

        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("First Name", temp.getFirstName());
        data.put("Last Name", temp.getLastName());
        data.put("Major", temp.getMajor());
        data.put("Department", temp.getDepartment());
        data.put("Email", temp.getEmail());
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        clearTextFields();
    }
    public boolean readFirebase()
    {
        studentList.clear();
        key = false;
        String id;
        String fname;
        String lname;
        String dept;
        String major;
        String email;
        Student temp;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  App.fstore.collection("References").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(!documents.isEmpty())
            {
                System.out.println("Outing....");
                for (QueryDocumentSnapshot document : documents)
                {
                    id = (String) document.getId();
                    fname = (String) document.getData().get("First Name");
                    lname = (String) document.getData().get("Last Name");
                    dept = (String) document.getData().get("Department");
                    major = (String) document.getData().get("Major");
                    email = (String) document.getData().get("Email");
                    studentList.add(new Student(id,fname,lname,dept,major,email));
//                    outputField.setText(outputField.getText()+ document.getData().get("Name")+ " , Major: "+
//                            document.getData().get("Major")+ " , Age: "+
//                            document.getData().get("Age")+ " \n ");
//                    System.out.println(document.getId() + " => " + document.getData().get("Name"));
//                    person  = new Person(String.valueOf(document.getData().get("Name")),
//                            document.getData().get("Major").toString(),
//                            Integer.parseInt(document.getData().get("Age").toString()));
//                    listOfUsers.add(person);
                }
            }
            else
            {
                System.out.println("No data");
            }
            key=true;

        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return key;
    }

    private void clearTextFields(){
        idTF.clear();
        fnameTF.clear();
        lnameTF.clear();
        deptTF.clear();
        majorTF.clear();
        emailTF.clear();
    }

    private Student getTypedStudentData(){
        String id = idTF.getText();
        String fname = fnameTF.getText();
        String lname = lnameTF.getText();
        String dept = deptTF.getText();
        String major = majorTF.getText();
        String email = emailTF.getText();

        return new Student(id,fname,lname,dept,major,email);

    }



}