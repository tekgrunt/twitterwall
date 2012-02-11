import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;


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
