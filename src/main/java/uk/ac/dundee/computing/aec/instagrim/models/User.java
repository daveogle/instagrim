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
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
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
            EncodedPassword = AeSimpleSHA1.SHA1(Password);
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
        String EncodedPassword = null;
        try {
            EncodedPassword = AeSimpleSHA1.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {

                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0) {
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
                    user.getMap("addresses", String.class, UDTValue.class).values().stream().forEach((addr) -> {
                        lg.setAddress(addr.getString("street"), addr.getString("city"), addr.getString("post_code"));
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to set accountInfo" + e);
        }
        return lg;
    }

    public boolean setAccountInfo(LoggedIn lg) {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement firstName = session.prepare("update userprofiles set first_name =? where login =?");
            BoundStatement addAccountInfo = new BoundStatement(firstName);
            session.execute(addAccountInfo.bind(lg.getFirstName(), lg.getUsername()));//Add first Name
            PreparedStatement lastName = session.prepare("update userprofiles set last_name =? where login =?");
            addAccountInfo = new BoundStatement(lastName);
            session.execute(addAccountInfo.bind(lg.getLastName(), lg.getUsername()));
            //Add address
            PreparedStatement address = session.prepare("update userprofiles set addresses =? where login=?");
            UserType addressUDT = session.getCluster().getMetadata().getKeyspace("instagrim").getUserType("address");
            UDTValue addresses = addressUDT.newValue().setString("street", lg.getStreet()).setString("city", lg.getCity()).setString("post_code", lg.getPostCode());
            java.util.Map<String, UDTValue> addressMap = new java.util.HashMap<>();
            addressMap.put("Home", addresses);
            addAccountInfo = new BoundStatement(address);
            session.execute(addAccountInfo.bind(addressMap, lg.getUsername()));
            session.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public java.util.List<String> getFriendList(String user) {
        java.util.List<String> friendSet = new java.util.LinkedList<>();
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select friends from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement friends = new BoundStatement(ps);
            rs = session.execute(friends.bind(user));
            if (!rs.isExhausted())//If there is a result
            {
                for (Row row : rs) {
                    friendSet = row.getList("friends", String.class);
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
            java.util.List<String> friendList = getFriendList(user);
            java.util.LinkedList<String> newFriendList = new java.util.LinkedList<>();
            for (int i = 0; i < friendList.size(); i++) {
                newFriendList.add(friendList.get(i));
            }
            newFriendList.add(friend);
            PreparedStatement ps = session.prepare("update userprofiles set friends=? where login =?");
            BoundStatement addFriend = new BoundStatement(ps);
            session.execute(addFriend.bind(newFriendList, user));
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public java.util.LinkedList<String> getUsers(String userName) {
        java.util.LinkedList<String> userList = new java.util.LinkedList<>();
        //java.util.LinkedList<String> newUserList = new java.util.LinkedList<>();
        try {
            Session session = cluster.connect("instagrim");
            java.util.List<String> friendList = getFriendList(userName);
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
            userList.removeAll(friendList);
        } catch (Exception e) {

        }
        return userList;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

}
