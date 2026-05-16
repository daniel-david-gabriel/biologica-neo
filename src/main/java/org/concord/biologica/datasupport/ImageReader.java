package org.concord.biologica.datasupport;

import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.concord.biologica.engine.SpeciesImage;


public class ImageReader{

static Color 	[]mainColors = {Color.cyan,new Color(255,128,0),Color.red,Color.green,Color.magenta,new Color(255,255,100),Color.white};
static int 		[]saturInd = {10,10,100,10,10,10,10};
static MyFilter	[]filters;

public static Hashtable	imageDescriptions = new Hashtable();



	static{
		initFilters();
		
	}

	public static void initFilters(){
		if(filters != null) return;
		filters = new MyFilter[mainColors.length];
		for(int i = 0; i < mainColors.length; i++){
			filters[i] = new MyFilter(mainColors[i],saturInd[i]);
		}
	}
	
	public static Image getImageByColor(Image img,int color){
		if(filters == null || img == null || color < 0 || color >= filters.length) return null;
		return filters[color].createImage(img.getSource());
	}



// 0 <= color	< 7
// 0 <= body 	< 9
// 0 <= fire 	< 6 for first 3 bodies 0 or 1; for second 3 bodies 2 or 3 ...
// 0 <= head 	< 3 for first 3 bodies can pick up any first 3 heads, second 3 bodies - any second 3 heads ...
// 0 <= wing 	< 2 for first 3 bodies can pick up any first 2 wing, second 3 bodies - any second 2 wing ...
// 0 <= plate 	< 2 for first 3 bodies can pick up any first 2 plate, second 3 bodies - any second 2 plate ...
// 0 <= scale 	< 3

	public static int createHashIndex(int partID,int sizeID){
		int size = sizeID & 0xFF;
		int part = partID & 0xFF;
		int		hashIndex = (size << 8) + part;
		return hashIndex;
	}
	
	public static boolean isShortImageDescriptionInCache(int partID,int sizeID){
		return (getShortImageDescription(partID,sizeID) != null);
	}
	
	public static ShortImageDescription getShortImageDescription(int partID,int sizeID){
		if(imageDescriptions == null) return null;
		int		hashIndex = createHashIndex(partID,sizeID);
		ShortImageDescription obj = (ShortImageDescription)imageDescriptions.get(new Integer(hashIndex));
		return obj;
	}
	
	public static void readFile(URL url,int partID,int sizeID,Component c){
		byte []bytes;
		int wasRead = 0;
		try{
			DataInputStream dis = new DataInputStream(new GZIPInputStream(url.openStream()));

			int 	wsize 		= dis.readInt();								//wsize
			int 	hsize 		= dis.readInt();								//hsize
			int 	nColors		= dis.readInt();								//nColors
			int 	ncells		= dis.readInt();								//N cells
			int 	xNumb		= dis.readInt();								//xNumb
			boolean allImages	= dis.readBoolean();							//allImages

			Image []mainimages = new Image[ncells*xNumb];
			int			[]xoffsets = new int[ncells*xNumb];
			int			[]yoffsets = new int[ncells*xNumb];
			MediaTracker mt = (c != null)?new MediaTracker(c):null;
			for(int j = 0; j < ncells; j++){
				for(int i = 0; i < xNumb; i++){
					int index = i + j*xNumb;
					ImageDescription imgdescription = new ImageDescription(dis);
					int zero = dis.readInt();
					mainimages[index] = imgdescription.createImage();
					if(mt != null) mt.addImage(mainimages[index],index);
					xoffsets[index] = imgdescription.contentRect.x;
					yoffsets[index] = imgdescription.contentRect.y;
				}
			}
			if(mt != null){
				try{
					mt.waitForAll();
				}catch(Exception e){
				}
			}
			imageDescriptions.put(new Integer(createHashIndex(partID,sizeID)),new ShortImageDescription(sizeID,partID,ncells,nColors,allImages,mainimages,xoffsets,yoffsets));
			
			dis.close();
		}catch(Exception e){
			System.out.println("Exception e"+e);
		}
	}
}




class ImageDescription{
public int 				index;
public int 				width;
public int  			height;
public Rectangle 		contentRect;  
public boolean 			isIndexColorModel;
int 					pixelsize;
ColorModel				cm;
private byte			[]buffer;
Object					pixels;
	public ImageDescription(DataInputStream dis) throws Exception{
		if(dis == null) return;
		index = dis.readInt();
		width = dis.readInt();
		height = dis.readInt();
		int rx = dis.readInt();
		int ry = dis.readInt();
		int rw = dis.readInt();
		int rh = dis.readInt();
		contentRect = new Rectangle(rx,ry,rw,rh);
		
		isIndexColorModel = dis.readBoolean();
/*
		if(isIndexColorModel){
			System.out.println("INDEXED COLOR MODEL");
		}else{
			System.out.println("DIRECT COLOR MODEL");
		}
*/
		pixelsize = dis.readInt();
		if(isIndexColorModel){
			int transparentpixel = dis.readInt();
			int redslength = dis.readInt();
			int mapsize = redslength;
			int needBytes = redslength;
			byte []cmap = new byte[4*mapsize];
			buffer = new byte[needBytes];
			fullReadInBuffer(buffer,dis);
			for(int i = 0; i < buffer.length; i++){
				cmap[4*i] = buffer[i];
			}
			int greenslength = dis.readInt();
			if(greenslength != needBytes){
				throw new Exception("wrong image data");
			}
			fullReadInBuffer(buffer,dis);
			for(int i = 0; i < buffer.length; i++){
				cmap[4*i+1] = buffer[i];
			}
			int blueslength = dis.readInt();
			if(blueslength != needBytes){
				throw new Exception("wrong image data");
			}
			fullReadInBuffer(buffer,dis);
			for(int i = 0; i < buffer.length; i++){
				cmap[4*i+2] = buffer[i];
			}
			int alphaslength = dis.readInt();
			if(alphaslength != needBytes){
				throw new Exception("wrong image data");
			}
			fullReadInBuffer(buffer,dis);
			for(int i = 0; i < buffer.length; i++){
				cmap[4*i+3] = buffer[i];
			}
			
			cm = new IndexColorModel(pixelsize,needBytes,cmap,0,true,transparentpixel);
			
			int pixelslength = dis.readInt();
			pixels = new byte[pixelslength];
			fullReadInBuffer((byte [])pixels,dis);
		}else{
			int redmask = dis.readInt();
			int greenmask = dis.readInt();
			int bluemask = dis.readInt();
			int alphamask = dis.readInt();
			cm = new DirectColorModel(pixelsize,redmask,greenmask,bluemask,alphamask);
			int pixelslength = dis.readInt();
			int []intPixels = new int[pixelslength];
			for(int x = 0; x < rw; x++){
				for(int y = 0; y < rh; y++){
					int index = y * rw + x;
					intPixels[index] = dis.readInt();
				}
			}
			pixels = intPixels;
		}
//				int zero = dis.readInt();
//				System.out.println("zero "+zero);
	}
	public Image createImage(){
		MemoryImageSource mis = null;
		if(isIndexColorModel){
			mis = new MemoryImageSource(contentRect.width,contentRect.height,cm,(byte [])pixels,0,contentRect.width);
		}else{
			mis = new MemoryImageSource(contentRect.width,contentRect.height,cm,(int [])pixels,0,contentRect.width);
		}
		return Toolkit.getDefaultToolkit().createImage(mis);
	}
	private int fullReadInBuffer(byte []buffer,InputStream is){
		int currRead = 0;
		try{
			int needRead = buffer.length;
			while(currRead < needRead){
				currRead += is.read(buffer,currRead,needRead - currRead);
			}
		}catch(Exception e){
		}
		return currRead;
	}
}

class MyFilter extends ImageFilter{
int rc,gc,bc;
float hue;
int hueI;
boolean isWhite;
boolean javaHSV = true;

static float 	[]hsb 		= new float[3];
static int 		[]hsvd 		= new int[3];
static int 		[]rgbd 		= new int[3];
ColorModel cModel;
int	saturInd = 10;
	public MyFilter(Color color,int saturInd){
		super();
		setColor(color);
		cModel = Toolkit.getDefaultToolkit().getColorModel();
		this.saturInd = saturInd;
	}
	
	public Image createImage(ImageProducer ip){
		return createImage(ip,null);
	}
	
	public Image createImage(ImageProducer ip,Component c){
		ImageProducer cp = new FilteredImageSource(ip,this);
		Toolkit toolkit = (c == null)?Toolkit.getDefaultToolkit():c.getToolkit();
		return toolkit.createImage(cp);
	}
	
	public void setColor(Color color){
		rc = color.getRed();
		gc = color.getGreen();
		bc = color.getBlue();
		float []hsb = Color.RGBtoHSB(rc,gc,bc,null);
		hue = hsb[0];
		hueI = (int)(hue*255f+0.5);
		isWhite = Color.white.equals(color);
		
	}
    public void setColorModel(ColorModel model) {
		cModel = model;
    	if(model instanceof DirectColorModel){
			cModel = model;
    	}else{
    		IndexColorModel icm = (IndexColorModel)model;
    		int mapsize = icm.getMapSize();
	    	byte cmap[] = new byte[4*mapsize];
	    	byte reds[] = new byte[mapsize];
	    	byte greens[] = new byte[mapsize];
	    	byte blues[] = new byte[mapsize];
	    	icm.getReds(reds);
	    	icm.getGreens(greens);
	    	icm.getBlues(blues);
	    	int k = 0;
	    	int tp = icm.getTransparentPixel();
	    	for(int i = 0; i < mapsize; i++){
				int r = reds[i];	if(r < 0) r+= 256;
				int g = greens[i];	if(g < 0) g+= 256;
				int b = blues[i];	if(b < 0) b+= 256;
				if(javaHSV){
					Color.RGBtoHSB(r,g,b,hsb);
					if(saturInd < 10) hsb[1] *= (saturInd/10f);
					int newc = Color.HSBtoRGB(hue,(isWhite)?0.f:hsb[1],hsb[2]);
					int tempI;
					tempI = newc & 0xFF0000;tempI >>= 16; tempI &= 0xFF;
					reds[i] 	= (byte)(tempI);
					tempI = newc & 0xFF00;tempI >>= 8; tempI &= 0xFF;
					greens[i] 	= (byte)(tempI);
					tempI = newc &  0xFF;
					blues[i] 	= (byte)(tempI);
				}else{
					ColorUtil.RGB2HSV(r,g,b,hsvd);
					if(saturInd < 10) hsvd[1] = (hsvd[1]*saturInd/10);
					ColorUtil.HSV2RGB(hueI,(isWhite)?0:hsvd[1],hsvd[2],rgbd);
					reds[i] 	= (byte)(rgbd[0] & 0xFF);
					greens[i] 	= (byte)(rgbd[1] & 0xFF);
					blues[i] 	= (byte)(rgbd[2] & 0xFF);
				}
				cmap[k++] = reds[i];
				cmap[k++] = greens[i];
				cmap[k++] = blues[i];
				cmap[k++] = (i == tp)?0:(byte)255;
	    	}
	    	
	    	
			cModel = new IndexColorModel(model.getPixelSize(),mapsize,cmap,0,true,tp);
		}
		super.setColorModel(cModel);
    }
    public synchronized void setPixels(  int x, int y, int w, int h,
			  				ColorModel model, byte pixels[], int off,
			  				int scansize) {
		super.setPixels(x, y, w, h, cModel, pixels, 0, scansize);
	}
    public synchronized void setPixels(	int x, int y, int w, int h,
			  				ColorModel model, int pixels[], int off,
			  				int scansize) {
		super.setPixels(x, y, w, h, cModel, pixels, 0, scansize);
    }
}


