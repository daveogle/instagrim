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

   public Message() {
   }

   public String getMessageTitle() {
      return messageTitle;
   }

   public void setMessageTitle(String messageTitle) {
      this.messageTitle = messageTitle;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getPageRedirect() {
      return pageRedirect;
   }

   public void setPageRedirect(String pageRedirect) {
      this.pageRedirect = pageRedirect;
   }

   public String getPageRedirectName() {
      return pageRedirectName;
   }

   public void setPageRedirectName(String pageRedirectName) {
      this.pageRedirectName = pageRedirectName;
   }
}
