//
//Class : VirtualFamily
//
//Copyright © 2004, The Concord Consortium
//
//Original Author: Shengyao Wang
//
//$Revision: 1.3 $
//$Date: 2004/05/17 18:59:31 $
//$Author: swang $
//
//
package org.concord.biologica.engine;
import java.lang.IllegalArgumentException;
import java.lang.Integer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class VirtualFamily
{
    /**
     * Female or NO_SEX parent. May not be null. <p>
     * 
     * Should be either different from male or NO_SEX.
     */
    private Organism femaleParent;

    /**
     * Male or NO_SEX parent. May not be null. <p>
     * 
     * Should be either different from female or NO_SEX.
     */
    private Organism maleParent;

    /**
     * Number of children to be created in the family. May not be less than zero. <p>
     */
    private int numberOfChildren;
    
    /**     * Number of dead offsprings     */    private int deadNumber;        /**     * Number of female offsprings, if sex is considered.     */    private int femaleOffspringNumber;    /**     * Number of male offsprings, if sex is considered.     */    private int maleOffspringNumber;        /**     * World of the family. May not be null.     */    private World world;    /**     * Species of the family. May not be null.     */    private Species species;        /**     * Vector to store traits.     */    private Vector traits;    /**     * Array of vectors.<p>     *      * Each vector stores characteristics of specific trait for female or male organism.     */    private Vector femaleCharacteristicsVector[];    private Vector maleCharacteristicsVector[];    private Vector characteristicsVector[];        /**     * Array of vectors.<p>     *      * Each vector stores count or percentage of characteristics of specific trait for all the offsprings.     */    private Vector femaleCharacteristicsNumberVector[];    private Vector maleCharacteristicsNumberVector[];    private Vector characteristicsNumberVector[];        /**     * Hashtable to store count of charcteristics of the family. <p>     *      * Keys are characteristics, e.g. "No Legs", "Two Legs", etc. <p>     * Value is count of that characteristics.     */    private Hashtable femaleCharacteristicsHashtable;    private Hashtable maleCharacteristicsHashtable;    private Hashtable characteristicsHashtable;        /**     * Seperate offsprings by sex?     */    private boolean seperateSex;        /**     * Is setCharacteristicsHashtable() finished?     */    private boolean createOffspringDone;        /**     * Task progress, number of created offsprings.     */    private int taskProgress = 0;
    
    private boolean isCancelled = false;    
    /**     * Creates a VirtualFamily object. <p>
     * If this constructure is used, one of the two sets of methods must be called to create VirtualFamily object: <p>
     * 1. setParents(), setNumberOfChildren(), and setSeperateSex()<p>
     * or<p>
     * 2. setFemaleParent(), setMaleParent(), setNumberOfChildren(), and setSeperateSex()     */    public VirtualFamily()    {    		createOffspringDone = false;    }        /**     * Creates a VirtualFamily object. <p>
     * aParentOrganismOne and aParentOrganismTwo must be either different sexes or NO_SEX. <p>
     * 
     * @param aParentOrganismOne Organism - a parent organism, can be male, female or NO_SEX, may not be null.
     * @param aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne or NO_SEX, may not be null.
     * @param aNumberOfChildren int - the number of children to be created
     * @param aSeperateSex boolean - seperate offsprings by sex?     */    public VirtualFamily(Organism aParentOrganismOne, Organism aParentOrganismTwo, int aNumberOfChildren, boolean aSeperateSex)     {		checkInput(aParentOrganismOne,aParentOrganismTwo,aNumberOfChildren, aSeperateSex);
		createOffspringDone = false;    }        /**     * Private utility method to check input.<p>
     * 
     * @param     aParentOrganismOne Organism - a parent organism, can be male, female or NO_SEX, may not be null.
     * @param     aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne or NO_SEX, may not be null
     * @param     aNumberOfChildren int - the number of children to be created
     * @exception     IllegalArgumentException - input argument(s) illegal
    **/    private void checkInput(Organism aParentOrganismOne, Organism aParentOrganismTwo,                            int aNumberOfChildren, boolean aSeperateSex)    {        seperateSex = aSeperateSex;                // Check input arguments        if (aParentOrganismOne == null || aParentOrganismTwo == null || aNumberOfChildren < 0)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }
        // if parents are not the same species, throw exception
        if (aParentOrganismOne.getSpecies() != aParentOrganismTwo.getSpecies())
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
                // throw exception when either one or both of parents are dead.        if(aParentOrganismOne.containsCharacteristic("Dead") || aParentOrganismTwo.containsCharacteristic("Dead"))        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL                     + "\nDead parent(s) can't create children!");        }                numberOfChildren = aNumberOfChildren;        int sexParentOne = aParentOrganismOne.getSex();        int sexParentTwo = aParentOrganismTwo.getSex();        if (sexParentOne == Organism.MALE && sexParentTwo == Organism.FEMALE)        {            maleParent = aParentOrganismOne;            femaleParent = aParentOrganismTwo;        } else if (sexParentOne == Organism.FEMALE && sexParentTwo == Organism.MALE) {
            femaleParent = aParentOrganismOne;            maleParent = aParentOrganismTwo;
        } else if (sexParentOne == Organism.NO_SEX && sexParentTwo == Organism.NO_SEX) {            // diploid species with no sex chromosomes            femaleParent = aParentOrganismOne;            maleParent = aParentOrganismTwo;            seperateSex = false; // don't seperate sex if NO_SEX        } else {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }
        world = femaleParent.getWorld();
        if (world == null)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        species = femaleParent.getSpecies();        
        if (species == null)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }    }    /**     * Get number of children in the family. <p>
     * 
     * @return numberOfChildren int - number of children in the family.     */    public int getNumberOfChildren()    {    		return numberOfChildren;    }        /**     * Set number of children in the family. <p>
     * 
     * @param aNumberOfChildren int - number of children to be created in the family.     */    public void setNumberOfChildren(int aNumberOfChildren)    {          if(aNumberOfChildren < 0)              throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);          else              numberOfChildren = aNumberOfChildren;    }        /**     * Get number of dead offsprings.
     * 
     * @return int - number of dead offsprings.     */    public int getNumberOfDeadChildren() 
    {
        return deadNumber;
    }        /**     * Get seperateSex? <p>
     * 
     * @return seperateSex boolean - true if seperate sex for offsprings     */    public boolean getSeperateSex() 
    {
        return seperateSex;
    }        /**     * Set seperateSex? <p>
     * 
     * @param aSeperateSex boolean - set whether to seperate sex for offsprings     */    public void setSeperateSex(boolean aSeperateSex)    {        seperateSex = aSeperateSex;        if(!(femaleParent == null) && femaleParent.getSex() == Organism.NO_SEX) seperateSex = false;    }
    /**     * Get world of the family. <p>
     * 
     * @return world World - world of the family.     */    public World getWorld()    {		if(world == null) world = femaleParent.getWorld();		return world;
    }
    /**     * Get species of the family. <p>
     * 
     * @return species Species - species of the family.     */    public Species getSpecies()    {		if(species == null) species = femaleParent.getSpecies();		return species;    }        /**     * Get female parent. <p>
     * 
     * @return femaleParent Organism - female parent of the family.     */    public Organism getFemaleParent()    {    		return femaleParent;    }
    /**     * Set female parent. The sex can be FEMALE or NO_SEX. May not be null <p>
     * 
     * @param aFemaleParent Organism - female parent of the family.     */    public void setFemaleParent(Organism aFemaleParent)    {        if (aFemaleParent == null)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }        int sexParentOne = aFemaleParent.getSex();        if (sexParentOne == Organism.FEMALE)        {            femaleParent = aFemaleParent;        }        else if (sexParentOne == Organism.NO_SEX)        {            // diploid species with no sex chromosomes            femaleParent = aFemaleParent;        }        else        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }    }    /**     * Get male parent. <p>
     * 
     * @return maleParent Organism - male parent of the family.     */    public Organism getMaleParent()    {    		return maleParent;    }        /**     * Set male parent. The sex may be MALE or NO_SEX. May not be null. <p>
     * 
     * @param aMaleParent Organism - male parent of the family.     */    public void setMaleParent(Organism aMaleParent)    {        if (aMaleParent == null)        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }        int sexParentOne = aMaleParent.getSex();        if (sexParentOne == Organism.MALE)        {            maleParent = aMaleParent;        }        else if (sexParentOne == Organism.NO_SEX)        {            // diploid species with no sex chromosomes            maleParent = aMaleParent;        }        else        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }    }        /**     * Set parents of the family. <p>
     * 
     * @param     aParentOrganismOne Organism - a parent organism, can be male, female or NO_SEX, may not be null
     * @param     aParentOrganismTwo Organism - a parent organism, must be other sex of anOrganismOne or NO_SEX, may not be null
     * @exception     IllegalArgumentException - input argument(s) illegal     */    public void setParents(Organism aParentOrganismOne, Organism aParentOrganismTwo)    {        if (aParentOrganismOne == null || aParentOrganismTwo == null)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }          // if parents are not the same species, throw exception        if (aParentOrganismOne.getSpecies() != aParentOrganismTwo.getSpecies())        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }        int sexParentOne = aParentOrganismOne.getSex();        int sexParentTwo = aParentOrganismTwo.getSex();
        if (sexParentOne == Organism.MALE && sexParentTwo == Organism.FEMALE)        {            maleParent = aParentOrganismOne;            femaleParent = aParentOrganismTwo;        } else if (sexParentOne == Organism.FEMALE && sexParentTwo == Organism.MALE) {            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        } else if (sexParentOne == Organism.NO_SEX && sexParentTwo == Organism.NO_SEX) {
            // diploid species with no sex chromosomes
            femaleParent = aParentOrganismOne;
            maleParent = aParentOrganismTwo;
        } else {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }
    }
    /**
     * Get number of traits for the species. <p>
     * 
     * @return numberOfTraits int - number of traits the species has.
     */
    public int getNumberOfTraits()
    {
        if(traits == null) setTraits();
        return traits.size();
    }

    /**
     * Get traits of the species. <p>
     * 
     * @return traitVector Vector - Vector to store the traits of the species.
     */
    public Vector getTraits()
    {
        if(traits == null) setTraits();
        return traits;
    }
    /**     * Set trait vector by implementing species.getTraits().     */    private void setTraits()    {    		if(species == null) getSpecies();
    		if(traits == null) traits = new Vector();
        traits.removeAllElements();    		
        Enumeration enu = species.getTraits();
    		while(enu.hasMoreElements()) traits.addElement((Trait) enu.nextElement());    }
    /**     * Get female characteristics vectors. <p>
     * 
     * @return femaleCharacteristicVector Vector[] - Array of vectors, each vector contains      *                characteristics for female children.     */    public Vector[] getFemaleCharacteristicsVector()     {        if(femaleCharacteristicsVector == null) setFemaleCharacteristicsVector();
        return femaleCharacteristicsVector;
    }
    /**     * Set female characteristics vectors. <p>     */    private void setFemaleCharacteristicsVector()    {
        int numberOfTraits = getNumberOfTraits();        
        femaleCharacteristicsVector = new Vector[numberOfTraits];
        for(int i = 0; i < numberOfTraits; i++)         {            Trait trait1 = (Trait) traits.elementAt(i);            Enumeration enu = trait1.getCharacteristics();            //if(femaleCharacteristicsVector[i] == null)            femaleCharacteristicsVector[i] = new Vector();            femaleCharacteristicsVector[i].removeAllElements();            while(enu.hasMoreElements())            {                Characteristic cha1 = (Characteristic)enu.nextElement();                femaleCharacteristicsVector[i].addElement(cha1);                         }        }    }        /**     * Get male characteristics vectors. <p>
     * 
     * @return maleCharacteristicVector Vector[] - Array of vector. Each vector contains      *                characteristics for male children.     */    public Vector[] getMaleCharacteristicsVector()     {        if(maleCharacteristicsVector == null) setMaleCharacteristicsVector();        return maleCharacteristicsVector;    }
    /**     * Set male characteristics vector. <p>     */    private void setMaleCharacteristicsVector()    {        int numberOfTraits = getNumberOfTraits();        maleCharacteristicsVector = new Vector[numberOfTraits];        for(int i = 0; i < numberOfTraits; i++)        {            Trait trait1 = (Trait) traits.elementAt(i);            Enumeration enu = trait1.getCharacteristics();            if(maleCharacteristicsVector[i] == null) maleCharacteristicsVector[i] = new Vector();            maleCharacteristicsVector[i].removeAllElements();
            while(enu.hasMoreElements())            {                Characteristic cha1 = (Characteristic)enu.nextElement();                maleCharacteristicsVector[i].addElement(cha1);            }        }    }        /**     * Get characteristics vectors, despite of sex.
     * 
     * @return characteristicsVector Vector[] - Array of vector. Each vector contains      *                characteristics for all offsprings.     */    public Vector[] getCharacteristicsVector()    {          if(characteristicsVector == null) setCharacteristicsVector();          return characteristicsVector;    }        /**     * Set characteristics vectors.     */    private void setCharacteristicsVector()    {   
        int numberOfTraits = getNumberOfTraits();        characteristicsVector = new Vector[numberOfTraits];

        for(int i = 0; i < numberOfTraits; i++)         {            Trait trait1 = (Trait) traits.elementAt(i);            Enumeration enu = trait1.getCharacteristics();            if(characteristicsVector[i] == null) characteristicsVector[i] = new Vector();                                    characteristicsVector[i].removeAllElements();
            while(enu.hasMoreElements())            {                Integer integer = new Integer(0);                Characteristic cha1 = (Characteristic)enu.nextElement();                characteristicsVector[i].addElement(cha1);            }        }    }
    /**     * Initiate characteristicsHashtable.     */    private void initCharacteristicsHashtable()    {        characteristicsHashtable = new Hashtable();        femaleCharacteristicsHashtable = new Hashtable();        maleCharacteristicsHashtable = new Hashtable();                setCharacteristicsVector();        setFemaleCharacteristicsVector();        setMaleCharacteristicsVector();
        // initiate hashtable to 0 for all keys.        int initNumber = 0;
                int numberOfTraits = getNumberOfTraits();
        
        // Assumption: All characteristics have different names, so they can be used as keys for hashtables.        for(int i = 0; i<numberOfTraits; i++)         {            for(int j = 0; j<characteristicsVector[i].size(); j++)            {                characteristicsHashtable.put(characteristicsVector[i].elementAt(j), new Integer(initNumber));                femaleCharacteristicsHashtable.put(characteristicsVector[i].elementAt(j), new Integer(initNumber));                maleCharacteristicsHashtable.put(characteristicsVector[i].elementAt(j), new Integer(initNumber));            }        }    }        /**     * Set characteristicsHashtable by randomly creating a huge number of offsprings.      */    private void createOffspring()    {
        initCharacteristicsHashtable();            
        deadNumber = 0;
        maleOffspringNumber = 0;        femaleOffspringNumber = 0;
        
        int numberOfTraits = traits.size();
        
        for(int k = 1; k<=numberOfChildren; k++)         {            if(isCancelled) 
            {
                //clearAll();
                break;
            }

            Vector vector = new Vector();
            int aSex = Organism.NO_SEX;
            
            // species.randomlySpecifyOffspring returns sex.            aSex = species.randomlySpecifyOffspring(femaleParent, maleParent, vector);
            int aSize = vector.size();
            boolean isDead = false;
            // count dead offsprings
            for(int i = 0; i < aSize; i++)
            {
                Characteristic characteristic = (Characteristic) vector.elementAt(i);
                if(characteristic.getName().equalsIgnoreCase("dead"))
                {
                    isDead = true;                    deadNumber ++;                    break;                }            }
            // count alive offsprings
            if(!isDead)
            {
                // count NO_SEX offsprings
                if(aSex == Organism.NO_SEX || !seperateSex)
                {
                    for(int i = 0; i<aSize; i++)
                    {
                        Characteristic characteristic = (Characteristic)vector.elementAt(i);
                        int value = (new Integer(characteristicsHashtable.get(characteristic).toString())).intValue() + 1;
                        characteristicsHashtable.put(characteristic, new Integer(value));                        
                    }                }
                // count male offsprings
                else if(aSex == Organism.MALE)
                {
                    maleOffspringNumber++;
                    for(int i = 0; i<aSize; i++)
                    {
                        Characteristic characteristic = (Characteristic)vector.elementAt(i);
                        int value = (new Integer(maleCharacteristicsHashtable.get(characteristic).toString())).intValue() + 1;
                        maleCharacteristicsHashtable.put(characteristic, new Integer(value));                        
                    }                    
                }
                //count female offsprings
                else if(aSex == Organism.FEMALE)
                {
                    femaleOffspringNumber++;
                    for(int i = 0; i<aSize; i++)
                    {
                        Characteristic characteristic = (Characteristic)vector.elementAt(i);
                        int value = (new Integer(femaleCharacteristicsHashtable.get(characteristic).toString())).intValue() + 1;
                        femaleCharacteristicsHashtable.put(characteristic, new Integer(value));
                    }
                }
            }
            
            //clear vector so that it can be used for the next offspring.
            vector.removeAllElements();
            
            taskProgress = k; // used by progressbar in VirtualFamilyView.
        }
        
        if(isCancelled)  createOffspringDone = false;
        else createOffspringDone = true;
    }
    /**     * Get number of female offsprings. <p>
     * 
     * @return femaleOffspringNumber int - number of female offspring number.     */    public int getFemaleOffspringNumber()    {        return femaleOffspringNumber;    }        /**     * Get number of male offsprings. <p>
     * 
     * @return maleOffspringNumber int - number of male offspring number.     */    public int getMaleOffspringNumber()    {        return maleOffspringNumber;    }        /**     * Get characteristicsNumberVector. <p>
     * Array of Vectors. Each vector contais count of characteristics in one trait. <p>
     * 
     * @return characteristicsNumberVector Vector[] - Array of vectors.     */    public Vector[] getCharacteristicsNumberVector()    {
        int numberOfTraits = getNumberOfTraits();
        
        // initate if null        if(characteristicsNumberVector == null)        {            characteristicsNumberVector = new Vector[numberOfTraits];                        for(int i = 0; i < numberOfTraits; i++)            {                characteristicsNumberVector[i] = new Vector();            }        }
        
        // remove existing elements so that vectors are empty initially.
        for(int i = 0; i < numberOfTraits; i++) characteristicsNumberVector[i].removeAllElements();                if(!createOffspringDone) createOffspring();        
        for(int i = 0; i<traits.size(); i++)        {        	    for(int j = 0; j<characteristicsVector[i].size(); j++)            {                Characteristic characteristic = (Characteristic) characteristicsVector[i].elementAt(j);                Integer integer = (Integer) characteristicsHashtable.get(characteristic);                characteristicsNumberVector[i].addElement(integer);                        }        }        return characteristicsNumberVector;    }        /**     * Get femaleCharacteristicsNumberVector. <p>
     * Array of Vectors. Each vector contais count of characteristics in one trait. <p>
     * 
     * @return femaleCharacteristicsNumberVector Vector[] - Array of vectors.     */    public Vector[] getFemaleCharacteristicsNumberVector()    {
        int numberOfTraits = getNumberOfTraits();
        
        // initiate if null		if(femaleCharacteristicsNumberVector == null)        {            femaleCharacteristicsNumberVector = new Vector[numberOfTraits];            for(int i = 0; i < numberOfTraits; i++)            {                femaleCharacteristicsNumberVector[i] = new Vector();            }        }
        
        // remove existing elements so that vectors are empty initially.
        for(int i = 0; i < numberOfTraits; i++) femaleCharacteristicsNumberVector[i].removeAllElements();
        
        if(!createOffspringDone) createOffspring();

        for(int i = 0; i<traits.size(); i++)        {           for(int j = 0; j<femaleCharacteristicsVector[i].size(); j++)            {                Characteristic characteristic = (Characteristic) femaleCharacteristicsVector[i].elementAt(j);                Integer integer = (Integer) femaleCharacteristicsHashtable.get(characteristic);                	femaleCharacteristicsNumberVector[i].addElement(integer);             }        }        return femaleCharacteristicsNumberVector;    }        /**     * Get maleCharacteristicsNumberVector. <p>
     * Array of Vectors. Each vector contais count of characteristics in one trait. <p>
     * 
     * @return maleCharacteristicsNumberVector Vector[] - Array of vectors.     */    public Vector[] getMaleCharacteristicsNumberVector()    {        int numberOfTraits = getNumberOfTraits();

        // initiate if null
        if(maleCharacteristicsNumberVector == null)        {            maleCharacteristicsNumberVector = new Vector[numberOfTraits];                        for(int i = 0; i < numberOfTraits; i++)            {                maleCharacteristicsNumberVector[i] = new Vector();            }        }
        // remove existing elements so that vectors are empty initially.
        for(int i = 0; i < numberOfTraits; i++) maleCharacteristicsNumberVector[i].removeAllElements();
        
        if(!createOffspringDone) createOffspring();
        
        for(int i = 0; i<traits.size(); i++)
        {            for(int j = 0; j<maleCharacteristicsVector[i].size(); j++)            {
                Characteristic characteristic = (Characteristic) maleCharacteristicsVector[i].elementAt(j);                Integer integer = (Integer) maleCharacteristicsHashtable.get(characteristic);                maleCharacteristicsNumberVector[i].addElement(integer);                        }        }        return maleCharacteristicsNumberVector;    }        /**     * Get characteristicsPercentVector. <p>
     * Array of Vectors. Each vector contais percent of characteristics in one trait. <p>
     * Percentage is got by dividing count of this characteristic with number of alive offsprings then times 100. <p>
     * 
     * @return characteristicsNumberVector Vector[] - Array of vectors.     */    public Vector[] getCharacteristicsPercentVector()    {		if(characteristicsNumberVector == null) getCharacteristicsNumberVector();        
        int numberOfTraits = getNumberOfTraits();
        // if not null but empty, set vectors.        for(int i = 0; i < numberOfTraits; i++)
        {
            if(characteristicsNumberVector[i].size() == 0)
            {
                getCharacteristicsNumberVector();
                break;
            }
        }
        
        Vector characteristicsPercentVector[] = new Vector[numberOfTraits];        for(int i = 0; i<numberOfTraits; i++)        {            characteristicsPercentVector[i] = new Vector();        }                for(int i = 0; i<traits.size(); i++)        {            for(int j = 0; j<characteristicsVector[i].size(); j++)            {                Integer integer = (Integer) (characteristicsNumberVector[i].elementAt(j));                double aDouble = Math.round(integer.intValue()*1000./(numberOfChildren-deadNumber))/10.;                characteristicsPercentVector[i].addElement(new Double(aDouble));            
            }        }
        return characteristicsPercentVector;    }
    /**     * Get femaleCharacteristicsPercentVector. <p>
     * Array of Vectors. Each vector contais percent of characteristics in one trait. <p>
     * Percentage is got by dividing count of this characteristic with number of alive offsprings then times 100. <p>
     * 
     * @return femaleCharacteristicsNumberVector Vector[] - Array of vectors.    */    public Vector[] getFemaleCharacteristicsPercentVector()    {        if(femaleCharacteristicsNumberVector == null) getFemaleCharacteristicsNumberVector();        
        int numberOfTraits = getNumberOfTraits();

        // if not null but empty, also set vectors
        for(int i = 0; i < numberOfTraits; i++)
        {
            if(femaleCharacteristicsNumberVector[i].size() == 0)
            {
                getFemaleCharacteristicsNumberVector();
                break;
            }
        }
                Vector characteristicsPercentVector[] = new Vector[numberOfTraits];
        for(int i = 0; i<numberOfTraits; i++)        {            characteristicsPercentVector[i] = new Vector();        }
                for(int i = 0; i<traits.size(); i++)        {            int vectorSize = femaleCharacteristicsNumberVector[i].size();    		    for(int j = 0; j<vectorSize; j++)            {                Integer integer = (Integer) (femaleCharacteristicsNumberVector[i].elementAt(j));                double aDouble = Math.round(integer.intValue()*1000./(numberOfChildren-deadNumber))/10.;                characteristicsPercentVector[i].addElement(new Double(aDouble));                        }        }        return characteristicsPercentVector;    }
    /**     * Get maleCharacteristicsPercentVector. <p>
     * Array of Vectors. Each vector contais percent of characteristics in one trait. <p>
     * Percentage is got by dividing count of this characteristic with number of alive offsprings then times 100. <p>
     * 
     * @return maleCharacteristicsNumberVector Vector[] - Array of vectors.     */    public Vector[] getMaleCharacteristicsPercentVector()    {        if(maleCharacteristicsNumberVector == null) getMaleCharacteristicsNumberVector();
        int numberOfTraits = getNumberOfTraits();
        // if not null but empty, also set vectors
        for(int i = 0; i < numberOfTraits; i++)
        {
            if(maleCharacteristicsNumberVector[i].size() == 0)
            {
                getMaleCharacteristicsNumberVector();
                break;
            }
        }
        
        Vector characteristicsPercentVector[] = new Vector[numberOfTraits];
        for(int i = 0; i<numberOfTraits; i++) characteristicsPercentVector[i] = new Vector();
        for(int i = 0; i<traits.size(); i++)        	{        		int vectorSize = maleCharacteristicsVector[i].size();        	    for(int j = 0; j<vectorSize; j++)        	    {        	        Integer integer = (Integer) maleCharacteristicsNumberVector[i].elementAt(j);        	        double aDouble = Math.round(integer.intValue()*1000./(numberOfChildren-deadNumber))/10.;                characteristicsPercentVector[i].addElement(new Double(aDouble));                    	     }        	}        return characteristicsPercentVector;    }
    /**     * Get percent vector for specific trait of female offsprings. <p>
     * Element is percent of characteristics for that specific trait of alive offsprings. <p>
     * 
     * @param aTrait Trait - trait that needs to retrieve number
     * @return Vector - vector of percentage.     */    public Vector getFemalePercentPerTrait(Trait aTrait)    {        Vector vector[] = getFemaleCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();        
        int index = -1;
                for(int i = 0; i<numberOfTraits; i++)        {            String str1 = aTrait.getName();            String str2 = ((Trait)(traits.elementAt(i))).getName();            if(str1.equalsIgnoreCase(str2))             {                index = i;                break;            }        }
        // if trait not existing, throw exception        if(index == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                return vector[index];    }        /**     * Get percent vector for specific trait of female offsprings. <p>
     * Element is percent of characteristics for that specific trait of alive offsprings. <p>
     * 
     * @param traitStr String - trait name
     * @return Vector - vector of percentage.     */    public Vector getFemalePercentPerTrait(String traitStr)    {        Vector vector[] = getFemaleCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();
                int index = -1;        
        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = ((Trait)(traits.elementAt(i))).getName();            if(str1.equalsIgnoreCase(traitStr))             {                index = i;                break;            }        }                if(index == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                return vector[index];    }        /**     * Get percentage for specific characteristic of specific trait for female offsprings. <p>
     * 
     * @param aTrait Trait - the trait
     * @param aChar Characteristic - the characteristic
     * @return double - Percentage     */    public double getFemalePercentPerTraitCharacteristic(Trait aTrait, Characteristic aChar)    {        Vector vector[] = getFemaleCharacteristicsPercentVector();
        int numberOfTraits = getNumberOfTraits();
                int index1 = -1;        int index2 = -1;        
        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = aTrait.getName();            String str2 = ((Trait)(traits.elementAt(i))).getName();            if(str1.equalsIgnoreCase(str2))             {                index1 = i;                break;            }                    }        
        // if invalid trait, throw exception        if(index1 == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                for(int i = 0; i < vector[index1].size(); i++)        {            String str1 = aChar.getName();            String str2 = ((Characteristic)(femaleCharacteristicsVector[index1].elementAt(i))).getName();            if(str1.equalsIgnoreCase(str2)) index2 = i;
        }
        // if invalid characteristics, throw exception        if(index2 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        return ((Double)(vector[index1].elementAt(index2))).doubleValue();
    }

    /**
     * Get percentage for specific characteristic of specific trait for female offsprings. <p>
     * 
     * @param traitStr String - name of the Trait
     * @param charStr String - name of the Characteristic
     * @return double - Percentage
     */
    public double getFemalePercentPerTraitCharacteristic(String traitStr, String charStr)
    {
        Vector vector[] = getFemaleCharacteristicsPercentVector();
        
        int numberOfTraits = getNumberOfTraits();
        int index1 = -1;
        int index2 = -1;
        for(int i = 0; i<numberOfTraits; i++)
        {
            String str1 = ((Trait)(traits.elementAt(i))).getName();
            if(str1.equalsIgnoreCase(traitStr)) 
            {
                index1 = i;
                break;
            }            
        }
        
        if(index1 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        for(int i = 0; i < vector[index1].size(); i++)
        {
            String str1 = ((Characteristic)(femaleCharacteristicsVector[index1].elementAt(i))).getName();
            if(str1.equalsIgnoreCase(charStr)) index2 = i;
        }

        if(index2 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        return ((Double)(vector[index1].elementAt(index2))).doubleValue();
    }

    /**
     * Get percent vector for specific trait of female offsprings. <p>
     * Element is percent of characteristics for that specific trait of alive offsprings. <p>
     * 
     * @param aTrait Trait - trait that needs to retrieve number
     * @return Vector - vector of percentage.
     */
    public Vector getPercentPerTrait(Trait aTrait)
    {
        Vector vector[] = getCharacteristicsPercentVector();
        
        int numberOfTraits = getNumberOfTraits();
        int index = -1;
        for(int i = 0; i<numberOfTraits; i++)
        {
            String str1 = aTrait.getName();
            String str2 = ((Trait)(traits.elementAt(i))).getName();
            if(str1.equalsIgnoreCase(str2)) 
            {
                index = i;
                break;
            }
        }

        if(index == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        return vector[index];
    }
    
    /**
     * Get percent vector for specific trait of female offsprings. <p>
     * Element is percent of characteristics for that specific trait of alive offsprings. <p>
     * 
     * @param traitStr String - trait name
     * @return Vector - vector of percentage.
     */
    public Vector getPercentPerTrait(String traitStr)
    {
        Vector vector[] = getCharacteristicsPercentVector();
        
        int numberOfTraits = getNumberOfTraits();
        int index = -1;
        for(int i = 0; i<numberOfTraits; i++)
        {
            String str1 = ((Trait)(traits.elementAt(i))).getName();
            if(str1.equalsIgnoreCase(traitStr)) 
            {
                index = i;
                break;
            }
        }
        
        if(index == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        return vector[index];
    }
    
    /**
     * Get percentage for specific characteristic of specific trait for female offsprings. <p>
     * 
     * @param aTrait Trait
     * @param aChar Characteristic
     * @return double - Percentage
     */
    public double getPercentPerTraitCharacteristic(Trait aTrait, Characteristic aChar)
    {
        Vector vector[] = getCharacteristicsPercentVector();
        
        int numberOfTraits = getNumberOfTraits();
        int index1 = -1;
        int index2 = -1;
        for(int i = 0; i<numberOfTraits; i++)
        {
            String str1 = aTrait.getName();
            String str2 = ((Trait)(traits.elementAt(i))).getName();
            if(str1.equalsIgnoreCase(str2)) 
            {
                index1 = i;
                break;
            }            
        }
        
        if(index1 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        for(int i = 0; i < vector[index1].size(); i++)
        {
            String str1 = aChar.getName();
            String str2 = ((Characteristic)(characteristicsVector[index1].elementAt(i))).getName();
            if(str1.equalsIgnoreCase(str2)) index2 = i;
        }        if(index2 == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                return ((Double)(vector[index1].elementAt(index2))).doubleValue();    }    /**     * Get percentage for specific characteristic of specific trait for female offsprings. <p>
     * 
     * @param traitStr String - name of the Trait
     * @param charStr String - name of the Characteristic
     * @return double - Percentage     */    public double getPercentPerTraitCharacteristic(String traitStr, String charStr)    {        Vector vector[] = getCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();        int index1 = -1;        int index2 = -1;        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = ((Trait)(traits.elementAt(i))).getName();            if(str1.equalsIgnoreCase(traitStr))             {                index1 = i;                break;            }                    }                if(index1 == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                for(int i = 0; i < vector[index1].size(); i++)        {            String str1 = ((Characteristic)(characteristicsVector[index1].elementAt(i))).getName();            if(str1.equalsIgnoreCase(charStr)) index2 = i;        }        if(index2 == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                return ((Double)(vector[index1].elementAt(index2))).doubleValue();    }    /**     * Get percent vector for specific trait of male offsprings. <p>
     * Element is percent of characteristics for that specific trait of alive offsprings. <p>
     * 
     * @param aTrait Trait - trait
     * @return Vector - vector of percentage.     */    public Vector getMalePercentPerTrait(Trait aTrait)    {        Vector vector[] = getMaleCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();        int index = -1;        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = aTrait.getName();            String str2 = ((Trait)(traits.elementAt(i))).getName();            if(str1.equalsIgnoreCase(str2))             {                index = i;                break;            }        }        if(index == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                return vector[index];    }        /**
     * Get percent vector for specific trait of male offsprings. <p>
     * Element is percent of characteristics for that specific trait of alive offsprings. <p>
     * 
     * @param traitStr String - trait name
     * @return Vector - vector of percentage.
     */
    public Vector getMalePercentPerTrait(String traitStr)    {        Vector vector[] = getMaleCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();        int index = -1;        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = ((Trait)(traits.elementAt(i))).getName();            if(str1.equalsIgnoreCase(traitStr))             {                index = i;                break;            }        }                if(index == -1)        {            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);        }                return vector[index];    }        /**
     * Get percentage for specific characteristic of specific trait for male offsprings. <p>
     * 
     * @param aTrait Trait - the Trait
     * @param aChar Characteristic - the Characteristic
     * @return double - Percentage
     */
    public double getMalePercentPerTraitCharacteristic(Trait aTrait, Characteristic aChar)    {        Vector vector[] = getMaleCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();        int index1 = -1;        int index2 = -1;        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = aTrait.getName();            String str2 = ((Trait)(traits.elementAt(i))).getName();
            if(str1.equalsIgnoreCase(str2)) 
            {
                index1 = i;
                break;
            }            
        }
        
        if(index1 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        for(int i = 0; i < vector[index1].size(); i++)
        {
            String str1 = aChar.getName();
            String str2 = ((Characteristic)(maleCharacteristicsVector[index1].elementAt(i))).getName();
            if(str1.equalsIgnoreCase(str2)) index2 = i;        }
        if(index2 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        return ((Double)(vector[index1].elementAt(index2))).doubleValue();
    }

    /**
     * Get percentage for specific characteristic of specific trait for male offsprings. <p>
     * 
     * @param traitStr String - name of the Trait
     * @param charStr String - name of the Characteristic
     * @return double - Percentage
     */
    public double getMalePercentPerTraitCharacteristic(String traitStr, String charStr)
    {        Vector vector[] = getMaleCharacteristicsPercentVector();                int numberOfTraits = getNumberOfTraits();        int index1 = -1;        int index2 = -1;        for(int i = 0; i<numberOfTraits; i++)        {            String str1 = ((Trait)(traits.elementAt(i))).getName();
            if(str1.equalsIgnoreCase(traitStr)) 
            {
                index1 = i;
                break;
            }            
        }
        
        if(index1 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        for(int i = 0; i < vector[index1].size(); i++)
        {
            String str1 = ((Characteristic)(maleCharacteristicsVector[index1].elementAt(i))).getName();
            if(str1.equalsIgnoreCase(charStr))
            {
                index2 = i;
            }
        }

        if(index2 == -1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        return ((Double)(vector[index1].elementAt(index2))).doubleValue();
    }
    

    /**
     * Get task progress. For timer in VirtualFamilyView.
     * 
     * @return int - progress: number of created offsprings so far.
     */
    public int getTaskProgress()
    {
    		return taskProgress;
    }
    
    /**
     * Is creating offspring done? 
     * 
     * @return boolean - is createOffspringDone?
     */    public boolean isCreateOffspringDone()    {    		return createOffspringDone;    }
    
    /**
     * set whether to cancel calculation or not.
     * 
     * @param aIsCancelled boolean - cancel the calculation?
     */
    public void setCancelled(boolean aIsCancelled)
    {
        this.isCancelled = aIsCancelled;
    }
    
    /**
     * get isCancelled?
     * 
     * @return isCancelled boolean - is calculation cancelled?
     */
    public boolean getCancelled()
    {
        return isCancelled;
    }
    
    /**
     * Set all to initial values. For VirtualFamilyView.stopCalculation().
     *
     */
    public void clearAll()
    {        
        taskProgress = 0;
        
        createOffspringDone = false;
        
        if(characteristicsHashtable != null) characteristicsHashtable.clear();
        if(femaleCharacteristicsHashtable != null) femaleCharacteristicsHashtable.clear();
        if(maleCharacteristicsHashtable != null) maleCharacteristicsHashtable.clear();

        deadNumber = 0;
        femaleOffspringNumber = 0;
        maleOffspringNumber = 0;
        
        if(characteristicsNumberVector != null)
        {
            for(int i = 0; i < getNumberOfTraits(); i++)
            {
                if(characteristicsNumberVector[i] != null) characteristicsNumberVector[i].removeAllElements();
            }
        }
        
        if(femaleCharacteristicsNumberVector != null)
        {
            for(int i = 0; i < getNumberOfTraits(); i++)
            {
                if(femaleCharacteristicsNumberVector[i] != null) femaleCharacteristicsNumberVector[i].removeAllElements();
            }
        }
        
        if(maleCharacteristicsNumberVector != null)
        {
            for(int i = 0; i < getNumberOfTraits(); i++)
            {
                if(maleCharacteristicsNumberVector[i] != null) maleCharacteristicsNumberVector[i].removeAllElements();
            }
        }
    }}