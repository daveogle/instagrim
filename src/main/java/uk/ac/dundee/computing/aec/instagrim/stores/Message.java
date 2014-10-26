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
public class Message {

   private String messageTitle;
   private String message;
   private String pageRedirect;
   private String pageRedirectName;

   /**
    * Constructor
    */
   public Message() {
   }

   /**
    * 
    * @return MessageTitle
    */
   public String getMessageTitle() {
      return messageTitle;
   }

   /**
    * Set the message Title
    * @param messageTitle 
    */
   public void setMessageTitle(String messageTitle) {
      this.messageTitle = messageTitle;
   }

   /**
    * 
    * @return the message
    */
   public String getMessage() {
      return message;
   }

   /**
    * set the message
    * @param message 
    */
   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * 
    * @return the page redirect URL
    */
   public String getPageRedirect() {
      return pageRedirect;
   }

   /**
    * Set the page redirect URL
    * @param pageRedirect 
    */
   public void setPageRedirect(String pageRedirect) {
      this.pageRedirect = pageRedirect;
   }

   /**
    * 
    * @return the redirect Name
    */
   public String getPageRedirectName() {
      return pageRedirectName;
   }

   /**
    * Set the redirect name
    * @param pageRedirectName 
    */
   public void setPageRedirectName(String pageRedirectName) {
      this.pageRedirectName = pageRedirectName;
   }
}
