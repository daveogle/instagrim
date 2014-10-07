/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.exceptions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(name = "fileSizeException", urlPatterns = {"/fileSizeException"})
public class fileSizeException extends ServletException {
   public void fileSizeException(){}
}
