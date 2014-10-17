/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import java.util.Date;

/**
 *
 * @author Dave Ogle
 */
public class Comment {

   private String commentor;
   private String comment;
   private Date commentDate;

   public Comment() {

   }

   /**
    * @return the commentor
    */
   public String getCommentor() {
      return commentor;
   }

   /**
    * @param commentor the commentor to set
    */
   public void setCommentor(String commentor) {
      this.commentor = commentor;
   }

   /**
    * @return the comment
    */
   public String getComment() {
      return comment;
   }

   /**
    * @param comment the comment to set
    */
   public void setComment(String comment) {
      this.comment = comment;
   }

   /**
    * @return the commentDate
    */
   public Date getCommentDate() {
      return commentDate;
   }

   /**
    * @param commentDate the commentDate to set
    */
   public void setCommentDate(Date commentDate) {
      this.commentDate = commentDate;
   }

}
