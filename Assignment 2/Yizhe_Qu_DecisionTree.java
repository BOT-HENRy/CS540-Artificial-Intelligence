import processing.core.*;
import processing.data.*;

import java.math.*;

public class Yizhe_Qu_DecisionTree extends DrawableTree
{	
	public Yizhe_Qu_DecisionTree(PApplet p) { super(p); }
		
	XML dataTree;
	
	// This method loads the examples from the provided filename, and
	// then builds a decision tree (stored in the inherited field: tree).
	// Each of the nodes in this resulting tree will be named after
	// either an attribute to split on (vote01, vote02, etc), or a party
	// classification (DEMOCRAT, REPUBLICAN, or possibly TIE).
	public void learnFromTrainingData(String filename)
	{
		// NOTE: Set the inherited field dirtyTree to true after building the
		// decision tree and storing it in the inherited field tree.  This will
		// trigger the DrawableTree's graphical rendering of the tree.
		
		// TODO - implement this method
		dataTree = p.loadXML(filename);
		deleteBlank(dataTree);
		tree = new XML("tree");		
		recursiveBuildTree(dataTree,tree);
		dirtyTree = true;
	}
	
	//This method load the xml data and also remove empty whitespace nodes
	public void deleteBlank(XML xml){
		if(xml == null){
			return;
		}else if (!xml.hasChildren()){
			return;
		}else{
			XML[] kids = xml.getChildren();
			for(int i = 0; i < kids.length; i ++){
				if(kids[i].getName().equals("#test")){
					XML curParent = kids[i].getParent();
					curParent.removeChild(kids[i]);
				}
				deleteBlank(kids[i]);
			}
		}
	}
			
	// This method recursively builds a decision tree based on
	// the set of examples that are children of dataset.
	public void recursiveBuildTree(XML dataset, XML tree)
	{
		// NOTE: You MUST add YEA branches to your decision nodes before
		// adding NAY branches.  This will result in YEA branches being
		// child[0], which will be drawn to the left of any NAY branches.
		// The grading tests assume that you are following this convention.
		
		// TODO - implement this method
	
		XML curDataset = dataset;
		String curChoice = "";
		
		if(curDataset.getChildren().length > 1){
			curChoice = chooseSplitAttribute(dataset);
			XML left = dataset;
			XML right = dataset;
			XML curChild = null;
			for(int i = 0; i < curDataset.getChildren().length; i++){
				curChild = dataset.getChild(i);
				if(curChild.getString(curChoice).equals("YEA")){
					right.removeChild(curChild);
				}else{
					left.removeChild(curChild);
				}
			}
			tree.addChild(curChoice);
			tree.addChild(left);
			tree.addChild(right);
			recursiveBuildTree(left,tree.getChild(0));
			recursiveBuildTree(right,tree.getChild(1));
		}else{
			dataset.addChild(plurality(dataset));
		}
			
	}

	// This method calculates and returns the mode (most common value) among
	// the party attributes of the children examples under dataset.  If there
	// happens to be an exact tie, this method returns "TIE".
	public String plurality(XML dataset)
	{
		// TODO - implement this method
		int dem = 0;
		int rep = 0;
		XML curChild = null;
		if(!dataset.hasChildren()){
			return null;
		}else{
			for(int i = 0; i < dataset.getChildren().length; i++){
				curChild = dataset.getChild(i);
				if(curChild.getString("party").equals("DEMOCRAT")){
					dem++;
				}else{
					rep++;
				}
			}
		}
		if(dem > rep){
			return "DEMOCRAT";
		}else if(dem < rep){
			return "REPUBLICAN";
		}else{
			return "TIE";
		}
	}

	// This method calculates and returns the name of the attribute that results
	// in the lowest entropy, after splitting all children examples according
	// to their value for this attribute into two separate groups: YEA vs. NAY.	
	public String chooseSplitAttribute(XML dataset)
	{
		// TODO - implement this method

		String curChoice = "";
		String bestChoice = "";
		double lowE = 1;
		
		if(!dataset.hasChildren()){
			return null;
		}else{
			for(int i = 1; i <= 16; i++){
				if(i<10){
					curChoice = "vote"+"0"+i;
				}else{
					curChoice = "vote"+i;
				}
				if(calculatePostSplitEntropy(curChoice,dataset) < lowE){
					lowE = calculatePostSplitEntropy(curChoice,dataset);
					bestChoice = curChoice;
				}			
			}
		}
		return bestChoice;
	}
		
	// This method calculates and returns the entropy that results after 
	// splitting the children examples of dataset into two groups based
	// on their YEA vs. NAY value for the specified attribute.
	public double calculatePostSplitEntropy(String attribute, XML dataset)
	{		
		// TODO - implement this method
		int yea = 0;
		int nay = 0;
		XML Y = null;
		XML N = null;
		XML curChild = dataset;
		
		if(!curChild.hasChildren()){
			return -2.0;
		}else{
			for(int i = 0; i < curChild.getChildren().length; i++){
				curChild = dataset.getChild(i);
				if(curChild.getString(attribute).equals("YEA")){
					yea ++;
					Y.addChild(curChild);
				}else{
					nay ++;
					N.addChild(curChild);
				}
			}
		}
		return (yea/(yea+nay))*calculateEntropy(Y)+(nay/(yea+nay))*calculateEntropy(N);
	}
	
	// This method calculates and returns the entropy for the children examples
	// of a single dataset node with respect to which party they belong to.
	public double calculateEntropy(XML dataset)
	{
		// TODO - implement this method
		int REP = 0;
		XML curChild = null;
		if(!dataset.hasChildren()){
			return -1.0;
		}else{
			for(int i = 0; i < dataset.getChildren().length; i++){
				curChild = dataset.getChild(i);
				if(curChild.getString("party").equals("REPUBLICAN")){
					REP++;
				}
			}
		}
		return B(REP/dataset.getChildren().length);
	}

	// This method calculates and returns the entropy of a Boolean random 
	// variable that is true with probability q (as on page 704 of the text).
	// Don't forget to use the limit, when q makes this formula unstable.
	public static double B(double q)
	{
		// TODO - implement this method
		if(q == 0.0 || q == 1.0){
			return 0.0;
		}
		double result = -(q * (Math.log(q) / Math.log(2)) + (1 - q)*(Math.log(1-q)/ Math.log(2)));
		return result;
	}

	// This method loads and runs an entire file of examples against the 
	// decision tree, and returns the percentage of those examples that this
	// decision tree correctly predicts.
	public double runTests(String filename)
	{
		// TODO - implement this method
		
		XML database = p.loadXML(filename);
		XML curr = null;
		int correct = 0;
		for(int i = 0; i < database.getChildren().length; i++){
			curr = database.getChild(i);
			String predict = predict(curr, tree);
			if(curr.getString("party").equals(predict)){
				correct++;
			}
		}
		double precentage = (correct)/(double)(database.getChildren().length);
		return precentage;
	}
	
	// This method runs a single example through the decision tree, and then 
	// returns the party that this tree predicts the example to belonging to.
	// If this example contains a party attribute, it should be ignored here.	
	public String predict(XML example, XML decisionTree)
	{
		// TODO - implement this method
		String choice = decisionTree.getContent();
		XML curTree = decisionTree;
		XML curr = example;
		for(int i = 0; i < curr.getChildren().length; i++){
			if(curr.getString(choice).equals("YEA")){
				curTree = curTree.getChild(0);
			}else{
				curTree = curTree.getChild(1);
			}
		}
		return curTree.getContent();
	}
}
