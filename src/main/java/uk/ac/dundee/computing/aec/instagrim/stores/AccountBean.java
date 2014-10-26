/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Dave Ogle
 */
public class AccountBean {

    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String street = "";
    private String city = "";
    private String postCode = "";

    public AccountBean() {

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
        String address = "Street : " + getStreet() + "</br>City : " + getCity() + "</br>Post Code : " + getPostCode();
        return address;
    }

    /**
     * Set the address
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
        if (email != null) {
            this.email = email;
        }
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the postCode
     */
    public String getPostCode() {
        return postCode;
    }
}
