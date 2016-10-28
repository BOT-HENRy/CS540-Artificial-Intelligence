import processing.core.*;
import processing.data.*;

public class qu_Assmt01 extends PApplet
{		
	private XML data;
	private boolean keyAlreadyPressed;
	
	public void settings() {
		  size(800, 600);
	}
	
	public void loadBoxes(String filename)
	{
		// TODO: implement this method in step 1
		data = loadXML("boxData.xml");
		getTree(data);
		
	}
		
	//get info from xml file 
	public void getTree(XML xml){
		if(xml == null){
			return;
		}else if(!xml.hasChildren()){
			return;
		}else{
			XML[] kids = xml.getChildren();
			for(int i = 0; i < kids.length; i ++){
				if(kids[i].getName().equals("#text")){
					XML curParent = kids[i].getParent();
					curParent.removeChild(kids[i]);
				}
				//use recursion
				getTree(kids[i]);
			}
		}
	}
	
	//this method recursively visits nodes in the xml tree and draw 
	public void drawBoxes(XML xml, int x, int y)
	{
		// TODO: implement this method in step 2
		if(xml == null){
			return;
		}else if(!xml.hasChildren()){
			return;
		}else{
			//rect(x, y, 20, 20);
			XML boxes[] = xml.getChildren();
			
			for(int i = 0; i < boxes.length; i ++){
				int xPosition = x + boxes[i].getInt("x");
				int yPosition = y + boxes[i].getInt("y");
				if(boxes[i].getName().equals("box")){
					rect(xPosition, yPosition, 20, 20);
				}
				//rect(xPosition, yPosition, 20, 20);
				drawBoxes(boxes[i], xPosition, yPosition);
			}
			
		}
		
	}
	
	//This method double the x and y attributes of every move node in the xml tree
	public void doubleMoves(XML xml)
	{
		// TODO: implement this method in step 3
		if(xml == null){
			return;
		}else if(!xml.hasChildren()){
			return;
		}else{
			XML boxes[] = xml.getChildren();
			
			for(int i = 0; i < boxes.length; i ++){
				boxes[i].setInt("x", boxes[i].getInt("x") * 2);
				boxes[i].setInt("y", boxes[i].getInt("y") * 2);
				doubleMoves(boxes[i]);
			}
			
		}
	}
	
	//This method  replace every box node in the tree with four move nodes that 
	//each have a single box node as a child
	public void doubleBoxes(XML xml)
	{
		// TODO: implement this method in step 4	
		
		if(xml == null){
			return;
		}else if(!xml.hasChildren()){
			return;
		}else{
			XML boxes[] = xml.getChildren();
			
			for(int i = 0; i < boxes.length; i ++){
				if(boxes[i].getName().equals("box")){
					//get information and remove current node
					XML curParent = boxes[i].getParent();
					int xPosition = boxes[i].getInt("x");
					int yPosition = boxes[i].getInt("y");
					curParent.removeChild(boxes[i]);
					
					//replace with four children
					XML childOne = curParent.addChild("move");
					childOne.addChild("box");
					childOne.setInt("x", xPosition - 10);
					childOne.setInt("y", yPosition - 10);
					
					XML childTwo = curParent.addChild("move");
					childTwo.addChild("box");
					childTwo.setInt("x", xPosition - 10);
					childTwo.setInt("y", yPosition + 10);
					
					XML childThree = curParent.addChild("move");
					childThree.addChild("box");
					childThree.setInt("x", xPosition + 10);
					childThree.setInt("y", yPosition - 10);
					
					XML childFour = curParent.addChild("move");
					childFour.addChild("box");
					childFour.setInt("x", xPosition + 10);
					childFour.setInt("y", yPosition + 10);
					
				}
			doubleBoxes(boxes[i]);
			}
		}
		
		
	}
	
	// tie key press events to calling the functions above:
	// 1 - loadBoxes
	// 2 - drawBoxes
	// 3 - doubleMoves
	// 4 - doubleBoxes
	public void draw()
	{
		if(keyPressed)
		{
			if(keyAlreadyPressed == false)
			{
				switch(key)
				{
				case '1':
					loadBoxes("boxData.xml");
					
					break;
				case '2':
					background( 255 );
					drawBoxes(data, width/2, height/2);	
					save("output.png");
					break;
				case '3':
					doubleMoves(data);		
					break;
				case '4':
					doubleBoxes(data);		
					break;
				}
			}
			keyAlreadyPressed = true;
		}
		else
			keyAlreadyPressed = false;
	}

	// basic processing setup: window size and background color
	public void setup()
	{
		size( 800, 600 );
		background( 255 );
		data = null;
		keyAlreadyPressed = true;
	}
		
	// run as an Application instead of as an Applet
	public static void main(String[] args) 
	{
		String thisClassName = new Object(){}.getClass().getEnclosingClass().getName();
		PApplet.main( new String[] { thisClassName } );
	}
}
