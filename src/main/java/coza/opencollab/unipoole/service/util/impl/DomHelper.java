package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.Defaults;
import static coza.opencollab.unipoole.service.ErrorCodes.XML_PARSING;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

/**
 * This abstract class contains some convenience methods for the converters.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public final class DomHelper {
    
    /**
     * static class
     */
    private DomHelper(){}
    
    /**
     * Builds the document from the xml given.
     * 
     * @param xml The xml string.
     * @throws UnipooleException If the xml could not be parsed to a document.
     */
    public static Document createDocument(String xml){
        try {
            return DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            throw new UnipooleException(XML_PARSING, "Could not parse the xml.", e);
        }
    }
    
    /**
     * Builds the document from the xml given.
     * 
     * @param xml The xml string.
     * @throws UnipooleException If the xml could not be parsed to a document.
     */
    public static Document createDocumentLinient(String xml){
        try {
            SAXReader reader = new SAXReader();
            InputSource source = new InputSource(new StringReader(xml));
            source.setEncoding(Defaults.UTF8.displayName());
            reader.setValidation(false);
            return reader.read(source);
        } catch (DocumentException e) {
            throw new UnipooleException(XML_PARSING, "Could not parse the xml.", e);
        }
    }
    
    /**
     * Retrieve the element from the document/element/node given the path.
     * <p>
     * Note: The xpath must be correct for a single element.
     * 
     * @param node The node to do the xpath on
     * @param path The xpath to call on the document.
     * @return The element.
     */
    public static Element getElement(Node node, String xpath) throws UnipooleException{
        return (Element) node.selectSingleNode(xpath);
    }
    
    /**
     * Retrieve the list of elements from the document/element/node given the path.
     * <p>
     * Note: The xpath must be correct for elements of the document.
     * 
     * @param node The node to do the xpath on
     * @param path The xpath to call on the document.
     * @return The elements.
     */
    public static List<Element> getElements(Node node, String xpath) throws UnipooleException{
        return node.selectNodes(xpath);
    }
    
    /**
     * Retrieves the text value of the subelement.
     * 
     * @param element The element.
     * @param subElementName The name of the sub element.
     * @return The text value.
     */
    public static String getSubElementValue(Element element, String subElementName){
        Element e = getElement(element, subElementName);
        if(e == null){
            return null;
        }
        return e.getText();
    }
    
    /**
     * Retrieves the text value of the subelement as a date.
     * 
     * @param element The element.
     * @param subElementName The name of the sub element.
     * @return The text value as a date.
     */
    public static Date getSubElementValueAsDate(Element element, String subElementName){
        Element e = getElement(element, subElementName);
        if(e == null){
            return null;
        }
        return getDate(e.getText());
    }
    
    /**
     * Retrieves the text value of the subelement as a boolean.
     * 
     * @param element The element.
     * @param subElementName The name of the sub element.
     * @return The text value as a boolean.
     */
    public static boolean getSubElementValueAsBoolean(Element element, String subElementName){
        Element e = getElement(element, subElementName);
        if(e == null){
            return false;
        }
        return getBoolean(e.getText());
    }
    
    /**
     * Retrieve a attribute value from a element.
     * 
     * @param element The element.
     * @param attribute The attribute name.
     */
    public static String getAttribute(Element element, String attribute){
        return element.attributeValue(attribute);
    }
    
    /**
     * Retrieve a attribute value from a node as a date.
     * Note that the attribute value must be time in millis.
     * 
     * @param element The element.
     * @param attribute The attribute name.
     */
    public static Date getAttributeAsDate(Element element, String attribute){
        return getDate(getAttribute(element, attribute));
    }
    
    private static Date getDate(String dateString){
        if(StringUtils.isEmpty(dateString)){
            return null;
        }
        return new Date(Long.parseLong(dateString));
    }
    
    /**
     * Retrieve a attribute value from a node as a int.
     * 
     * @param element The element.
     * @param attribute The attribute name.
     */
    public static int getAttributeAsInt(Element element, String attribute){
        String intStr = getAttribute(element, attribute);
        if(StringUtils.isEmpty(intStr)){
            return 0;
        }
        return Integer.parseInt(intStr);
    }
    
    /**
     * Retrieve a attribute value from a node as a boolean.
     * 
     * @param element The element.
     * @param attribute The attribute name.
     */
    public static boolean getAttributeAsBoolean(Element element, String attribute){
        return getBoolean(getAttribute(element, attribute));
    }
    
    private static boolean getBoolean(String boolString){
        if(StringUtils.isEmpty(boolString)){
            return false;
        }
        return Boolean.parseBoolean(boolString);
    }
}
