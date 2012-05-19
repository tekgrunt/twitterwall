package twitter.data.object;

import processing.core.PImage;

public interface IMovingImage 
{
	public void applyUpdate();
	public PImage getImage();
	public int getY();
	public int getX(); 
}
