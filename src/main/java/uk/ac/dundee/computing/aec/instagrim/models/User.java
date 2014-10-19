/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;

/**
 *
 * @author Administrator
 */
public class User {

    Cluster cluster;

    public User() {

    }

    public boolean RegisterUser(String username, String Password) {
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String EncodedPassword = null;
        try {
            EncodedPassword = sha1handler.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password) Values(?,?)");

        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username, EncodedPassword));
        //We are assuming this always works.  Also a transaction would be good here !
        session.close();
        return true;
    }

    public boolean IsValidUser(String username, String Password) {
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String EncodedPassword = null;
        try {
            EncodedPassword = sha1handler.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {

                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0) {
                    //Here the rest of the account info can be set.
                    session.close();
                    return true;
                }
            }
        }
        session.close();
        return false;
    }

    public LoggedIn getAccountInfo(LoggedIn lg) {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select * from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement selectUser = new BoundStatement(ps);
            rs = session.execute(selectUser.bind(lg.getUsername()));
            if (!rs.isExhausted())//If there is a result
            {
                for (Row user : rs) {
                    lg.setFirstName(user.getString("first_name"));
                    lg.setLastName(user.getString("last_name"));
                    //lg.setEmail(user.getString("email")); This needs to be set to a linked list?
                    lg.setAddress("", "", "");//find out how to do this
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to set accountInfo");
        }
        return lg;
    }

    public boolean setAccountInfo(LoggedIn lg) {//FIX THIS!
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement firstName = session.prepare("update userprofiles set first_name =? where login =?");
            BoundStatement addAccountInfo = new BoundStatement(firstName);
            session.execute(addAccountInfo.bind(lg.getFirstName(), lg.getUsername()));//Add first Name
            PreparedStatement lastName = session.prepare("update userprofiles set last_name =? where login =?");
            addAccountInfo = new BoundStatement(lastName);
            session.execute(addAccountInfo.bind(lg.getLastName(), lg.getUsername()));
            PreparedStatement address = session.prepare("update userprofiles set addresses = { 'currentAddress' : {street : ? , city : ? , post_code : ? } } where login =?");
            addAccountInfo = new BoundStatement(address);
            session.execute(addAccountInfo.bind(lg.getStreet(), lg.getCity(), lg.getPostCode(), lg.getUsername()));
            session.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public java.util.LinkedList<String> getFriendList(String user) {
        java.util.LinkedList<String> friendSet = new java.util.LinkedList<String>();
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select friends from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement friends = new BoundStatement(ps);
            rs = session.execute(friends.bind());
            if (!rs.isExhausted())//If there is a result
            {
                for (Row row : rs) {
                    friendSet = (java.util.LinkedList<String>) row.getList("friends", String.class);
                }
            }
            session.close();
        } catch (Exception e) {

        }
        return friendSet;
    }

    public boolean addFriend(String user, String friend) {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("update userprofiles set friends = [ ? ] + friends WHERE login =?");
            BoundStatement addFriend = new BoundStatement(ps);
            session.execute(addFriend.bind(friend, user));
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public java.util.LinkedList<String> getUsers() {
        java.util.LinkedList<String> userList = new java.util.LinkedList<>();
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select * from userprofiles");
            ResultSet rs = null;
            BoundStatement users = new BoundStatement(ps);
            rs = session.execute(users.bind());
            if (!rs.isExhausted())//If there is a result
            {
                for (Row user : rs) {
                    userList.add(user.getString("login"));
                }
            }

        } catch (Exception e) {

        }
        return userList;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

}
