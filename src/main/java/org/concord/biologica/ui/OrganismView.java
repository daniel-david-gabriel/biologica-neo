//
// Class : OrganismView - the base view for all BioLogica views that draw organisms in a phenotypical manner
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.16 $
// $Date: 2002/10/16 18:18:22 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.net.URL;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;
import org.concord.biologica.datasupport.*;


/**
 * The basic organism view of BioLogica, used to hold all the code that is
 * common to all views that draw organisms in a phenotypical manner.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.BACKGROUND - the background color of the view changed
 * <li> org.concord.biologica.ui.UIProp.CHARACTERISTICS_TEXT_VISIBLE - the characteristics text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.FONT - the font of the view changed
 * <li> org.concord.biologica.ui.UIProp.FOREGROUND - the foreground color of the view changed
 * <li> org.concord.biologica.ui.UIProp.LOCK_SYMBOL_VISIBLE - the lock symbol should or should not be displayed if appropriate
 * <li> org.concord.biologica.ui.UIProp.NAME_TEXT_VISIBLE - the name text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_IMAGE_SIZE - the image size to use for drawing organisms in this view
 * <li> org.concord.biologica.ui.UIProp.SEX_TEXT_VISIBLE - the sex text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.SPECIES_TEXT_VISIBLE - the species text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.TEXT_INDENT - the indentation of text from left edge of image
 * <li> org.concord.biologica.ui.UIProp.TEXT_LINE_SPACING - the number of pixels between lines of text
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#CHARACTERISTICS_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#LOCK_SYMBOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#NAME_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGE_SIZE
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.16 $ $Date: 2002/10/16 18:18:22 $
 * @author 		$Author: qliao $
**/
public class OrganismView
extends UIView
implements ImageObserver
{

    /**
     * Flag indicating if the images have been loaded
    **/
    static private boolean imagesLoaded = false;

    /**
     * Locked organism image
    **/
    static private Image lockImage = null;

    /**
     * The height of the area below organism's image where we put the
     * name of the organism, it's species name and color.
    **/
    static private final int NAME_AREA_HEIGHT = 25;

    /**
     * Default highlight color
    **/
    static private Color defaultHighlightColor = Color.pink;

    /**
     * Default selection color
    **/
    static private Color defaultSelectionColor = Color.yellow;

    /**
     * Lock symbol visible boolean flag
    **/
    protected boolean lockSymbolVisible = true;

    /**
     * Name text visible boolean flag.
    **/
    protected boolean nameTextVisible = true;

    /**
     * Sex text visible boolean flag.
    **/
    protected boolean sexTextVisible = true;

    /**
     * Species text visible boolean flag.
    **/
    protected boolean speciesTextVisible = true;

    /**
     * Characteristics text visible boolean flag.
    **/
    protected boolean characteristicsTextVisible = true;

    /**
     * Organism image size, used when drawing organisms.
     * One of SpeciesImage.XXX_IMAGE_SIZE static values.
    **/
    protected int organismImageSize;

    /**
     * Indentation in pixels from left edge of image for text underneath the organism.
     * A default value is chosen when the view is created based but a script can set
     * this value to something else explicitly.
    **/
    protected int textIndent;

    /**
     * Text line spacing, the distance between lines of text under an organism.<p>
    **/
    protected int textLineSpacing;

    /**
     * Transient hotspot organism.  When this value is non-null, then we should paint
     * the hotspotOrganismImage (below) for this organism.<p>
    **/
    protected transient Organism hotspotOrganism;

    /**
     * Transient hotspot organism image.  When this value and hotspotOrganism (above)
     * are non-null, then we should paint this image for the hotspotOrganism.<p>
    **/
    protected transient OrganismImage hotspotOrganismImage;

    /**
     * Highlight color for a particular view
    **/
    protected Color highlightColor;

    /**
     * Selection color for a particular view
    **/
    protected Color selectionColor;

    /**
     * Get the default selection color
     *
     * @return		Color - default selection color, never null
    **/
    public static Color getDefaultHighlightColor()
    {
        return defaultHighlightColor;
    }

    /**
     * Set the default Highlight color.  This will be used as the default
     * Highlight color for views created after calling this method.<p>
     *
     * @param		aColor Color - a default Highlight color, may not be null
     * @exception	IllegalArgumentException - input argument illegal (null)
    **/
    public static void setDefaultHighlightColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException("aColor null");
        }

        defaultHighlightColor = aColor;
    }

    /**
     * Get the default selection color
     *
     * @return		Color - default selection color, never null
    **/
    public static Color getDefaultSelectionColor()
    {
        return defaultSelectionColor;
    }

    /**
     * Set the default selection color.  This will be used as the default
     * selection color for views created after calling this method.<p>
     *
     * @param		aColor Color - a default selection color, may not be null
     * @exception	IllegalArgumentException - input argument illegal (null)
    **/
    public static void setDefaultSelectionColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException("aColor null");
        }

        defaultSelectionColor = aColor;
    }

    /**
     * Creates an organism view.<p>
    **/
    public OrganismView()
    {
        super();

        organismImageSize = SpeciesImage.LARGE_IMAGE_SIZE;
        textIndent = 30;
        textLineSpacing = 2;
        hotspotOrganism = null;
        hotspotOrganismImage = null;
        highlightColor = defaultHighlightColor;
        selectionColor = defaultSelectionColor;

        // Load images
        if (imagesLoaded == false)
        {
            try
            {
                URL url = PathStrings.getGIFURL("lock.gif");
                lockImage = getToolkit().getImage(url);
            }
            catch (Exception e)
            {
                lockImage = null;
                e.printStackTrace();
            }

            imagesLoaded = true;
        }
    }

    /**
     * Is the lock symbol visible?
     *
     * @return		boolean - is the lock symbol visible
    **/
    public boolean isLockSymbolVisible()
    {
        return lockSymbolVisible;
    }

    /**
     * Set the lock symbol visible boolean.
     *
     * @param		aLockSymbolVisible boolean - visible?
    **/
    public void setLockSymbolVisible(boolean aLockSymbolVisible)
    {
        // Return immediately if no change of state
        if (aLockSymbolVisible == lockSymbolVisible)
        {
            return;
        }

        // Change
        boolean oldLockSymbolVisible = lockSymbolVisible;
        lockSymbolVisible = aLockSymbolVisible;

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.LOCK_SYMBOL_VISIBLE,
                                   new Boolean(oldLockSymbolVisible),
                                   new Boolean(lockSymbolVisible));
    }

    /**
     * Is the characteristics text visible?
     *
     * @return		boolean - is the characteristics text visible
    **/
    public boolean isCharacteristicsTextVisible()
    {
        return characteristicsTextVisible;
    }

    /**
     * Set the characteristics text visible boolean.
     *
     * @param		aCharacteristicsTextVisible boolean - visible?
    **/
    public void setCharacteristicsTextVisible(boolean aCharacteristicsTextVisible)
    {
        // Return immediately if no change of state
        if (aCharacteristicsTextVisible == characteristicsTextVisible)
        {
            return;
        }

        // Change
        boolean oldCharacteristicsTextVisible = characteristicsTextVisible;
        characteristicsTextVisible = aCharacteristicsTextVisible;

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.CHARACTERISTICS_TEXT_VISIBLE,
                                   new Boolean(oldCharacteristicsTextVisible),
                                   new Boolean(characteristicsTextVisible));
    }

    /**
     * Is the name text visible?
     *
     * @return		boolean - is the name text visible
    **/
    public boolean isNameTextVisible()
    {
        return nameTextVisible;
    }

    /**
     * Set the name text visible boolean.
     *
     * @param		aNameTextVisible boolean - visible?
    **/
    public void setNameTextVisible(boolean aNameTextVisible)
    {
        // Return immediately if no change of state
        if (aNameTextVisible == nameTextVisible)
        {
            return;
        }

        // Change
        boolean oldNameTextVisible = nameTextVisible;
        nameTextVisible = aNameTextVisible;

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.NAME_TEXT_VISIBLE,
                                   new Boolean(oldNameTextVisible),
                                   new Boolean(nameTextVisible));
    }

    /**
     * Is the sex text visible?
     *
     * @return		boolean - is the sex text visible
    **/
    public boolean isSexTextVisible()
    {
        return sexTextVisible;
    }

    /**
     * Set the sex text visible boolean.
     *
     * @param		aSexTextVisible boolean - visible?
    **/
    public void setSexTextVisible(boolean aSexTextVisible)
    {
        // Return immediately if no change of state
        if (aSexTextVisible == sexTextVisible)
        {
            return;
        }

        // Change
        boolean oldSexTextVisible = sexTextVisible;
        sexTextVisible = aSexTextVisible;

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.SEX_TEXT_VISIBLE,
                                   new Boolean(oldSexTextVisible),
                                   new Boolean(sexTextVisible));
    }

    /**
     * Is the species text visible?
     *
     * @return		boolean - is the species text visible
    **/
    public boolean isSpeciesTextVisible()
    {
        return speciesTextVisible;
    }

    /**
     * Set the species text visible boolean.
     *
     * @param		aSpeciesTextVisible boolean - visible?
    **/
    public void setSpeciesTextVisible(boolean aSpeciesTextVisible)
    {
        // Return immediately if no change of state
        if (aSpeciesTextVisible == speciesTextVisible)
        {
            return;
        }

        // Change
        boolean oldSpeciesTextVisible = speciesTextVisible;
        speciesTextVisible = aSpeciesTextVisible;

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.SPECIES_TEXT_VISIBLE,
                                   new Boolean(oldSpeciesTextVisible),
                                   new Boolean(speciesTextVisible));
    }

    /**
     * Get the highlight color
     *
     * @return		Color - highlight color for this
    **/
    public Color getHighlightColor()
    {
        return highlightColor;
    }

    /**
     * Set the highlight color.  If null is input, the color reverts
     * back to the default highlight color.<p>
     *
     * @param		aColor Color - a new highlight color, may be null
    **/
    public void setHighlightColor(Color aColor)
    {
        if (aColor == null)
        {
            highlightColor = defaultHighlightColor;
        }
        else
        {
            highlightColor = aColor;
        }

        repaint();
    }

    /**
     * Get the selection color
     *
     * @return		Color - selection color for this
    **/
    public Color getSelectionColor()
    {
        return selectionColor;
    }

    /**
     * Set the selection color.  If null is input, the color reverts
     * back to the default selection color.<p>
     *
     * @param		aColor Color - a new selection color, may be null
    **/
    public void setSelectionColor(Color aColor)
    {
        if (aColor == null)
        {
            selectionColor = defaultSelectionColor;
        }
        else
        {
            selectionColor = aColor;
        }

        repaint();
    }

    /**
     * Draw an organism at the given location.  Assumes that the correct
     * font, foreground and background colors have already been set.  Returns
     * the dimensions of the cell rectangle containing the drawn organism
     * and any text, etc. drawn.<p>
     *
     * Nothing is drawn and a zero width and height rectangle is returned
     * if organism or g is null.<p>
     *
     * @param 		g Graphics - the given graphics to use in drawing
     * @param		organism Organism - the organism to draw
     * @param		x int - top left x location
     * @param		y int - top left y location
     * @param		fontHeight int - font height
     * @param		selected boolean - is organism selected?
     * @return		Dimension of the cell used to draw organism		
    **/
    Dimension paintOrganism(Graphics g, Organism organism, int x, int y,
                            int fontHeight, boolean selected)
    {
        // Allocate Dimension to return
        Dimension cellSize = new Dimension(0,0);


        if ((g != null) && (organism != null))
        {
            if (organism.isDeleted())
                return cellSize;
            // Determine image width and height
            Species species = organism.getSpecies();
            cellSize.width = species.getImageColumnWidth(organismImageSize);
            cellSize.height = species.getImageRowHeight(organismImageSize);
            SpeciesImage speciesImage;
            Image image;

            if (selected)
            {
                g.setColor(selectionColor);
                g.fillRect(x-2, y-2, cellSize.width+4, cellSize.height+4);
                g.setColor(getBackground());
                g.fillRect(x+3, y+3, cellSize.width-6, cellSize.height-6);
                g.setColor(getForeground());
            }

            if (SpeciesImage.isImageVisible())
            {
               if (organism.isVisible() == false)
                {
                    // Invisible, so find and draw the single invisible species image
                    Enumeration eSpeciesImages = species.getSpeciesImages();
                    while (eSpeciesImages.hasMoreElements())
                    {
                        speciesImage = (SpeciesImage) eSpeciesImages.nextElement();
                        if (speciesImage.getImageType() == SpeciesImage.INVISIBLE_IMAGE_TYPE)
                        {
							if(speciesImage.isDataSourceExist(organismImageSize)){
                           		speciesImage.drawDataImage(g,organismImageSize,0,0,x,y,this);
							}else{
								image = speciesImage.getImage(organismImageSize,this);
           						if(image != null) g.drawImage(image, x, y, this);
							}
                            break;
                        }
                    }
                }
                else
                {
                    // Visible, so find and draw multiple normal and hotspot organism images
    
                    // Create a graphics object to use while drawing
                    Graphics g2 = g.create(x,y,cellSize.width,cellSize.height);
    
                    // Draw organism by walking through the organism images for this
                    // organism, using the information in the organism image to draw
                    int xInImage, yInImage;
                    int rowIndex, columnIndex;
                    int imageType;
                    int hotspotRadius;
                    Color hotspotColor;
                    Point hotspot;
                    SpeciesImageRow speciesImageRow;
                    OrganismImage organismImage;
                    Enumeration eOrganismImages = organism.getOrganismImages();
    				
                    while (eOrganismImages.hasMoreElements())
                    {
                        organismImage = (OrganismImage) eOrganismImages.nextElement();
                        imageType = organismImage.getImageType();
    
                        if (imageType == SpeciesImage.NORMAL_IMAGE_TYPE ||
                            (imageType == SpeciesImage.HOTSPOT_IMAGE_TYPE &&
                             (organism == hotspotOrganism &&
                              organismImage == hotspotOrganismImage)))
                        {
                            columnIndex = organismImage.getColumnIndex();
                            rowIndex = organismImage.getRowIndex();
                            speciesImage = (SpeciesImage) organismImage.getSpeciesImage();
                            
                          if(speciesImage.isDataSourceExist(organismImageSize)){
                           		speciesImage.drawDataImage(g2,organismImageSize,rowIndex,columnIndex,this);
                            }else{
    
                            // Draw the cell at the given row and column
                            	image = speciesImage.getImage(organismImageSize,this);
                            	xInImage = -(columnIndex * cellSize.width);
                            	yInImage = -(rowIndex * cellSize.height);
                            	if(image != null) g2.drawImage(image,xInImage,yInImage,this);//dima
                            }
                        }
                        else if (imageType == SpeciesImage.HOTSPOT_IMAGE_TYPE)
                        {
                            speciesImage = (SpeciesImage) organismImage.getSpeciesImage();
                            hotspotRadius = speciesImage.getHotspotRadius();
                            hotspotColor = speciesImage.getHotspotColor();
                            rowIndex = organismImage.getRowIndex();
                            speciesImageRow = speciesImage.getSpeciesImageRowAt(rowIndex);
                            if (speciesImageRow != null)
                            {
                                hotspot = speciesImageRow.getHotspot(organismImageSize);
        
                                // If the hotspot is 0,0, don't draw anything
                                if (hotspot.x > hotspotRadius && hotspot.y > hotspotRadius)
                                {
                                    // Draw hotspot as an oval the size and color specified in species
                                    g.setColor(hotspotColor);
                                    g.fillOval(hotspot.x-hotspotRadius,
                                               hotspot.y-hotspotRadius,
                                               2*hotspotRadius,
                                               2*hotspotRadius);
                                    g.setColor(getForeground());
        
                                   if (organism == hotspotOrganism &&
                                        organismImage == hotspotOrganismImage)
                                    {
                                        // Draw hotspot image
                                        columnIndex = organismImage.getColumnIndex();
                                        rowIndex = organismImage.getRowIndex();
                                        speciesImage = (SpeciesImage) organismImage.getSpeciesImage();
                
                                        // Draw the cell at the given row and column
	                            		if(speciesImage.isDataSourceExist(organismImageSize)){
			                           		speciesImage.drawDataImage(g2,organismImageSize,rowIndex,columnIndex,this);
			                            }else{
                                        	image = speciesImage.getImage(organismImageSize,this);
                                        	xInImage = -(columnIndex * cellSize.width);
                                     		yInImage = -(rowIndex * cellSize.height);
                                        	if(image != null) g2.drawImage(image,xInImage,yInImage,this);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Dispose of the graphics object
                    g2.dispose();
                    g2 = null;
                }
            }

            paintOrganismText(g, organism, x, y, fontHeight, selected, cellSize);

            // if (selected)
            // {
            //	g.drawRect(x, y, cellSize.width, cellSize.height+1);
            // }
        }

        return cellSize;
    }

    /**
     * Paint the text underneath an organism.
     *
     * @param 		g Graphics - the given graphics to use in drawing
     * @param		organism Organism - the organism to draw
     * @param		x int - top left x location
     * @param		y int - top left y location
     * @param		fontHeight int - font height
     * @param		selected boolean - is organism selected
     * @param		cellSize Dimension - the dimension of a cell, modified by this method
    **/
    void paintOrganismText(Graphics g, Organism organism, int x, int y,
                            int fontHeight, boolean selected, Dimension cellSize)
    {
        // Draw the lock symbol if appropriate
        if (lockSymbolVisible && organism.isLocked())
        {
            g.drawImage(lockImage, x+cellSize.width-20, y+5, this);
        }

        // Draw name of organism's species, the organism's name and its sex if it has one
        String text;
        int yText = y + cellSize.height + fontHeight;
        int yRect = y + cellSize.height;
        {
            if (nameTextVisible ||
                organism.isNameSuperVisible())
            {
                text = organism.getName();
                if (text != null)
                {
                    if (selected)
                    {
                        g.setColor(selectionColor);
                        g.fillRect(x-2,yRect,cellSize.width+4,fontHeight+textLineSpacing+4);
                        g.setColor(getForeground());
                    }
                    g.drawString(text, x+textIndent, yText);
                    yRect += fontHeight + textLineSpacing;
                    yText += fontHeight + textLineSpacing;
                }
            }

            if (speciesTextVisible)
            {
                text = organism.getSpecies().getName();
                if (text != null)
                {
                    if (selected)
                    {
                        g.setColor(selectionColor);
                        g.fillRect(x-2,yRect,cellSize.width+4,fontHeight+textLineSpacing+4);
                        g.setColor(getForeground());
                    }
                    g.drawString(text, x+textIndent, yText);
                    yRect += fontHeight + textLineSpacing;
                    yText += fontHeight + textLineSpacing;
                }
            }

            if (sexTextVisible)
            {
                if (organism.getSex() == Organism.MALE)
                {
                    if (selected)
                    {
                        g.setColor(selectionColor);
                        g.fillRect(x-2,yRect,cellSize.width+4,fontHeight+textLineSpacing+4);
                        g.setColor(getForeground());
                    }
                    g.drawString("Male", x+textIndent, yText);
                    yRect += fontHeight + textLineSpacing;
                    yText += fontHeight + textLineSpacing;
                }
                else if (organism.getSex() == Organism.FEMALE)
                {
                    if (selected)
                    {
                        g.setColor(selectionColor);
                        g.fillRect(x-2,yRect,cellSize.width+4,fontHeight+textLineSpacing+4);
                        g.setColor(getForeground());
                    }
                    g.drawString("Female", x+textIndent, yText);
                    yRect += fontHeight + textLineSpacing;
                    yText += fontHeight + textLineSpacing;
                }
                // Don't draw anything if NO_SEX
            }
        }

        // Draw the traits that should be shown in text if organism is visible
        if (organism.isVisible() == true && characteristicsTextVisible)
        {
            Trait aTrait;
            Characteristic aCharacteristic;
            Enumeration eCharacteristics = organism.getCharacteristics();
            while (eCharacteristics.hasMoreElements())
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                aTrait = aCharacteristic.getTrait();
                if (aTrait.isShowAsTextInOrganismView() == true)
                {
                    if (selected)
                    {
                        g.setColor(selectionColor);
                        g.fillRect(x-2,yRect,cellSize.width+4,fontHeight+textLineSpacing+4);
                        g.setColor(getForeground());
                    }
                    g.drawString(aTrait.getName() + ": " + aCharacteristic.getName(),
                                 x+textIndent,yText);
                    yRect += fontHeight + textLineSpacing;
                    yText += fontHeight + textLineSpacing;
                }
            }
        }

        // Update cellSize height to reflect text drawn
        if ((y + cellSize.height) < yText)
        {
            cellSize.height = yText-y;
        }
    }

    /**
     * Paint the organism as a characteristic, not its phenotype.  It's assumed that
     * organism is non-null and not deleted, as this method will hit a null pointer
     * exception otherwise.<p>
     *
     * @param 		g Graphics - the given graphics to use in drawing
     * @param		organism Organism - the organism to draw
     * @param		characteristic Characteristic - the characteristic to draw
     * @param		x int - top left x location
     * @param		y int - top left y location
     * @param		fontHeight int - font height
     * @param		selected boolean - is organism selected?
     * @param		highlighted boolean - is organism highlighted?
     * @return		Dimension of the cell used to draw organism		
    **/
    Dimension paintOrganismCharacteristic(Graphics g, Organism organism, Characteristic characteristic,
                                          int x, int y, int fontHeight, boolean selected, boolean highlighted)
    {
        // Allocate Dimension to return
        Dimension cellSize = new Dimension(0,0);

        if ((g != null) && (organism != null) && (characteristic != null))
        {
            if (organism.isDeleted())
                return cellSize;
            // Determine image width and height
            Species species = organism.getSpecies();
            cellSize.width = species.getImageColumnWidth(organismImageSize);
            cellSize.height = cellSize.width;	// So selection rectangle is square
            int side = cellSize.width-4;
            Image image;
            SpeciesImage speciesImage;
            
            Color color1 = characteristic.getPedigreeSymbolFirstColor();
            Color color2 = characteristic.getPedigreeSymbolSecondColor();

            // Draw females and hermaphrodites as ovals, males as squares
            boolean square = false;
            if (organism.getSex() == Organism.MALE)
            {
                square = true;
            }

            // Highlighting is done to show the path up from an org to its parents
            if (highlighted)
            {
                g.setColor(highlightColor);
                if (square)
                    g.fillRect(x-1, y-1, cellSize.width+3, cellSize.height+3);
                else
                    g.fillOval(x-1, y-1, cellSize.width+3, cellSize.height+3);
            }
            else if (selected)
            {
                g.setColor(selectionColor);
                g.fillRect(x-1, y-1, cellSize.width+3, cellSize.height+3);
            }

            // If invisible, draw question mark image
            // If visible, draw correct filled or partially filled symbol
            if (organism.isVisible() == false)
            {
                // Invisible, so find and draw the single invisible species image
                Enumeration eSpeciesImages = species.getSpeciesImages();
                while (eSpeciesImages.hasMoreElements())
                {
                    speciesImage = (SpeciesImage) eSpeciesImages.nextElement();
                    if (speciesImage.getImageType() == SpeciesImage.INVISIBLE_IMAGE_TYPE)
					{
						if(speciesImage.isDataSourceExist(organismImageSize)){
                       		speciesImage.drawDataImage(g,organismImageSize,0,0,x,y,this);
						}else{
							image = speciesImage.getImage(organismImageSize,this);
       						if(image != null) g.drawImage(image, x, y, this);
						}
                        break;
                    }
                }
            }
            else
            {
                // Draw the appropriate symbol in the appropriate colors
                switch (characteristic.getPedigreeSymbolType())
                {
                    case Characteristic.PEDIGREE_SYMBOL_SOLID_COLOR:
                    {
                        g.setColor(color1);
                        if (square)
                        {
                            g.fillRect(x+2, y+2, side, side);
                        }
                        else
                        {
                            g.fillOval(x+2, y+2, side, side);
                        }
                    }
                    break;
            
                    case Characteristic.PEDIGREE_SYMBOL_FORWARD_SLASH:
                    {
                        if (square)
                        {
                            g.setColor(color1);
                            g.fillRect(x+2, y+2, side, side);
                            Polygon p = new Polygon();
                            p.addPoint(x+2, y+2+side);
                            p.addPoint(x+2+side,y+2);
                            p.addPoint(x+2+side,y+2+side);
                            p.addPoint(x+2, y+2+side);
                            g.setColor(color2);
                            g.fillPolygon(p);
                        }
                        else
                        {
                            g.setColor(color1);
                            g.fillOval(x+2, y+2, side, side);
                            g.setColor(color2);
                            g.fillArc(x+2, y+2, side, side, 45, -180);
                        }
                    }
                    break;
                }
            }
                
            // For both visible and invisible, draw square or circle
            g.setColor(getForeground());
            if (square)
            {
                g.drawRect(x+2, y+2, side, side);
                if (highlighted || selected)
                {
                    g.drawRect(x+1,y+1,side+2,side+2);
                }
            }
            else
            {
                g.drawOval(x+2, y+2, side, side);
                if (highlighted || selected)
                {
                    g.drawOval(x+1,y+1,side+1,side+1);
                    g.drawOval(x+2,y+2,side+1,side+1);
                    g.drawOval(x+1,y+1,side+2,side+2);
                    g.drawOval(x+1,y+2,side+1,side+1);
                    g.drawOval(x+2,y+1,side+1,side+1);
                }
            }

            if (organism.containsFatalCharacteristic())
            {
                g.drawLine(x+side+6,y-1,x-1,y+side+6);
            }

            paintOrganismText(g, organism, x, y, fontHeight, selected, cellSize);
        }

        return cellSize;
    }

    /**
     * Preload the species images for the given species at the current
     * image size.<p>
     *
     * Note that this is just a performance enhancement and is not something
     * a user of the BioLogica engine must call.  It just speeds drawing of
     * the first organism of this species at this image size.<p>
     *
     * @param		aSpecies Species - a species
    **/
    public void preloadSpeciesImages(Species aSpecies)
    {
        Graphics g = this.getGraphics();

        if (g != null && aSpecies != null)
        {
            Image image;
            SpeciesImage speciesImage;
            Enumeration eSpeciesImages = aSpecies.getSpeciesImages();
            while (eSpeciesImages.hasMoreElements())
            {
                speciesImage = (SpeciesImage) eSpeciesImages.nextElement();
				if(speciesImage.isDataSourceExist(organismImageSize)){
               		speciesImage.drawDataImage(g,organismImageSize,0,0,0,0,this);
				}else{
                	image = speciesImage.getImage(organismImageSize,this);
                	if(image != null) g.drawImage(image,0,0,this);
				}
            }
        }
        g.dispose();
    }

    /**
     * Get the organism image size for this view.<p>
     *
     * @return		int - organism image size
    **/
    public int getOrganismImageSize()
    {
        return organismImageSize;
    }

    /**
     * Set the organism image size for this view.  The size must
     * be one of the SpeciesImage.XXX_IMAGE_SIZE static values.<p>
     *
     * @param		anOrganismImageSize int - a new organism image size
     * @exception	IllegalArgumentException - illegal input value
    **/
    public void setOrganismImageSize(int anOrganismImageSize)
    {
        // Return immediately if no change
        if (organismImageSize == anOrganismImageSize)
        {
            return;
        }

        // Ok - make change
        int oldOrganismImageSize = organismImageSize;
        organismImageSize = anOrganismImageSize;

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM_IMAGE_SIZE,
                                   new Integer(oldOrganismImageSize),
                                   new Integer(organismImageSize));
    }

    /**
     * Get the text indent for this view.<p>
     *
     * @return		int - text indent
    **/
    public int getTextIndent()
    {
        return textIndent;
    }

    /**
     * Set the text indent for this view.<p>
     *
     * @param		aTextIndent int - a new text indent
    **/
    public void setTextIndent(int aTextIndent)
    {
        // Return immediately if no change
        if (textIndent == aTextIndent)
        {
            return;
        }

        // Ok - make change
        int oldTextIndent = textIndent;
        textIndent = aTextIndent;

        // Notify listeners
        changes.firePropertyChange(UIProp.TEXT_INDENT,
                                   new Integer(oldTextIndent),
                                   new Integer(textIndent));
    }

    /**
     * Get the text line spacing for this view, the number of
     * pixels between lines of text below an organism.<p>
     *
     * @return		int - text line spacing
    **/
    public int getTextLineSpacing()
    {
        return textLineSpacing;
    }

    /**
     * Set the text line spacing for this view, the number of pixels
     * between lines of text below an organism.<p>
     *
     * @param		aTextLineSpacing int - a new text line spacing
    **/
    public void setTextLineSpacing(int aTextLineSpacing)
    {
        // Return immediately if no change
        if (textLineSpacing == aTextLineSpacing)
        {
            return;
        }

        // Ok - make change
        int oldTextLineSpacing = textLineSpacing;
        textLineSpacing = aTextLineSpacing;

        // Notify listeners
        changes.firePropertyChange(UIProp.TEXT_LINE_SPACING,
                                   new Integer(oldTextLineSpacing),
                                   new Integer(textLineSpacing));
    }

    /**
     * ImageObserver method
    **/
    public boolean imageUpdate(Image anImage,
                               int infoFlags,
                               int x,
                               int y,
                               int width,
                               int height)
    {
        if (infoFlags == ImageObserver.ALLBITS)
        {
            repaint();
        }

        return true;
    }

    /**
     * Get organism cell dimensions, used when picking.
     *
     * @param		organism Organism - the organism to draw
     * @param		x int - x location
     * @param		y int - y location
     * @param		fontHeight int - font height
     * @param		drawAsTrait boolean - is organism drawn as a trait?
     * @return		Dimension - cell size
    **/
    protected Dimension getOrganismCellDimensions(Organism organism, int x, int y, int fontHeight, boolean drawAsTrait)
    {
        // Allocate Dimension to return
        Dimension cellSize = new Dimension(0,0);

        if (organism != null)
        {
            if (organism.isDeleted())
                return cellSize;
            // Determine image width and height
            Species species = organism.getSpecies();
            cellSize.width = species.getImageColumnWidth(organismImageSize);
            cellSize.height = species.getImageRowHeight(organismImageSize);

            if (drawAsTrait == true)
            {
                cellSize.height = cellSize.width;	// Since that's what the paintOrgChar.. does
            }

            if (drawAsTrait == false)
            {
                // Determine what text would be drawn
                int yText = y + cellSize.height;
                {
                    if (nameTextVisible)
                    {
                        yText += fontHeight + textLineSpacing;
                    }
    
                    if (speciesTextVisible)
                    {
                        yText += fontHeight + textLineSpacing;
                    }
    
                    if (sexTextVisible)
                    {
                        if (organism.getSex() == Organism.MALE)
                        {
                            yText += fontHeight + textLineSpacing;
                        }
                        else if (organism.getSex() == Organism.FEMALE)
                        {
                            yText += fontHeight + textLineSpacing;
                        }
                    }
                }
    
                // Draw the traits that should be shown in text if organism is visible
                if (organism.isVisible() == true && characteristicsTextVisible)
                {
                    Trait aTrait;
                    Characteristic aCharacteristic;
                    Enumeration eCharacteristics = organism.getCharacteristics();
                    while (eCharacteristics.hasMoreElements())
                    {
                        aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                        aTrait = aCharacteristic.getTrait();
                        if (aTrait.isShowAsTextInOrganismView() == true)
                        {
                            yText += fontHeight + textLineSpacing;
                        }
                    }
                }
                
                // Update cellSize height to reflect text drawn
                if ((y + cellSize.height) < (yText + 10))
                {
                    cellSize.height = yText+10-y;
                }
            }
        }

        return cellSize;
    }

    /**
     * Update the hotspot state given that the user has pressed the mouse on the given
     * organism at the given location in this view.  This may or may not turn on a hotspot.<p>
     *
     * @param		anOrganism Organism - the organism that the user has pressed on
     * @param		xMouse int - x coordinate of mouse press relative to topleft of organism's cell
     * @param		yMouse int - y coordinate of mouse press relative to topleft of organism's cell
    **/
    protected void updateHotspotOnMousePress(Organism anOrganism, int xMouse, int yMouse)
    {
        // Turn off hotspot initially, just to be safe
        hotspotOrganism = null;
        hotspotOrganismImage = null;

        // Look for an organism image with a hotspot at the given coordinates
        if (anOrganism != null)
        {
            if (anOrganism.isVisible() == true)
            {
                int rowIndex;
                SpeciesImage speciesImage;
                SpeciesImageRow speciesImageRow;
                Enumeration eSpeciesImageRows;
                OrganismImage organismImage;
                Enumeration eOrganismImages = anOrganism.getOrganismImages();
                while (eOrganismImages.hasMoreElements())
                {
                    organismImage = (OrganismImage) eOrganismImages.nextElement();
                    if (organismImage.getImageType() == SpeciesImage.HOTSPOT_IMAGE_TYPE)
                    {
                        rowIndex = organismImage.getRowIndex();
                        speciesImage = organismImage.getSpeciesImage();

                        eSpeciesImageRows = speciesImage.getSpeciesImageRows();
                        while (eSpeciesImageRows.hasMoreElements())
                        {
                            speciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
                            if (speciesImageRow.isOnHotspot(organismImageSize,xMouse,yMouse))
                            {
                                hotspotOrganism = anOrganism;
                                hotspotOrganismImage = organismImage;
                                repaint();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the hotspot state given that the user has released the mouse.
     * This will always turn off a hotspot if one is active.
    **/
    protected void updateHotspotOnMouseReleased()
    {
        hotspotOrganism = null;
        hotspotOrganismImage = null;
        repaint();
    }


}

