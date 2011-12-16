package com.rackspacecloud.client.cloudfiles;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class AuthResponse {

    private static Logger logger = Logger.getLogger(AuthResponse.class);

    private HttpMethod httpmethod = null;
    private Document document = null;

    public AuthResponse(HttpMethod method) throws FilesException {
        this.httpmethod = method;
        loadDocument();
    }

    private void loadDocument() throws FilesException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            String responseBody = httpmethod.getResponseBodyAsString();

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource(new StringReader(responseBody));
            document = db.parse(is);
        } catch (IOException e) {
            logger.error(e);
            throw new FilesException("Error loading the response", e);
        } catch (ParserConfigurationException e) {
            logger.error(e);
            throw new FilesException("Error loading the response", e);
        } catch (SAXException e) {
            logger.error(e);
            throw new FilesException("Error loading the response", e);
        }
    }

    public String getStorageURL() {
        Element docEle = document.getDocumentElement();
        NodeList nodeList = ((Element) docEle.getElementsByTagName("serviceCatalog").item(0)).getElementsByTagName("service");
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                if (el.getAttribute("name").equalsIgnoreCase("cloudFiles")) {
                    Element endpointEL = (Element) el.getElementsByTagName("endpoint").item(0);
                    String storageUrl = endpointEL.getAttribute("publicURL");
                    return storageUrl;
                }
            }
        }
        return null;
    }

    public String getCDNManagementURL() {
        Element docEle = document.getDocumentElement();
        NodeList nodeList = ((Element) docEle.getElementsByTagName("serviceCatalog").item(0)).getElementsByTagName("service");
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                if (el.getAttribute("name").equalsIgnoreCase("cloudFiles")) {
                    Element endpointEL = (Element) el.getElementsByTagName("endpoint").item(0);
                    String storageUrl = endpointEL.getAttribute("publicURL");
                    return storageUrl;
                }
            }
        }
        return null;
    }

    public String getAuthToken() {
        Element docEle = document.getDocumentElement();
        String token = ((Element) docEle.getElementsByTagName("token").item(0)).getAttribute("id");
        return token;
    }

    public boolean loginSuccess() {
        int statusCode = httpmethod.getStatusLine().getStatusCode();

        if (statusCode >= 200 && statusCode < 300)
            return true;

        return false;
    }

}
