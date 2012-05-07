import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * This is the original moving image class. It gets updated the same way the tweets do and
 * handles its own position on the screen. The other image class was a cut/paste last minute
 * addition and the images should all be wrapped in one class that takes a direction. Also need to
 * move the images into their own source folder instead of the main.
 * 
 * @author christopherluft
 *
 */
public class FallingImage 
{
	private PImage fallingImage = new PImage();;
	private PApplet p;
	private int x;
	private int y;
	private int speed;

	public FallingImage(PImage fallingImage, PApplet p)
	{
		Random randomGenerator = new Random();
		this.fallingImage = fallingImage;
		this.p = p;
		y = -1 * fallingImage.height;
		x = randomGenerator.nextInt(1024);
		speed = 1 + randomGenerator.nextInt(4);
	}
	
	public FallingImage(PImage fallingImage, PApplet p, String noMove)
	{
		Random randomGenerator = new Random();
		this.fallingImage = fallingImage;
		this.p = p;
		y = -1 * fallingImage.height;
		x = 0;
		
		if(noMove.equals("random"))
		{
			speed = 1 + randomGenerator.nextInt(4);
		}
		else
		{
			speed = 1;// + randomGenerator.nextInt(4);
		}
	}
	
	public void updateImage()
	{
		p.image(fallingImage,x,y);
		y += speed;
	}

	public int getY() {
		return y;
	}
	public int getX() {
		return x;
	}
}
