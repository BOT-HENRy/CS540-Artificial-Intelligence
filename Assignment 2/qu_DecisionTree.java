import processing.core.*;
import processing.data.*;
import java.math.*;

public class qu_DecisionTree extends DrawableTree
{
	public qu_DecisionTree(PApplet p) { super(p); }
		
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
		tree = p.loadXML(filename);
		
	}
	
	//This method load the xml data and also remove empty whitespace nodes
	public void deleteBlank(XML xml){
		if(xml == null){
			return;
		}else if (!){
			
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
	}

	// This method calculates and returns the mode (most common value) among
	// the party attributes of the children examples under dataset.  If there
	// happens to be an exact tie, this method returns "TIE".
	public String plurality(XML dataset)
	{
		// TODO - implement this method
		return "";
	}

	// This method calculates and returns the name of the attribute that results
	// in the lowest entropy, after splitting all children examples according
	// to their value for this attribute into two separate groups: YEA vs. NAY.	
	public String chooseSplitAttribute(XML dataset)
	{
		// TODO - implement this method
		return "";
	}
		
	// This method calculates and returns the entropy that results after 
	// splitting the children examples of dataset into two groups based
	// on their YEA vs. NAY value for the specified attribute.
	public double calculatePostSplitEntropy(String attribute, XML dataset)
	{		
		// TODO - implement this method
		return 0.0;
	}
	
	// This method calculates and returns the entropy for the children examples
	// of a single dataset node with respect to which party they belong to.
	public double calculateEntropy(XML dataset)
	{
		// TODO - implement this method
		return 0.0;
	}

	// This method calculates and returns the entropy of a Boolean random 
	// variable that is true with probability q (as on page 704 of the text).
	// Don't forget to use the limit, when q makes this formula unstable.
	public static double B(double q)
	{
		// TODO - implement this method
		return 0.0;
	}

	// This method loads and runs an entire file of examples against the 
	// decision tree, and returns the percentage of those examples that this
	// decision tree correctly predicts.
	public double runTests(String filename)
	{
		// TODO - implement this method
		return 0.0;
	}
	
	// This method runs a single example through the decision tree, and then 
	// returns the party that this tree predicts the example to belonging to.
	// If this example contains a party attribute, it should be ignored here.	
	public String predict(XML example, XML decisionTree)
	{
		// TODO - implement this method
		return "";
	}
}
