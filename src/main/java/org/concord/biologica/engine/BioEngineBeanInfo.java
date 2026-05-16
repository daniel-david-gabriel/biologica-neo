//
// Class : BioEngineBeanInfo
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:11 $
// $Author: ed $
//
package org.concord.biologica.engine;

import java.io.Serializable;

import java.beans.*;
import java.beans.SimpleBeanInfo;

/**
 * This class contains the information necessary for a Java Beans
 * builder (e.g. a beanbox) to work with the BioEngine bean.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/
public class BioEngineBeanInfo
extends java.beans.SimpleBeanInfo
{
	/**
	 * The class for which this is a bean info.
	**/
	private final static Class beanClass = BioEngine.class;

	/**
	 * Creates the bean info object.
	**/
	public BioEngineBeanInfo()
	{
	}

	/**
	 * Gets a BeanInfo for the superclass of this bean.
	 * @return BeanInfo[] containing this bean's superclass BeanInfo
	 */
	public BeanInfo[] getAdditionalBeanInfo()
	{
		try
		{
			BeanInfo[] bi = new BeanInfo[1];
			bi[0] = Introspector.getBeanInfo(beanClass.getSuperclass());
			return bi;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e.toString());
		}
	}

	/**
	 * Gets the BeanDescriptor for this bean.
	 *
	 * @return an object of type BeanDescriptor
	**/
	public BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor bd = new BeanDescriptor(beanClass);
		return bd;
	}

	/**
	 * Gets an image that may be used to visually represent this bean
	 * (in the toolbar, on a form, etc).
	 * @param iconKind the type of icon desired, one of: BeanInfo.ICON_MONO_16x16,
	 * BeanInfo.ICON_COLOR_16x16, BeanInfo.ICON_MONO_32x32, or BeanInfo.ICON_COLOR_32x32.
	 * @return an image for this bean
	 * @see BeanInfo#ICON_MONO_16x16
	 * @see BeanInfo#ICON_COLOR_16x16
	 * @see BeanInfo#ICON_MONO_32x32
	 * @see BeanInfo#ICON_COLOR_32x32
	**/
	public java.awt.Image getIcon(int nIconKind)
	{
		java.awt.Image img = null;
		if (nIconKind == BeanInfo.ICON_COLOR_16x16)
				img = loadImage("be16col.gif");
		if (nIconKind == BeanInfo.ICON_MONO_16x16)
			img = loadImage("be16mono.gif");
		if (nIconKind == BeanInfo.ICON_COLOR_32x32)
			img = loadImage("be32col.gif");
		if (nIconKind == BeanInfo.ICON_MONO_32x32)
			img = loadImage("be32mono.gif");
		return img;
	}

	/**
	 * Returns descriptions of this bean's properties.
	 *
	 * @return	PropertyDescriptor[] - array of property descriptors
	**/
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor name = new PropertyDescriptor("name",beanClass, "getName","setName");
			name.setConstrained(true);
			PropertyDescriptor[] rv = { name };
			return rv;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e.toString());
		}
	}
}