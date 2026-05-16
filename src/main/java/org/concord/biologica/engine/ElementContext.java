//
// Class : ElementContext
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2003/01/28 18:40:14 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;


import org.apache.xerces.parsers.SAXParser; //dima


/**
 * This class represents a context for an XML element during the
 * process of reading that element in from a BioLogica XML file.<p>
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class ElementContext
extends HandlerBase
{
	/**
	 * XML element text buffer
	**/
	private StringBuffer xmlTextBuffer;

	/**
	 * Parent document handler.
	**/
	private DocumentHandler parentDocumentHandler;
	 
	/**
	 * XML element name
	**/
	private String xmlElementName;

	/**
	 * XML element id
	**/
	private int xmlElementID;

	/**
	 * Import context
	**/
	private ImportContext importContext;

	/**
	 * XML Parser
	**/
//    private com.sun.xml.parser.Parser	 xmlParser = null;
    private SAXParser	 xmlParser = null;//dima
    

	/**
	 * Creates a new ElementContext.  This version of the constructor
	 * should be used in the main document element, when we already
	 * know what the first element will be and we haven't started
	 * parsing yet.  Hence we do NOT claim the document handler here.<p>
	 *
	 * Note that the import context may not be changed once set.<p>
	 *
	 * @param      aParentDocumentHandler DocumentHandler - the parent document handler
	 * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   anImportContext ImportContext - the import context
	 * @exception  IllegalArgumentException - illegal argument input
	**/
/*
	public ElementContext(DocumentHandler aParentDocumentHandler,
						  com.sun.xml.parser.Parser anXMLParser,
						  ImportContext anImportContext)
*/
	public ElementContext(DocumentHandler aParentDocumentHandler,
						  SAXParser anXMLParser,
						  ImportContext anImportContext)
	{
		if (aParentDocumentHandler == null ||
			anImportContext == null ||
			anXMLParser == null)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		xmlTextBuffer = new StringBuffer();
		xmlElementName = null;
		xmlElementID = 0;
		parentDocumentHandler = aParentDocumentHandler;
		importContext = anImportContext;
		xmlParser = anXMLParser;

		// Do NOT claim the document handler
	}

    /**
	 * New element is starting in an existing element context. This
	 * method is called by the parent of this element (e.g. World).
	 * This element context should make itself the document handler
	 * and handle character and white space notifications until the
	 * new element is ended.
	 *
	 * @param      anElementName String - the element name
	 * @param      anElementID int - the element id
	 * @exception  IllegalArgumentException - illegal argument input
	**/
	public void newElement(String anElementName,
						   int anElementID)
	{
		if (anElementName == null)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		xmlTextBuffer.setLength(0);
		xmlElementName = anElementName;
		xmlElementID = anElementID;

		// Finally, claim document handler
		xmlParser.setDocumentHandler(this);
	}

    /**
	 * Receive notification of character data.
	 *
	 * @param           ch char[] - characters
	 * @param           start int - start of string
	 * @param           length int - length of string
	**/
	public void characters(char[] ch, int start, int length)
	throws SAXException
	{
		xmlTextBuffer.append(ch,start,length);
	}

    /**
	 * Receive notification of ignorable white space character data.
	 *
	 * @param           ch char[] - characters
	 * @param           start int - start of string
	 * @param           length int - length of string
	**/
	public void ignorableWhitespace(char[] ch, int start, int length)
	throws SAXException
	{
		xmlTextBuffer.append(ch,start,length);
	}

	/**
	 * Handle notification that the parsing of the given element has ended.
	 * This results in this object notifying the parent document handler,
	 * who then reclaims the parsing notifications and processes the
	 * element that this object just received string data for.<p>
	 *
	 * @param     anElementName String - the element name
	**/
	public void endElement(String anElementName)
	throws SAXException
	{
		if (!xmlElementName.equals(anElementName))
		{
			throw new SAXException("Malformed XML file - mismatched element name encountered");
		}

		// Notify parent document handler
		parentDocumentHandler.endElement(anElementName);
	}

	/**
	 * Return the element name
	 *
	 * @return   String - the element name
	**/
	public String getElementName()
	{
		return xmlElementName;
	}

	/**
	 * Return the element id
	 *
	 * @return   int - the element id
	**/
	public int getElementID()
	{
		return xmlElementID;
	}

	/**
	 * Return the parser
	 *
	 * @return com.sun.xml.parser.Parser - the parser
	**/
//	public com.sun.xml.parser.Parser getXMLParser()
	public SAXParser getXMLParser()
	{
		return xmlParser;
	}

	/**
	 * Return the import context.
	 *
	 * @return   ImportContext - the import context
	**/
	public ImportContext getImportContext()
	{
		return importContext;
	}

	/**
	 * Return a String that contains the contents of the text buffer
	 *
	 * @return    String - the contents of this element context text buffer
	**/
	public String getValueString()
	{
		return xmlTextBuffer.toString();
	}
}
