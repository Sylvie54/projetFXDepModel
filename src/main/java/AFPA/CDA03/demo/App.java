package AFPA.CDA03.demo;

import controller.PersonEditDialogController;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Person;
import controller.PersonOverviewController;
import javafx.stage.Modality;
import DAO.*;
import javafx.scene.control.Alert;
import utilitaires.Alertes;

/**
 * classe App
 *
 */
public class App extends Application
{
    // première étape : Main container
    private static Stage primaryStage;
    // disposition de base
    private BorderPane rootLayout;
    
    // liste observable d'objets Person
    private static ObservableList<Person> personData = FXCollections.observableArrayList();

    // méthode start lancé automatiquement (classe Application)
    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("AddressApp");
            /*
                sortie de l'application par la croix du borderPane,
                fermeture de la connexion
            */ 
            this.primaryStage.setOnCloseRequest(event ->
            {
                Connexion.closeConnection();
                System.exit(0);
            });  
            // appel de la méthode d'initialisation de la base    
            initRootLayout();
            // appel de la méthode initialisant la  première scène 
            showPersonOverview();
        }
        catch (Exception e) {
            Alertes.alerte(Alert.AlertType.WARNING,primaryStage,
                    "Attention", "Un problème est survenu", "Veuillez réessayer ultérieurement");
            e.printStackTrace();
            Connexion.closeConnection();
            System.exit(0);
        }
    }
    public static ObservableList<Person> getPersonData() {
        return personData;
    }
    
    public static void ajouterPersonne(Person person) {
        personData.add(person);
    }
    /**
     * Initializes the root layout.
     * @throws java.lang.Exception
     */
    public void initRootLayout() throws Exception {
       
        // Load root layout from fxml file.

        FXMLLoader loader = new FXMLLoader();
        // spécifique à Maven
        loader.setLocation(getClass().getClassLoader().getResource("RootLayout.fxml"));
        rootLayout = (BorderPane) loader.load();

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        /*
        connexion à base de données et chargement des données de la base
        */
        Connexion.AccesBase();
        BaseSQLServer.selectAll();
            
    }

    /**
     * Shows the person overview inside the root layout.
     * @throws java.lang.Exception
     */
    public void showPersonOverview() throws Exception {
        
        // Load person overview.
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getClassLoader().getResource("PersonOverview.fxml"));
        AnchorPane personOverview = (AnchorPane) loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(personOverview);

         // Give the controller access to the main app.
        PersonOverviewController controller = loader.getController();
        controller.setMainApp(this);
    }
        
        
        
    
    
    /**
     * Returns the main stage.
     * @return
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
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
            dialogStage.initOwner(primaryStage);
            // sortie del'application par la croix du borderPane
            dialogStage.setOnCloseRequest(event ->
            {
                Connexion.closeConnection();
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
    
    public static void main( String[] args )
    {
         launch(args);
    }
}
