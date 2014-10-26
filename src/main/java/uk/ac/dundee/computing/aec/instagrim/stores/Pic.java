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
   private final java.util.LinkedList<CommentBean> comments = new java.util.LinkedList<>();
   private java.util.UUID UUID = null;
   private String type;

   /**
    * Constructor
    */
   public void Pic() {

   }

   /**
    * Add a comment to comments list
    * @param comment 
    */
   public void addComment(CommentBean comment) {
      if (comments.isEmpty()) {
         comments.addFirst(comment);
      } else {
         comments.add(comment);
      }
   }

   /**
    * 
    * @return the comments
    */
   public java.util.LinkedList<CommentBean> getComments() {
      return comments;
   }

   /**
    * Return a specific comment
    * @param i
    * @return 
    */
   public CommentBean getComment(int i) {
      CommentBean c = comments.get(i);
      return c;
   }

   /**
    * Set the Unique User ID
    * @param UUID 
    */
   public void setUUID(java.util.UUID UUID) {
      this.UUID = UUID;
   }

   /**
    * Get the SUUID
    * @return 
    */
   public String getSUUID() {
      return UUID.toString();
   }

   /**
    * set the PIC
    * @param bImage
    * @param length
    * @param type 
    */
   public void setPic(ByteBuffer bImage, int length, String type) {
      this.bImage = bImage;
      this.length = length;
      this.type = type;
   }

   /**
    * Set the pic type
    * @param type 
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * 
    * @return the buffer
    */
   public ByteBuffer getBuffer() {
      return bImage;
   }

   /**
    * 
    * @return the length
    */
   public int getLength() {
      return length;
   }

   /**
    * 
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * 
    * @return the bytes
    */
   public byte[] getBytes() {

      byte image[] = Bytes.getArray(bImage);
      return image;
   }

}
