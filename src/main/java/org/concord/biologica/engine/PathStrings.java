//
// Class : PathStrings
//
// Copyright � 1999, The Concord Consortium
//
// Original Author: Ed Burke
//
// $Revision: 1.5 $
// $Date: 2002/02/19 22:18:17 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.applet.Applet;
import java.io.File;
import java.net.URL;

/**
 * PathStrings is a class containing static references to path strings
 * used in various parts of BioLogica, including the engine.  So it's
 * put here in engine as the core package of the Biologica packages,
 * making these methods available throughout Biologica.<p>
 *
 * @version		$Revision: 1.5 $ $Date: 2002/02/19 22:18:17 $
 * @author 		$Author: dima $
**/
public final class PathStrings
{
    /**
     * In case this is run inside a browser...
    **/
    private static Applet applet = null;
    private static URL baseURL = null;

    /**
     * General platform file separator string
    **/
    private static String fileSeparator = File.separator;
    
    /**
     * The current user directory string
     * Must end in file separator.
    **/
    private static String userDirectory = null;

    /**
     * Path strings to directories containing general purpose BioLogica resources (gifs, audio, etc.)
     * Must end in file separator.
    **/
    private static String lockedDirectory = null;

    /**
     * Path to audio files directory
     * Must end in file separator.
    **/
    private static String audioDirectory = null;

    /**
     * Path to gif files directory
     * Must end in file separator.
    **/
    private static String gifsDirectory = null;

    /**
     * Path to video files directory
     * Must end in file separator.
    **/
    private static String videoDirectory = null;
    
    /**
     * Path to world files directory
     * Must end in file separator.
    **/
    private static String worldsDirectory = null;
    
    /**
     * Path to species files directory
     * Must end in file separator.
    **/
    private static String speciesDirectory = null;

    /**
     * Path to html files directory
     * Must end in file separator.
    **/
    private static String htmlDirectory = null;

    /**
     * Flag indicating if path strings have been initialized
    **/
    private static boolean initialized = false;
    
    public static void setApplet(Applet theApplet)
    {
        applet = theApplet;
        try
        {
            baseURL = new URL(applet.getCodeBase(), ".");
        }
        catch (Exception e)
        {
        }
    }

    // Recalculate path strings, usually because userDirectory changed
    private static void recalculatePathStrings()
    {
    	// Moved resources into src/main/resources
        speciesDirectory = userDirectory + "species" + fileSeparator;
        worldsDirectory = userDirectory + "worlds" + fileSeparator;
        lockedDirectory = userDirectory + "locked" + fileSeparator;

        htmlDirectory = lockedDirectory + "html" + fileSeparator;
        gifsDirectory = lockedDirectory + "gifs" + fileSeparator;
        videoDirectory = lockedDirectory + "video" + fileSeparator;
        audioDirectory = lockedDirectory + "audio" + fileSeparator;
    }

    // Initialize path strings
    private static void initializePathStrings()
    {
        if (initialized == false)
        {
            fileSeparator = File.separator;
            if (applet instanceof Applet)
            {
                userDirectory = baseURL.toExternalForm();
            }
            else {
            	// TODO: Update for resource loader when running from jar
            	StringBuilder stringBuilder = new StringBuilder(System.getProperty("user.dir")).append(fileSeparator)
            			.append("src").append(fileSeparator).append("main").append(fileSeparator).append("resources")
            			.append(fileSeparator).append("org").append(fileSeparator).append("concord").append(fileSeparator)
            			.append("biologica");
            	userDirectory = stringBuilder.toString();
            }
            if (userDirectory.endsWith(fileSeparator) == false)
            {
                userDirectory = userDirectory + fileSeparator;
            }

            recalculatePathStrings();

            initialized = true;
        }
    }

    /**
     * Get the Gif Directory, which will end with a file separator
     *
     * @return		String - gif directory path with a file separator at the end
    **/
    public static String getGIFDirectory()
    {
        if (initialized == false)
        {
            initializePathStrings();
        }

        return gifsDirectory;
    }

    /**
     * Get the Worlds Directory, which will end with a file separator
     *
     * @return		String - worlds directory path with a file separator at the end
    **/
    public static String getWorldsDirectory()
    {
        if (initialized == false)
        {
            initializePathStrings();
        }

        return worldsDirectory;
    }

    /**
     * Get the Species Directory, which will end with a file separator
     *
     * @return		String - species directory path with a file separator at the end
    **/
    public static String getSpeciesDirectory()
    {
        if (initialized == false)
        {
            initializePathStrings();
        }

        return speciesDirectory;
    }
    
    public static String getHTMLDirectory()
    {
        if (initialized == false)
        {
            initializePathStrings();
        }

        return htmlDirectory;
    }

    public static URL getHTMLURL(String fn)
    {
        return getResource("org/concord/biologica/locked/html/" + fn);
    }

    public static URL getGIFURL(String fn)
    {
        return getResource("org/concord/biologica/locked/gifs/" + fn);
    }

    public static URL getResource(Object object, String resource)
    {
        if (object == null)
            return getResource(resource);
        ClassLoader loader = object.getClass().getClassLoader();
        if (loader instanceof ClassLoader)
            return loader.getResource(resource);
        return getResource(resource);
    }

    public static URL getResource(String resource)
    {
        ClassLoader loader = PathStrings.class.getClassLoader();
        if (loader instanceof ClassLoader)
            return loader.getResource(resource);
        else
            return ClassLoader.getSystemResource(resource);
    }

    public static java.io.InputStream getResourceAsStream(Object object, String resource)
    {
        if (object == null)
            return getResourceAsStream(resource);
        ClassLoader loader = object.getClass().getClassLoader();
        if (loader instanceof ClassLoader)
            return loader.getResourceAsStream(resource);
        return getResourceAsStream(resource);
    }

    public static java.io.InputStream getResourceAsStream(String resource)
    {
        ClassLoader loader = PathStrings.class.getClassLoader();
        if (loader instanceof ClassLoader)
            return loader.getResourceAsStream(resource);
        else
            return ClassLoader.getSystemResourceAsStream(resource);
    }

}

