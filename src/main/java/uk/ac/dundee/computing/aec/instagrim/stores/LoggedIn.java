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
    private Pic avatar = null;

    /**
     * Constructor
     */
    public void LoggedIn() {
    }

    /**
     * A method to set the username
     * @param name 
     */
    public void setUsername(String name) {
        this.Username = name;
    }

    /**
     * A method to get the username
     * @return 
     */
    public String getUsername() {
        return Username;
    }

    /**
     * A method to set the user as logged in
     */
    public void setLogedin() {
        logedin = true;
    }

    /**
     * A method to set the user as logged out
     */
    public void setLogedout() {
        logedin = false;
    }

    /**
     * A method to set the login state
     * @param logedin 
     */
    public void setLoginState(boolean logedin) {
        this.logedin = logedin;
    }

    /**
     * A method to get the login state
     * @return 
     */
    public boolean getlogedin() {
        return logedin;
    }

    /**
     * @return the avatar
     */
    public Pic getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(Pic avatar) {
        this.avatar = avatar;
    }
}
