package com.hosnag.app;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ccarreno
 */
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class GenericXMLSignature
{
  private String pathSignature;
  private String passSignature;
  
  public String getPathSignature()
  {
    return this.pathSignature;
  }
  
  public void setPathSignature(String pathSignature)
  {
    this.pathSignature = pathSignature;
  }
  
  public String getPassSignature()
  {
    return this.passSignature;
  }
  
  public void setPassSignature(String passSignature)
  {
    this.passSignature = passSignature;
  }
  
  protected void execute()
    throws CertificateException
  {
    KeyStore keyStore = getKeyStore();
    if (keyStore == null)
    {
      System.err.println("No se pudo obtener almacen de firma.");
      return;
    }
    String alias = getAlias(keyStore);
    


    X509Certificate certificate = null;
    try
    {
      certificate = (X509Certificate)keyStore.getCertificate(alias);
      if (certificate == null)
      {
        System.err.println("No existe ningún certificado para firmar.");
        return;
      }
    }
    catch (KeyStoreException e1)
    {
        System.err.println("Error: " + e1.getMessage());
      //e1.printStackTrace();
    }
    PrivateKey privateKey = null;
    KeyStore tmpKs = keyStore;
    try
    {
      privateKey = (PrivateKey)tmpKs.getKey(alias, this.passSignature.toCharArray());
    }
    catch (UnrecoverableKeyException e)
    {
      System.err.println("No existe clave privada para firmar.");
      //e.printStackTrace();
    }
    catch (KeyStoreException e)
    {
      System.err.println("No existe clave privada para firmar.");
      //e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e)
    {
      System.err.println("No existe clave privada para firmar.");
      //e.printStackTrace();
    }
    Provider provider = keyStore.getProvider();

    DataToSign dataToSign = createDataToSign();

    FirmaXML firma = new FirmaXML();

    Document docSigned = null;
    try
    {
      Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
      docSigned = (Document)res[0];
    }
    catch (Exception ex)
    {
      System.err.println("Error realizando la firma: " + ex.getMessage());
      //ex.printStackTrace();
      return;
    }
    String filePath = getPathOut() + File.separatorChar + getSignatureFileName();
    System.out.println("Firma guardada en: " + filePath);
    
    saveDocumenteDisk(docSigned, filePath);
  }
  
  protected abstract DataToSign createDataToSign();
  
  protected abstract String getSignatureFileName();
  
  protected abstract String getPathOut();
  
  protected Document getDocument(String resource)
  {
    Document doc = null;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    File file = new File(resource);
    try
    {
      DocumentBuilder db = dbf.newDocumentBuilder();
      
      doc = db.parse(file);
    }
    catch (ParserConfigurationException|SAXException|IOException|IllegalArgumentException ex)
    {
      System.err.println("Error al parsear el documento: " + ex.getMessage());
      //ex.printStackTrace();
      System.exit(-1);
    }
    return doc;
  }
  
  private KeyStore getKeyStore()
    throws CertificateException
  {
    KeyStore ks = null;
    try
    {
      ks = KeyStore.getInstance("PKCS12");
      ks.load(new FileInputStream(this.pathSignature), this.passSignature.toCharArray());
    }
    catch (KeyStoreException e)
    {
        System.err.println("Error: " + e.getMessage());
        //e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e)
    {
System.err.println("Error: " + e.getMessage());      
//e.printStackTrace();
    }
    catch (CertificateException e)
    {
      System.err.println("Error: " + e.getMessage());
//        e.printStackTrace();
    }
    catch (IOException e)
    {
  System.err.println("Error: " + e.getMessage());
        //e.printStackTrace();
    }
    return ks;
  }
  
  private static String getAlias(KeyStore keyStore)
  {
    String alias = null;
    try
    {
      Enumeration nombres = keyStore.aliases();
      while (nombres.hasMoreElements())
      {
        String tmpAlias = (String)nombres.nextElement();
        if (keyStore.isKeyEntry(tmpAlias)) {
          alias = tmpAlias;
        }
      }
    }
    catch (KeyStoreException e)
    {
      System.err.println("Error: " + e.getMessage());
        //e.printStackTrace();
    }
    return alias;
  }
  
  public static void saveDocumenteDisk(Document document, String pathXml)
  {
    try
    {
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(new File(pathXml));
      
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      
      Transformer transformer = transformerFactory.newTransformer();
      transformer.transform(source, result);
    }
    catch (TransformerConfigurationException e)
    {
        System.err.println("Error: " + e.getMessage());
      //e.printStackTrace();
    }
    catch (TransformerException e)
    {
        System.err.println("Error: " + e.getMessage());
      //e.printStackTrace();
    }
  }
}

