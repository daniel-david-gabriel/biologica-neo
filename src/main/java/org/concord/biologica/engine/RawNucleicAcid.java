//
// Class : RawNucleicAcid
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2003/01/28 18:40:14 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;
import java.lang.String;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;

import org.apache.xerces.parsers.SAXParser;

/**
 * This class represents a nucleic acid stored as a sequence of base values.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.BASE_VALUES - base values changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.IN_DNA_OR_RNA - whether this is in DNA or RNA changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.START_INDEX_IN_HOLDER - start index in holder changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#BASE_VALUES
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#IN_DNA_OR_RNA
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#START_INDEX_IN_HOLDER
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class RawNucleicAcid
extends EngineObject
implements INucleicAcid, Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Holder of this nucleic acid.  Never null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to a holder, the holder will generate a
     * property change event for a new nucleic acid.<p>
    **/
    private INucleicAcidHolder	holder;

    /**
     * Index of this nucleic acid in its holder (e.g. index of the
     * first base in the overall sequence of bases in the holder).<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.START_INDEX_IN_HOLDER.<p>
    **/
    private int					startIndexInHolder;

    /**
     * Is this nucleic acid in DNA or RNA? Must have a value of
     * Base.IN_DNA or Base.IN_RNA.  Read-only.<p>
     *
     * When this property changes, a property change event is
     * generated for the property named EngineProp.IN_DNA_OR_RNA.<p>
     *
     * @see			org.concord.biologica.engine.Base#IN_DNA
     * @see			org.concord.biologica.engine.Base#IN_RNA
    **/
    private int					inDNAorRNA;

    /**
     * Array of base values for this nucleic acid.
     * May be null, which indicates an empty nucleic acid.<p>
     *
     * When this property is changed (the values in the array),
     * a property change event is generated for the property named
     * EngineProp.BASE_VALUES.<p>
    **/
    private byte[]				bases;

    /**
     * Create a new raw nucleic acid given some base values.<p>
     *
     * @param		aHolder INucleicAcidHolder - holder of this nucleic acid (e.g. allele), may not be null
     * @param		startIndexInHolder int - index of the raw nucleic acid in its holder
     * @param		baseValues byte[] - base values array, may be null or zero length
     * @param		inDNAorRNA int - Base.IN_DNA or Base.IN_RNA
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public RawNucleicAcid(INucleicAcidHolder aHolder,
                          int startIndexInHolder,
                          byte[] baseValues,
                          int inDNAorRNA)
    {
        // Do coarse checks of input values
        if (aHolder == null ||
            startIndexInHolder < 0 ||
            (inDNAorRNA != Base.IN_DNA && inDNAorRNA != Base.IN_RNA))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK
        lockedState = EngineObject.UNLOCKED;

        // Create base values array, doing a better input values check of baseValues array
        byte curBaseValue;
        int i;
        if (baseValues != null)
        {
            int len = baseValues.length;
            bases = new byte[len];
            for (i=0;i<len;i++)
            {
                curBaseValue = baseValues[i];
                if (Base.isValidBase(curBaseValue,inDNAorRNA) == true)
                {
                    bases[i] = curBaseValue;
                }
                else
                {
                    bases = null;
                    throw new IllegalArgumentException(EngineStrings.BAD_BASE_VALUE + curBaseValue);
                }
            }
        }
        else
        {
            bases = null;
        }

        this.inDNAorRNA = inDNAorRNA;
        this.startIndexInHolder = startIndexInHolder;

        // Creation successful, add this nucleic acid to holder
        holder = aHolder;
        holder.addNucleicAcid(this);
    }

    /**
     * Create a new raw nucleic acid in the given holder
     * from a given raw nucleic acid.<p>
     *
     * @param		aHolder INucleicAcidHolder - holder of this nucleic acid (e.g. allele, chromosome), may not be null
     * @param		startIndexInHolder int - start index of raw nucleic acid in the given holder
     * @param		RawNucleicAcid aRawNucleicAcid - an existing raw nucleic acid
     * @param		inDNAorRNA int - Base.IN_DNA or Base.IN_RNA
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public RawNucleicAcid(INucleicAcidHolder aHolder,
                          int startIndexInHolder,
                          RawNucleicAcid aRawNucleicAcid,
                          int inDNAorRNA)
    {
        // Do coarse checks of input values
        if (aHolder == null ||
            startIndexInHolder < 0 ||
            aRawNucleicAcid == null ||
            (inDNAorRNA != Base.IN_DNA && inDNAorRNA != Base.IN_RNA))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK
        lockedState = EngineObject.UNLOCKED;

        // Create base values array, copying them from existing raw nucleic acid
        int i;
        byte curBaseValue;
        byte[] otherBaseValues = aRawNucleicAcid.bases;
        if (otherBaseValues != null)
        {
            int len = otherBaseValues.length;
            this.bases = new byte[len];
            for (i=0;i<len;i++)
            {
                curBaseValue = otherBaseValues[i];
                if (Base.isValidBase(curBaseValue,inDNAorRNA) == true)
                {
                    this.bases[i] = curBaseValue;
                }
                else
                {
                    this.bases = null;
                    throw new IllegalArgumentException(EngineStrings.BAD_BASE_VALUE + curBaseValue);
                }
            }
        }
        else
        {
            this.bases = null;
        }

        this.inDNAorRNA = inDNAorRNA;
        this.startIndexInHolder = startIndexInHolder;

        // Creation successful, add this nucleic acid to holder
        holder = aHolder;
        holder.addNucleicAcid(this);
    }

    /**
     * Create a new raw nucleic acid and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aHolder INucleicAcidHolder - the enclosing holder for this raw nucleic acid
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
/*
    public RawNucleicAcid(INucleicAcidHolder aHolder,
                          String anElementName,
                          int anElementID,
                          com.sun.xml.parser.Parser anXMLParser,
                          ImportContext importContext)
*/
    public RawNucleicAcid(INucleicAcidHolder aHolder,
                          String anElementName,
                          int anElementID,
                          SAXParser anXMLParser,
                          ImportContext importContext)
    {
        if (aHolder == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        startIndexInHolder = 0;
        inDNAorRNA = Base.IN_DNA;
        bases = null;

        // Creation successful, so add this raw nucleic acid to holder
        holder = aHolder;
        holder.addNucleicAcid(this);

        // Create an element context
        xmlElementContext = new ElementContext(this,anXMLParser,importContext);

        // Claim document handler and return, allowing parsing to continue
        anXMLParser.setDocumentHandler(this);
    }

    /**
     * Handle notification that the parser has hit the start
     * of a new element with the given name and attributes.<p>
     *
     * For internal elements (elements that are non-EngineObject
     * instance variables of this object), call xmlElementContext.startElement()
     * and xmlElementContext will receive the remaining element notifications.
     *
     * For external elements (elements that are EngineObject instance
     * variables of this object), create an object of the appropriate
     * class and that object will receive the remaining element notifications.
     *
     * @param   anElementName String - name of element
     * @param   amap AttributeList - an attribute list
    **/
    public void startElement(String anElementName, AttributeList amap)
    throws SAXException
    {
        int anElementID = Elements.mapElementNameToID(anElementName);

        switch (anElementID)
        {
            case Elements.SCHEMA_VERSION_ELEMENT_ID:
            case Elements.ID_ELEMENT_ID:
            case Elements.DELETED_ELEMENT_ID:
            case Elements.LOCKED_STATE_ELEMENT_ID:
            case Elements.HOLDER_ID_ELEMENT_ID:
            case Elements.START_INDEX_IN_HOLDER_ELEMENT_ID:
            case Elements.IN_DNA_OR_RNA_ELEMENT_ID:
            case Elements.BASES_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("RNA " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
        }
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
        // throw new IllegalArgumentException("Should never be here!!");
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
        // throw new IllegalArgumentException("Should never be here!!");
    }

    /**
     * Handle notification that the parsing of the given element has ended.
     *
     * @param     anElementName String - the element name
    **/
    public void endElement(String anElementName)
    throws SAXException
    {
        boolean reclaimDocumentHandler = true;
        String valueString;

        int anElementID = Elements.mapElementNameToID(anElementName);

        switch (anElementID)
        {
            case Elements.SCHEMA_VERSION_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // schemaVersion = Integer.valueOf(valueString).intValue();
                break;

            case Elements.ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // Ignore world id, as we've already created it.
                // But we do need to add it to importContext.
                {
                    int oldID = Integer.valueOf(valueString).intValue();
                    xmlElementContext.getImportContext().addObject(this,oldID);
                }
                break;

            case Elements.DELETED_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                deleted = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.LOCKED_STATE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setLockedStateAsString(valueString);
                break;

            case Elements.HOLDER_ID_ELEMENT_ID:
                // Ignore, as we already have the holder
                break;

            case Elements.START_INDEX_IN_HOLDER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                startIndexInHolder = Integer.valueOf(valueString).intValue();
                break;

            case Elements.IN_DNA_OR_RNA_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                inDNAorRNA = Base.getInDNAorRNAasInt(valueString);
                break;

            case Elements.BASES_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    bases = new byte[valueString.length()];
                    int i;
                    for (i=0;i<bases.length;i++)
                    {
                        switch (valueString.charAt(i))
                        {
                            case 'a': bases[i] = Base.ADENINE; break;
                            case 'c': bases[i] = Base.CYTOSINE; break;
                            case 'g': bases[i] = Base.GUANINE; break;
                            case 't': bases[i] = Base.THYMINE; break;
                            case 'u': bases[i] = Base.URACIL; break;
                            default:
                                throw new IllegalArgumentException("Illegal base value " + valueString.charAt(i));
                        }
                    }
                }
                break;

            case Elements.RAW_NUCLEIC_ACID_ELEMENT_ID:
                holder.endElement(anElementName);
                reclaimDocumentHandler = false;
                break;

            default:
                // External object. Do nothing here.
                reclaimDocumentHandler = false;
                break;
        }

        if (reclaimDocumentHandler)
        {
            // Reclaim the document handler to this object, making this
            // object ready for the next startElement() call from the parser.
            xmlElementContext.getXMLParser().setDocumentHandler(this);
        }
    }

    /**
     * Delete this object, notifying parent objects and deleting any child objects.<p>
    **/
    public void delete()
    {
        delete(true);
    }
    
    public void delete(boolean notifyChange)
    {
        // Avoid double deletions gracefully
        if (deleted == true)
        {
            return;
        }
        deleted = true;

        if (notifyChange)
        {
            // Notify listeners before deleting
            changes.firePropertyChange(EngineProp.DELETED,FALSE,TRUE);
        }

        // Delete instance variables, enabling garbage collection on them
        bases = null;

        // Remove from holder
        holder.removeNucleicAcid(this);
        holder = null;
        id = EngineObject.NULL_ID;
    }
    
    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return new String(EngineStrings.RAW_NUCLEIC_ACID_COLON + id);
    }

    /**
     * Returns the raw nucleic acid's holder.<p>
     *
     * @return		INucleicAcidHolder - raw nucleic acid's holder, never null
    **/
    public INucleicAcidHolder getHolder()
    {
        return holder;
    }

    /**
     * Returns the world containing this nucleic acid.<p>
     *
     * @return		World - world containing this object
    **/
    public World getWorld()
    {
        return holder.getWorld();
    }

    /**
     * Returns the length of the raw nucleic acid in bases.<p>
     *
     * @return		int - length of this autosome in bases
    **/
    public int getLengthInBases()
    {
        if (bases == null)
        {
            return 0;
        }

        return bases.length;
    }

    /**
     * Returns the length of the raw nucleic acid in codons, which is
     * calculated by dividing the length in bases by 3, ignoring any remainder.<p>
     *
     * @return		int - length of this autosome in codons
    **/
    public int getLengthInCodons()
    {
        if (bases == null)
        {
            return 0;
        }
        
        return bases.length / 3;
    }

    /**
     * Returns the base value at the given index in this raw nucleic acid.
     * Note that this value is along one strand.  If the value of the other
     * strand is desired, use Base.getPairBase(). <p>
     *
     * @see			org.concord.biologica.engine.Base#getPairBase
     *
     * @param		index int - index into autosome, must be 0 to length of autosome-1
     * @return		byte - base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public byte getBase(int index)
    {
        if (bases != null && index >= 0 && index < bases.length)
        {
            return bases[index];
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
    }

    /**
     * Sets the base value at the given index in this nucleic acid.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.BASE_VALUES.<p>
     *
     * @param		index int - index into allele, must be 0 to length of nucleic acid-1
     * @param		newBase byte - new base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setBase(int index, byte newBase)
    {
        if (index < 0 || bases == null || index >= bases.length)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (bases[index] != newBase)
        {
            bases[index] = newBase;

            changes.firePropertyChange(EngineProp.BASE_VALUES,null,null);
        }
    }

    /**
     * Get the bases of this raw nucleic acid as an array of bytes.
     *
     * @return      byte[] - array of byte base values
    **/
    public byte[] getBases()
    {
        return bases;
    }

    /**
     * Sets the base values array for this nucleic acid.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.BASE_VALUES.<p>
     *
     * @param		newBases byte[] - new base values array, may be null
    **/
    public void setBases(byte[] newBases)
    {
        bases = newBases;
        changes.firePropertyChange(EngineProp.BASE_VALUES,null,null);
    }

    /**
     * Returns the start index of this nucleic acid in its holder.<p>
     *
     * @return		int - index in holder
    **/
    public int getStartIndexInHolder()
    {
        return startIndexInHolder;
    }

    /**
     * Set the start index of this nucleic acid in its holder.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.START_INDEX_IN_HOLDER.<p>
     *
     * @param		int - new index in holder, must be between 0 and the length in bases
    **/
    public void setStartIndexInHolder(int anIndex)
    {
        if (anIndex != startIndexInHolder)
        {
            int oldIndex = startIndexInHolder;
            startIndexInHolder = anIndex;
            changes.firePropertyChange(EngineProp.START_INDEX_IN_HOLDER,
                                       new Integer(oldIndex),
                                       new Integer(startIndexInHolder));
        }
    }

    /**
     * Writes this object to the given stream in XML format.  Usually
     * this method is used to write the object to a file for saving it,
     * but that is potentially not the only use.<p>
     *
     * We could supply a default implementation that knows how to
     * write the id value.  But that convenience is not chosen
     * because it's never correct to use just this implementation
     * of these methods, so I'd rather have a compilation fail if a
     * derived class fails to implement this method.<p>
     *
     * The inverse of this method is a constructor that takes a
     * org.xml.sax.Parser argument and other arguments.<p>
     *
     * @param       stream PrintWriter - print stream
     * @exception	IllegalArgumentException - input arguments illegal
     * @exception	IOException - an IO error occurred in java libraries
    **/
    public void writeToStream(PrintWriter stream)
    throws java.io.IOException
    {
        // Output stream may not be null
        if (stream == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Start object
        stream.println("<" + Elements.RAW_NUCLEIC_ACID_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Holder ID
        if (holder == null)
        {
            stream.println("<" + Elements.HOLDER_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.HOLDER_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.HOLDER_ID_ELEMENT_NAME + ">" + holder.getID() +
                              "</" + Elements.HOLDER_ID_ELEMENT_NAME + ">");
        }

        // Start index in holder
        stream.println("<" + Elements.START_INDEX_IN_HOLDER_ELEMENT_NAME + ">" + startIndexInHolder +
                          "</" + Elements.START_INDEX_IN_HOLDER_ELEMENT_NAME + ">");

        // In DNA or RNA
        stream.println("<" + Elements.IN_DNA_OR_RNA_ELEMENT_NAME + ">" +
                          Base.getInDNAorRNAasString(inDNAorRNA) +
                          "</" + Elements.IN_DNA_OR_RNA_ELEMENT_NAME + ">");

        // Bases
        stream.print("<" + Elements.BASES_ELEMENT_NAME + ">");
        if (bases != null)
        {
            int i;
            int basesLength = bases.length;
            for (i=0;i<basesLength;i++)
            {
                switch (bases[i])
                {
                    case Base.URACIL:
                        stream.print("u");
                        break;
                    case Base.CYTOSINE:
                        stream.print("c");
                        break;
                    case Base.ADENINE:
                        stream.print("a");
                        break;
                    case Base.GUANINE:
                        stream.print("g");
                        break;
                    case Base.THYMINE:
                        stream.print("t");
                        break;
                    default:
                        stream.print("-");
                        break;
                }
            }
        }
        stream.println("</" + Elements.BASES_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.RAW_NUCLEIC_ACID_ELEMENT_NAME + ">");
    }
}

