/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hosnag.app;

/**
 *
 * @author ccarreno
 */

public class DevelopedSignature {
    
    //args[0] = certificado
    //args[1] = contraseña ceritificado
    //args[2] = ruta + archivo xml entrada
    //args[3] = ruta xml salida
    //args[4] = archivo xml salida
    public static void main(String[] args) throws Exception {
              
    /*String xmlPath = "c:\\facturaSinFirmar\factura.XML";
    String pathSignature = "c:\\certificados\certificado.p12";
    String passSignature = "password";
    String pathOut = "c:\\facturaFirmada\\";
    String nameFileOut = "factura_firmada.xml";*/
    
    String xmlPath = args[2];
    String pathSignature = args[0];
    String passSignature = args[1];
    String pathOut = args[3];
    String nameFileOut = args[4];
    
    System.out.println("Ruta del XML de entrada: " + xmlPath);
    System.out.println("Ruta Certificado: " + pathSignature);
    System.out.println("Clave del Certificado: " + passSignature);
    System.out.println("Ruta de salida del XML: " + pathOut);
    System.out.println("Nombre del archivo salido: " + nameFileOut);
    
    try{
    XAdESBESSignature.firmar(xmlPath, pathSignature, passSignature, pathOut, nameFileOut);
    }catch(Exception e){
        System.out.println("Error: " + e);
    }
    }

  
    }

 

