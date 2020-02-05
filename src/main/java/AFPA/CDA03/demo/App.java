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
                try {
                    Connexion.closeConnection();
                }
                catch (Exception ec) {
                    System.out.println("pb clôture connexion : "+ ec.getMessage());
                }
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
            try {
                Connexion.closeConnection();
            }
            catch (Exception ec) {
                System.out.println("pb clôture connexion : "+ ec.getMessage());
            }
            System.exit(0);
        }
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
     
    
    public static void main( String[] args )
    {
         launch(args);
    }
}
