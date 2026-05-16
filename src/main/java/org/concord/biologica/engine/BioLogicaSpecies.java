package org.concord.biologica.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
//import com.sun.xml.parser.Parser;
//import com.sun.xml.tree.XmlDocument;

import org.apache.xerces.parsers.SAXParser;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class BioLogicaSpecies
implements DocumentHandler
{
    protected Vector names = new Vector();
    protected Vector files = new Vector();
    protected Locator locator;
    
    public BioLogicaSpecies(String fileName)
    {
        try
        {
            FileInputStream fis = new FileInputStream(new File(fileName));
            parseInput(fis);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    public int getSpeciesNumber()
    {
        return names.size();
    }
    
    public String getSpeciesName(int i)
    {
        return (String) names.elementAt(i);
    }
    
    public String getSpeciesFile(int i)
    {
        return (String) files.elementAt(i);
    }
    
    public void parseInput(InputStream input)
    {
        try
        {
//            Parser parser = new Parser();
            SAXParser parser = new SAXParser();
            parser.setDocumentHandler(this);
            InputSource source = new InputSource(input);
            parser.parse(source);
            input.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }
    
    public void startDocument() throws SAXException
    {
    }
    
    public void endDocument() throws SAXException
    {
    }
    
    public void startElement(String name, AttributeList atts) throws SAXException
    {
        if (name.toLowerCase().equals("species"))
        {
            names.addElement(atts.getValue("name"));
            files.addElement(atts.getValue("file"));
        }
    }
    
    public void endElement(String name) throws SAXException
    {
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException
    {
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
    }
    
    public void processingInstruction(String target, String data) throws SAXException
    {
    }
}

