//
// Class : GenotypeToPhenotypeRule
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;

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
 * This class represents a rule for choosing a phenotype from a genotype.<p>
 *
 * In detail, an object of this class represents a statement of the form:
 *
 *		if <a set of "if species alleles">
 *		then <"then characteristic">
 *		else <"else characteristic">
 *
 * So the instance variables and properties of this class are named appropriately
 * (ifAlleles, thenCharacteristic, elseCharacteristic, etc.).<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ELSE_CHARACTERISTIC - the else characteristic changed
 * <li> EngineProp.GENDER - gender of this rule has changed
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.IF_SPECIES_ALLELE_ADDED - a species allele was added to the if clause
 * <li> EngineProp.IF_SPECIES_ALLELE_REMOVED - a species allele was removed from the if clause
 * <li> EngineProp.LOCKED_STATE - object has been locked or unlocked
 * <li> EngineProp.NAME - name of this rule has changed
 * <li> EngineProp.THEN_CHARACTERISTIC - the then characteristic changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ELSE_CHARACTERISTIC
 * @see org.concord.biologica.engine.EngineProp#GENDER
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#IF_SPECIES_ALLELE_ADDED
 * @see org.concord.biologica.engine.EngineProp#IF_SPECIES_ALLELE_REMOVED
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#THEN_CHARACTERISTIC
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class GenotypeToPhenotypeRule
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Name of this rule.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * The species which contains this rule.  May not be null.<p>
    **/
    private Species				species;

    /**
     * Gender of the organisms for this rule.  This must be one of
     * Species.FEMALE_AND_MALE, Species.FEMALE_ONLY or Species.MALE_ONLY.
     * For haploid species, use FEMALE_AND_MALE.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.GENDER.<p>
     *
     * @see		org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see		org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see		org.concord.biologica.engine.Species#MALE_ONLY
    **/
    private int					gender;

    /**
     * The vector of "if species alleles" for this rule. They are considered
     * to be "anded" together for the sake of testing this rule.<p>
     *
     * When a SpeciesAllele object is created and added to this vector,
     * a EngineProp.IF_SPECIES_ALLELE_ADDED property change event is fired.<p>
     *
     * When a SpeciesAllele object is deleted and removed from this vector,
     * a EngineProp.IF_SPECIES_ALLELE_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#IF_SPECIES_ALLELE_ADDED
     * @see		org.concord.biologica.engine.EngineProp#IF_SPECIES_ALLELE_REMOVED
    **/
    private Vector				ifSpeciesAlleles;

    /**
     * The "then characteristic" of this rule.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.THEN_CHARACTERISTIC.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#THEN_CHARACTERISTIC
    **/
    private Characteristic		thenCharacteristic;

    /**
     * The "else characteristic" of this rule.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.ELSE_CHARACTERISTIC.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#ELSE_CHARACTERISTIC
    **/
    private Characteristic		elseCharacteristic;

    /**
     * Creates a new genotype to phenotype rule with the given name.<p>
     *
     * @param		aSpecies Species - the species containing this character, may not be null
     * @param		aName String - name of this rule, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public GenotypeToPhenotypeRule(Species aSpecies, String aName)
    {
        // Check input arguments
        if (aSpecies == null ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        lockedState = EngineObject.UNLOCKED;

        // Copy name, so caller can use the name multiple times without
        // causing the String to be used in multiple engine objects.
        name = new String(aName);

        // Initialize gender to female and male
        gender = Species.FEMALE_AND_MALE;

        // Initialize other instance variables
        ifSpeciesAlleles = new Vector();
        thenCharacteristic = null;
        elseCharacteristic = null;

        // Creation successful - tell species
        species = aSpecies;
        species.addGenotypeToPhenotypeRule(this);
    }

    /**
     * Creates a new genotype to phenotype rule with the given name, an existing rule as
     * a template and 2 species alleles.  This new rule should be a copy of the given one
     * with all references to the first allele replaced with references to the second allele.
     *
     * @param		aSpecies Species - the species containing this rule, may not be null
     * @param		aName String - name of this rule, may not be null
     * @param       aRule GenotypeToPhenotypeRule - rule to copy, may not be null
     * @param       oldIfSpeciesAllele SpeciesAllele - original if species allele, may not be null
     * @param       newIfSpeciesAllele SpeciesAllele - new if species allele, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public GenotypeToPhenotypeRule(Species aSpecies, String aName,
                                   GenotypeToPhenotypeRule aRule,
                                   SpeciesAllele oldIfSpeciesAllele,
                                   SpeciesAllele newIfSpeciesAllele)
    {
        if (aSpecies == null ||
            aName == null ||
            aRule == null ||
            oldIfSpeciesAllele == null ||
            newIfSpeciesAllele == null ||
            oldIfSpeciesAllele.getGene() != newIfSpeciesAllele.getGene())
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        lockedState = EngineObject.UNLOCKED;

        // Copy name, so caller can use the name multiple times without
        // causing the String to be used in multiple engine objects.
        name = new String(aName);

        // Initialize other instance variables
        gender = aRule.getGender();
        thenCharacteristic = aRule.getThenCharacteristic();
        elseCharacteristic = aRule.getElseCharacteristic();
        ifSpeciesAlleles = new Vector();

        // Copy if species alleles, replacing old species allele with new one
        SpeciesAllele anIfSpeciesAllele;
        Enumeration eIfSpeciesAlleles = aRule.getIfSpeciesAlleles();
        while (eIfSpeciesAlleles.hasMoreElements())
        {
            anIfSpeciesAllele = (SpeciesAllele) eIfSpeciesAlleles.nextElement();
            if (anIfSpeciesAllele == oldIfSpeciesAllele)
            {
                ifSpeciesAlleles.addElement(newIfSpeciesAllele);
            }
            else
            {
                ifSpeciesAlleles.addElement(anIfSpeciesAllele);
            }
        }

        // Creation successful - tell species
        species = aSpecies;
        species.addGenotypeToPhenotypeRuleAfter(this,aRule);
    }

    /**
     * Create a new genotype to phenotype rule and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpecies Species - the enclosing species for this new genotype to phenotype rule
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public GenotypeToPhenotypeRule(Species aSpecies,
                                   String anElementName,
                                   int anElementID,
                                   /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                                   ImportContext importContext)
    {
        if (aSpecies == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        name = null;
        gender = Species.FEMALE_AND_MALE;
        thenCharacteristic = null;
        elseCharacteristic = null;
        lockedState = EngineObject.UNLOCKED;

        // Create vectors
        ifSpeciesAlleles = new Vector();

        // Creation successful, so add this genotype to phenotype rule to species
        species = aSpecies;
        species.addGenotypeToPhenotypeRule(this);

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
            case Elements.SPECIES_ID_ELEMENT_ID:
            case Elements.NAME_ELEMENT_ID:
            case Elements.GENDER_ELEMENT_ID:
            case Elements.IF_SPECIES_ALLELE_IDS_ELEMENT_ID:
            case Elements.THEN_CHARACTERISTIC_ID_ELEMENT_ID:
            case Elements.ELSE_CHARACTERISTIC_ID_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("GenotypeToPhenotypeRule " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.SPECIES_ID_ELEMENT_ID:
                // Ignore, as we already have the species
                break;

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.GENDER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setGenderAsString(valueString);
                break;

            case Elements.IF_SPECIES_ALLELE_IDS_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    int alleleId;
                    String alleleIdString;
                    SpeciesAllele aSpeciesAllele;
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    while (parser.hasMoreTokens())
                    {
                        alleleIdString = parser.nextToken();
                        alleleId = Integer.valueOf(alleleIdString).intValue();
                        aSpeciesAllele = (SpeciesAllele) xmlElementContext.getImportContext().getObject(alleleId);
                        ifSpeciesAlleles.addElement(aSpeciesAllele);
                    }
                }
                break;

            case Elements.THEN_CHARACTERISTIC_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    thenCharacteristic = null;
                }
                else
                {
                    int thenCharacteristicID = Integer.valueOf(valueString).intValue();
                    if (thenCharacteristicID != EngineObject.NULL_ID)
                    {
                        thenCharacteristic = (Characteristic) xmlElementContext.getImportContext().getObject(thenCharacteristicID);
                    }
                    else
                    {
                        thenCharacteristic = null;
                    }
                }
                break;

            case Elements.ELSE_CHARACTERISTIC_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    elseCharacteristic = null;
                }
                else
                {
                    int elseCharacteristicID = Integer.valueOf(valueString).intValue();
                    if (elseCharacteristicID != EngineObject.NULL_ID)
                    {
                        elseCharacteristic = (Characteristic) xmlElementContext.getImportContext().getObject(elseCharacteristicID);
                    }
                    else
                    {
                        elseCharacteristic = null;
                    }
                }
                break;

            case Elements.GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_ID:
                // Done with this genotype to phenotype rule, so pop up to enclosing species
                species.endElement(anElementName);
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
     *
     * When this method is successful, a property change event is
     * generated for the property named EngineProp.DELETED.<p>
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
        name = null;

        species.removeGenotypeToPhenotypeRule(this);
        species = null;
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
        return EngineStrings.G_TO_P_RULE_COLON + name;
    }

    /**
     * Return the species of this genotype to phenotype rule.<p>
     *
     * @return		Species - species of this genotype to phenotype rule, never null
    **/
    public Species getSpecies()
    {
        return species;
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return species.getWorld();
    }

    /**
     * Return the name of this genotype to phenotype rule.<p>
     *
     * @return		String - name of this genotype to phenotype rule, may not be null
    **/
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the name.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - rule name, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setName(String aName)
    {
        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
        {
            return;
        }

        // Validate input arguments
        if (aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldName = name;
        name = new String(aName);

        // Notify listeners
        changes.firePropertyChange(EngineProp.NAME,oldName,name);
    }

    /**
     * Return the gender of this rule, which will be one
     * of Species.FEMALE_AND_MALE, Species.FEMALE_ONLY
     * or Species.MALE_ONLY.<p>
     *
     * @return		int - gender aspect of this rule
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see			org.concord.biologica.engine.Species#MALE_ONLY
    **/
    public int getGender()
    {
        return gender;
    }

    /**
     * Return the gender of this rule, which will be one
     * of Species.FEMALE_AND_MALE_STRING, Species.FEMALE_ONLY_STRING
     * or Species.MALE_ONLY_STRING.  Returns a string.<p>
     *
     * @return		int - gender aspect of this rule
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE_STRING
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY_STRING
     * @see			org.concord.biologica.engine.Species#MALE_ONLY_STRING
    **/
    public String getGenderAsString()
    {
        if (gender == Species.FEMALE_ONLY)
        {
            return Species.FEMALE_ONLY_STRING;
        }
        else if (gender == Species.MALE_ONLY)
        {
            return Species.MALE_ONLY_STRING;
        }

        // Default to female and male
        return Species.FEMALE_AND_MALE_STRING;
    }

    /**
     * Set the gender of this genotype to phenotype rule.<p>
     *
     * The new gender must be one of Species.FEMALE_AND_MALE,
     * Species.FEMALE_ONLY or Species.MALE_ONLY.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.COLUMN_GENDER.<p>
     *
     * @param		aGender int - the new gender of this rule
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see			org.concord.biologica.engine.Species#MALE_ONLY
    **/
    public void setGender(int aGender)
    {
        // Return immediately if aGender equals the current gender
        if (aGender == gender)
        {
            return;
        }

        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Exception if illegal new gender
        if (aGender != Species.FEMALE_AND_MALE &&
            aGender != Species.FEMALE_ONLY &&
            aGender != Species.MALE_ONLY)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input aGender ok, do it
        int oldGender = gender;
        gender = aGender;

        // Notify listeners
        changes.firePropertyChange(EngineProp.GENDER,
                                   new Integer(oldGender),
                                   new Integer(gender));
    }

    /**
     * Set the gender of this genotype to phenotype rule.<p>
     *
     * The new gender must be one of Species.FEMALE_AND_MALE_STRING,
     * Species.FEMALE_ONLY_STRING or Species.MALE_ONLY_STRING.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.COLUMN_GENDER.<p>
     *
     * @param		aGenderString String - the new gender of this rule as a string
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE_STRING
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY_STRING
     * @see			org.concord.biologica.engine.Species#MALE_ONLY_STRING
    **/
    public void setGenderAsString(String aGenderString)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }
        else if (aGenderString == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Convert gender to an integer
        int aGender;
        if (aGenderString.equals(Species.FEMALE_AND_MALE_STRING))
        {
            aGender = Species.FEMALE_AND_MALE;
        }
        else if (aGenderString.equals(Species.FEMALE_ONLY_STRING))
        {
            aGender = Species.FEMALE_ONLY;
        }
        else if (aGenderString.equals(Species.MALE_ONLY_STRING))
        {
            aGender = Species.MALE_ONLY;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if aGender equals the current gender
        if (aGender == gender)
        {
            return;
        }

        // Exception if illegal new gender
        if (aGender != Species.FEMALE_AND_MALE &&
            aGender != Species.FEMALE_ONLY &&
            aGender != Species.MALE_ONLY)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input aGender ok, do it
        int oldGender = gender;
        gender = aGender;

        // Notify listeners
        changes.firePropertyChange(EngineProp.GENDER,
                                   new Integer(oldGender),
                                   new Integer(gender));
    }

    /**
     * Returns an enumeration over the rule's if species alleles.<p>
     *
     * If there are no if species alleles in this rule, an enumeration with no
     * elements is returned.<p>
     *
     * @return		Enumeration - an enumeration over the vector of if species alleles of this rule, never null
    **/
    public Enumeration getIfSpeciesAlleles()
    {
        if (ifSpeciesAlleles == null)
        {
            Vector dummyVector = new Vector();
            return dummyVector.elements();
        }

        Vector ifSpeciesAllelesClone = (Vector) ifSpeciesAlleles.clone();
        return ifSpeciesAllelesClone.elements();
    }

    /**
     * Return if the if species alleles contains the given species allele
     *
     * @param    anIfSpeciesAllele SpeciesAllele - an if species allele, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public boolean containsIfSpeciesAllele(SpeciesAllele anIfSpeciesAllele)
    {
        if (anIfSpeciesAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        return ifSpeciesAlleles.contains(anIfSpeciesAllele);
    }

    /**
     * Return the number of if species alleles of this rule.<p>
     *
     * @return		int - the number of if species alleles of this rule
    **/
    public int getNumberOfIfSpeciesAlleles()
    {
        return ifSpeciesAlleles.size();
    }

    /**
     * Adds an if species allele to this rule.  Does not check if this allele
     * or an equivalent allele already has been added to this rule.<p>
     *
     * @param		anIfSpeciesAllele SpeciesAllele - the allele to add to this rule, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void addIfSpeciesAllele(SpeciesAllele anIfSpeciesAllele)
    {
        // Do coarse check of input values
        if (anIfSpeciesAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Create ifSpeciesAlleles vector if it's null
        if (ifSpeciesAlleles == null)
        {
            ifSpeciesAlleles = new Vector();
        }

        ifSpeciesAlleles.addElement(anIfSpeciesAllele);

        // Notify listeners
        changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_ADDED,null,anIfSpeciesAllele);
    }

    /**
     * Replace the given old if species allele with the given new if species allele.
     * The key here is that we should maintain the position, so the old and
     * new if species alleles have the same order in this object.<p>
     *
     * If the oldIfSpeciesAllele is null or not found, then we just add the
     * newIfSpeciesAllele on to the end of the list of ifSpeciesAlleles.<p>
     *
     * If the newIfSpeciesAllele is null, then we just remove the oldIfSpeciesAllele.<p>
     *
     * If both the oldIfSpeciesAllele and the newIfSpeciesAllele are null or not found,
     * do nothing.<p>
     *
     * @param		SpeciesAllele oldIfSpeciesAllele - if species allele to remove, may be null
     * @param		SpeciesAllele newIfSpeciesAllele - if species allele to add, may be null
    **/
    public void replaceIfSpeciesAllele(SpeciesAllele oldIfSpeciesAllele,
                                       SpeciesAllele newIfSpeciesAllele)
    {
        int index = -1;

        if (oldIfSpeciesAllele != null)
        {
            index = ifSpeciesAlleles.indexOf(oldIfSpeciesAllele);
        }

        if (oldIfSpeciesAllele != null && index != -1)
        {
            // oldIfSpeciesAllele not null and in ifSpeciesAlleles Vector
            if (newIfSpeciesAllele != null)
            {
                // Replace oldIfSpeciesAllele with newIfSpeciesAllele and
                // fire REMOVED and ADDED events
                ifSpeciesAlleles.setElementAt(newIfSpeciesAllele,index);

                oldIfSpeciesAllele.removePropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_REMOVED, null, oldIfSpeciesAllele);

                newIfSpeciesAllele.addPropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_ADDED, null, newIfSpeciesAllele);
            }
            else
            {
                // newIfSpeciesAllele null, so just remove oldIfSpeciesAllele and fire REMOVED event
                ifSpeciesAlleles.removeElementAt(index);

                oldIfSpeciesAllele.removePropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_REMOVED, null, oldIfSpeciesAllele);
            }
        }
        else
        {
            // oldIfSpeciesAllele null or not in ifSpeciesAlleles Vector
            if (newIfSpeciesAllele != null)
            {
                // Add newIfSpeciesAllele and fire ADDED event
                ifSpeciesAlleles.addElement(newIfSpeciesAllele);

                newIfSpeciesAllele.addPropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_ADDED, null, newIfSpeciesAllele);
            }
        }
    }

    /**
     * Removes an if species allele from this rule.<p>
     *
     * This method should not be called yet, as we haven't determined
     * how objects are destroyed yet in this engine.  There's a good
     * chance that we'll add destroy() methods to all engine classes
     * eventually, which would cause this method to be called. <p>
     *
     * This method returns without doing anything if anIfSpeciesAllele is null.<p>
     *
     * @param		anIfSpeciesAllele SpeciesAllele - the if species allele to remove from this rule, may be null
     * @return		boolean - returns true if element found, false otherwise
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    boolean removeIfSpeciesAllele(SpeciesAllele anIfSpeciesAllele)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }

        // Return immediately if anIfSpeciesAllele or ifSpeciesAlleles null
        if (anIfSpeciesAllele == null || ifSpeciesAlleles == null)
        {
            return false;
        }
        
        boolean result = ifSpeciesAlleles.removeElement(anIfSpeciesAllele);
        
        // Notify listeners if allele removed
        if (result == true)
        {
            anIfSpeciesAllele.removePropertyChangeListener(this);
            changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_REMOVED,null,anIfSpeciesAllele);
        }

        return result;
    }

    /**
     * Return the then characteristic of this genotype to phenotype rule.<p>
     *
     * @return		Characteristic - then characteristic of this genotype to phenotype rule, may be null
    **/
    public Characteristic getThenCharacteristic()
    {
        return thenCharacteristic;
    }
    
    /**
     * Sets the then characteristic of this genotype to phenotype rule.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.THEN_CHARACTERISTIC.<p>
     *
     * @param		aCharacteristic Characteristic - new then characteristic, may be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setThenCharacteristic(Characteristic aCharacteristic)
    {
        // Return immediately if aCharacteristic equals the current thenCharacteristic
        if (aCharacteristic == thenCharacteristic)
        {
            return;
        }

        // Remove property change listener from old thenCharacteristic
        if (thenCharacteristic != null)
        {
            thenCharacteristic.removePropertyChangeListener(this);
        }

        // Switch to new thenCharacteristic
        Characteristic oldThenCharacteristic = thenCharacteristic;
        thenCharacteristic = aCharacteristic;

        // Add property change listener to new thenCharacteristic
        if (thenCharacteristic != null)
        {
            thenCharacteristic.addPropertyChangeListener(this);
        }

        // Notify listeners
        changes.firePropertyChange(EngineProp.THEN_CHARACTERISTIC,oldThenCharacteristic,thenCharacteristic);
    }

    /**
     * Return the else characteristic of this genotype to phenotype rule.<p>
     *
     * @return		Characteristic - else characteristic of this genotype to phenotype rule, may be null
    **/
    public Characteristic getElseCharacteristic()
    {
        return elseCharacteristic;
    }
    
    /**
     * Sets the else characteristic of this genotype to phenotype rule.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.ELSE_CHARACTERISTIC.<p>
     *
     * @param		aCharacteristic Characteristic - new else characteristic, may be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setElseCharacteristic(Characteristic aCharacteristic)
    {
        // Return immediately if aCharacteristic equals the current thenCharacteristic
        if (aCharacteristic == elseCharacteristic)
        {
            return;
        }

        // Remove property change listener from old elseCharacteristic
        if (elseCharacteristic != null)
        {
            elseCharacteristic.removePropertyChangeListener(this);
        }

        // Switch to new elseCharacteristic
        Characteristic oldElseCharacteristic = elseCharacteristic;
        elseCharacteristic = aCharacteristic;

        // Add property change listener to new elseCharacteristic
        if (elseCharacteristic != null)
        {
            elseCharacteristic.addPropertyChangeListener(this);
        }

        // Notify listeners
        changes.firePropertyChange(EngineProp.ELSE_CHARACTERISTIC,oldElseCharacteristic,elseCharacteristic);
    }

    /**
     * Test this rule given a sex, a set of species alleles and generating
     * zero or one characteristic, which is placed on the given
     * characteristics vector and its trait put on the traits
     * UNLESS there is already another characteristic of that trait
     * on the traits vector.  This last bit prevents 2 characteristics
     * of the same trait being put on the characteristics vector,
     * meaning that rules should be evaluated in priority order (highest
     * to lowest) to get the proper characteristics on the vector.<p>
     *
     * @param       sex int - sex of organism - Organism.MALE, Organism.FEMALE or Organism.NO_SEX
     * @param		speciesAlleles Vector - species alleles vector
     * @param		characteristics Vector - characteristics vector
     * @param		traits Vector - traits vector
    **/
    public void test(int sex, Vector speciesAlleles, Vector characteristics, Vector traits)
    {
        // Return immediately if either input vector is null
        if ((sex != Organism.MALE && sex != Organism.FEMALE && sex != Organism.NO_SEX) ||
            speciesAlleles == null || characteristics == null || traits == null)
        {
            return;
        }

        // Test if the sex of the organism and the gender of this rule are compatible
        if ((gender == Species.FEMALE_ONLY && sex == Organism.MALE) ||
            (gender == Species.MALE_ONLY && sex == Organism.FEMALE))
        {
            // Not compatible, so return
            return;
        }

        int i,j;

        // Default to the thenCharacteristic as the result
        Characteristic resultCharacteristic = thenCharacteristic;

        // Create an array of the indices in the speciesAlleles vector
        // used to satisfy this rule.  This is critical to making sure
        // that a single SpeciesAllele in the speciesAllele vector is
        // used to satisfy only one ifSpeciesAllele in this rule.
        int usedIndices[] = new int[ifSpeciesAlleles.size()];
        for (i=0;i<usedIndices.length;i++)
        {
            // Make them all -2 initially, a sort of null as -2 is an invalid value
            usedIndices[i] = -2;
        }

        // Loop through the ifSpeciesAlleles.  For each ifSpeciesAllele,
        // try to find it on the speciesAllele vector.  Avoid using the
        // same speciesAllele multiple times by tracking which indices
        // of the speciesAllele vector have been used to satisfy previous
        // rule ifSpeciesAlleles.
        i = 0;
        int indexMatch;
        SpeciesAllele ifSpeciesAllele, aSpeciesAllele;
        Enumeration eIfSpeciesAlleles = ifSpeciesAlleles.elements();
        while (eIfSpeciesAlleles.hasMoreElements())
        {
            ifSpeciesAllele = (SpeciesAllele) eIfSpeciesAlleles.nextElement();

            indexMatch = speciesAlleles.indexOf(ifSpeciesAllele);
            if (indexMatch != -1)
            {
                // So species allele is in vector.  But if we've already used
                // this index to match another if species allele, then we must
                // find another index / speciesAllele.
                for (j=0;j<usedIndices.length;j++)
                {
                    if (indexMatch == usedIndices[j])
                    {
                        // Index already used, so try to find another one
                        indexMatch = speciesAlleles.indexOf(ifSpeciesAllele,indexMatch+1);
                        if (indexMatch == -1)
                        {
                            // Ran out of matches, so we've failed to find a match
                            break;
                        }
                    }
                }
            }

            // Done looking for a match, see if we succeeded or failed
            if (indexMatch == -1)
            {
                // Failed to find match, so result is the else clause and break
                resultCharacteristic = elseCharacteristic;
                break;
            }
            else
            {
                // Succeeded in finding match, so record used index and go on to
                // the next ifSpeciesAllele
                usedIndices[i] = indexMatch;
                i++;
            }
        }

        // Done looking for matches and we have a resultCharacteristic which
        // is either the thenCharacteristic or the elseCharacteristic.  So now
        // determine if the resultCharacteristic's trait is already on the
        // traits vector.  If not, add the resultCharacteristic to the characteristics
        // vector and add its trait to the traits vector.  If it is, then return
        // without doing anything, as we don't want to add two characteristics of
        // the same trait to the list of characteristics.
        if (resultCharacteristic != null)
        {
            Trait resultTrait = resultCharacteristic.getTrait();
            if (traits.contains(resultTrait))
            {
                // A characteristic for this trait already is on list,
                // so do nothing and return.
                return;
            }

            // Add resultCharacteristic to characteristics vector and
            // add its trait to the traits vector
            characteristics.addElement(resultCharacteristic);
            traits.addElement(resultTrait);
        }
    }

    /**
     * Handle property change events, mostly DELETED events.<p>
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.DELETED))
        {
            // A characteristic or species allele is about to be deleted,
            // so remove any references to it from this object.

            Object object = event.getSource();

            if (object instanceof Characteristic)
            {
                Characteristic characteristic = (Characteristic) object;

                if (characteristic != null &&
                    thenCharacteristic != null &&
                    characteristic == thenCharacteristic)
                {
                    thenCharacteristic.removePropertyChangeListener(this);
                    thenCharacteristic = null;
                    changes.firePropertyChange(EngineProp.THEN_CHARACTERISTIC, null, characteristic);
                }

                if (characteristic != null &&
                    elseCharacteristic != null &&
                    characteristic == elseCharacteristic)
                {
                    elseCharacteristic.removePropertyChangeListener(this);
                    elseCharacteristic = null;
                    changes.firePropertyChange(EngineProp.ELSE_CHARACTERISTIC, null, characteristic);
                }
            }
            else if (object instanceof SpeciesAllele)
            {
                // Remove species allele from our ifSpeciesAllele vector
                SpeciesAllele speciesAllele = (SpeciesAllele) object;

                boolean result = ifSpeciesAlleles.removeElement(speciesAllele);

                if (result == true)
                {
                    speciesAllele.removePropertyChangeListener(this);
                    changes.firePropertyChange(EngineProp.IF_SPECIES_ALLELE_REMOVED, null, speciesAllele);
                }
            }
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
        stream.println("<" + Elements.GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Name
        if (name == null)
        {
            stream.println("<" + Elements.NAME_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.NAME_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.NAME_ELEMENT_NAME + ">" + name +
                              "</" + Elements.NAME_ELEMENT_NAME + ">");
        }

        // Species ID
        if (species == null)
        {
            stream.println("<" + Elements.SPECIES_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SPECIES_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SPECIES_ID_ELEMENT_NAME + ">" + species.getID() +
                              "</" + Elements.SPECIES_ID_ELEMENT_NAME + ">");
        }

        // Gender
        stream.println("<" + Elements.GENDER_ELEMENT_NAME + ">" +
                          getGenderAsString() +
                          "</" + Elements.GENDER_ELEMENT_NAME + ">");

        // If species allele id list
        {
            stream.print("<" + Elements.IF_SPECIES_ALLELE_IDS_ELEMENT_NAME + ">");

            SpeciesAllele ifSpeciesAllele;
            Enumeration eIfSpeciesAlleles = ifSpeciesAlleles.elements();
            while (eIfSpeciesAlleles.hasMoreElements())
            {
                ifSpeciesAllele = (SpeciesAllele) eIfSpeciesAlleles.nextElement();
                stream.print(ifSpeciesAllele.getID() + ",");
            }

            stream.println("</" + Elements.IF_SPECIES_ALLELE_IDS_ELEMENT_NAME + ">");
        }

        // Then Characteristic ID
        if (thenCharacteristic == null)
        {
            stream.println("<" + Elements.THEN_CHARACTERISTIC_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.THEN_CHARACTERISTIC_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.THEN_CHARACTERISTIC_ID_ELEMENT_NAME + ">" + thenCharacteristic.getID() +
                              "</" + Elements.THEN_CHARACTERISTIC_ID_ELEMENT_NAME + ">");
        }

        // Else Characteristic ID
        if (elseCharacteristic == null)
        {
            stream.println("<" + Elements.ELSE_CHARACTERISTIC_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.ELSE_CHARACTERISTIC_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.ELSE_CHARACTERISTIC_ID_ELEMENT_NAME + ">" + elseCharacteristic.getID() +
                              "</" + Elements.ELSE_CHARACTERISTIC_ID_ELEMENT_NAME + ">");
        }

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_NAME + ">");
    }
}
