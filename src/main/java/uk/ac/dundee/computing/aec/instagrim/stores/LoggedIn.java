/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Administrator
 */
public class LoggedIn {

   private boolean logedin = false;
   private String Username = null;
   private String firstName = "";
   private String lastName = "";
   private String email = null;
   private String street = null;
   private String city = null;
   private String postCode = null;

   public void LoggedIn() {

   }

   public void setUsername(String name) {
      this.Username = name;
   }

   public String getUsername() {
      return Username;
   }

   public void setLogedin() {
      logedin = true;
   }

   public void setLogedout() {
      logedin = false;
   }

   public void setLoginState(boolean logedin) {
      this.logedin = logedin;
   }

   public boolean getlogedin() {
      return logedin;
   }

   /**
    * @return the firstName
    */
   public String getFirstName() {
      return firstName;
   }

   /**
    * @param firstName the firstName to set
    */
   public void setFirstName(String firstName) {
      if (firstName != null) {
         this.firstName = firstName;
      }
   }

   /**
    * @return the lastName
    */
   public String getLastName() {
      return lastName;
   }

   /**
    * @param lastName the lastName to set
    */
   public void setLastName(String lastName) {
      if (lastName != null) {
         this.lastName = lastName;
      }
   }

   /**
    * @return the address
    */
   public String getAddress() {
      String address = "Street : " + street + "\nCity : " + city + "\nPost Code : " + postCode;
      return address;
   }

   /**
    *
    * @param street
    * @param city
    * @param postCode
    */
   public void setAddress(String street, String city, String postCode) {
      this.street = street;
      this.city = city;
      this.postCode = postCode;
   }

   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email the email to set
    */
   public void setEmail(String email) {
      this.email = email;
   }
}
