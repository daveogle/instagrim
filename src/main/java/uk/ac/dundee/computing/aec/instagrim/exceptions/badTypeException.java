/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.exceptions;

import javax.servlet.ServletException;

/**
 *
 * @author Dave Ogle
 */
public class badTypeException extends ServletException {

   String errorType;
   String errorMessage;

   public void badTypeException() {
      errorType = "Bad Type Exeception";
      errorMessage = "Error: you may only upload image files";
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public String getErrorType() {
      return errorType;
   }
}
