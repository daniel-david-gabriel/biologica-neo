//
// Class : World
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.6 $
// $Date: 2003/01/28 18:40:14 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.io.File;
import java.net.URL;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import org.concord.biologica.datasupport.SpecieFileDesc;

/**
 * This class represents a BioLogica world.  There is a single world
 * per BioLogica file.
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.CURRENT_SPECIES - the current species in this world changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.DIRTY - object has been modified since last save
 * <li> EngineProp.FILE - world's associated file has changed
 * <li> EngineProp.ENVIRONMENT_ADDED - an environment has been created and added to world
 * <li> EngineProp.ENVIRONMENT_REMOVED - an environment has been deleted and removed from world
 * <li> EngineProp.FAMILY_ADDED - a family has been created and added to world
 * <li> EngineProp.FAMILY_REMOVED - a family has been deleted and removed from world
 * <li> EngineProp.LOCKED_STATE - the locked state of the object has changed
 * <li> EngineProp.ORGANISM_ADDED - an organism has been created and added to world
 * <li> EngineProp.ORGANISM_REMOVED - an organism has been deleted and removed from world
 * <li> EngineProp.SPECIES_ADDED - a species has been created and added to world
 * <li> EngineProp.SPECIES_REMOVED - a species has been deleted and removed from world
 * <li> EngineProp.TERRAIN_ADDED - a terrain was added
 * <li> EngineProp.TERRAIN_REMOVED - a terrain was removed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#CURRENT_SPECIES
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#DIRTY
 * @see org.concord.biologica.engine.EngineProp#FILE
 * @see org.concord.biologica.engine.EngineProp#ENVIRONMENT_ADDED
 * @see org.concord.biologica.engine.EngineProp#ENVIRONMENT_REMOVED
 * @see org.concord.biologica.engine.EngineProp#FAMILY_ADDED
 * @see org.concord.biologica.engine.EngineProp#FAMILY_REMOVED
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_ADDED
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_REMOVED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_ADDED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_REMOVED
 * @see org.concord.biologica.engine.EngineProp#TERRAIN_ADDED
 * @see org.concord.biologica.engine.EngineProp#TERRAIN_REMOVED
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.6 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class World
extends EngineObject
implements Serializable, DocumentHandler
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The vector of species in this world.  All of the objects in
     * this vector are instances of Species.  These species may or may
     * not span all the types of organisms, as there may be organisms
     * of new as-yet-unknown species created during evolution.<p>
     *
     * When a Species object is created and added to this vector,
     * a EngineProp.SPECIES_ADDED property change event is fired.<p>
     * If the added species is the first species in the world, then
     * it is set to the current species and an EngineProp.CURRENT_SPECIES
     * property change event is fired.<p>
     *
     * When a Species object is deleted and removed from this vector,
     * a EngineProp.SPECIES_REMOVED property change event is fired.<p>
     * If the deleted species is also the current species, then another
     * species is made the current one (if one exists) and an
     * EngineProp.CURRENT_SPECIES property change event is also fired.<p>
     *
     * @see		org.concord.biologica.engine.Species
     * @see		org.concord.biologica.engine.EngineProp#CURRENT_SPECIES
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_ADDED
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_REMOVED
    **/
    private Vector				species;

    /**
     * The current species in this world.  This is used to set the
     * context for other actions in the world and user interface
     * - creating organisms, etc.<p>
     *
     * When the current species changes,
     * an EngineProp.CURRENT_SPECIES property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Species
     * @see		org.concord.biologica.engine.EngineProp#CURRENT_SPECIES
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_ADDED
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_REMOVED
    **/
    private Species				currentSpecies;

    /**
     * The vector of environments in this world.  All of the objects in
     * this vector are instances of Environment.<p>
     *
     * When an Environment object is created and added to this vector,
     * a EngineProp.ENVIRONMENT_ADDED property change event is fired.<p>
     *
     * When an Environment object is deleted and removed from this vector,
     * a EngineProp.ENVIRONMENT_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Environment
    **/
    private Vector				environments;

    /**
     * Terrains in this world.  A terrain is a type of space that may
     * be used in creating environments for this world.  Examples of
     * terrains are "grass", "water", "swamp", "forest", etc.<p>
     *
     * All the objects on this vector must be instances of a Terrain.<p>
     *
     * When a Terrain object is created and added to this vector,
     * a EngineProp.TERRAIN_ADDED property change event is fired.<p>
     *
     * When a Terrain object is deleted and removed from this vector,
     * a EngineProp.TERRAIN_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Terrain
     * @see		org.concord.biologica.engine.EngineProp#TERRAIN_ADDED
     * @see		org.concord.biologica.engine.EngineProp#TERRAIN_REMOVED
    **/
    private Vector				terrains;

    /**
     * The vector of organisms in this run.  All of the objects in
     * this vector are instances of Organism.<p>
     *
     * When an Organism object is created and added to this vector,
     * a EngineProp.ORGANISM_ADDED property change event is fired.<p>
     *
     * When an Organism object is deleted and removed from this vector,
     * a EngineProp.ORGANISM_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Organism
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_ADDED
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_REMOVED
    **/
    private Vector				organisms;

    /**
     * The vector of families in this world.  All of the objects in
     * this vector are instances of Family.<p>
     *
     * When a Family object is created and added to this vector,
     * an EngineProp.FAMILY_ADDED property change event is fired.<p>
     *
     * When a Family object is deleted and removed from this vector,
     * an EngineProp.FAMILY_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Family
     * @see		org.concord.biologica.engine.EngineProp#FAMILY_ADDED
     * @see		org.concord.biologica.engine.EngineProp#FAMILY_REMOVED
    **/
    private Vector				families;

    /**
     * Indicates if the currently open BioLogica state has been modified since
     * the last time the state was saved to a file.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.DIRTY.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#DIRTY
    **/
    private boolean				dirty;

    /**
     * File for the currently open BioLogica XML file.  Null if no file is open.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.FILE.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#FILE
    **/
    private File				file;

    /**
     * URL for the currently open BioLogica XML file.  Null if no url is open.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.FILE.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#FILE
    **/
    private URL				url;

    /**
     * Import context used when reading in world from a file.
     * Null except when we're in the midst of reading from a file.
    **/
    private ImportContext       importContext = null;

    /**
     * XML Parser for the BioLogica file being read in.
    **/
//    private com.sun.xml.parser.Parser      xmlParser = null;
    private SAXParser      xmlParser = null;

    /**
     * Creates a new world, as when creating a new world that has no associated
     * file yet.<p>
    **/
    public World()
    {
        file = null;
        url = null;
        dirty = true;
        lockedState = EngineObject.UNLOCKED;

        species = new Vector();
        currentSpecies = null;
        environments = new Vector();
        organisms = new Vector();
        terrains = new Vector();
        families = new Vector();
    }

    /**
     * Creates a new world with by opening the given file and retrieving the contents
     * of the file.<p>
     *
     * @param		aFile File - file to be opened to create this world, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception	InternalEngineException - multiple Engine objects detected in process
    **/
    public World(File aFile)
    {

        // Validate input arguments
        if (aFile == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        file = aFile;
        dirty = false;
        lockedState = EngineObject.UNLOCKED;

        species = new Vector();
        currentSpecies = null;
        environments = new Vector();
        organisms = new Vector();
        terrains = new Vector();
        families = new Vector();

        // Open the file, crossing fingers :-)
        try
        {
            String s = file.getName();
            int length = s.length();
            if (length > 4 &&
                s.charAt(length-1) == 'l' &&
                s.charAt(length-2) == 'm' &&
                s.charAt(length-3) == 'x' &&
                s.charAt(length-4) == '.')
            {
                open();
            }
            else
            {
                throw new IllegalArgumentException("File " + s + " is not an XML file.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException(EngineStrings.UNABLE_TO_OPEN_WORLD_FILE + e);
        }

    }

    /**
     * Creates a new world with by opening the given url and retrieving the contents
     * of the url file.<p>
     *
     * @param		urlString String - file to be opened to create this world, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception	InternalEngineException - multiple Engine objects detected in process
    **/
    public World(String urlString)
    {
        dirty = false;
        lockedState = EngineObject.UNLOCKED;

        species = new Vector();
        currentSpecies = null;
        environments = new Vector();
        organisms = new Vector();
        terrains = new Vector();
        families = new Vector();

        // Open the url, crossing fingers :-)
        try
        {
            String s = urlString;
            int length = s.length();
            if (length > 4 &&
                s.charAt(length-1) == 'l' &&
                s.charAt(length-2) == 'm' &&
                s.charAt(length-3) == 'x' &&
                s.charAt(length-4) == '.')
            {
                url = new URL(urlString);
                open();
            }
            else
            {
                throw new IllegalArgumentException("URL " + s + " is not an XML file.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException(EngineStrings.UNABLE_TO_OPEN_WORLD_FILE + e);
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
        delete(true);
    }
    
    public void delete(boolean notifyChange)
    {
        // Avoid double deletions gracefully
        if (deleted == true)
        {
            return;
        }

        // Proceed with deletion
        deleted = true;

        if (notifyChange)
        {
            // Notify listeners before deleting
            changes.firePropertyChange(EngineProp.DELETED,FALSE,TRUE);
        }
        
        // Delete instance variables, enabling garbage collection on them
        currentSpecies = null;

        deleteAllFamilies(notifyChange, true);
        families = null;
        deleteAllOrganisms(notifyChange);
        organisms = null;

        Environment anEnvironment;
        Vector environmentsClone = (Vector) environments.clone();
        Enumeration eEnvironments = environmentsClone.elements();
        //Enumeration eEnvironments = environments.elements();
        while (eEnvironments.hasMoreElements())
        {
            anEnvironment = (Environment) eEnvironments.nextElement();
            anEnvironment.setLockedState(EngineObject.UNLOCKED);
            anEnvironment.delete(notifyChange);
        }
        environments.removeAllElements();
        environments = null;
        environmentsClone = null;

        Terrain aTerrain;
        Vector terrainsClone = (Vector) terrains.clone();
        Enumeration eTerrains = terrainsClone.elements();
       // Enumeration eTerrains = terrains.elements();
        while (eTerrains.hasMoreElements())
        {
            aTerrain = (Terrain) eTerrains.nextElement();
            aTerrain.setLockedState(EngineObject.UNLOCKED);
            aTerrain.delete(notifyChange);
        }
        terrains.removeAllElements();
        terrains = null;
        terrainsClone = null;

        Species aSpecies;
        Vector speciesClone = (Vector) species.clone();
        Enumeration eSpecies = speciesClone.elements();
        //Enumeration eSpecies = species.elements();
        while (eSpecies.hasMoreElements())
        {
            aSpecies = (Species) eSpecies.nextElement();
            aSpecies.setLockedState(EngineObject.UNLOCKED);
            aSpecies.delete(notifyChange);
        }
        species.removeAllElements();
        species = null;
        speciesClone = null;

        file = null;
        dirty = false;
        //System.runFinalization();
    }
    
    public void deleteSpecifiedFamilies(Vector familyList, boolean notifyChange, boolean deleteChildren)
    {
        // Delete children in vectors, deleting "leaf-most" vectors first
        Family aFamily;
       // Vector familiesClone = (Vector) familyList.clone();
        //Enumeration eFamilies = familiesClone.elements();
        Enumeration eFamilies = familyList.elements();
        while (eFamilies.hasMoreElements())
        {
            aFamily = (Family) eFamilies.nextElement();
            aFamily.setLockedState(EngineObject.UNLOCKED);
            aFamily.delete(notifyChange, deleteChildren);
            aFamily = null;
        }
        families.removeAllElements();
        eFamilies = null;
        //familiesClone = null;
    }
    
    public void deleteAllFamilies(boolean notifyChange, boolean deleteChildren)
    {
        deleteSpecifiedFamilies(families, notifyChange, deleteChildren);
    }
    
    public void deleteSpecifiedOrganisms(Vector orgList, boolean notifyChange)
    {
        Organism anOrganism;
        //Vector organismsClone = (Vector) orgList.clone();
        //Enumeration eOrganisms = organismsClone.elements();
        Enumeration eOrganisms = orgList.elements();
        while (eOrganisms.hasMoreElements())
        {
            anOrganism = (Organism) eOrganisms.nextElement();
            anOrganism.setLockedState(EngineObject.UNLOCKED);
            anOrganism.delete(notifyChange);
            anOrganism = null;
        }
        organisms.removeAllElements();
        eOrganisms = null;
        //organismsClone = null;
    }
    public void deleteOrganism(Organism org)
    {
    	org.setLockedState(EngineObject.UNLOCKED);
        org.delete(false);
        org = null;
      // Runtime.getRuntime().gc();

    }
    public void deleteAllOrganisms(boolean notifyChange)
    {
 
        deleteSpecifiedOrganisms(organisms, notifyChange);
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return EngineStrings.WORLD_COLON + getName();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return this;
    }

    /**
     * Return the name of this world.  The name of a
     * world is the path of the filename of the world.<p>
     *
     * @return		String - filename of this world's file, may be null
    **/
    public String getName()
    {
        if (file != null)
        {
            return file.getAbsolutePath();
        }

        return EngineStrings.NOT_SAVED;
    }


    /**
     * Return the dirty state of this engine.  Dirty means the engine
     * has been modified since the last save or open.<p>
     *
     * @return		boolean - dirty (true) or not dirty (false)
    **/
    public boolean isDirty()
    {
        return dirty;
    }
    
    /**
     * Set the dirty state of this engine to true.  Only this object
     * can set its dirty state to false as that only happens when
     * the engine state is saved or a new engine is created.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.DIRTY.<p>
     *
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setDirty()
    {
        if (dirty != true)
        {
            dirty = true;
    
            // Notify listeners
            changes.firePropertyChange(EngineProp.DIRTY,FALSE,TRUE);
        }
    }

    /**
     * Return the current world file.
     *
     * @return		File - the current file, may be null
    **/
    public File getFile()
    {
        return file;
    }

    /**
     * Set file.  I'm not sure if this method makes sense, as instead
     * you should probaby only specify the file when opening or saving
     * a file.  So this method may be removed in the future.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.FILE.<p>
     *
     * @param		aFile File - the new file, may be null
    **/
    public void setFile(File aFile)
    {
        if (file != aFile)
        {
            File oldFile = file;
            file = aFile;
            changes.firePropertyChange(EngineProp.FILE,oldFile,file);
        }
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
        Species aSpecies;
        Enumeration eSpecies = species.elements();
        while (eSpecies.hasMoreElements())
        {
            aSpecies = (Species) eSpecies.nextElement();
            aSpecies.setAutomaticLocked(automaticLocked);
        }

        Environment anEnvironment;
        Enumeration eEnvironments = environments.elements();
        while (eEnvironments.hasMoreElements())
        {
            anEnvironment = (Environment) eEnvironments.nextElement();
            anEnvironment.setAutomaticLocked(automaticLocked);
        }

        Organism anOrganism;
        Enumeration eOrganisms = organisms.elements();
        while (eOrganisms.hasMoreElements())
        {
            anOrganism = (Organism) eOrganisms.nextElement();
            anOrganism.setAutomaticLocked(automaticLocked);
        }

        Family aFamily;
        Enumeration eFamilies = families.elements();
        while (eFamilies.hasMoreElements())
        {
            aFamily = (Family) eFamilies.nextElement();
            aFamily.setAutomaticLocked(automaticLocked);
        }
        
        Terrain aTerrain;
        Enumeration eTerrains = terrains.elements();
        while (eTerrains.hasMoreElements())
        {
            aTerrain = (Terrain) eTerrains.nextElement();
            aTerrain.setAutomaticLocked(automaticLocked);
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
        Species aSpecies;
        Enumeration eSpecies = species.elements();
        while (eSpecies.hasMoreElements())
        {
            aSpecies = (Species) eSpecies.nextElement();
            aSpecies.setManualLocked(manualLocked);
        }

        Environment anEnvironment;
        Enumeration eEnvironments = environments.elements();
        while (eEnvironments.hasMoreElements())
        {
            anEnvironment = (Environment) eEnvironments.nextElement();
            anEnvironment.setManualLocked(manualLocked);
        }

        Organism anOrganism;
        Enumeration eOrganisms = organisms.elements();
        while (eOrganisms.hasMoreElements())
        {
            anOrganism = (Organism) eOrganisms.nextElement();
            anOrganism.setManualLocked(manualLocked);
        }

        Family aFamily;
        Enumeration eFamilies = families.elements();
        while (eFamilies.hasMoreElements())
        {
            aFamily = (Family) eFamilies.nextElement();
            aFamily.setManualLocked(manualLocked);
        }
        
        Terrain aTerrain;
        Enumeration eTerrains = terrains.elements();
        while (eTerrains.hasMoreElements())
        {
            aTerrain = (Terrain) eTerrains.nextElement();
            aTerrain.setManualLocked(manualLocked);
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
        Species aSpecies;
        Enumeration eSpecies = species.elements();
        while (eSpecies.hasMoreElements())
        {
            aSpecies = (Species) eSpecies.nextElement();
            aSpecies.setLockedState(lockedState);
        }

        Environment anEnvironment;
        Enumeration eEnvironments = environments.elements();
        while (eEnvironments.hasMoreElements())
        {
            anEnvironment = (Environment) eEnvironments.nextElement();
            anEnvironment.setLockedState(lockedState);
        }

        Organism anOrganism;
        Enumeration eOrganisms = organisms.elements();
        while (eOrganisms.hasMoreElements())
        {
            anOrganism = (Organism) eOrganisms.nextElement();
            anOrganism.setLockedState(lockedState);
        }

        Family aFamily;
        Enumeration eFamilies = families.elements();
        while (eFamilies.hasMoreElements())
        {
            aFamily = (Family) eFamilies.nextElement();
            aFamily.setLockedState(lockedState);
        }
        
        Terrain aTerrain;
        Enumeration eTerrains = terrains.elements();
        while (eTerrains.hasMoreElements())
        {
            aTerrain = (Terrain) eTerrains.nextElement();
            aTerrain.setLockedState(lockedState);
        }
    }

    /**
     * Returns an enumeration over the vector of species in this world.<p>
     *
     * @return		Enumeration - an enumeration over the species in this world
    **/
    public Enumeration getSpecies()
    {
        return species.elements();
    }

	public void initLoadImages(){
		initLoadImages("all");
	}
	
	class ImageInitializer implements Runnable{
		String s;
		Enumeration species;
		ImageInitializer(String s,Enumeration species){
			this.s = s;
			this.species = species;
		}
		public void run(){
			Enumeration species = getSpecies();
			if(species == null) return;
			int needSpeciesSize 	= SpeciesImage.UNKNOWN_IMAGE_SIZE;
			boolean isOK = false;
			if(s.equals("all")){
				isOK 	= true;
			}else{
				for(int i = 0; i < SpecieFileDesc.sizeNames.length; i++){
					if(SpecieFileDesc.sizeNames[i].equals(s)){
						needSpeciesSize = i;
						isOK = true;
						break;
					}
				}
			}
			if(!isOK){
				java.awt.Toolkit.getDefaultToolkit().beep();
				return;
			}
	        while (species.hasMoreElements()){
	            Species aSpecies = (Species) species.nextElement();
				 Enumeration speciesImages = aSpecies.getSpeciesImages();
				 if(speciesImages == null) continue;
				 while(speciesImages.hasMoreElements()){
	            	SpeciesImage aSpeciesImage = (SpeciesImage) speciesImages.nextElement();
	            	if(aSpeciesImage == null) continue;
	            	aSpeciesImage.initImageDownloading(needSpeciesSize,null);
				 }
	        }
		}
	}
	
	
	public void initLoadImages(String s){
		if(s == null) return;
		
		javax.swing.SwingUtilities.invokeLater(new ImageInitializer(s,getSpecies()));
		
	}

    /**
     * Return the number of species.<p>
     *
     * @return		int - the number of species in this world
    **/
    public int getNumberOfSpecies()
    {
        return species.size();
    }

    /**
     * Adds a species to the world.<p>
     *
     * Package protected because this is only called from the Species
     * constructor.  Creating a species automatically adds it to its
     * world.<p>
     *
     * Automatically fires an EngineProp.SPECIES_ADDED property change
     * event.  If current species is null, the added species is set to the
     * current species and an EngineProp.CURRENT_SPECIES property change
     * event is fired.<p>
     *
     * @param		aSpecies Species - a new species, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addSpecies(Species aSpecies)
    {
        if (aSpecies == null)
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

        // Create species vector if it's null
        if (species == null)
        {
            species = new Vector();
        }

        species.addElement(aSpecies);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.SPECIES_ADDED,null,aSpecies);

        // If currentSpecies is null, make this new species the current species
        if (currentSpecies == null)
        {
            currentSpecies = aSpecies;
            changes.firePropertyChange(EngineProp.CURRENT_SPECIES,null,aSpecies);
        }
    }

    /**
     * Removes a species from the world.<p>
     *
     * Package protected because this is only called from the Species
     * delete method.  Deleting a species automatically removes it from
     * its world.<p>
     *
     * This method returns without doing anything if aSpecies is null.<p>
     *
     * When a Species object is deleted and removed from this vector,
     * a EngineProp.SPECIES_REMOVED property change event is fired.<p>
     * If the deleted species is also the current species, then an
     * EngineProp.CURRENT_SPECIES property change event is also fired.<p>
     *
     * @param		aSpecies Species - a species, may be null
     * @return		boolean indicating whether or not the species was found and removed
    **/
    boolean removeSpecies(Species aSpecies)
    {
        // Return immediately if this world is deleted
        if (deleted)
        {
            return false;
        }

        // Return immediately if aSpecies or species null
        if (aSpecies == null || species == null)
        {
            return false;
        }

        // If the removed species is the current species,
        // try to find another species to make current or
        // at least change the currentSpecies to null,
        // notifying listeners.
        if (aSpecies == currentSpecies)
        {
            // Try to find another species to make the current one
            Species anotherSpecies = null;
            Enumeration eSpecies = species.elements();
            while (eSpecies.hasMoreElements())
            {
                anotherSpecies = (Species) eSpecies.nextElement();
                if (anotherSpecies == aSpecies)
                {
                    // Can't use this species, as it's the species being removed
                    anotherSpecies = null;
                }
                else
                {
                    // Use this species, so break out of loop
                    break;
                }
            }

            currentSpecies = anotherSpecies;
            changes.firePropertyChange(EngineProp.CURRENT_SPECIES,aSpecies,anotherSpecies);
        }

        // Try to remove the species
        boolean result = species.removeElement(aSpecies);
        
        // Notify listeners if species removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.SPECIES_REMOVED,null,aSpecies);
        }

        return result;
    }

    /**
     * Get the current species.
     *
     * @return		Species - current species in this world, may be null
    **/
    public Species getCurrentSpecies()
    {
        return currentSpecies;
    }

    /**
     * Set the current species.
     *
     * @param		aSpecies Species - a species, may be null
    **/
    public void setCurrentSpecies(Species aSpecies)
    {
        // If this isn't a change, return immediately
        if (currentSpecies == aSpecies)
        {
            return;
        }

        // Change current species and notify
        Species oldCurrentSpecies = currentSpecies;
        currentSpecies = aSpecies;
        changes.firePropertyChange(EngineProp.CURRENT_SPECIES,oldCurrentSpecies,currentSpecies);
    }

    /**
     * Returns an enumeration over the vector of environments in this engine.<p>
     *
     * @return		Enumeration - an enumeration over the environments in this engine
    **/
    public Enumeration getEnvironments()
    {
        Vector environmentsClone = (Vector) environments.clone();
        return environmentsClone.elements();
    }

    /**
     * Return the number of environments.<p>
     *
     * @return		int - the number of environments in this engine
    **/
    public int getNumberOfEnvironments()
    {
        return environments.size();
    }

    /**
     * Adds an environment to the world.<p>
     *
     * Package protected because this is only called from the Environment
     * constructor.  Creating an environment automatically adds it to its
     * world.<p>
     *
     * @param		anEnvironment Environment - a new environment, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addEnvironment(Environment anEnvironment)
    {
        environments.addElement(anEnvironment);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.ENVIRONMENT_ADDED,null,anEnvironment);
    }

    /**
     * Removes an environment from the world.<p>
     *
     * Package protected because this is only called from the Environment
     * delete method.  Deleting an environment automatically removes it from
     * its world.<p>
     *
     * This method returns without doing anything if anEnvironment is null.<p>
     *
     * @param		anEnvironment Environment - an environment, may be null
     * @return		boolean indicating whether or not the environment was found and removed
    **/
    boolean removeEnvironment(Environment anEnvironment)
    {
        // Return immediately if this world is deleted
        if (deleted)
        {
            return false;
        }

        // Return immediately if anEnvironment or environments null
        if (anEnvironment == null || environments == null)
        {
            return false;
        }

        boolean result = environments.removeElement(anEnvironment);
        
        // Notify listeners if environment removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.ENVIRONMENT_REMOVED,null,anEnvironment);
        }

        return result;
    }

    /**
     * Return the number of organisms.<p>
     *
     * @return		int - the number of organisms in this world
    **/
    public int getNumberOfOrganisms()
    {
        return organisms.size();
    }

    /**
     * Returns an enumeration over the vector of organisms in this world.<p>
     *
     * @return		Enumeration - an enumeration over the organisms in this world
    **/
    public Enumeration getOrganisms()
    {
        if (organisms == null)
        {
            Vector v = new Vector();
            return v.elements();
        }

        Vector organismsClone = (Vector) organisms.clone();
        return organismsClone.elements();
    }

    /**
     * Adds a organism to the world.<p>
     *
     * Package protected because this is only called from the Organism
     * constructor.  Creating a organism automatically adds it to the
     * engine by calling this method.<p>
     *
     * @param		anOrganism Organism - a new organism, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addOrganism(Organism anOrganism)
    {
        if (anOrganism == null ||
            organisms == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
            
        // Okay - add element
        organisms.addElement(anOrganism);
            
        // Lock the species of the organism
        Species species = anOrganism.getSpecies();
        if (species.isAutomaticLocked() == false)
        {
            species.setAutomaticLocked(true);
        }
        // Notify listeners
        changes.firePropertyChange(EngineProp.ORGANISM_ADDED,null,anOrganism);
    }

    /**
     * Removes an organism from the world.<p>
     *
     * Package protected because this is only called from the Organism
     * delete method.  Deleting a organism automatically removes it from
     * the world.<p>
     *
     * This method returns without doing anything if anOrganism is null.<p>
     *
     * @param		anOrganism Organism - an organism, may be null
     * @return		boolean indicating whether or not the organism was found and removed
    **/
    boolean removeOrganism(Organism anOrganism)
    {
        // Return immediately if this world is deleted
        if (deleted)
        {
            return false;
        }

        // Return immediately if anOrganism or organisms null
        if (anOrganism == null || organisms == null)
        {
            return false;
        }

        boolean result = organisms.removeElement(anOrganism);

        Species aSpecies = anOrganism.getSpecies();

        // If we've gone back to zero organisms, unlock species
        if (organisms.size() == 0 &&
            aSpecies.isAutomaticLocked())
        {
            aSpecies.setAutomaticLocked(false);
        }

        // Notify listeners if organism removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.ORGANISM_REMOVED,null,anOrganism);
        }
        anOrganism.delete();
		anOrganism = null;
        return result;
    }

    /**
     * Return the number of families.<p>
     *
     * @return		int - the number of families in this engine
    **/
    public int getNumberOfFamilies()
    {
        return families.size();
    }

    /**
     * Returns an enumeration over the vector of families in this engine.<p>
     *
     * @return		Enumeration - an enumeration over the families in this engine
    **/
    public Enumeration getFamilies()
    {
        if (families == null)
        {
            Vector v = new Vector();
            return v.elements();
        }

        Vector familiesClone = (Vector) families.clone();
        return familiesClone.elements();
    }

    /**
     * Adds a family to the world.<p>
     *
     * Package protected because this is only called from the Family
     * constructor.  Creating a family automatically adds it to the
     * world by calling this method.<p>
     *
     * @param		aFamily Family - a new family, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addFamily(Family aFamily)
    {
        if (aFamily == null ||
            families == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
            
        // Okay - add element
        families.addElement(aFamily);
            
        // Notify listeners
        changes.firePropertyChange(EngineProp.FAMILY_ADDED,null,aFamily);
    }

    /**
     * Removes a family from the world.<p>
     *
     * Package protected because this is only called from the Family
     * delete method.  Deleting a family automatically removes it from
     * the world.<p>
     *
     * This method returns without doing anything if aFamily is null.<p>
     *
     * @param		aFamily Family - a family, may be null
     * @return		boolean indicating whether or not the family was found and removed
    **/
    boolean removeFamily(Family aFamily)
    {
        // Return immediately if this world is deleted
        if (deleted)
        {
            return false;
        }

        // Return immediately if aFamily or families null
        if (aFamily == null || families == null)
        {
            return false;
        }

        boolean result = families.removeElement(aFamily);

        // Notify listeners if family removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.FAMILY_REMOVED,null,aFamily);
        }

        return result;
    }

    /**
     * Return the number of terrains of this world.<p>
     *
     * @return		int - the number of terrains of this world
    **/
    public int getNumberOfTerrains()
    {
        return terrains.size();
    }

    /**
     * Returns an enumeration over the world's terrains.<p>
     *
     * If there are no terrains in this world, an enumeration with no elements is returned.<p>
     *
     * @return		Enumeration - an enumeration over the vector of terrains of this world, never null
    **/
    public Enumeration getTerrains()
    {
        if (terrains == null)
        {
            Vector dummyVector = new Vector();
            return dummyVector.elements();
        }

        Vector terrainsClone = (Vector) terrains.clone();
        return terrainsClone.elements();
    }

    /**
     * Adds a terrain to this world.  Does not check if this terrain
     * or an equivalent terrain already has been added to this world.<p>
     *
     * This method is called from the Terrain constructors when a new
     * terrain is created.  This method should not be called any other time,
     * as we wanted to limit an terrain to only being in one world.<p>
     *
     * Automatically fires an EngineProp.TERRAIN_ADDED property change
     * event.<p>
     *
     * @param		aTerrain Terrain - the terrain to add to this world, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    void addTerrain(Terrain aTerrain)
    {
        // Do coarse check of input values
        if (aTerrain == null)
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

        // Create terrains vector if it's null
        if (terrains == null)
        {
            terrains = new Vector();
        }

        terrains.addElement(aTerrain);

        // Notify listeners
        changes.firePropertyChange(EngineProp.TERRAIN_ADDED,null,aTerrain);
    }

    /**
     * Removes a terrain from this world.<p>
     *
     * This method should not be called yet, as we haven't determined
     * how objects are destroyed yet in this engine.  There's a good
     * chance that we'll add destroy() methods to all engine classes
     * eventually, which would cause this method to be called. <p>
     *
     * This method returns without doing anything if aTerrain is null.<p>
     *
     * When a Terrain object is deleted and removed from this vector,
     * a EngineProp.TERRAIN_REMOVED property change event is fired.<p>
     *
     * @param		aTerrain Terrain - the terrain to remove from this world, may be null
     * @return		boolean - returns true if element found, false otherwise
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    boolean removeTerrain(Terrain aTerrain)
    {
        // Return immediately if this world is deleted
        if (deleted)
        {
            return false;
        }

        // Return immediately if aTerrain or terrains null
        if (aTerrain == null || terrains == null)
        {
            return false;
        }
        
        // Try to remove the terrain
        boolean result = terrains.removeElement(aTerrain);
        
        // Notify listeners if terrain removed
        if (result == true)
        {
            // Notify the world's environments first - implied listeners
            Environment anEnvironment;
            Enumeration eEnvironments = environments.elements();
            while (eEnvironments.hasMoreElements())
            {
                anEnvironment = (Environment) eEnvironments.nextElement();
                anEnvironment.removeTerrain(aTerrain);
            }

            // Notify other listeners
            changes.firePropertyChange(EngineProp.TERRAIN_REMOVED,null,aTerrain);
        }

        return result;
    }

    /**
     * Save As the world state to the new given file.<p>
     *
     * @param		aFile File - file to use in saving
     * @exception	IllegalArgumentException - file not a valid file
    **/
    public void saveAs(File aFile)
    {
        if (aFile == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Save old file properties and set new file properties
        File oldFile = file;
        file = aFile;

        // Do normal save
        save();

        // Fire event if file changed
        if (file != oldFile)
        {
            changes.firePropertyChange(EngineProp.FILE,oldFile,file);
        }
    }

    /**
     * Save the world state to the current file.<p>
    **/
    public void save()
    {
        if (file == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        else
        {
            // Try to save to the file
            FileOutputStream fileOutputStream = null;
            PrintWriter stream = null;
        
            try
            {
                // Create stream
                fileOutputStream = new FileOutputStream(file);
                stream = new PrintWriter(fileOutputStream);

                stream.println("<?xml version=\"1.0\" encoding=\"us-ascii\"?>");
                stream.println("");

                // Write this engine state to the stream as XML 
                writeToStream(stream);
                stream.flush();
            }
            catch (IOException e1)
            {
                System.err.println(EngineStrings.CANNOT_CREATE_FILE_OUTPUT_STREAM_DUE_TO_EXCEPTION + e1);
            }
            catch (SecurityException e2)
            {
                System.err.println(EngineStrings.CANNOT_CREATE_FILE_OUTPUT_STREAM_DUE_TO_EXCEPTION + e2);
            }
            finally
            {
                try
                {
                    if (fileOutputStream != null)
                    {
                        fileOutputStream.close();
                    }
                }
                catch (IOException e3)
                {
                    System.err.println(EngineStrings.CANNOT_CLOSE_FILE_OUTPUT_STREAM_DUE_TO_EXCEPTION + e3);
                }
            }
        }
    }

    /**
     * Private method to open the current file and retrieve its data into this world.<p>
     *
     * Normally this method is called only from the World constructor that
     * takes a File argument.<p>
     *
     * @exception 	InternalEngineException - file was null
    **/
    private void open()
    {
        boolean succeeding = true;

        if ((file == null) && (url == null))
        {
            throw new InternalEngineException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (file != null)
        {
            if (file.exists() == false)
            {
                System.err.println(EngineStrings.CANNOT_FIND_FILE + file.getAbsolutePath());
                succeeding = false;
            }
            else if (file.canRead() == false)
            {
                System.err.println(EngineStrings.CANNOT_READ_FILE + file.getAbsolutePath());
                succeeding = false;
            }
        }

        if (succeeding)
        {
            try
            {
                InputSource inputSource = null;
                
                // Create input source
                if (file instanceof File){
//                    inputSource = Resolver.createInputSource(file);
                    inputSource = new InputSource(new java.io.FileInputStream(file));//dima
                }
                if (url instanceof URL){
 //                   inputSource = Resolver.createInputSource(url, false);
                    inputSource = new InputSource(url.openStream());
                }
                   
                // Create parser
                if (xmlParser == null)
                {
//                    xmlParser = new com.sun.xml.parser.Parser();
                    xmlParser = new SAXParser();//dima
                }

                // Create import context to track id mappings
                importContext = new ImportContext();

                // Create xml element context to manage internal element parsing
                xmlElementContext = new ElementContext(this,xmlParser,importContext);

                // Set the first document handler to this object
                xmlParser.setDocumentHandler(this);

                // Start parsing
                xmlParser.parse(inputSource);
            }
            catch (Exception e1)
            {
                System.err.println(EngineStrings.ERROR_PARSING_XML_FILE + e1);
                if (e1 instanceof SAXParseException)
                {
                    SAXParseException spe = (SAXParseException) e1;
                    System.err.println("Line number = " + spe.getLineNumber());
                    System.err.println("Column number = " + spe.getColumnNumber());
                    System.err.println("Public ID = " + spe.getPublicId());
                    System.err.println("System ID = " + spe.getSystemId());
                }
                succeeding = false;
            }
        }
    }

    /**
     * Handle notification that a new document parse has started.
    **/
    public void startDocument()
    throws SAXException
    {
    }

    /**
     * Handle notification that a document parse has ended.
    **/
    public void endDocument()
    throws SAXException
    {
        // Throw import context away
        importContext = null;

        // Throw element context away
        xmlElementContext = null;
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
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.ENVIRONMENT_ELEMENT_ID:
                new Environment(this,anElementName,id,xmlParser,importContext);
                break;

            case Elements.FAMILY_ELEMENT_ID:
                new Family(this,anElementName,id,xmlParser,importContext);
                break;

            case Elements.ORGANISM_ELEMENT_ID:
                new Organism(this,anElementName,id,xmlParser,importContext);
                break;

            case Elements.SPECIES_ELEMENT_ID:
                new Species(this,anElementName,id,xmlParser,importContext);
                break;

            case Elements.TERRAIN_ELEMENT_ID:
                new Terrain(this,anElementName,id,xmlParser,importContext);
                break;

            case Elements.WORLD_ELEMENT_ID:
                // Expected.  Do nothing but wait for nested start element events
                break;

            default:
                throw new IllegalArgumentException("World " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
                    importContext.addObject(this,oldID);
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

            case Elements.ENVIRONMENT_ELEMENT_ID:
            case Elements.FAMILY_ELEMENT_ID:
            case Elements.ORGANISM_ELEMENT_ID:
            case Elements.SPECIES_ELEMENT_ID:
            case Elements.TERRAIN_ELEMENT_ID:
            case Elements.TRAIT_ELEMENT_ID:
                // Done with a child object, so reclaim document handler
                break;

            case Elements.WORLD_ELEMENT_ID:
                // End of the file.  Do something here?
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
            xmlParser.setDocumentHandler(this);
        }
    }

    /**
     * Close the current engine, which is identical to delete -- I think!
    **/
    public void close()
    {
        delete();
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
        stream.println("<" + Elements.WORLD_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                       "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                       "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                       "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Locked State
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Write species
        {
            Species aSpecies;
            Enumeration eSpecies = species.elements();
            while (eSpecies.hasMoreElements())
            {
                aSpecies = (Species) eSpecies.nextElement();
                aSpecies.writeToStream(stream);
            }
        }

        // Write terrains
        {
            Terrain aTerrain;
            Enumeration eTerrains = terrains.elements();
            while (eTerrains.hasMoreElements())
            {
                aTerrain = (Terrain) eTerrains.nextElement();
                aTerrain.writeToStream(stream);
            }
        }

        // Write environments
        {
            Environment anEnvironment;
            Enumeration eEnvironments = environments.elements();
            while (eEnvironments.hasMoreElements())
            {
                anEnvironment = (Environment) eEnvironments.nextElement();
                anEnvironment.writeToStream(stream);
            }
        }

        // Write organisms
        {
            Organism anOrganism;
            Enumeration eOrganisms = organisms.elements();
            while (eOrganisms.hasMoreElements())
            {
                anOrganism = (Organism) eOrganisms.nextElement();
                anOrganism.writeToStream(stream);
            }
        }

        // Write families
        {
            Family aFamily;
            Enumeration eFamilies = families.elements();
            while (eFamilies.hasMoreElements())
            {
                aFamily = (Family) eFamilies.nextElement();
                aFamily.writeToStream(stream);
            }
        }

        // End of world
        stream.println("</" + Elements.WORLD_ELEMENT_NAME + ">");
    }
}
