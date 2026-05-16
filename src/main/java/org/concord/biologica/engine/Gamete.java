package org.concord.biologica.engine;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.PrintWriter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.Hashtable;

import org.concord.biologica.ui.*;

public final class Gamete extends EngineObject{
	String alleleText= new String();
	int type;

    Hashtable traits;

	private World world;
	public Gamete()
	{
		type = 0;
		alleleText="";
		world = null;
		traits =  new Hashtable();
	}
	public void setAlleleInfor(OrganismAllele allele)
	{
		if (deleted)
			return;
		String geneName = (allele.getGene()).getName();
		traits.put(geneName,allele);
	}
	
	public OrganismAllele getAlleleInforOfTrait(String trait)
	{
		if (deleted)
			throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
		OrganismAllele allele = (OrganismAllele)traits.get(trait);
		return allele;
	}
	public World getWorld()
    {
        return getAlleleInforOfTrait("Horns").getWorld();
    }
	public String getAlleleInforOfTraitAsString(String trait)
	{
		if (deleted)
			throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
		if (traits.isEmpty()) return "";
		OrganismAllele alleles = (OrganismAllele)traits.get(trait);
		if (alleles == null) return "";
		String alleleStr =alleles.getTextSymbol();
		return alleleStr;
	}
	public void setAllelesString(String str)
	{
		if (deleted)
			return;
		alleleText = alleleText+str+",";
	}
	public String getAllelesString()
	{
		if (deleted)
			throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
		return alleleText;
	}
	public void setTypeOfGamete(int t)
	{
		if (deleted) return;
		type = t;
	}
	public int getTypeOfGamete()
	{
	
		if (deleted)
			throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
		int tempType = type;
		if (type == MeiosisChromosomeModel.TOP_LEFT_GAMETE_CHROMOSOME)
			tempType = MeiosisModel.TOP_LEFT_GAMETE;
		else if (type == MeiosisChromosomeModel.TOP_RIGHT_GAMETE_CHROMOSOME)
			tempType =MeiosisModel.TOP_RIGHT_GAMETE;
		else if (type == MeiosisChromosomeModel.BOTTOM_LEFT_GAMETE_CHROMOSOME)
			tempType = MeiosisModel.BOTTOM_LEFT_GAMETE;
		else if (type == MeiosisChromosomeModel.BOTTOM_RIGHT_GAMETE_CHROMOSOME)
			tempType = MeiosisModel.BOTTOM_RIGHT_GAMETE;
		return tempType;
	}
	public String getTypeOfGameteAsString()
	{
		if (deleted)
			throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
		String strType = "NO_GAMETE";
		
		if (type == MeiosisChromosomeModel.TOP_LEFT_GAMETE_CHROMOSOME)
			strType ="TOP_LEFT_GAMETE";
		else if (type ==MeiosisChromosomeModel.TOP_RIGHT_GAMETE_CHROMOSOME)
			strType ="TOP_RIGHT_GAMETE";
		else if (type == MeiosisChromosomeModel.BOTTOM_LEFT_GAMETE_CHROMOSOME)
			strType ="BOTTOM_LEFT_GAMETE";
		else if (type == MeiosisChromosomeModel.BOTTOM_RIGHT_GAMETE_CHROMOSOME)
			strType = "BOTTOM_RIGHT_GAMETE;";
		
		return strType;
	}
	public void delete()
	{
		delete(true);
	}
	public void delete(boolean notifyChange) {
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
        
        type = 0;
		alleleText=null;
		traits = null;
	}
	public String toString()
    {
        return "Gamete:" + getTypeOfGameteAsString();
    }
    
    public void writeToStream(PrintWriter stream) throws java.io.IOException
    {
    	if (stream == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
    }
 
}