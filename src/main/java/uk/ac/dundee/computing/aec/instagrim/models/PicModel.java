package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.awt.image.BufferedImage;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;
import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.*;
import uk.ac.dundee.computing.aec.instagrim.Exceptions.*;

/**
 * Class to handle interactions with database that involve pictures
 *
 * @author Administrator
 */
public class PicModel {

    private Cluster cluster;

    /**
     * Constructor
     */
    public PicModel() {
    }

    /**
     *
     * @param cluster
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * A method to check if the user has images in the database
     *
     * @param user
     * @return
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public boolean hasPictures(String user) throws ImageException {
        try {
            Session session = cluster.connect("instagrim");//Connect to Instagrim db
            PreparedStatement psGetPics = session.prepare("select * from Pics where user =?");
            ResultSet hasPictures;
            BoundStatement boundStatementPics = new BoundStatement(psGetPics);
            hasPictures = session.execute(boundStatementPics.bind(user));
            if (hasPictures.all().isEmpty()) {//If there are no pictures
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new ImageException("Error accessing users Pictures" + e.getMessage());
        }
    }

    /**
     * Method to initially set a user Avatar
     *
     * @param b - byte array containing pic
     * @param type - type of image
     * @param name - name of image
     * @param user - user name
     * @throws IOException
     */
    public void setAvatar(byte[] b, String type, String name, String user) throws IOException {
        try {
            String types[] = Convertors.SplitFiletype(type);
            int length = b.length;
            java.util.UUID picid = Convertors.getTimeUUID();
            byte[] thumbb = picresize(picid.toString(), types[1], b);
            int thumblength = thumbb.length;
            ByteBuffer thumbbuf = ByteBuffer.wrap(thumbb);
            Session session = cluster.connect("instagrim");//Connect to Instagrim db
            ByteBuffer buffer = ByteBuffer.wrap(b);
            PreparedStatement psInsertAvatar = session.prepare("insert into avatar (picid, image, thumb, user, imagelength, thumblength, type, name) values(?,?,?,?,?,?,?,?)");
            BoundStatement bsInsertAvatar = new BoundStatement(psInsertAvatar);
            session.execute(bsInsertAvatar.bind(picid, buffer, thumbbuf, user, length, thumblength, type, name));
            session.close();
        } catch (Exception e) {
            System.out.println("Error --> " + e);
            throw new IOException();
        }
    }

    /**
     * Method to update an avatar
     *
     * @param b
     * @param type
     * @param name
     * @param user
     * @return
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.AccountException
     */
    public void updateAvatar(byte[] b, String type, String name, String user) throws AccountException {
        try {
            String types[] = Convertors.SplitFiletype(type);
            int length = b.length;
            java.util.UUID picid = Convertors.getTimeUUID();
            byte[] thumbb = picresize(picid.toString(), types[1], b);
            int thumblength = thumbb.length;
            ByteBuffer thumbbuf = ByteBuffer.wrap(thumbb);
            Session session = cluster.connect("instagrim");//Connect to Instagrim db
            ByteBuffer buffer = ByteBuffer.wrap(b);
            PreparedStatement psUpdateAvatar = session.prepare("update avatar set image =?, thumb =?, imagelength =?, thumblength =?, type =?, name =? where user =?");
            BoundStatement bsInsertAvatar = new BoundStatement(psUpdateAvatar);
            session.execute(bsInsertAvatar.bind(buffer, thumbbuf, length, thumblength, type, name, user));
            session.close();
        } catch (Exception e) {
            System.out.println("Error --> " + e);
            throw new AccountException(e.toString());
        }
    }

    /**
     * A method to insert a picture into the database
     *
     * @param b
     * @param type
     * @param name
     * @param user
     * @throws ImageException
     */
    public void insertPic(byte[] b, String type, String name, String user) throws ImageException {
        try {
            String types[] = Convertors.SplitFiletype(type);
            int length = b.length;
            java.util.UUID picid = Convertors.getTimeUUID();
            byte[] thumbb = picresize(picid.toString(), types[1], b);
            int thumblength = thumbb.length;
            ByteBuffer thumbbuf = ByteBuffer.wrap(thumbb);
            byte[] processedb = picdecolour(picid.toString(), types[1], b);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            int processedlength = processedb.length;
            Session session = cluster.connect("instagrim");//Connect to Instagrim db
            ByteBuffer buffer = ByteBuffer.wrap(b);
            /*
             Insert the picture into the keyspaces pics & userpiclist
             */
            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image, thumb, processed, user, interaction_time, imagelength, thumblength, processedlength, type, name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf, processedbuf, user, DateAdded, length, thumblength, processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();
        } catch (IOException | ImageException ex) {
            throw new ImageException(ex.toString());
        }
    }

    /**
     * Method to Add a new comment
     *
     * @param user
     * @param picid
     * @param commentToAdd
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public void insertComment(String user, java.util.UUID picid, String commentToAdd) throws ImageException {
        try {
            UUID commentId = UUID.randomUUID();
            Session session = cluster.connect("instagrim");//Connect to Instagrim db
            Date dateAdded = new Date();
            PreparedStatement psInsertComment = session.prepare("insert into commentlist (commentid, picid, user, comment_added, comment) values (?, ?, ?, ?, ?)");
            BoundStatement bsInsertComment = new BoundStatement(psInsertComment);
            session.execute(bsInsertComment.bind(commentId, picid, user, dateAdded, commentToAdd));
            session.close();
        } catch (Exception e) {
            System.out.println("Error -->" + e);
            throw new ImageException(e.toString());
        }
    }

    /**
     * A method to delete a picture
     *
     * @param picid
     * @param user
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public void deletePic(java.util.UUID picid, String user) throws ImageException {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement getTimeStamp = session.prepare("select interaction_time from pics where picid =?");
            ResultSet rs;
            BoundStatement bsgetTimeStatement = new BoundStatement(getTimeStamp);
            rs = session.execute(bsgetTimeStatement.bind(picid));
            Date timeStamp = null;
            for (Row row : rs) {
                timeStamp = row.getDate("interaction_time");
            }
            PreparedStatement psDeletePic = session.prepare("DELETE FROM pics where picid =?");
            PreparedStatement psDeletePicFromUser = session.prepare("DELETE FROM userpiclist where user =? and pic_added =?");
            BoundStatement bsDeletePic = new BoundStatement(psDeletePic);
            BoundStatement bsDeletePicFromUser = new BoundStatement(psDeletePicFromUser);
            session.execute(bsDeletePic.bind(picid));
            session.execute(bsDeletePicFromUser.bind(user, timeStamp));
            session.close();
        } catch (Exception e) {
            throw new ImageException(e.toString());
        }
    }

    /**
     * Method to delete a comment
     *
     * @param picid
     * @param commentId
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public void deleteComment(java.util.UUID picid, java.util.UUID commentId) throws ImageException {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement psDeleteComment = session.prepare("DELETE FROM commentlist where commentid =? and  picid =?");
            BoundStatement bsDeleteComment = new BoundStatement(psDeleteComment);
            session.execute(bsDeleteComment.bind(commentId, picid));
            session.close();
        } catch (Exception e) {
            throw new ImageException(e.toString());
        }
    }

    /**
     * A method to delete all comments from a pic
     *
     * @param picid
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public void deleteComments(java.util.UUID picid) throws ImageException {
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement getComments = session.prepare("select * from commentlist where picid =?");
            BoundStatement bsGetComments = new BoundStatement(getComments);
            ResultSet rs = session.execute(bsGetComments.bind(picid));
            if (!rs.isExhausted()) {
                for (Row row : rs) {
                    java.util.UUID commentId = row.getUUID("commentid");
                    deleteComment(picid, commentId);
                }
            }
            session.close();
        } catch (Exception e) {
            throw new ImageException(e.toString());
        }
    }

    /**
     * Method to resize an Image
     *
     * @param picid
     * @param type
     * @param b
     * @return
     * @throws IOException
     */
    public byte[] picresize(String picid, String type, byte[] b) throws IOException {
        try {
            InputStream bais = new ByteArrayInputStream(b);
            BufferedImage BI = ImageIO.read(bais);
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {
            throw new IOException();
        }
    }

    /**
     * A method to decolour the picture
     *
     * @param picid
     * @param type
     * @param b
     * @return the pic as a byte array
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public byte[] picdecolour(String picid, String type, byte[] b) throws ImageException {
        try {
            InputStream bais = new ByteArrayInputStream(b);
            BufferedImage BI = ImageIO.read(bais);
            BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (Exception et) {
            throw new ImageException(et.toString());
        }
    }

    /**
     *
     * @param img
     * @return
     */
    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // add a little border before we return result.
        return pad(img, 2);
    }

    /**
     *
     * @param img
     * @return
     */
    public static BufferedImage createProcessed(BufferedImage img) {
        int Width = img.getWidth() - 1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(img, 4);
    }

    /**
     * Method to get a list of Pictures for the user
     *
     * @param User
     * @param comments
     * @return a list of pictures
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public java.util.LinkedList<Pic> getPicsForUser(String User, boolean comments) throws ImageException {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        try {
            Session session = cluster.connect("instagrim");
            PreparedStatement picturePs = session.prepare("select picid from userpiclist where user =?");//Get the use pictures
            ResultSet pictures;
            BoundStatement boundStatementPics = new BoundStatement(picturePs);
            pictures = session.execute( // this is where the query is executed
                    boundStatementPics.bind(User));
            if (pictures.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : pictures) {
                    Pic pic = new Pic();
                    java.util.UUID UUID = row.getUUID("picid");
                    if (comments) {
                        pic = getComments(session, pic, UUID);//get the comments for the picture
                    }
                    System.out.println("UUID" + UUID.toString());
                    pic.setUUID(UUID);
                    Pics.add(pic);
                }
            }
            session.close();
            return Pics;
        } catch (Exception e) {
            throw new ImageException(e.toString());
        }
    }

    /**
     * A method to get comments
     *
     * @param session
     * @param pic
     * @param UUID
     * @return the Pic
     */
    private Pic getComments(Session session, Pic pic, java.util.UUID UUID) throws ImageException {
        ResultSet comments = null;
        try {
            PreparedStatement commentPs = session.prepare("select * from commentlist where picid =?");
            BoundStatement boundStatementComments = new BoundStatement(commentPs);
            comments = session.execute(boundStatementComments.bind(UUID));
            if (!comments.isExhausted()) {//if there are comments
                for (Row commentRow : comments) {
                    CommentBean newComment = new CommentBean();
                    newComment.setCommentID(commentRow.getUUID("commentid"));
                    newComment.setCommentor(commentRow.getString("user"));
                    newComment.setComment(commentRow.getString("comment"));
                    newComment.setCommentDate(commentRow.getDate("comment_added"));
                    pic.addComment(newComment);
                }
            }
            return pic;
        } catch (Exception e) {
            throw new ImageException(e.toString());
        }
    }

    /**
     * A method to get a users picture
     *
     * @param image_type
     * @param picid
     * @return the Picture
     * @throws uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException
     */
    public Pic getPic(int image_type, java.util.UUID picid) throws ImageException {
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        try {
            Session session = cluster.connect("instagrim");
            Convertors convertor = new Convertors();
            ResultSet rs;
            PreparedStatement ps = null;
            if (image_type == Convertors.DISPLAY_IMAGE) {
                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(picid));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");

                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    type = row.getString("type");
                }
            }
            session.close();
        } catch (Exception et) {
            throw new ImageException(et.toString());
        }
        Pic p = new Pic();
        p.setPic(bImage, length, type);
        return p;
    }

    /**
     * Method to return the user avatar
     *
     * @param userName
     * @return the user avatar
     */
    public Pic getAvatar(String userName) {
        try {
            Session session = cluster.connect("instagrim");
            ByteBuffer bImage = null;
            String type = null;
            int length = 0;
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
            ps = session.prepare("select * from avatar where user =?");
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(userName));
            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    bImage = row.getBytes("thumb");
                    length = row.getInt("thumblength");
                    type = row.getString("type");
                }
            }
            session.close();
            Pic p = new Pic();
            p.setPic(bImage, length, type);
            return p;
        } catch (Exception et) {
            System.out.println("Error retriving avatar" + et);
            return null;
        }
    }
}
