//
// Class : SpeciesImage
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.15 $
// $Date: 2003/01/28 18:40:14 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.PrintWriter;

import java.net.URL;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;
import javax.swing.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Properties;

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

import org.concord.biologica.datasupport.*;

/**
 * This class represents an image containing some of the graphics for drawing
 * the organisms of a particular species.  Every species must have one or
 * more of these image objects in order to draw its organisms.<p>
 *
 * All the images for a given species MUST use the same cell size - the width
 * and height of cells in the tables shown in the pictures below.  So the
 * image column width and image row height numbers are kept in the Species
 * object, not here.  A Species' column width and row height apply to all
 * the SpeciesImage objects in that Species.<p>
 *
 * Essentially this class maps phenotypes to drawing operations for the
 * species.  A species image is a collection of species image cells in
 * rows and columns as shown below.  Each row and column has one or more
 * characteristics associated with it.  An image cell - the portion of the
 * species image at a given row and column - is the image to be drawn
 * for an organism with the characteristics of that column and row.<p>
 *
 * For convenience in managing the characteristics associated with
 * each row and column, a SpeciesImage object has a collection of
 * SpeciesImageColumn and another collection SpeciesImageRow objects.
 * These SpeciesImageColumn and SpeciesImageRow objects each represent
 * one column or row in the SpeciesImage and manage the characteristics
 * for that column or row.<p>
 *
 *               <-- Species Image Columns -->
 *		+----------+----------+----------+----------+----->
 *  	|          |          |          |          |
 *  ^ 	|  Image   |  Image   |  Image   |  Image   |  Images -->
 *  |	|  (0,0)   |  (0,1)   |  (0,2)   |  (0,3)   |
 *  |	|          |          |          |          |
 *  	|          |          |          |          |
 *  S	+----------+----------+----------+----------+----->
 *  p	|          |          |          |          |
 *  e	|  Image   |  Image   |  Image   |  Image   |
 *  c	|  (1,0)   |  (1,1)   |  (1,2)   |  (1,3)   |
 *  i	|          |          |          |          |
 *  e	|          |          |          |          |
 *  s	+----------+----------+----------+----------+----->
 *  	|          |          |          |          |
 *  I 	|  Image   |          |          |          |
 *	m   |  (2,0)   |  ...     |          |          |
 *  a	|          |          |          |          |
 *  g	|          |          |          |          |
 *  e	+----------+----------+----------+----------+----->
 *  	|          |          |          |          |
 *  R	|          |          |          |          |
 *  o	|  ...     |          |          |          |
 *  w	|          |          |          |          |
 *  s	|          |          |          |          |
 *  	+----------+----------+----------+----------+----->
 *  |	|          |          |          |          |
 *  | 	|          |          |          |          |
 *  |	|          |          |          |          |
 *  |	|          |          |          |          |
 *  v	|          |          |          |          |
 *  	+----------+----------+----------+----------+----->
 *  	|          |          |          |          |
 *  	|          |          |          |          |
 *  	v          v          v          v          v
 *
 * Each of the above cells is the same size and contains
 * either a complete image for an organism or a partial image.<p>
 *
 * For example, for humans, the above SpeciesImage might have
 * only 2 cells - a male image and a female image - and each
 * cell would contain the complete image to be drawn.<p>
 *
 * Another example, for dragons, a SpeciesImage might have
 * many, many cells where the color characteristics are
 * across the top (e.g. Blue, Brown, Gold, Green, Purple, Yellow)
 * and other characteristics would be down the left side (e.g. Dead,
 * Plain Tail, Fancy Tail, Arrow Tail, etc.)  So drawing a dragon
 * means determining the characteristics of the dragon and then
 * picking out and drawing the correct cells from the dragon
 * SpeciesImage's.  A second SpeciesImage for dragon might have
 * the head images for the dragon.  A third SpeciesImage for
 * dragon might have the wing types, etc.<p>
 *
 * So a dragon Bodies SpeciesImage might look something like:
 *
 *            Blue       Brown      Green      Gold
 *		   +----------+----------+----------+----------+
 *    D	   |          |          |          |          |
 *    e	   |  Blue +  |  Brown + |  Green + |  Gold +  |
 *    a	   |  Dead    |  Dead    |  Dead    |  Dead    |
 *    d	   |  Dragon  |  Dragon  |  Dragon  |  Dragon  |
 *  	   |          |          |          |          |
 *  	   +----------+----------+----------+----------+
 * 4   P   |          |          |          |          |
 * L   l T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + a a |  Plain   |  Plain   |  Plain   |  Plain   |
 * g   i i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   n l |  4 Legs  |  4 Legs  |  4 Legs  |  4 Legs  |
 *  	   +----------+----------+----------+----------+
 * 4   F   |          |          |          |          |
 * L   a T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + n a |  Fancy   |  Fancy   |  Fancy   |  Fancy   |
 * g   c i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   y l |  4 Legs  |  4 Legs  |  4 Legs  |  4 Legs  |
 *  	   +----------+----------+----------+----------+
 * 4   A   |          |          |          |          |
 * L   r T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + r a |  Arrow   |  Arrow   |  Arrow   |  Fancy   |
 * g   o i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   w l |  4 Legs  |  4 Legs  |  4 Legs  |  4 Legs  |
 *  	   +----------+----------+----------+----------+
 * 0   P   |          |          |          |          |
 * L   l T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + a a |  Plain   |  Plain   |  Plain   |  Plain   |
 * g   i i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   n l |  0 Legs  |  0 Legs  |  0 Legs  |  0 Legs  |
 *  	   +----------+----------+----------+----------+
 * 0   F   |          |          |          |          |
 * L   a T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + n a |  Fancy   |  Fancy   |  Fancy   |  Fancy   |
 * g   c i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   y l |  0 Legs  |  0 Legs  |  0 Legs  |  0 Legs  |
 *  	   +----------+----------+----------+----------+
 * 0   A   |          |          |          |          |
 * L   r T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + r a |  Arrow   |  Arrow   |  Arrow   |  Fancy   |
 * g   o i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   w l |  0 Legs  |  0 Legs  |  0 Legs  |  0 Legs  |
 *  	   +----------+----------+----------+----------+
 * 2   P   |          |          |          |          |
 * L   l T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + a a |  Plain   |  Plain   |  Plain   |  Plain   |
 * g   i i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   n l |  2 Legs  |  2 Legs  |  2 Legs  |  2 Legs  |
 *  	   +----------+----------+----------+----------+
 * 2   F   |          |          |          |          |
 * L   a T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + n a |  Fancy   |  Fancy   |  Fancy   |  Fancy   |
 * g   c i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   y l |  2 Legs  |  2 Legs  |  2 Legs  |  2 Legs  |
 *  	   +----------+----------+----------+----------+
 * 2   A   |          |          |          |          |
 * L   r T |  Blue +  |  Brown + |  Green + |  Gold +  |
 * e + r a |  Arrow   |  Arrow   |  Arrow   |  Fancy   |
 * g   o i |  Tail +  |  Tail +  |  Tail +  |  Tail +  |
 * s   w l |  2 Legs  |  2 Legs  |  2 Legs  |  2 Legs  |
 *  	   +----------+----------+----------+----------+
 *
 * Drawing a particular dragon organism means drawing
 * 1 or more of the image cells from 1 or more SpeciesImage's.
 * For a dead dragon, we'd draw just one cell from one
 * SpeciesImage (because all dead dragons look the same
 * or all the variations in one SpeciesImage).  For a live
 * dragon, we'd draw 4 to 6 cells from multiple SpeciesImage
 * depending on the phenotype of the dragon.<p>
 *
 * In general, drawing an organism of a species amounts to:<p>
 *
 * <ul>
 * <li>Use the species' GenotypeToPhenotypeRule objects to determine
 *     the phenotype (collection of characteristics) of an organism.
 *
 * <li>Using the collection of characteristics of the organism
 *     and the SpeciesImage's for the species, create a list
 *     of the image cells in the SpeciesImage's that should be drawn
 *     for that organism.
 *
 * <li>Store that list of image cells in an OrganismImage object
 *     in the organism.
 *
 * <li>Draw the organism by asking the OrganismImage to draw itself.
 * </ul>
 *
 * The actual GIF image file corresponding to a SpeciesImage object MUST, MUST
 * be in the file named:
 *
 *		<BioLogica Main Directory>/species/<species name>/<species image name>_<image size>.gif
 *
 * For example, the GIF image for a SpeciesImage of the name "bodies" of the large size
 * in the species "dragon" must be in the file:
 *
 *		<BioLogica Main Directory>/species/dragon/bodies_large.gif
 *
 * Sorry about this restrictiveness, but it's the only simple way I could think to
 * avoid machine or platform dependent image paths.
 *
 * There are 6 image sizes:
 *
 *		SpeciesImage.XXSMALL_IMAGE_SIZE		- xxsmall
 *		SpeciesImage.XSMALL_IMAGE_SIZE		- xsmall
 *		SpeciesImage.SMALL_IMAGE_SIZE		- small
 *		SpeciesImage.MEDIUM_IMAGE_SIZE		- medium
 *		SpeciesImage.LARGE_IMAGE_SIZE		- large
 *		SpeciesImage.XLARGE_IMAGE_SIZE		- xlarge
 *
 * and each size must have a file with the appropriate name.  For example, the dragon
 * species has the following 5 files for bodies:
 *
 *		<BioLogica Main Directory>/species/dragon/bodies_xxsmall.gif
 *		<BioLogica Main Directory>/species/dragon/bodies_xsmall.gif
 *		<BioLogica Main Directory>/species/dragon/bodies_small.gif
 *		<BioLogica Main Directory>/species/dragon/bodies_medium.gif
 *		<BioLogica Main Directory>/species/dragon/bodies_large.gif
 *		<BioLogica Main Directory>/species/dragon/bodies_xlarge.gif
 *
 * The species creator determines the exact sizes of the images for each size.  Every
 * image must have EXACTLY the same number of columns and rows and the same rules for
 * pulling images from the files.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.HOTSPOT_COLOR - hotspot color has changed
 * <li> EngineProp.HOTSPOT_RADIUS - hotspot radius has changed
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.IMAGE_TYPE - the image type has changed
 * <li> EngineProp.LOCKED_STATE - the object's locked state has changed
 * <li> EngineProp.NAME - name of this species image has changed
 * <li> EngineProp.SPECIES_IMAGE_COLUMN_ADDED - column added
 * <li> EngineProp.SPECIES_IMAGE_COLUMN_REMOVED - column removed
 * <li> EngineProp.SPECIES_IMAGE_ROW_ADDED - row added
 * <li> EngineProp.SPECIES_IMAGE_ROW_REMOVED - row removed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#HOTSPOT_COLOR
 * @see org.concord.biologica.engine.EngineProp#HOTSPOT_RADIUS
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#IMAGE_TYPE
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_COLUMN_ADDED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_COLUMN_REMOVED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_ROW_ADDED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_ROW_REMOVED
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.15 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class SpeciesImage
extends EngineObject
implements Serializable
{
    /**
     * Image sizes, generally used as follows:<p>
     *
     * 	XXSMALL_IMAGE_SIZE - default in population view, often used in pedigree, impossible to discern phenotype
     * 	XSMALL_IMAGE_SIZE - default in pedigree view, barely possible to discern phenotype
     *  SMALL_IMAGE_SIZE - smallest where you can easily discern phenotype
     *  MEDIUM_IMAGE_SIZE - used sometimes in multiple and single organism views, may discern phenotype
     *  LARGE_IMAGE_SIZE - default size in multiple and single organism views, easily discern phenotype
     *  XLARGE_IMAGE_SIZE - extra large size for special circumstances
    **/
	static final public int	UNKNOWN_IMAGE_SIZE	= -1;
    static final public int XXSMALL_IMAGE_SIZE	= 0;
    static final public int XSMALL_IMAGE_SIZE	= 1;
    static final public int SMALL_IMAGE_SIZE	= 2;
    static final public int MEDIUM_IMAGE_SIZE	= 3;
    static final public int LARGE_IMAGE_SIZE	= 4;
    static final public int XLARGE_IMAGE_SIZE	= 5;

    /**
     * Possible image types:<p>
     *
     * NORMAL_IMAGE_TYPE - normal, phenotype image type, shown in all cases for an organism
     * HOTSPOT_IMAGE_TYPE - an image shown for the organism when a hotspot is pressed
     * SCOPE_IMAGE_TYPE - an image shown when an organism is clicked on with the scope tool
     * INVISIBLE_IMAGE_TYPE - an image shown when an organism is invisible
    **/
    static final public int NORMAL_IMAGE_TYPE		= 30;
    static final public int HOTSPOT_IMAGE_TYPE		= 31;
    static final public int SCOPE_IMAGE_TYPE		= 32;
    static final public int INVISIBLE_IMAGE_TYPE	= 33;

    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * File separator for pathnames
    **/
    static private String pathSeparator = File.separator;
    
    static private boolean imageVisible = true;

    /**
     * Name of this species image, to which we append a suffix "_xsmall.gif",
     * "_small.gif", etc. to get the actual GIF filename.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * The species which contains this image.  May not be null.<p>
    **/
    private Species				species;

    /**
     * Vector of SpeciesImageColumn objects in column order.
    **/
    private Vector				speciesImageColumns;

    /**
     * Vector of SpeciesImageRow objects in row order.
    **/
    private Vector				speciesImageRows;

    /**
     * XXSmall image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private SpeciesImageIcon				xxSmallIcon;

    /**
     * XSmall image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private SpeciesImageIcon				xSmallIcon;

    /**
     * Small image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private SpeciesImageIcon				smallIcon;

    /**
     * Medium image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private SpeciesImageIcon				mediumIcon;

    /**
     * Large image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private SpeciesImageIcon				largeIcon;

    /**
     * XLarge image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private SpeciesImageIcon				xLargeIcon;
    
    private String				xxSmallDataSource;//dima
    private String				xSmallDataSource;
    private String				smallDataSource;
    private String				mediumDataSource;
    private String				largeDataSource;
    private String				xLargeDataSource;

	private int					partID = SpecieFileDesc.PART_UNKNOWN;

    private boolean				isImageDataSourceExist = false;//dima
    private static Properties	biologicaProperties = null;
    
    
    private String				xxSmallSource;

    /**
     * XSmall image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private String				xSmallSource;

    /**
     * Small image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private String				smallSource;

    /**
     * Medium image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private String				mediumSource;

    /**
     * Large image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private String				largeSource;

    /**
     * XLarge image file.   Transient, meaning it's not saved out to a file
     * when a species is saved.
    **/
    private String				xLargeSource;
    
    /**
     * Store whether using file or URL for image data.
    **/
    private boolean				haveFile;

    /**
     * XXSmall image itself.  Transient.  Loaded the first time getImage()                                                       * is called.  Keeping it here means it only needs to be loaded
     * once per process.
    **/
    private Image				xxSmallImage;

    /**
     * XSmall image itself.  Transient.  Loaded the first time getImage()                                                       * is called.  Keeping it here means it only needs to be loaded
     * once per process.
    **/
    private Image				xSmallImage;

    /**
     * Small image itself.  Transient.  Loaded the first time getImage()                                                       * is called.  Keeping it here means it only needs to be loaded
     * once per process.
    **/
    private Image				smallImage;

    /**
     * Medium image itself.  Transient.  Loaded the first time getImage()                                                       * is called.  Keeping it here means it only needs to be loaded
     * once per process.
    **/
    private Image				mediumImage;

    /**
     * Large image itself.  Transient.  Loaded the first time getImage()                                                       * is called.  Keeping it here means it only needs to be loaded
     * once per process.
    **/
    private Image				largeImage;

    /**
     * XLarge image itself.  Transient.  Loaded the first time getImage()                                                       * is called.  Keeping it here means it only needs to be loaded
     * once per process.
    **/
    private Image				xLargeImage;

    /**
     * Image type - normal, hotspot, scope?
    **/
    private int					imageType;

    /**
     * Hotspot color
    **/
    private Color				hotspotColor = Color.red;

    /**
     * Hotspot radius
    **/
    private int					hotspotRadius = 3;


    /**
     * Creates a new species image given a species and a name.<p>
     *
     * @param		aSpecies Species - the species containing this image, may not be null
     * @param		aName String - the species image name, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public SpeciesImage(Species aSpecies, String aName)
    {
        // Check input arguments
    	initBiologicaProperties(this,aSpecies);
        if (aSpecies == null || aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        species = aSpecies;

        // Set instance variables
        name = new String(aName);
        haveFile = aSpecies.getWorld().getFile() instanceof File;
        initializeImageSource(name);	
        xxSmallImage = null;
        xSmallImage = null;
        smallImage = null;
        mediumImage = null;
        largeImage = null;
        xLargeImage = null;

        lockedState = EngineObject.UNLOCKED;
        speciesImageColumns = new Vector();
        speciesImageRows = new Vector();

        imageType = NORMAL_IMAGE_TYPE;
        hotspotColor = Color.red;
        hotspotRadius = 3;

        // Creation successful - tell species
        species.addSpeciesImage(this);
    }

	private static void initBiologicaProperties(SpeciesImage obj,Species aSpecies){
    	if(biologicaProperties == null){
    		biologicaProperties = new Properties();
    		try{
   				biologicaProperties.load(PathStrings.getResourceAsStream(obj,"org/concord/biologica/biologica.properties"));
    		}catch(Throwable t){
    		}
    	}
		try{
			String tempString = biologicaProperties.getProperty(aSpecies.getName(),"false");
			obj.isImageDataSourceExist = ((tempString != null) && tempString.equals("yes"));
		}catch(Throwable t){
			obj.isImageDataSourceExist = false;
		}
	}

    /**
     * Create a new species image and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpecies Species - the enclosing species for this new species image
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public SpeciesImage(Species aSpecies,
                        String anElementName,
                        int anElementID,
                        /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                        ImportContext importContext)
    {
    
    	initBiologicaProperties(this,aSpecies);
        if (aSpecies == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        haveFile = aSpecies.getWorld().getFile() instanceof File;
        
        // Null out image files
        xxSmallIcon = null;
        xSmallIcon = null;
        smallIcon = null;
        mediumIcon = null;
        largeIcon = null;
        xLargeIcon = null;
        
        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        imageType = NORMAL_IMAGE_TYPE;
        hotspotColor = Color.red;
        hotspotRadius = 3;
        name = null;

        // Create vectors
        speciesImageColumns = new Vector();
        speciesImageRows = new Vector();

        // Creation successful, so add this genotype to phenotype rule to species
        species = aSpecies;
        species.addSpeciesImage(this);

        // Create an element context
        xmlElementContext = new ElementContext(this,anXMLParser,importContext);

        // Claim document handler and return, allowing parsing to continue
        anXMLParser.setDocumentHandler(this);
    }

    protected String getImagePathName(String name)
    {
        return "org/concord/biologica/species/" + species.getName() + "/" + name;
    }
    
    protected String getDataPathName(String name)
    {
        return "org/concord/biologica/data/" + species.getName() + "/" + name;
    }
    
    protected void initializeImageSource(String name)
    {
 
 		partID = SpecieFileDesc.getPartByName(name);
 
		xxSmallDataSource = getDataPathName(name + "_xxsmall.dat");//dima
		xSmallDataSource = getDataPathName(name + "_xsmall.dat");//dima
		smallDataSource = getDataPathName(name + "_small.dat");//dima
		mediumDataSource = getDataPathName(name + "_medium.dat");//dima
		largeDataSource = getDataPathName(name + "_large.dat");//dima
		xLargeDataSource = getDataPathName(name + "_xlarge.dat");//dima

    	
    
        xxSmallSource = getImagePathName(name + "_xxsmall.gif");
        xSmallSource = getImagePathName(name + "_xsmall.gif");
        smallSource = getImagePathName(name + "_small.gif");
        mediumSource = getImagePathName(name + "_medium.gif");
        largeSource = getImagePathName(name + "_large.gif");
        xLargeSource = getImagePathName(name + "_xlarge.gif");
    
//        preloadSpeciesImageSize(LARGE_IMAGE_SIZE);
    }
    
    public URL getDataResource(String nameResource){


		return PathStrings.getResource(this, nameResource);

    }
    
    
    public void preloadSpeciesImageSize(int imageSize)
    {
        URL url = null;
        boolean dataImageInCache = (ImageReader.getShortImageDescription(partID,imageSize) != null);
        switch (imageSize)
        {
            case XXSMALL_IMAGE_SIZE:
            	if(isImageDataSourceExist && !dataImageInCache){
            		ImageReader.readFile(getDataResource(xxSmallDataSource),partID,imageSize,null);
           		}else{
	                url = PathStrings.getResource(this, xxSmallSource);
	                xxSmallIcon = new SpeciesImageIcon(url);
	                xxSmallIcon.waitForLoadImage();
            	}
            break;
            case XSMALL_IMAGE_SIZE:
            	if(isImageDataSourceExist && !dataImageInCache){
            		ImageReader.readFile(getDataResource(xSmallDataSource),partID,imageSize,null);
            	}else{
                	url = PathStrings.getResource(this, xSmallSource);
               	 	xSmallIcon = new SpeciesImageIcon(url);
                	xSmallIcon.waitForLoadImage();
                }
            break;
            case SMALL_IMAGE_SIZE:
            	if(isImageDataSourceExist && !dataImageInCache){
            		ImageReader.readFile(getDataResource(smallDataSource),partID,imageSize,null);
            	}else{
                	url = PathStrings.getResource(this, smallSource);
                	smallIcon = new SpeciesImageIcon(url);
                	smallIcon.waitForLoadImage();
                }
            break;
            case MEDIUM_IMAGE_SIZE:
            	if(isImageDataSourceExist && !dataImageInCache){
             		ImageReader.readFile(getDataResource(mediumDataSource),partID,imageSize,null);
          		}else{
               	 	url = PathStrings.getResource(this, mediumSource);
                	mediumIcon = new SpeciesImageIcon(url);
                	mediumIcon.waitForLoadImage();
                }
            break;
            case LARGE_IMAGE_SIZE:
            	if(isImageDataSourceExist && !dataImageInCache){
             		ImageReader.readFile(getDataResource(largeDataSource),partID,imageSize,null);
            	}else{
                	url = PathStrings.getResource(this, largeSource);
                	largeIcon = new SpeciesImageIcon(url);
                	largeIcon.waitForLoadImage();
                }
            break;
            case XLARGE_IMAGE_SIZE:
            	if(isImageDataSourceExist && !dataImageInCache){
             		ImageReader.readFile(getDataResource(xLargeDataSource),partID,imageSize,null);
           		}else{
                	url = PathStrings.getResource(this, xLargeSource);
                	xLargeIcon = new SpeciesImageIcon(url);
                	xLargeIcon.waitForLoadImage();
                }
            break;
        }
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
            case Elements.IMAGE_TYPE_ELEMENT_ID:
            case Elements.HOTSPOT_COLOR_ELEMENT_ID:
            case Elements.HOTSPOT_RADIUS_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.SPECIES_IMAGE_COLUMN_ELEMENT_ID:
                new SpeciesImageColumn(this,anElementName,anElementID,
                                       xmlElementContext.getXMLParser(),
                                       xmlElementContext.getImportContext());
                break;

            case Elements.SPECIES_IMAGE_ROW_ELEMENT_ID:
                new SpeciesImageRow(this,anElementName,anElementID,
                                    xmlElementContext.getXMLParser(),
                                    xmlElementContext.getImportContext());
                break;
			case Elements.IMAGE_DATA_SOURCE_SUPPORT_ELEMENT_ID:
				if(amap != null){
					String dataSupport = amap.getValue("all");
					boolean allSupport = (dataSupport != null && dataSupport.equals("yes"));
					if(allSupport){
						isImageDataSourceExist = true;
					}else if(dataSupport != null && dataSupport.equals("no")){
						isImageDataSourceExist = false;
					}else{
						dataSupport = amap.getValue("xxsmall");
						isImageDataSourceExist = (dataSupport != null && dataSupport.equals("yes"));
						if(isImageDataSourceExist) break;
						dataSupport = amap.getValue("xsmall");
						isImageDataSourceExist = (dataSupport != null && dataSupport.equals("yes"));
						if(isImageDataSourceExist) break;
						dataSupport = amap.getValue("small");
						isImageDataSourceExist = (dataSupport != null && dataSupport.equals("yes"));
						if(isImageDataSourceExist) break;
						dataSupport = amap.getValue("medium");
						isImageDataSourceExist = (dataSupport != null && dataSupport.equals("yes"));
						if(isImageDataSourceExist) break;
						dataSupport = amap.getValue("large");
						isImageDataSourceExist = (dataSupport != null && dataSupport.equals("yes"));
						if(isImageDataSourceExist) break;
						dataSupport = amap.getValue("xlarge");
						isImageDataSourceExist = (dataSupport != null && dataSupport.equals("yes"));
					}
				}
				break;
            default:
                throw new IllegalArgumentException("SpeciesImage " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        String valueString, colorString;
        int colorRed, colorGreen, colorBlue;

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
                initializeImageSource(name);
                break;

            case Elements.IMAGE_TYPE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                imageType = Integer.valueOf(valueString).intValue();
                break;

            case Elements.HOTSPOT_COLOR_ELEMENT_ID:
                {
                    valueString = xmlElementContext.getValueString();
                    StringTokenizer st = new StringTokenizer(valueString,EngineStrings.COMMA);
                    colorString = st.nextToken();
                    colorRed = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorGreen = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorBlue = Integer.valueOf(colorString).intValue();
                    hotspotColor = new Color(colorRed,colorGreen,colorBlue);
                }
                break;

            case Elements.HOTSPOT_RADIUS_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                hotspotRadius = Integer.valueOf(valueString).intValue();
                break;

            case Elements.SPECIES_IMAGE_COLUMN_ELEMENT_ID:
                // Done with a species image column, so reclaim document handler
                break;

            case Elements.SPECIES_IMAGE_ROW_ELEMENT_ID:
                // Done with a species image row, so reclaim document handler
                break;

            case Elements.SPECIES_IMAGE_ELEMENT_ID:
                // Done with this species image, so pop up to enclosing species
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
        hotspotColor = null;

        xxSmallIcon = null;
        xSmallIcon = null;
        smallIcon = null;
        mediumIcon = null;
        largeIcon = null;
        xLargeIcon = null;

        xxSmallImage = null;
        xSmallImage = null;
        smallImage = null;
        mediumImage = null;
        largeImage = null;
        xLargeImage = null;

        SpeciesImageColumn aSpeciesImageColumn;
        Vector speciesImageColumnsClone = (Vector) speciesImageColumns.clone();
        Enumeration eSpeciesImageColumns = speciesImageColumnsClone.elements();
        //Enumeration eSpeciesImageColumns = speciesImageColumns.elements();
        while (eSpeciesImageColumns.hasMoreElements())
        {
            aSpeciesImageColumn = (SpeciesImageColumn) eSpeciesImageColumns.nextElement();
            aSpeciesImageColumn.delete(notifyChange);
        }
        speciesImageColumns.removeAllElements();
        speciesImageColumns = null;
        speciesImageColumnsClone = null;
        
        SpeciesImageRow aSpeciesImageRow;
        Vector speciesImageRowsClone = (Vector) speciesImageRows.clone();
        Enumeration eSpeciesImageRows = speciesImageRowsClone.elements();
        //Enumeration eSpeciesImageRows = speciesImageRows.elements();
        while (eSpeciesImageRows.hasMoreElements())
        {
            aSpeciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
            aSpeciesImageRow.delete(notifyChange);
        }
        speciesImageRows.removeAllElements();
        speciesImageRows = null;
        speciesImageRowsClone = null;

        // Tell species
        species.removeSpeciesImage(this);
        species = null;
        id = EngineObject.NULL_ID;
    }

    /**
     * Return a string representation of this object.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return EngineStrings.SPECIES_IMAGE_COLON + name;
    }

    /**
     * Return the species of this object.<p>
     *
     * @return		Species - species of this object, never null
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
     * Return the name of this species image.  May be null.<p>
     *
     * @return		String - name of this species image, may not be null.
    **/
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this species image.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this species image, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
    **/
    public void setName(String aName)
    {
        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
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

        if (aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldName = name;
        name = new String(aName);

        // Update file
        initializeImageSource(name);
        
        // Clear image, as it's no longer valid given the new name
        xxSmallImage = null;
        xSmallImage = null;
        smallImage = null;
        mediumImage = null;
        largeImage = null;
        xLargeImage = null;

        // Notify listeners
        changes.firePropertyChange(EngineProp.NAME,oldName,name);
    }

    /**
     * Called by parent species when it's name changes, as this image cares.
    **/
    public void updateFilename()
    {
        // Update file
        initializeImageSource(name);
        
        // Clear image, as it's no longer valid given the new name
        xxSmallImage = null;
        xSmallImage = null;
        smallImage = null;
        mediumImage = null;
        largeImage = null;
        xLargeImage = null;

        // Notify listeners, lieing a little bit since filename, not name changed
        changes.firePropertyChange(EngineProp.NAME,null,name);
    }

    /**
     * Return the file of this species image in File form.
     *
     * @param		imageSize int - the image size (e.g. XSMALL_IMAGE_SIZE)
     * @return		File - file containing this image, may be null
    **/
    public String getSource(int imageSize)
    {
        String source = null;

        switch (imageSize)
        {
            case XXSMALL_IMAGE_SIZE: source = xxSmallSource; break;
            case XSMALL_IMAGE_SIZE: source = xSmallSource; break;
            case SMALL_IMAGE_SIZE: source = smallSource; break;
            case MEDIUM_IMAGE_SIZE: source = mediumSource; break;
            case LARGE_IMAGE_SIZE: source = largeSource; break;
            case XLARGE_IMAGE_SIZE: source = xLargeSource; break;
        }

        return source;
    }

    /**
     * Get the actual Image object for this species image.  It's
     * expected this method will be called by code wishing to
     * draw some portion of this image (e.g. the OrganismView).
     * The actual Image is loaded the first time this method is
     * called and then the Image is cached for future use.
     *
     * @param		imageSize int - the image size (e.g. XSMALL_IMAGE_SIZE)
     * @param		component Component - the component on whose behalf we'll get image, may not be null
     * @return		Image - the image
    **/
    public Image getImage(int imageSize, Component component)
    {
        Image image = null;
        preloadSpeciesImageSize(imageSize);
        switch (imageSize)
        {
            case XXSMALL_IMAGE_SIZE:
                if (xxSmallImage == null && xxSmallIcon != null)
                {
                    xxSmallImage = xxSmallIcon.getImage();
                }
                image = xxSmallImage;
                break;

            case XSMALL_IMAGE_SIZE:
                if (xSmallImage == null && xSmallIcon != null)
                {
                    xSmallImage = xSmallIcon.getImage();
                }
                image = xSmallImage;
                break;

            case SMALL_IMAGE_SIZE:
                if (smallImage == null && smallIcon != null)
                {
                    smallImage = smallIcon.getImage();
                }
                image = smallImage;
                break;

            case MEDIUM_IMAGE_SIZE:
                if (mediumImage == null && mediumIcon != null)
                {
                    mediumImage = mediumIcon.getImage();
                }
                image = mediumImage;
                break;

            case LARGE_IMAGE_SIZE:
                if (largeImage == null && largeIcon != null)
                {
                    largeImage = largeIcon.getImage();
                }
                image = largeImage;
                break;

            case XLARGE_IMAGE_SIZE:
                if (xLargeImage == null && xLargeIcon != null)
                {
                    xLargeImage = xLargeIcon.getImage();
                }
                image = xLargeImage;
                break;
        }

        return image;
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
        SpeciesImageColumn aSpeciesImageColumn;
        Enumeration eSpeciesImageColumns = speciesImageColumns.elements();
        while (eSpeciesImageColumns.hasMoreElements())
        {
            aSpeciesImageColumn = (SpeciesImageColumn) eSpeciesImageColumns.nextElement();
            aSpeciesImageColumn.setAutomaticLocked(automaticLocked);
        }
                
        SpeciesImageRow aSpeciesImageRow;
        Enumeration eSpeciesImageRows = speciesImageRows.elements();
        while (eSpeciesImageRows.hasMoreElements())
        {
            aSpeciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
            aSpeciesImageRow.setAutomaticLocked(automaticLocked);
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
        SpeciesImageColumn aSpeciesImageColumn;
        Enumeration eSpeciesImageColumns = speciesImageColumns.elements();
        while (eSpeciesImageColumns.hasMoreElements())
        {
            aSpeciesImageColumn = (SpeciesImageColumn) eSpeciesImageColumns.nextElement();
            aSpeciesImageColumn.setManualLocked(manualLocked);
        }
                
        SpeciesImageRow aSpeciesImageRow;
        Enumeration eSpeciesImageRows = speciesImageRows.elements();
        while (eSpeciesImageRows.hasMoreElements())
        {
            aSpeciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
            aSpeciesImageRow.setManualLocked(manualLocked);
        }
    }

    /**
     * Set the lock state of the object, recursively setting the lock
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
        SpeciesImageColumn aSpeciesImageColumn;
        Enumeration eSpeciesImageColumns = speciesImageColumns.elements();
        while (eSpeciesImageColumns.hasMoreElements())
        {
            aSpeciesImageColumn = (SpeciesImageColumn) eSpeciesImageColumns.nextElement();
            aSpeciesImageColumn.setLockedState(lockedState);
        }
                
        SpeciesImageRow aSpeciesImageRow;
        Enumeration eSpeciesImageRows = speciesImageRows.elements();
        while (eSpeciesImageRows.hasMoreElements())
        {
            aSpeciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
            aSpeciesImageRow.setLockedState(lockedState);
        }
    }

    /**
     * Returns an enumeration over the vector of species image columns in this species image.<p>
     *
     * @return		Enumeration - an enumeration over the species image columns in this species image
    **/
    public Enumeration getSpeciesImageColumns()
    {
        // Clone species image columns vector to make modifications safe during stepping
        Vector speciesImageColumnsClone = (Vector) speciesImageColumns.clone();
        return speciesImageColumnsClone.elements();
    }

    /**
     * Get the number of species image columns for this species image.<p>
     *
     * @return		int - the number of species image columns
    **/
    public int getNumberOfSpeciesImageColumns()
    {
        return speciesImageColumns.size();
    }

    /**
     * Adds a SpeciesImageColumn to this species image.<p>
     *
     * Package protected because this is only called from the SpeciesImageColumn
     * constructor.  Creating a SpeciesImageColumn automatically adds it to the
     * SpeciesImage.<p>
     *
     * @param		aSpeciesImageColumn SpeciesImageColumn - a new SpeciesImageColumn, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addSpeciesImageColumn(SpeciesImageColumn aSpeciesImageColumn)
    {
        // Validate input argument
        if (aSpeciesImageColumn == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - add species image column
        speciesImageColumns.addElement(aSpeciesImageColumn);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.SPECIES_IMAGE_COLUMN_ADDED, null, aSpeciesImageColumn);
    }

    /**
     * Removes a SpeciesImageColumn from this SpeciesImage.<p>
     *
     * Package protected because this is only called from the SpeciesImageColumn
     * delete method.  Deleting a SpeciesImageColumn automatically removes it from
     * the SpeciesImage.<p>
     *
     * @param		aSpeciesImageColumn SpeciesImageColumn - a SpeciesImageColumn, may not be null
     * @return		boolean indicating whether or not the SpeciesImageColumn was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeSpeciesImageColumn(SpeciesImageColumn aSpeciesImageColumn)
    {
        if (aSpeciesImageColumn == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - remove SpeciesImageColumn
        boolean result = speciesImageColumns.removeElement(aSpeciesImageColumn);

        // Notify listeners if SpeciesImageColumn removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.SPECIES_IMAGE_COLUMN_REMOVED, null, aSpeciesImageColumn);
        }

        return result;
    }

    /**
     * Returns an enumeration over the vector of species image columns in this species image.<p>
     *
     * @return		Enumeration - an enumeration over the species image columns in this species image
    **/
    public Enumeration getSpeciesImageRows()
    {
        // Clone species image columns vector to make modifications safe during stepping
        Vector speciesImageRowsClone = (Vector) speciesImageRows.clone();
        return speciesImageRowsClone.elements();
    }

    /**
     * Returns a specific species image row, null if invalid row.
     *
     * @param		aRowIndex int - a row index
     * @return		SpeciesImageRow - a SpeciesImageRow, null if invalid
    **/
    public SpeciesImageRow getSpeciesImageRowAt(int aRowIndex)
    {
        try
        {
            SpeciesImageRow sir = (SpeciesImageRow) speciesImageRows.elementAt(aRowIndex);
            return sir;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * Get the number of species image columns for this species image.<p>
     *
     * @return		int - the number of species image columns
    **/
    public int getNumberOfSpeciesImageRows()
    {
        return speciesImageRows.size();
    }

    /**
     * Adds a SpeciesImageRow to this species image.<p>
     *
     * Package protected because this is only called from the SpeciesImageRow
     * constructor.  Creating a SpeciesImageRow automatically adds it to the
     * SpeciesImage.<p>
     *
     * @param		aSpeciesImageRow SpeciesImageRow - a new SpeciesImageRow, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addSpeciesImageRow(SpeciesImageRow aSpeciesImageRow)
    {
        // Validate input argument
        if (aSpeciesImageRow == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - add species image column
        speciesImageRows.addElement(aSpeciesImageRow);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.SPECIES_IMAGE_ROW_ADDED, null, aSpeciesImageRow);
    }

    /**
     * Removes a SpeciesImageRow from this SpeciesImage.<p>
     *
     * Package protected because this is only called from the SpeciesImageRow
     * delete method.  Deleting a SpeciesImageRow automatically removes it from
     * the SpeciesImage.<p>
     *
     * @param		aSpeciesImageRow SpeciesImageRow - a SpeciesImageRow, may not be null
     * @return		boolean indicating whether or not the SpeciesImageRow was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeSpeciesImageRow(SpeciesImageRow aSpeciesImageRow)
    {
        if (aSpeciesImageRow == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - remove SpeciesImageRow
        boolean result = speciesImageRows.removeElement(aSpeciesImageRow);

        // Notify listeners if SpeciesImageRow removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.SPECIES_IMAGE_ROW_REMOVED, null, aSpeciesImageRow);
        }

        return result;
    }

    /**
     * Update an organism image given the current state of this species image,
     * its rows and columns.  Usually this amounts to determining which row
     * and column pair satisfies the characteristics of the organism associated
     * with the given organism image.
     *
     * @param		anOrganismImage OrganismImage - organism image to update, may not be null
    **/
    public void updateOrganismImage(OrganismImage anOrganismImage)
    {
        if (anOrganismImage == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int columnGender, rowGender, organismSex;
        boolean foundColumn = false;
        boolean foundRow = false;
        int indexColumn = -1;
        int indexRow = -1;

        Organism anOrganism = anOrganismImage.getOrganism();
        if (anOrganism != null)
        {
            organismSex = anOrganism.getSex();
    
            Vector organismCharacteristics = anOrganism.getCharacteristicsVector();

            Characteristic aCharacteristic;
            Enumeration eCharacteristics;
            SpeciesImageColumn aColumn;
            SpeciesImageRow aRow;

            // Loop through the columns looking for columns that have
            // a set of characteristics all in the current organism
            Enumeration eColumns = speciesImageColumns.elements();
            while (eColumns.hasMoreElements())
            {
                aColumn = (SpeciesImageColumn) eColumns.nextElement();
                indexColumn++;
                foundColumn = true;

                // Check to see if column gender matches the organism's sex
                columnGender = aColumn.getGender();

                if ((organismSex == Organism.NO_SEX) ||
                    (organismSex == Organism.MALE &&
                     (columnGender == Species.MALE_ONLY ||
                      columnGender == Species.FEMALE_AND_MALE)) ||
                    (organismSex == Organism.FEMALE &&
                     (columnGender == Species.FEMALE_ONLY ||
                      columnGender == Species.FEMALE_AND_MALE)))
                {
                    // Loop through the characteristics for the column
                    // checking to see if each column characteristic is in
                    // the organism.  Break the loop when the first
                    // non-match occurs.  Success is when we go through
                    // the entire list of characteristics with success.
                    eCharacteristics = aColumn.getCharacteristics();
                    while (eCharacteristics.hasMoreElements())
                    {
                        aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                        if (organismCharacteristics.contains(aCharacteristic) == false)
                        {
                            // Characteristic not in organism, so break characteristics loop
                            foundColumn = false;
                            break;
                        }
                    }
                }
                else
                {
                    // Column gender and organism's sex didn't match
                    foundColumn = false;
                }

                // If we're here with foundColumn == true, that means
                // the gender and characteristics in the column matched!
                // So break out of loop through columns.
                if (foundColumn == true)
                {
                    break;
                }
            }

            if (foundColumn == true && indexColumn != -1)
            {
                // Found a column match, so look for a row match now

                // Loop through the rows looking for rows that have
                // a set of characteristics all in the current organism
                Enumeration eRows = speciesImageRows.elements();
                while (eRows.hasMoreElements())
                {
                    aRow = (SpeciesImageRow) eRows.nextElement();
                    indexRow++;
                    foundRow = true;
    
                    // Check to see if row gender matches the organism's sex
                    rowGender = aRow.getGender();
                    if ((organismSex == Organism.NO_SEX) ||
                        (organismSex == Organism.MALE &&
                         (rowGender == Species.MALE_ONLY ||
                          rowGender == Species.FEMALE_AND_MALE)) ||
                        (organismSex == Organism.FEMALE &&
                         (rowGender == Species.FEMALE_ONLY ||
                          rowGender == Species.FEMALE_AND_MALE)))
                    {
                        // Row gender and organism's sex match.
                    
                        // Loop through the characteristics for the row
                        // checking to see if each row characteristic is in
                        // the organism.  Break the loop when the first
                        // non-match occurs.  Success is when we go through
                        // the entire list of characteristics with success.
                        eCharacteristics = aRow.getCharacteristics();
                        while (eCharacteristics.hasMoreElements())
                        {
                            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                            if (organismCharacteristics.contains(aCharacteristic) == false)
                            {
                                foundRow = false;
                                break;
                            }
                        }
                    }
                    else
                    {
                        // Row gender and organism's sex didn't match
                        foundRow = false;
                    }
        
                    // If we're here with foundRow == true, that means
                    // the gender and characteristic in the row matched!
                    // So b reak out of the loop through rows.
                    if (foundRow == true)
                    {
                            break;
                    }
                }
            }
        }

        // If we succeeded, set row and column indices in organism image
        // else set row and column indices to NO_CELL.
        if (foundColumn == true && foundRow == true &&
            indexColumn != -1 && indexRow != -1)
        {
            anOrganismImage.setColumnAndRowIndices(indexColumn,indexRow);
        }
        else
        {
            anOrganismImage.setColumnAndRowIndices(OrganismImage.NO_CELL,
                                                   OrganismImage.NO_CELL);
        }
    }
    
    public static void setImageVisible(boolean visible)
    {
        imageVisible = visible;
    }
    
    public static boolean isImageVisible()
    {
        return imageVisible;
    }

    /**
     * Get the image type for this species image.<p>
     *
     * @return		int - image type of this species image
    **/
    public int getImageType()
    {
        return imageType;
    }

    /**
     * Set the image type of this species image.<p>
     *
     * @param		anImageType int - new image type
    **/
    public void setImageType(int anImageType)
    {
        // Return immediately if no change
        if (anImageType == imageType)
        {
            return;
        }

        // Make change
        int oldImageType = imageType;
        imageType = anImageType;

        // Notify listeners
        changes.firePropertyChange(EngineProp.IMAGE_TYPE, new Integer(oldImageType), new Integer(imageType));
    }

    /**
     * Get the hotspot color of this species image
     *
     * @return		Color - hotspot color
    **/
    public Color getHotspotColor()
    {
        return hotspotColor;
    }

    /**
     * Set the hotspot color of this species image
     *
     * @param		aColor Color - the new hotspot color
     * @exception	IllegalArgumentException - input color null
    **/
    public void setHotspotColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if colors the same
        if (hotspotColor.getRGB() == aColor.getRGB())
        {
            return;
        }

        Color oldColor = hotspotColor;
        hotspotColor = aColor;

        // Notify listeners
        changes.firePropertyChange(EngineProp.HOTSPOT_COLOR,oldColor,hotspotColor);
    }

    /**
     * Get the hotspot radius for this species image.<p>
     *
     * @return		int - hotspot radius of this species image
    **/
    public int getHotspotRadius()
    {
        return hotspotRadius;
    }

    /**
     * Set the hotspot radius of this species image.<p>
     *
     * @param		aHotspotRadius int - new hotspot radius
    **/
    public void setHotspotRadius(int aHotspotRadius)
    {
        // Return immediately if no change
        if (aHotspotRadius == hotspotRadius)
        {
            return;
        }

        // Make change
        int oldHotspotRadius = hotspotRadius;
        hotspotRadius = aHotspotRadius;

        // Notify listeners
        changes.firePropertyChange(EngineProp.HOTSPOT_RADIUS, new Integer(oldHotspotRadius), new Integer(hotspotRadius));
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
        stream.println("<" + Elements.SPECIES_IMAGE_ELEMENT_NAME + ">");

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

        // Image type
        stream.println("<" + Elements.IMAGE_TYPE_ELEMENT_NAME + ">" + imageType +
                          "</" + Elements.IMAGE_TYPE_ELEMENT_NAME + ">");

        // Hotspot color
        stream.println("<" + Elements.HOTSPOT_COLOR_ELEMENT_NAME + ">" +
                          hotspotColor.getRed() + "," +
                          hotspotColor.getGreen() + "," +
                          hotspotColor.getBlue() +
                          "</" + Elements.HOTSPOT_COLOR_ELEMENT_NAME + ">");

        // Hotspot radius
        stream.println("<" + Elements.HOTSPOT_RADIUS_ELEMENT_NAME + ">" + hotspotRadius +
                          "</" + Elements.HOTSPOT_RADIUS_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Recursively write children of species image
        {
            SpeciesImageColumn aSpeciesImageColumn;
            Enumeration eSpeciesImageColumns = speciesImageColumns.elements();
            while (eSpeciesImageColumns.hasMoreElements())
            {
                aSpeciesImageColumn = (SpeciesImageColumn) eSpeciesImageColumns.nextElement();
                aSpeciesImageColumn.writeToStream(stream);
            }
        }

        {
            SpeciesImageRow aSpeciesImageRow;
            Enumeration eSpeciesImageRows = speciesImageRows.elements();
            while (eSpeciesImageRows.hasMoreElements())
            {
                aSpeciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
                aSpeciesImageRow.writeToStream(stream);
            }
        }

        // End object
        stream.println("</" + Elements.SPECIES_IMAGE_ELEMENT_NAME + ">");
    }
	public  boolean				isDataSourceExist(int imageSize){
		boolean retValue = isImageDataSourceExist;
		if(retValue){
			initImageDownloading(imageSize,null);
		}
        return retValue;
    }
    
    public Image getDataSourceImage(int sizeID,int rowID,int columnID,int xoffset[],int yoffset[],Component c){
    	if(!isDataSourceExist(sizeID)) return null;
    	initImageDownloading(sizeID,c);
    	Object key = DataOrganism.getHashKey(sizeID,partID,rowID,columnID);
		ShortImageDescription partsDesc = (ShortImageDescription)ImageReader.imageDescriptions.get(new Integer(ImageReader.createHashIndex(partID,sizeID)));
    	if(rowID < 0){
    		rowID = 0;
    		return null;
    	}
    	Image img = DataOrganism.getOrganismPartImage(partsDesc,key,rowID,columnID,xoffset,yoffset);
		return img;
    }

    public Image getDataSourceImage(int []pixels,int worig,int horig,int sizeID,int rowID,int columnID,int xoffset[],int yoffset[]){
    	if(!isDataSourceExist(sizeID)) return null;
    	initImageDownloading(sizeID,null);
    	Object key = DataOrganism.getHashKey(sizeID,partID,rowID,columnID);
		ShortImageDescription partsDesc = (ShortImageDescription)ImageReader.imageDescriptions.get(new Integer(ImageReader.createHashIndex(partID,sizeID)));
    	if(rowID < 0){
    		rowID = 0;
    		return null;
    	}
    	Image img = null;
		try{
			img = DataOrganism.getOrganismPartImage(pixels,worig,partsDesc,key,rowID,columnID,xoffset,yoffset);
		}catch(Exception e){}

		return img;
    }

    public void initImageDownloading(int sizeID,Component c){
    	if(sizeID < UNKNOWN_IMAGE_SIZE || sizeID > XLARGE_IMAGE_SIZE) return;
    	int beginSize 	= (sizeID == UNKNOWN_IMAGE_SIZE)?XXSMALL_IMAGE_SIZE:sizeID;
    	int endSize 	= (sizeID == UNKNOWN_IMAGE_SIZE)?XLARGE_IMAGE_SIZE:sizeID;
    	
		for(int i = beginSize; i <= endSize; i++){
	    	if(!ImageReader.isShortImageDescriptionInCache(partID,i)){
	    		try{
	    			String dataSourceString = getDataPathName(SpecieFileDesc.getDataURLString(partID,i));//dima
	        		ImageReader.readFile(getDataResource(dataSourceString),partID,i,c);
	        	}catch(Exception e){}
	    	}
		}
    }

	static private int []xoffset = new int[1];
	static private int []yoffset = new int[1];

    
    public boolean drawDataImage(Graphics g,int sizeID,int rowIndex,int columnIndex,Component c){
    	if(g == null) return false;
		Image img = getDataSourceImage(sizeID,rowIndex,columnIndex,xoffset,yoffset,c);
    	if(img != null){
    		g.drawImage(img,xoffset[0],yoffset[0],c);//dima
    	}
    	return (img != null);
    }
    public boolean drawDataImage(Graphics g,int sizeID,int rowIndex,int columnIndex,int xInit,int yInit, Component c){
    	if(g == null) return false;
		Image img = getDataSourceImage(sizeID,rowIndex,columnIndex,xoffset,yoffset,c);
    	if(img != null){
    		g.drawImage(img,xInit + xoffset[0],yInit + yoffset[0],c);//dima
    	}
    	return (img != null);
    }
    public boolean drawDataImage(int pixels[],int worig,int horig,int sizeID,int rowIndex,int columnIndex){
		Image img = getDataSourceImage(pixels,worig,horig,sizeID,rowIndex,columnIndex,xoffset,yoffset);
    	return (img != null);
    }

}

class SpeciesImageIcon
extends ImageIcon
{
    public SpeciesImageIcon(URL source)
    {
        super(source);
    }
    
    public void waitForLoadImage()
    {
        if (SpeciesImage.isImageVisible())
        {
            loadImage(getImage());
        }
    }
}

