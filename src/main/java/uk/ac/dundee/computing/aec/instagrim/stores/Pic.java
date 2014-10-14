/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;

/**
 *
 * @author Administrator
 */
public class Pic {

    private ByteBuffer bImage = null;
    private int length;
    private java.util.LinkedList<String> comments = null;
    private java.util.UUID UUID = null;
    private String type;

    public void Pic() {

    }

    public void addComment(String comment) {
        if (comments.isEmpty()) {
            comments.addFirst(comment);
        } else {
            comments.add(comment);
        }
    }

    public java.util.LinkedList<String> getComments() {
        return comments;
    }

    public String getComment(int i) {
        String c = comments.get(i);
        return c;
    }

    public void setUUID(java.util.UUID UUID) {
        this.UUID = UUID;
    }

    public String getSUUID() {
        return UUID.toString();
    }

    public void setPic(ByteBuffer bImage, int length, String type) {
        this.bImage = bImage;
        this.length = length;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ByteBuffer getBuffer() {
        return bImage;
    }

    public int getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public byte[] getBytes() {

        byte image[] = Bytes.getArray(bImage);
        return image;
    }

}
