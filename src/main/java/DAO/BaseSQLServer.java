/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import AFPA.CDA03.demo.App;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.ExceptionsModele;
import model.Person;

/**
 *
 * @author Sylvie
 */
public class BaseSQLServer {
   private static final Connection conn = Connexion.getConn();
   private static ResultSet resultat = null;
   
   /**
    * méthode de sélection de toutes les personnes
    * @throws Exception
    */
    public static void selectAll() throws Exception{
        try {
                Statement stm = conn.createStatement(); // crÃ©ation d'un objet requÃªte directe 
                resultat = stm.executeQuery("SELECT *  FROM client");
                Person person;
                while (resultat.next())
                {
                    person = new Person(resultat.getInt("id"), resultat.getString("nom"), resultat.getString("prenom"));
                    App.ajouterPersonne(person);
                }
    
        }
        catch ( SQLException | ExceptionsModele e )
        {
            throw new Exception (e.getMessage());
        }
        finally {
            resultat.close();
        }
        
    }
    /**
     * méthode d'insertion d'une personne
     * @param person Person
     * @return int valeur de l'identifiant crée en auto incrément 
     * @throws java.lang.Exception
     */
    public static int insert(Person person) throws Exception {
        int idGenere = 0;
        String query = "INSERT INTO client ("
                + " nom,"
                + " prenom) VALUES ("
                + "?, ?)";
        try{
        /*
            l'option RETURN_GENERATED_KEYS permet de réupéré dans le preparedStatement 
            la valeur de l'id qui vient d'être généré
        */    
            PreparedStatement pstatement =
                    conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS );
            
            //  Recuperation des parametres pour le PreparedStatement
            pstatement.setString(1, person.getFirstName());
            pstatement.setString(2, person.getLastName());
            //  Execution du PreparedStatement pour insertion
            pstatement.executeUpdate();
            // récupération de l'id généré
            resultat= pstatement.getGeneratedKeys();
            while (resultat.next()) {
                idGenere = resultat.getInt(1);
            }
        }catch(SQLException SQLex){
            System.out.println("message : " + SQLex.getMessage());
            SQLex.printStackTrace();
        }
        resultat.close();
        return idGenere;
        
    }
    /**
     * 
     * @param person Person
     * @param ancId String ancien nom de la personne
     * @throws java.lang.Exception
     */
    public static void update(Person person, int ancId) throws Exception {
        System.out.println(person.getFirstName() + " ancid " + ancId);
        String query = "UPDATE client "
                + "SET nom = ? "
                + ", prenom = ? "
                + "WHERE id = ? ";
        try(PreparedStatement pstatement = conn.prepareStatement(query)){
            //  Recuperation des parametres pour le PreparedStatement
            pstatement.setString(1, person.getFirstName());
            pstatement.setString(2, person.getLastName());
            pstatement.setInt(3, ancId);
            //  Execution du PreparedStatement pour modif
            pstatement.executeUpdate();
        }catch(SQLException SQLex){
            SQLex.printStackTrace();
        }
        
    }
    /**
     * méthode de supression d' une personne
     * @param person Person
     * @throws java.lang.Exception
     */
    public static void delete(Person person) throws Exception {
        String query = "DELETE FROM client WHERE nom = ? ";
        try(PreparedStatement pstatement = conn.prepareStatement(query)){
           
            //  Recuperation des parametres pour le PreparedStatement
            pstatement.setString(1, person.getFirstName());
            //  Execution du PreparedStatement pour modif
            pstatement.executeUpdate();
        }catch(SQLException SQLex){
            SQLex.printStackTrace();
        }
    }
 
}
