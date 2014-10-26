/**
 * User model class, handles all interaction with the database that is related
 * to the user.
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
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.AccountBean;
import uk.ac.dundee.computing.aec.instagrim.Exceptions.*;

/**
 *
 * @author Administrator
 */
public class User {

    private Cluster cluster;

    /**
     * Constructor
     */
    public User() {

    }

    /**
     * Method to handle a new user register request
     *
     * @param username
     * @param Password
     * @return if success
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.RegisterException
     */
    public boolean RegisterUser(String username, String Password) throws RegisterException {
        //AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        try {
            String EncodedPassword = null;
            EncodedPassword = AeSimpleSHA1.SHA1(Password);
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("insert into userprofiles (login,password) Values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(username, EncodedPassword));
            session.close();
            return true;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password" + et);
            throw new RegisterException(et.toString());
        }
    }

    /**
     * Method to check if a new user is a valid name.
     *
     * @param username
     * @param Password
     * @return if valid
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.RegisterException
     */
    public boolean IsValidUser(String username, String Password) throws RegisterException {
        String EncodedPassword = null;
        try {
            EncodedPassword = AeSimpleSHA1.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            throw new RegisterException((et.toString()));
        }
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
            BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs = session.execute(boundStatement.bind(username));
            if (rs.isExhausted()) {
                System.out.println("No Users returned");
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
        } catch (Exception e) {
            System.out.println("Unknown error occured :" + e);
            throw new RegisterException((e.toString()));
        }
    }

    /**
     * A method to return user account information
     *
     * @param ac - the account bean
     * @param userName - the username
     * @return - the accountBean with info
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.AccountException
     */
    public AccountBean getAccountInfo(AccountBean ac, String userName) throws AccountException {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select * from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement selectUser = new BoundStatement(ps);
            rs = session.execute(selectUser.bind(userName));
            if (!rs.isExhausted())//If there is a result
            {
                for (Row user : rs) {
                    ac.setFirstName(user.getString("first_name"));
                    ac.setLastName(user.getString("last_name"));
                    ac.setEmail(user.getString("email"));
                    user
                            .getMap("addresses", String.class, UDTValue.class
                            ).values().stream().forEach((addr) -> {
                                ac.setAddress(addr.getString("street"), addr.getString("city"), addr.getString("post_code"));
                            }
                            );
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to set accountInfo" + e);
            throw new AccountException(e.toString());
        }
        return ac;
    }

    /**
     * A method to set the users account information
     *
     * @param ac
     * @param userName
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.AccountException
     */
    public void setAccountInfo(AccountBean ac, String userName) throws AccountException {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement firstName = session.prepare("update userprofiles set first_name =? where login =?");
            BoundStatement addAccountInfo = new BoundStatement(firstName);
            session.execute(addAccountInfo.bind(ac.getFirstName(), userName));//Add first Name
            PreparedStatement lastName = session.prepare("update userprofiles set last_name =? where login =?");
            addAccountInfo = new BoundStatement(lastName);
            session.execute(addAccountInfo.bind(ac.getLastName(), userName));
            PreparedStatement email = session.prepare("update userprofiles set email =? where login =?");
            addAccountInfo = new BoundStatement(email);
            session.execute(addAccountInfo.bind(ac.getEmail(), userName));
            //Add address as a map
            PreparedStatement address = session.prepare("update userprofiles set addresses =? where login=?");
            UserType addressUDT = session.getCluster().getMetadata().getKeyspace("instagrim").getUserType("address");
            UDTValue addresses = addressUDT.newValue().setString("street", ac.getStreet()).setString("city", ac.getCity()).setString("post_code", ac.getPostCode());
            java.util.Map<String, UDTValue> addressMap = new java.util.HashMap<>();
            addressMap.put("Home", addresses);
            addAccountInfo = new BoundStatement(address);
            session.execute(addAccountInfo.bind(addressMap, userName));
            session.close();
        } catch (Exception e) {
            throw new AccountException(e.toString());
        }
    }

    /**
     * Method to return a list of users the current user is friends with
     *
     * @param user
     * @return
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.FriendException
     */
    public java.util.List<String> getFriendList(String user) throws FriendException {
        java.util.List<String> friendSet = new java.util.LinkedList<>();
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("select friends from userprofiles where login =?");
            ResultSet rs;
            BoundStatement friends = new BoundStatement(ps);
            rs = session.execute(friends.bind(user));
            if (!rs.isExhausted())//If there is a result
            {
                for (Row row : rs) {
                    friendSet = row.getList("friends", String.class
                    );
                }
            }
            session.close();
        } catch (Exception e) {
            throw new FriendException(e.toString());
        }
        return friendSet;
    }

    /**
     * A method to add a new friend
     * 
     * @param user
     * @param friend
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.FriendException
     */
    public void addFriend(String user, String friend) throws FriendException{
        try {
            Session session = cluster.connect("instagrim");
            java.util.List<String> friendList = getFriendList(user);
            java.util.LinkedList<String> newFriendList = new java.util.LinkedList<>();
            for (int i = 0; i < friendList.size(); i++) {
                if(friendList.get(i).equals(friend)){//If the friend already exists
                    throw new FriendException("Friend Already Exists");
                }
                newFriendList.add(friendList.get(i));
            }
            newFriendList.add(friend);//add the new friend to the end of the list
            PreparedStatement ps = session.prepare("update userprofiles set friends=? where login =?");
            BoundStatement addFriend = new BoundStatement(ps);
            session.execute(addFriend.bind(newFriendList, user));//put back in the database
            session.close();
        } catch (Exception e) {
            throw new FriendException(e.toString());
        }
    }

    /**
     * Method to return all users in the database who are not the current user
     * or friends of the current user
     *
     * @param userName
     * @return
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.FriendException
     */
    public java.util.LinkedList<String> getUsers(String userName) throws FriendException {
        java.util.LinkedList<String> userList = new java.util.LinkedList<>();
        try {
            Session session = cluster.connect("instagrim");
            java.util.List<String> friendList = getFriendList(userName);//Get a list of user friends
            PreparedStatement ps = session.prepare("select * from userprofiles");
            ResultSet rs;
            BoundStatement users = new BoundStatement(ps);
            rs = session.execute(users.bind());
            if (!rs.isExhausted())//If there is a result
            {
                for (Row user : rs) {
                    userList.add(user.getString("login"));
                }
            }
            userList.removeAll(friendList);//remove all friends from userList
        } catch (Exception e) {
            throw new FriendException(e.toString());
        }
        return userList;
    }

    /**
     * Method to set the cluster
     * @param cluster 
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

}
