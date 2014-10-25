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
