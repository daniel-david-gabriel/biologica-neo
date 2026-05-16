package org.concord.biologica.ui;

public interface CCDraggable{
	void startDrag(int x,int y);
	void doDrag(int x,int y);
	void endDrag(int x,int y);
	//Object getDraggableOwner();
	//boolean panelInfoSupport();
	//void setInfoDrawer(CCInfoDrawer infoDrtawer);

	public void setDraggable(boolean draggable);
	public boolean isDraggable();
}