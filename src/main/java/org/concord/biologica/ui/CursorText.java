package org.concord.biologica.ui;

import java.awt.*;

/*
// header - edit "Data/yourJavaHeader" to customize
// contents - edit "EventHandlers/Java file/onCreate" to customize
//
*/
public final class CursorText
{
	private Frame frame;
	private Window window;
	private Label label;
	private Point offset;
	private Component owner;
	private int displaceX;
	private int displaceY;
	
	public CursorText(Component creator, String text)
	{
		frame = findFrame(creator);
		window = new Window(frame);
		label = new Label(text);
		window.add("Center", label);
		owner = creator;
		displaceX = 20;
		displaceY = 20;
		window.pack();
	}
	
	protected Frame findFrame(Component component)
	{
		for (Container parent = component.getParent();
			 parent instanceof Container;
			 parent = component.getParent())
		{
			if (parent instanceof Frame)
			{
				return (Frame) parent;
			}
		}
		return new Frame();
	}
	
	public void setLocation(int x, int y)
	{
		if (owner.isVisible())
		{
			offset = owner.getLocationOnScreen();
			window.setLocation(offset.x + x + displaceX, offset.y + y + displaceY);
		}
	}
	
	public void setVisible(boolean visible)
	{
		window.setVisible(visible);
	}
}

