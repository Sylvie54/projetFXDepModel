/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Acer
 */
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import AFPA.CDA03.demo.App;
import DAO.BaseSQLServer;
import DAO.Connexion;
import java.io.IOException;
import model.Person;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ListePerson;
import util.DateUtil;
import utilitaires.Alertes;

public class PersonOverviewController {
    @FXML
    private TableView<Person> personTable;
    @FXML
  //  private TableColumn<Person, String> firstNameColumn;
    private TableColumn<Person, String> firstNameColumn;
    @FXML
  //  private TableColumn<Person, String> lastNameColumn;
    private TableColumn<Person, String>lastNameColumn;

    public TableColumn getFirstNameColumn() {
        return firstNameColumn;
    }

    public void setFirstNameColumn(TableColumn firstNameColumn) {
        this.firstNameColumn = firstNameColumn;
    }
    
    
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;

    // Reference to the main application.
    private App app;
  

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PersonOverviewController() {
    }
    
   

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        /*
        
        lambda : Une fabrique à valeurs (value factory en anglais) est un callback placé sur une colonne de type <S, T>
        et qui permet de retrouver une valeur de type T à partir d'un objet source de type S contenu dans la table.

        Les objets de type TableColumn<S, T> disposent d'une propriété cellValueFactory
        qui permet de spécifier ce callback (à ne pas confondre avec la propriété cellFactory).
        La colonne firstNameColumn sera mis à jour directement avec la valeur de la property firstNameProperty
        */
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
       
        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * @param App
     */
    public void setMainApp(App App) {
       

        // Add observable list data to the table
        personTable.setItems(ListePerson.getPersonData());
    }
    /**
 * Fills all text fields to show details about the person.
 * If the specified person is null, all text fields are cleared.
 *
 * @param person the person or null
 */
    private void showPersonDetails(Person person) {
        if (person != null) {
            // Fill the labels with info from the person object.
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
    //        streetLabel.setText(person.getStreet());
    //        postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
    //        cityLabel.setText(person.getCity());
             birthdayLabel.setText(DateUtil.format(person.getBirthday()));
        } else {
            // Person is null, remove all the text.
            firstNameLabel.setText("");
            lastNameLabel.setText("");
    //        streetLabel.setText("");
    //        postalCodeLabel.setText("");
    //        cityLabel.setText("");
            birthdayLabel.setText("");
        }
    }
    @FXML
    private void handleQuitter() {
        try {
                Connexion.closeConnection();
            }
            catch (Exception ec) {
                System.out.println("pb cloture connexion : "+ ec.getMessage());
            }
        System.exit(0);
    }
    @FXML
    private void handleDeletePerson() throws Exception {
        Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.initOwner(App.getPrimaryStage());
            alert.setTitle("Suppression");
            alert.setHeaderText("Confirmation de suppression");
            alert.setContentText("Etes vous sûr de vouloir sup cette personne ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                personTable.getItems().remove(selectedIndex);
                BaseSQLServer.delete(selectedPerson);
            }
        }
        else {
           // Nothing selected.
            Alertes.alerte(Alert.AlertType.WARNING,App.getPrimaryStage(), "pas de sélection", "No Person Selected","Please select a person in the table." );
        }
    }
    /**
 * Called when the user clicks the new button. Opens a dialog to edit
 * details for a new person.
 */
@FXML
private void handleNewPerson() throws Exception  {
    
    Person tempPerson = new Person();
 // appel de la méthode ouvrant la fenêtre modale de création/édition de la personne
    boolean okClicked = showPersonEditDialog(tempPerson);
    if (okClicked) {
        int dernierId = BaseSQLServer.insert(tempPerson);
        tempPerson.setId(dernierId);
        ListePerson.getPersonData().add(tempPerson);
       
    }
    
}

/**
 * Called when the user clicks the edit button. Opens a dialog to edit
 * details for the selected person.
 */
@FXML
private void handleEditPerson() throws Exception {
    Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
    if (selectedPerson != null) {
        int ancId = selectedPerson.getId();
        boolean okClicked = showPersonEditDialog(selectedPerson);
        if (okClicked) {
            showPersonDetails(selectedPerson);
            BaseSQLServer.update(selectedPerson, ancId);
        }
    }   else {
        // Nothing selected.
        Alertes.alerte(Alert.AlertType.WARNING,App.getPrimaryStage(), "pas de sélection", "No Person Selected","Please select a person in the table." );
        }
   }
   /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     * @throws java.lang.Exception
     */
public static boolean showPersonEditDialog(Person person) throws Exception  {
        try {            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getClassLoader().getResource("PersonEditDialog.fxml"));
           
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(App.getPrimaryStage());
            // sortie del'application par la croix du borderPane
            dialogStage.setOnCloseRequest(event ->
            {
                try {
                        Connexion.closeConnection();
                }
                catch (Exception ec) {
                     System.out.println("pb clôture connexion : "+ ec.getMessage());
                }
                System.exit(0);
            });    
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);
             
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
