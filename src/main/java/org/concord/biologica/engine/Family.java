//
// Class : Family
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.6 $
// $Date: 2003/02/10 14:38:51 $
// $Author: qliao $
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

import java.util.Enumeration;
import java.util.Random;
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
 * This class represents a family of diploid organisms consisting of a female
 * parent organism, a male parent organism and a set of child organisms.<p>
 *
 * An instance of this class is contained by one world.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.CHILD_ADDED - child added to this family
 * <li> EngineProp.CHILD_REMOVED - child removed from this family
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#CHILD_ADDED
 * @see org.concord.biologica.engine.EngineProp#CHILD_REMOVED
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.6 $ $Date: 2003/02/10 14:38:51 $
 * @author 		$Author: qliao $
**/

public final class Family
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Random number generator used in creating male and female children.<p>
    **/
    static private Random random = new Random();

    /**
     * World containing this family.  May not be null.<p>
    **/
    private World				world;

    /**
     * The female parent organism of this family.  May not be null and may not change over time.<p>
    **/
    private Organism			femaleParent;

    /**
     * The male parent organism of this family.  May not be null and may not change over time.<p>
    **/
    private Organism			maleParent;

    /**
     * The child organisms of this family.  May not be null but may be empty and may change over time.<p>
    **/
    private	Vector				children;

    /**
     * Generation (e.g. F1, F2)
    **/
    private int					generation;
    
    /**
     * Create a family from a given set of 2 parents and optionally some children.<p>
     *
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aChildrenVector Vector - a vector of children organisms, may be null or empty
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Family(Organism aParentOrganismOne, Organism aParentOrganismTwo, Vector aChildrenVector)
    {
        // Check input arguments
        if (aParentOrganismOne == null || aParentOrganismTwo == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int sexParentOne = aParentOrganismOne.getSex();
        int sexParentTwo = aParentOrganismTwo.getSex();

        if (sexParentOne == Organism.MALE &&
            sexParentTwo == Organism.FEMALE)
        {
            maleParent = aParentOrganismOne;
            femaleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.FEMALE &&
                 sexParentTwo == Organism.MALE)
        {
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.NO_SEX &&
                 sexParentTwo == Organism.NO_SEX)
        {
            // diploid species with no sex chromosomes
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        world = femaleParent.getWorld();
        if (world == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        children = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Determine generation
        int femaleParentGeneration = femaleParent.getGeneration();
        int maleParentGeneration = maleParent.getGeneration();
        if (femaleParentGeneration > maleParentGeneration)
        {
            generation = femaleParentGeneration + 1;
        }
        else
        {
            generation = maleParentGeneration + 1;
        }

        // Copy children from aChildrenVector if there are any
        if (aChildrenVector != null)
        {
            Organism aChild;
            Enumeration eChildren = aChildrenVector.elements();
            while (eChildren.hasMoreElements())
            {
                aChild = (Organism) eChildren.nextElement();
                if (aChild != null)
                {
                    children.addElement(aChild);
                    aChild.setParentFamily(this);
                }
            }
        }

        // Add this family to the world and parents
        femaleParent.addChildFamily(this);
        maleParent.addChildFamily(this);
        world.addFamily(this);
    }

    /**
     * Create a family from a given set of 2 parents and one child.  This is a convenience method equivalent to the
     * above constructor.  This constructor just takes a single child, making it easier for the meiosis / fertilization
     * code to create a family.<p>
     *
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aChildOrganism Organism - a child organism, may be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Family(Organism aParentOrganismOne, Organism aParentOrganismTwo, Organism aChildOrganism)
    {
        // Check input arguments
        if (aParentOrganismOne == null || aParentOrganismTwo == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int sexParentOne = aParentOrganismOne.getSex();
        int sexParentTwo = aParentOrganismTwo.getSex();

        if (sexParentOne == Organism.MALE &&
            sexParentTwo == Organism.FEMALE)
        {
            maleParent = aParentOrganismOne;
            femaleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.FEMALE &&
                 sexParentTwo == Organism.MALE)
        {
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.NO_SEX &&
                 sexParentTwo == Organism.NO_SEX)
        {
            // diploid species with no sex chromosomes
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        world = femaleParent.getWorld();
        if (world == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Determine generation
        int femaleParentGeneration = femaleParent.getGeneration();
        int maleParentGeneration = maleParent.getGeneration();
        if (femaleParentGeneration > maleParentGeneration)
        {
            generation = femaleParentGeneration + 1;
        }
        else
        {
            generation = maleParentGeneration + 1;
        }

        // Input arguments OK, initialize instance variables
        children = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Add child to children vector if not null
        if (aChildOrganism != null)
        {
            children.addElement(aChildOrganism);
            aChildOrganism.setParentFamily(this);
        }

        // Add this family to the world and parents
        femaleParent.addChildFamily(this);
        maleParent.addChildFamily(this);
        world.addFamily(this);
    }

    /**
     * Create a family from a given set of 2 parents, creating the number of children specified
     * and also respecting whether or not the children should all be alive.<p>
     *
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aNumberOfChildren int - the number of children to be created
     * @param       onlyLiveChildren boolean - only create live children
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Family(Organism aParentOrganismOne, Organism aParentOrganismTwo,
                int aNumberOfChildren, boolean onlyLiveChildren)
    {
        createFamilyFixedNumberOfChildren(aParentOrganismOne,aParentOrganismTwo,aNumberOfChildren,onlyLiveChildren);
    }

    /**
     * Create a family from a given set of 2 parents, creating the number of children specified.
     * Children will NOT be limited to alive.<p>
     *
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aNumberOfChildren int - the number of children to be created
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Family(Organism aParentOrganismOne, Organism aParentOrganismTwo, int aNumberOfChildren)
    {
        createFamilyFixedNumberOfChildren(aParentOrganismOne,aParentOrganismTwo,aNumberOfChildren,false);
    }

    /**
     * Private utility method to create a family with a set number of children.<p>
     *	 
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aNumberOfChildren int - the number of children to be created
     * @param       onlyLiveChildren boolean - only create live children
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    private void createFamilyFixedNumberOfChildren(Organism aParentOrganismOne, Organism aParentOrganismTwo,
                                                 int aNumberOfChildren, boolean onlyLiveChildren)
    {
        // Check input arguments
        if (aParentOrganismOne == null || aParentOrganismTwo == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int sexParentOne = aParentOrganismOne.getSex();
        int sexParentTwo = aParentOrganismTwo.getSex();

        if (sexParentOne == Organism.MALE &&
            sexParentTwo == Organism.FEMALE)
        {
            maleParent = aParentOrganismOne;
            femaleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.FEMALE &&
                 sexParentTwo == Organism.MALE)
        {
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.NO_SEX &&
                 sexParentTwo == Organism.NO_SEX)
        {
            // diploid species with no sex chromosomes
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        world = femaleParent.getWorld();
        if (world == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Determine generation
        int femaleParentGeneration = femaleParent.getGeneration();
        int maleParentGeneration = maleParent.getGeneration();
        if (femaleParentGeneration > maleParentGeneration)
        {
            generation = femaleParentGeneration + 1;
        }
        else
        {
            generation = maleParentGeneration + 1;
        }

        // Input arguments OK, initialize instance variables
        children = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Create children
        int i;
        Organism aChildOrganism;
        for (i=0;i<aNumberOfChildren;i++)
        {
            aChildOrganism = new Organism(femaleParent,maleParent,null);
            if (onlyLiveChildren)
            {
                while (aChildOrganism.containsFatalCharacteristic())
                {
                    aChildOrganism.delete();
                    aChildOrganism = new Organism(femaleParent,maleParent,null);
                }
            }
            children.addElement(aChildOrganism);
            aChildOrganism.setParentFamily(this);
        }

        // Add this family to the world and parents
        femaleParent.addChildFamily(this);
        maleParent.addChildFamily(this);
        world.addFamily(this);
    }

    /**
     * Get a random index inclusively between the low and high numbers.
    **/
    private int getRandomIndex(int low, int high)
    {
        if (low == high)
        {
            return low;
        }

        float aFloat = random.nextFloat();
        float range = (float) high-low;

        int index = (int) (range * aFloat);

        return index;
    }

    /**
     * Create a family from a given set of 2 parents, creating the number of female and male children specified.<p>
     *
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aNumberOfFemaleChildren int - the number of female children to be created
     * @param		aNumberOfMaleChildren int - the number of male children to be created
     * @param       onlyLiveChildren boolean - only create live children
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Family(Organism aParentOrganismOne, Organism aParentOrganismTwo,
                  int aNumberOfFemaleChildren, int aNumberOfMaleChildren, boolean onlyLiveChildren)
    {
        createFamilyMaleFemaleChildren(aParentOrganismOne,aParentOrganismTwo,
                                      aNumberOfFemaleChildren,aNumberOfMaleChildren,
                                      onlyLiveChildren);
    }

    /**
     * Create a family from a given set of 2 parents, creating the number of female and male children specified.
     * Children will NOT be limited to alive.<p>
     *
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aNumberOfFemaleChildren int - the number of female children to be created
     * @param		aNumberOfMaleChildren int - the number of male children to be created
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Family(Organism aParentOrganismOne, Organism aParentOrganismTwo,
                  int aNumberOfFemaleChildren, int aNumberOfMaleChildren)
    {
        createFamilyMaleFemaleChildren(aParentOrganismOne,aParentOrganismTwo,
                                      aNumberOfFemaleChildren,aNumberOfMaleChildren,false);
    }

    /**
     * Private utility method to create a family with a set number of children.<p>
     *	 
     * @param		aParentOrganismOne Organism - a parent organism, can be male or female, may not be null or NO_SEX
     * @param		aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne, may not be null or NO_SEX
     * @param		aNumberOfFemaleChildren int - the number of female children to be created
     * @param		aNumberOfMaleChildren int - the number of male children to be created
     * @param       onlyLiveChildren boolean - only create live children
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    private void createFamilyMaleFemaleChildren(Organism aParentOrganismOne, Organism aParentOrganismTwo,
                                              int aNumberOfFemaleChildren, int aNumberOfMaleChildren,
                                              boolean onlyLiveChildren)
    {
        // Check input arguments
        if (aParentOrganismOne == null || aParentOrganismTwo == null ||
            aNumberOfFemaleChildren < 0 || aNumberOfMaleChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int sexParentOne = aParentOrganismOne.getSex();
        int sexParentTwo = aParentOrganismTwo.getSex();

        if (sexParentOne == Organism.MALE &&
            sexParentTwo == Organism.FEMALE)
        {
            maleParent = aParentOrganismOne;
            femaleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.FEMALE &&
                 sexParentTwo == Organism.MALE)
        {
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else if (sexParentOne == Organism.NO_SEX &&
                 sexParentTwo == Organism.NO_SEX)
        {
            // diploid species with no sex chromosomes
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        world = femaleParent.getWorld();
        if (world == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        Species species = femaleParent.getSpecies();

        // Determine generation
        int femaleParentGeneration = femaleParent.getGeneration();
        int maleParentGeneration = maleParent.getGeneration();
        if (femaleParentGeneration > maleParentGeneration)
        {
            generation = femaleParentGeneration + 1;
        }
        else
        {
            generation = maleParentGeneration + 1;
        }

        // Input arguments OK, initialize instance variables
        children = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Create children
        int i, index;
        Organism aChildOrganism;
        int numberOfChildren = aNumberOfFemaleChildren + aNumberOfMaleChildren;
        Organism childrenArray[] = new Organism[numberOfChildren];
        for (i=0;i<numberOfChildren;i++)
        {
            childrenArray[i] = null;
        }

        // Create females first, putting them into random locations in an array
        for (i=0;i<aNumberOfFemaleChildren;i++)
        {
            aChildOrganism = new Organism(femaleParent,maleParent,null,Organism.FEMALE);
            if (onlyLiveChildren)
            {
                while (aChildOrganism.containsFatalCharacteristic())
                {
                    aChildOrganism.delete();
                    aChildOrganism = new Organism(femaleParent,maleParent,null);
                }
            }
            while (aChildOrganism != null)
            {
                index = getRandomIndex(0,numberOfChildren-1);
                if (childrenArray[index] == null)
                {
                    childrenArray[index] = aChildOrganism;
                    aChildOrganism = null;
                }
            }
        }

        // Put males in empty locations in array
        for (i=0;i<numberOfChildren;i++)
        {
            if (childrenArray[i] == null)
            {
                childrenArray[i] = new Organism(femaleParent,maleParent,null,Organism.MALE);
                if (onlyLiveChildren)
                {
                    while (childrenArray[i].containsFatalCharacteristic())
                    {
                        childrenArray[i].delete();
                        childrenArray[i] = new Organism(femaleParent,maleParent,null);
                    }
                }
            }
        }

        // Loop through array actually adding children to family
        for (i=0;i<numberOfChildren;i++)
        {
            children.addElement(childrenArray[i]);
            childrenArray[i].setParentFamily(this);
        }

        // Add this family to the world and parents
        femaleParent.addChildFamily(this);
        maleParent.addChildFamily(this);
        world.addFamily(this);
    }
    

    // These two methods should be used from original code that they
    // were copied and pasted from. 
    public Vector addChildren(int aNumberOfChildren, boolean onlyLiveChildren)
    {
        Vector retChildren = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Create children
        int i;
        Organism aChildOrganism;
        for (i=0;i<aNumberOfChildren;i++)
        {
            aChildOrganism = new Organism(getFemaleParent(),getMaleParent(),null);
            if (onlyLiveChildren)
            {
                while (aChildOrganism.containsFatalCharacteristic())
                {
                    aChildOrganism.delete();
                    aChildOrganism = new Organism(getFemaleParent(),getMaleParent(),null);
                }
            }
            children.addElement(aChildOrganism);
            aChildOrganism.setParentFamily(this);

            retChildren.addElement(aChildOrganism);
        }
        return retChildren;
    }
    
    public Vector addChildren(int aNumberOfFemaleChildren, int aNumberOfMaleChildren, boolean onlyLiveChildren)
    {
        Vector retChildren = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Create children
        int i, index;
        Organism aChildOrganism;
        int numberOfChildren = aNumberOfFemaleChildren + aNumberOfMaleChildren;
        Organism childrenArray[] = new Organism[numberOfChildren];
        for (i=0;i<numberOfChildren;i++)
        {
            childrenArray[i] = null;
        }

        // Create females first, putting them into random locations in an array
        for (i=0;i<aNumberOfFemaleChildren;i++)
        {
            aChildOrganism = new Organism(getFemaleParent(),maleParent,null,Organism.FEMALE);
            if (onlyLiveChildren)
            {
                while (aChildOrganism.containsFatalCharacteristic())
                {
                    aChildOrganism.delete();
                    aChildOrganism = new Organism(getFemaleParent(),getMaleParent(),null);
                }
            }
            while (aChildOrganism != null)
            {
                index = getRandomIndex(0,numberOfChildren-1);
                if (childrenArray[index] == null)
                {
                    childrenArray[index] = aChildOrganism;
                    aChildOrganism = null;
                }
            }
        }

        // Put males in empty locations in array
        for (i=0;i<numberOfChildren;i++)
        {
            if (childrenArray[i] == null)
            {
                childrenArray[i] = new Organism(getFemaleParent(),getMaleParent(),null,Organism.MALE);
                if (onlyLiveChildren)
                {
                    while (childrenArray[i].containsFatalCharacteristic())
                    {
                        childrenArray[i].delete();
                        childrenArray[i] = new Organism(getFemaleParent(),getMaleParent(),null);
                    }
                }
            }
        }

        // Loop through array actually adding children to family
        for (i=0;i<numberOfChildren;i++)
        {
            children.addElement(childrenArray[i]);
            childrenArray[i].setParentFamily(this);
            
            retChildren.addElement(childrenArray[i]);
        }

        return retChildren;
    }
    
    
    
    
    
    
    

    /**
     * Create a new family and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aWorld World - the enclosing world for this new species
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public Family(World aWorld,
                  String anElementName,
                  int anElementID,
                  /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                  ImportContext importContext)
    {
        if (aWorld == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        maleParent = null;
        femaleParent = null;
        children = new Vector();
        generation = 0;

        // Create vectors
        children = new Vector();

        // Creation successful, so add this organism to world
        world = aWorld;
        world.addFamily(this);

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
            case Elements.FEMALE_PARENT_ID_ELEMENT_ID:
            case Elements.MALE_PARENT_ID_ELEMENT_ID:
            case Elements.WORLD_ID_ELEMENT_ID:
            case Elements.CHILD_IDS_ELEMENT_ID:
            case Elements.GENERATION_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("Family " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int parentID;

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

            case Elements.WORLD_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // ignore as we already know the world
                break;

            case Elements.GENERATION_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                generation = Integer.valueOf(valueString).intValue();
                break;

            case Elements.FEMALE_PARENT_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                parentID = Integer.valueOf(valueString).intValue();
                if (parentID != EngineObject.NULL_ID)
                {
                    femaleParent = (Organism) xmlElementContext.getImportContext().getObject(parentID);
                    femaleParent.addChildFamily(this);
                }
                else
                {
                    femaleParent = null;
                }
                break;

            case Elements.MALE_PARENT_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                parentID = Integer.valueOf(valueString).intValue();
                if (parentID != EngineObject.NULL_ID)
                {
                    maleParent = (Organism) xmlElementContext.getImportContext().getObject(parentID);
                    maleParent.addChildFamily(this);
                }
                else
                {
                    maleParent = null;
                }
                break;

            case Elements.CHILD_IDS_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    int childId;
                    String childIdString;
                    Organism aChild;
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    while (parser.hasMoreTokens())
                    {
                        childIdString = parser.nextToken();
                        childId = Integer.valueOf(childIdString).intValue();
                        aChild = (Organism) xmlElementContext.getImportContext().getObject(childId);
                        children.addElement(aChild);
                        aChild.setParentFamily(this);
                    }
                }
                break;

            case Elements.FAMILY_ELEMENT_ID:
                // Done with this family, so pop up to enclosing world
                world.endElement(anElementName);
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
     * When this method is called, a property change event is
     * generated for the property named EngineProp.DELETED.<p>
    **/
    public void delete()
    {
        delete(true, true);
    }
    
    public void delete(boolean notifyChange)
    {
        delete(notifyChange, true);
    }
    
    public void delete(boolean notifyChange, boolean deleteChildren)
    {
        // Avoid double deletions gracefully
        if (deleted == true)
        {
            return;
        }

        // Unlock the object as we're about to delete it
        if (isLocked() == true)
        {
            setLockedState(EngineObject.UNLOCKED);
        }

        deleted = true;

        if (notifyChange)
        {
            // Notify listeners before deleting
            changes.firePropertyChange(EngineProp.DELETED,FALSE,TRUE);
        }

        // Delete children
        if (deleteChildren && (children != null))
        {
            Organism aChild;
            Vector childrenClone = (Vector) children.clone();
        
            Enumeration eChildren = childrenClone.elements();
           //Enumeration eChildren = children.elements();
            while (eChildren.hasMoreElements())
            {
                aChild = (Organism) eChildren.nextElement();
                aChild.delete(notifyChange);
                aChild = null;
            }
            children.removeAllElements();
            children = null;
            childrenClone = null;
        }

        // Remove reference to this family in female parent
        if (femaleParent != null)
        {
            femaleParent.removeChildFamily(this);
            
            femaleParent = null;
        }

        // Remove reference to this family in male parent
        if (maleParent != null)
        {
        	
            maleParent.removeChildFamily(this);
            maleParent = null;
        }

        // Tell world
        world.removeFamily(this);
        world = null;
        id = EngineObject.NULL_ID;
    }
    

    /**
     * Set or unset the automatic locked state of this object, leaving
     * other components of the locked state untouched.  Recursively
     * sets the automatic locked state of children of this object.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * @param		automaticLocked boolean - object should be automatic locked (true) or not (false)
    **/
    public void setAutomaticLocked(boolean automaticLocked)
    {
        // Use EngineObject implementation for changing the state of this object
        super.setAutomaticLocked(automaticLocked);

        // Set automatic locked state of children
        if (children != null)
        {
            Organism aChild;
            Enumeration eChildren = children.elements();
            while (eChildren.hasMoreElements())
            {
                aChild = (Organism) eChildren.nextElement();
                aChild.setAutomaticLocked(automaticLocked);
            }
        }
    }

    /**
     * Set or unset the manual locked state of this object, leaving
     * other components of the locked state untouched.  Recursively
     * sets the manual locked state of children of this object.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     *
     * @param		manualLocked boolean - object should be manually locked (true) or not (false)
    **/
    public void setManualLocked(boolean manualLocked)
    {
        // Use EngineObject implementation for changing the state of this object
        super.setManualLocked(manualLocked);

        // Set manual locked state of children
        if (children != null)
        {
            Organism aChild;
            Enumeration eChildren = children.elements();
            while (eChildren.hasMoreElements())
            {
                aChild = (Organism) eChildren.nextElement();
                aChild.setManualLocked(manualLocked);
            }
        }
    }

    /**
     * Set the locked state of the object, recursively setting the locked
     * state of children objects.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * @param	aLockedState int - new locked state of this object
     * @exception	IllegalArgumentException - new locked state invalid
    **/
    public void setLockedState(int aLockedState)
    {
        // Use EngineObject implementation for changing the locked state of this object
        super.setLockedState(aLockedState);
            
        // Set locked state of children
        if (children != null)
        {
            Organism aChild;
            Enumeration eChildren = children.elements();
            while (eChildren.hasMoreElements())
            {
                aChild = (Organism) eChildren.nextElement();
                aChild.setLockedState(lockedState);
            }
        }
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return EngineStrings.FAMILY_COLON + String.valueOf(id);
    }

    /**
     * Returns the female parent organism.<p>
     *
     * @return		Organism - female parent organism, never null
    **/
    public Organism getFemaleParent()
    {
        return femaleParent;
    }

    /**
     * Returns the male parent organism.<p>
     *
     * @return		Organism - male parent organism, never null
    **/
    public Organism getMaleParent()
    {
        return maleParent;
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return world;
    }

    /**
     * Returns an enumeration over the children organisms of this family.<p>
     *
     * If there are no children in this family, an enumeration with no elements is returned.<p>
     *
     * If there are children in this family, the vector of children is cloned and an
     * enumeration over that clone is returned.  This enables you to safely modify
     * the family and its children while using the enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the vector of children of this family, never null
    **/
    public Enumeration getChildren()
    {
        if (children == null)
        {
            Vector dummyVector = new Vector();
            return dummyVector.elements();
        }

        Vector childrenClone = (Vector) children.clone();
        return childrenClone.elements();
    }
    
    /**
     * Get the generation of this family.<p>
     *
     * @param		int - generation of this family (e.g. 1, 2, 3 as in F1, F2, F3)
    **/
    public int getGeneration()
    {
        return generation;
    }

    /*
     * Tests whether a given organism is one of the children
     *
    **/
    
    public int getCharacteristicCount(String aCharacteristicName)
    {
        int count = 0;
        Enumeration eChildren = getChildren();
        while (eChildren.hasMoreElements())
        {
            Organism organism = (Organism) eChildren.nextElement();
            if (organism.containsCharacteristic(aCharacteristicName))
            {
                count = count + 1;
            }
        }
        return count;
    }
    
    public boolean isChild(Organism organism)
    {
        if (children == null)
        {
            return false;
        }

        return children.contains(organism);
    }

    /**
     * Return the number of children of this family.<p>
     *
     * @return		int - the number of children of this family
    **/
    public int getNumberOfChildren()
    {
        if (children == null)
        {
            return 0;
        }
        else
        {
            return children.size();
        }
    }

    /**
     * Adds a child organism to this fammmily.  Does not check if this child
     * or an equivalent child already has been added to this family.<p>
     *
     * @param		aChild Organism - the child organism to add to this family, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void addChild(Organism aChild)
    {
        // Do coarse check of input values
        if (aChild == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Create children vector if it's null
        if (children == null)
        {
            children = new Vector();
        }

        children.addElement(aChild);

        // Notify listeners
        changes.firePropertyChange(EngineProp.CHILD_ADDED,null,aChild);
    }

    /**
     * Removes a child organism from this family.<p>
     *
     * This method returns without doing anything if aChild is null.<p>
     *
     * @param		aChild Organism - the child organism to remove from this family, may be null
     * @return		boolean - returns true if element found, false otherwise
    **/
    public boolean removeChild(Organism aChild)
    {
        // Return immediately if aChild or children null
        if (aChild == null || children == null)
        {
            return false;
        }
        
        boolean result = children.removeElement(aChild);
        
        // Notify listeners if allele removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.CHILD_REMOVED,null,aChild);
        }

        // If there are no children left, delete this Family
        if (deleted == false && getNumberOfChildren() == 0)
        {
            delete();
        }

        return result;
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
        stream.println("<" + Elements.FAMILY_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Female parent ID
        stream.println("<" + Elements.FEMALE_PARENT_ID_ELEMENT_NAME + ">" + femaleParent.getID() +
                          "</" + Elements.FEMALE_PARENT_ID_ELEMENT_NAME + ">");

        // Male parent ID
        stream.println("<" + Elements.MALE_PARENT_ID_ELEMENT_NAME + ">" + maleParent.getID() +
                          "</" + Elements.MALE_PARENT_ID_ELEMENT_NAME + ">");

        // World ID
        stream.println("<" + Elements.WORLD_ID_ELEMENT_NAME + ">" + world.getID() +
                          "</" + Elements.WORLD_ID_ELEMENT_NAME + ">");

        // Child ID's
        if (children != null)
        {
            stream.print("<" + Elements.CHILD_IDS_ELEMENT_NAME + ">");

            Organism aChild;
            Enumeration eChildren = children.elements();
            while (eChildren.hasMoreElements())
            {
                aChild = (Organism) eChildren.nextElement();
                stream.print(aChild.getID() + ",");
            }

            stream.println("</" + Elements.CHILD_IDS_ELEMENT_NAME + ">");
        }

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Generation
        stream.println("<" + Elements.GENERATION_ELEMENT_NAME + ">" + generation +
                          "</" + Elements.GENERATION_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.FAMILY_ELEMENT_NAME + ">");
    }
}

