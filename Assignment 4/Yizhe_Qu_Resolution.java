import processing.core.PApplet;
import processing.data.XML;

public class Yizhe_Qu_Resolution extends DrawableTree
{
	public Yizhe_Qu_Resolution(PApplet p, XML tree) 
	{ 
		super(p); 
		this.tree = tree; 
		dirtyTree = true;
	}	
	
	//helper method for eliminateBiconditions()
	public void eliBiconditions(XML currTree){
		//eliminate Biconditions
		// Replace X<=>Y with (X => Y) && (Y => X)
		if(currTree.getName().equals("bicondition")){
			XML currCL = currTree.getChild(0);  //left child
			XML currCR = currTree.getChild(1);  //right child
			currTree.addChild(currTree);
			currTree.addChild(currTree.getChild(2));
			currTree.getChild(3).removeChild(currTree.getChild(3).getChild(0));
			currTree.getChild(3).addChild(currCL);
			currTree.getChild(3).setName("condition");
			currTree.getChild(2).setName("condition");
			currTree.removeChild(currCR);
			currTree.removeChild(currCL);
			currTree.setName("and");			
		}		
	}
	//go through the tree once
	public void eliminateBiconditionsHelper(XML currTree){
		eliBiconditions(currTree);
		if(currTree.hasChildren()){
			for(int i = 0; i < currTree.getChildCount(); i++){				
				eliminateBiconditionsHelper(currTree.getChild(i));
			}
		}
	}
	public void eliminateBiconditions()
	{
		// TODO - Implement the first step in converting logic in tree to CNF:
		// Replace all biconditions with truth preserving conjunctions of conditions.
		dirtyTree = true;
		XML currTree = this.tree;
		for(int i = 0; i < depthOfTree(currTree); i++){
			eliminateBiconditionsHelper(currTree);
		}
	}	
	
	//helper method for eliminateConditions()
	public void eliConditions(XML currTree){
		//eliminate Conditions
		//Replace X => Y with (!X || Y)
		if(currTree.getName().equals("condition")){
			currTree.addChild(currTree);
			currTree.addChild(currTree.getChild(1));
			currTree.getChild(2).removeChild(currTree.getChild(2).getChild(1));
			currTree.getChild(2).setName("not");
			currTree.removeChild(currTree.getChild(1));
			currTree.removeChild(currTree.getChild(0));
			currTree.setName("or");
		}		
	}
	//go through the tree once
	public void eliminateConditionsHelper(XML currTree){
		eliConditions(currTree);
		if(currTree.hasChildren()){
			for(int i = 0; i < currTree.getChildCount(); i++){				
				eliminateConditionsHelper(currTree.getChild(i));
			}
		}
	}
	public void eliminateConditions()
	{
		// TODO - Implement the second step in converting logic in tree to CNF:
		// Replace all conditions with truth preserving disjunctions.		
		dirtyTree = true;
		XML currTree = this.tree;
		for(int i = 0; i < depthOfTree(currTree); i++){
			eliminateConditionsHelper(currTree);
		}		
	}
		
	//helper method for moveNegationInwards()
	public void moveNeInwards(XML currTree){
		//move Negation Inwards
		
		//Replace (!!X) with X
		if(currTree.getName().equals("not") && currTree.getChild(0).getName().equals("not") && currTree.getChild(0).getChildCount() == 1){
			XML currC = currTree.getChild(0).getChild(0);
			currTree.getParent().addChild(currC);
			currTree.getParent().removeChild(currTree);			
		}
		//Replace !(X && Y) with !X || !Y
		if(currTree.getName().equals("not") && currTree.getChild(0).getName().equals("and") && currTree.getChild(0).getChildCount() == 2){			
			currTree.getChild(0).setName("not");
			XML currC = currTree.getChild(0);
			currTree.addChild(currC);
			currTree.getChild(0).removeChild(currTree.getChild(0).getChild(1));
			currTree.getChild(1).removeChild(currTree.getChild(1).getChild(0));
			currTree.setName("or");
		}
		//Replace !(X || Y) with !X && !Y
		if(currTree.getName().equals("not") && currTree.getChild(0).getName().equals("or") && currTree.getChild(0).getChildCount() == 2){			
			currTree.getChild(0).setName("not");
			currTree.addChild(currTree.getChild(0));
			currTree.getChild(1).removeChild(currTree.getChild(1).getChild(0));
			currTree.getChild(0).removeChild(currTree.getChild(0).getChild(1));
			currTree.setName("and");
		}
	}	
	//go through the tree once
	public void moveNegationInwardsHelper(XML currTree){
		moveNeInwards(currTree);
		if(currTree.hasChildren()){
			for(int i = 0; i < currTree.getChildCount(); i++){
				moveNegationInwardsHelper(currTree.getChild(i));
			}
		}
	}		
	public void moveNegationInwards()
	{
		// TODO - Implement the third step in converting logic in tree to CNF:
		// Move negations in a truth preserving way to apply only to literals.
		dirtyTree = true;
		XML currTree = this.tree;
		for(int i = 0; i < depthOfTree(currTree); i++){
			moveNegationInwardsHelper(currTree);
		}	
	}
		
	//helper method for distributeOrsOverAnds()
	public void disOrsOverAnds(XML currTree){	
		//Replace X || (Y && Z) with (X || Y) && (X || Z)
		if(currTree.getName().equals("or") && currTree.getChild(1).getName().equals("and")){
			for(int i = 0; i < currTree.getChild(1).getChildCount(); i++){
				currTree.addChild(currTree.getChild(1));
				currTree.getChild(2+i).addChild(currTree.getChild(0));
				currTree.getChild(2+i).addChild(currTree.getChild(1).getChild(i));
				for(int j = 0; j < currTree.getChild(1).getChildCount(); j++){
					currTree.getChild(2+i).removeChild(currTree.getChild(2+i).getChild(0));
				}
				currTree.getChild(2+i).setName("or");
			}
			currTree.removeChild(currTree.getChild(1));
			currTree.removeChild(currTree.getChild(0));
			currTree.setName("and");
		}
		//Replace (Y && Z) || X with (X || Y) && (X || Z)
		if(currTree.getName().equals("or") && currTree.getChild(0).getName().equals("and") ){
			for(int i = 0; i < currTree.getChild(0).getChildCount(); i++){
				currTree.addChild(currTree.getChild(0));
				currTree.getChild(2+i).addChild(currTree.getChild(1));
				currTree.getChild(2+i).addChild(currTree.getChild(0).getChild(i));
				for(int j = 0; j < currTree.getChild(0).getChildCount(); j++){
					currTree.getChild(2+i).removeChild(currTree.getChild(2+i).getChild(0));
				}
				currTree.getChild(2+i).setName("or");
			}
			currTree.removeChild(currTree.getChild(1));
			currTree.removeChild(currTree.getChild(0));
			currTree.setName("and");
		}
	}	
	//go through the tree once
	public void distributeOrsOverAndsHelper(XML currTree){
		disOrsOverAnds(currTree);
		if(currTree.hasChildren()){
			for(int i = 0; i < currTree.getChildCount(); i++){				
				distributeOrsOverAndsHelper(currTree.getChild(i));
			}
		}
	}		
	public void distributeOrsOverAnds()
	{
		// TODO - Implement the fourth step in converting logic in tree to CNF:
		// Move negations in a truth preserving way to apply only to literals.
		dirtyTree = true;
		XML currTree = this.tree;
		for(int i = 0; i < depthOfTree(currTree); i++){
			distributeOrsOverAndsHelper(currTree);
		}		
	}
	
	//helper method for collapse()
	public void collapseTree(XML currTree){
		//collapsing all of the binary and-nodes into a single and node
		if(currTree.getName().equals("and")){
			for(int i = 0; i < currTree.getChildCount(); i++){
				if(currTree.getChild(i).getName().equals("and")){
					for(int j = 0; j < currTree.getChild(i).getChildCount(); j++){
						currTree.addChild(currTree.getChild(i).getChild(j));
					}
					//add both child from and-node to its parent and-node 
					currTree.removeChild(currTree.getChild(i));
				}
			}
		}
		//collapsing all of the binary or-nodes into a single or node
		if(currTree.getName().equals("or")){
			for(int i = 0; i < currTree.getChildCount(); i++){
				if(currTree.getChild(i).getName().equals("or")){
					for(int j = 0; j < currTree.getChild(i).getChildCount(); j++){
						currTree.addChild(currTree.getChild(i).getChild(j));
					}
					//add both child from and-node to its parent and-node 
					currTree.removeChild(currTree.getChild(i));
				}
			}					
		}
		//children of the and-node that are lone literals (not or-ed with any others)
		//insert an or-node between the andnode and each of these lone literals.
		if(currTree.getName().equals("and")){
			for(int i = 0; i < currTree.getChildCount(); i++){
				if(!currTree.getChild(i).hasChildren()){
					currTree.getChild(i).addChild(currTree.getChild(i));
					currTree.getChild(i).setName("or");
				}
			}
		}		
		//remove redundancy - literals 		
		if(isClause(currTree)){
			int index = 0;
			while(index < currTree.getChildCount() - 1){
				int check = 0;
				if(isLiteralNegated(currTree.getChild(index))){
					if(clauseContainsLiteral(currTree, getAtomFromLiteral(currTree.getChild(index).getChild(0)) , true)){
						currTree.removeChild(currTree.getChild(index));
						check ++;
					}
				}else{
					if(clauseContainsLiteral(currTree, getAtomFromLiteral(currTree.getChild(index)) , false)){
						currTree.removeChild(currTree.getChild(index));
						check ++;
					}
				}
				if(check == 0){
					index++;
				}			
			}
			//remove tautologies
			if(clauseIsTautology(currTree)){
				currTree.getParent().removeChild(currTree);
			}
		}
		//remove redundancy - clauses
		if(isSet(currTree)){			
			int index = 0;
			while(index < currTree.getChildCount() - 1){
				int check = 0;
				if(setContainsClause(currTree, currTree.getChild(index))){
					currTree.removeChild(currTree.getChild(index));
					check ++;
				}
				if(check == 0){
					index++;
				}
			}
		}
		//to complete the tree (logic-and-or-xxx)
		if(currTree.getName().equals("or") && currTree.getParent().getName().equals("logic")){
			//logic-or => add and
			currTree.addChild(currTree);
			for(int i = currTree.getChildCount()-2; i >=0 ; i--){
				currTree.removeChild(currTree.getChild(i));
			}
			currTree.setName("and");
		}
		if(!currTree.getName().equals("and") && !currTree.getName().equals("or") && !currTree.getName().equals("not")){
			//logic-XXX => add and-or
			if(currTree.getParent() != null && currTree.getParent().getName().equals("logic")){
				currTree.addChild(currTree);
				currTree.getChild(0).addChild(currTree.getChild(0));
				currTree.getChild(0).setName("or");
				currTree.setName("and");
			}
		}
		if(currTree.getName().equals("not")){
			if(currTree.getParent() != null && currTree.getParent().getName().equals("logic")){
				//logic-not => add and-or
				currTree.addChild(currTree);
				currTree.getChild(1).getChild(0).addChild(currTree.getChild(0));
				currTree.getChild(1).getChild(0).setName("not");
				currTree.getChild(1).setName("or");
				currTree.removeChild(currTree.getChild(0));
				currTree.setName("and");				
			}
			if(currTree.getParent() != null && currTree.getParent().getName().equals("and")){
				//logic-and-not => add or
				currTree.addChild(currTree);
				currTree.removeChild(currTree.getChild(0));
				currTree.setName("or");
			}			
		}
	}	
	//go through the tree
	public void collapseHelper(XML currTree){
		collapseTree(currTree);
		if(currTree.hasChildren()){
			for(int i = 0; i < currTree.getChildCount(); i++){
				collapseHelper(currTree.getChild(i));
			}
		}
	}	
	public void collapse()
	{
		// TODO - Clean up logic in tree in preparation for Resolution:
		// 1) Convert nested binary ands and ors into n-ary operators so
		// there is a single and-node child of the root logic-node, all of
		// the children of this and-node are or-nodes, and all of the
		// children of these or-nodes are literals: either atomic or negated	
		// 2) Remove redundant literals from every clause, and then remove
		// redundant clauses from the tree.
		// 3) Also remove any clauses that are always true (tautologies)
		// from your tree to help speed up resolution.
		dirtyTree = true;
		XML currTree = this.tree;
		for(int i = 0; i < depthOfTree(currTree); i++){
			collapseHelper(currTree);
		}
	}
	
	public boolean applyResolution()
	{
		// TODO - Implement resolution on the logic in tree.  New resolvents
		// should be added as children to the only and-node in tree.  This
		// method should return true when a conflict is found, otherwise it
		// should only return false after exploring all possible resolvents.
		// Note: you are welcome to leave out resolvents that are always
		// true (tautologies) to help speed up your search.
		dirtyTree = true;
		if(!this.tree.hasChildren()){
			return false;
		}
		XML andNode = this.tree.getChild(0);
		int index = 0;
		while(index < andNode.getChildCount() - 1){
			int check = 0;
			for(int i = index + 1; i < andNode.getChildCount(); i++){
				XML newNode = resolve(andNode.getChild(index), andNode.getChild(i));
				if(newNode != null){
					if(newNode.hasChildren()){
						collapseTree(newNode); //make sure there is no repeat
						andNode.addChild(newNode);
						andNode.removeChild(andNode.getChild(i));
						andNode.removeChild(andNode.getChild(index));
						check ++;
						break;
					}else{
						return true;
					}					
				}							
			}
			if(check == 0){
				index++;
			}
		}
		return false;
	}

	public XML resolve(XML clause1, XML clause2)
	{
		// TODO - Attempt to resolve these two clauses and return the resulting
		// resolvent.  You should remove any redundant literals from this 
		// resulting resolvent.  If there is a conflict, you will simply be
		// returning an XML node with zero children.  If the two clauses cannot
		// be resolved, then return null instead.
		int index = 0;
		int changed = 0;		
		if(clause1.getChildCount() == clause2.getChildCount()){
			//conflict
			int count = 0;
			for(int i = 0; i < clause1.getChildCount(); i++){
				if(isLiteralNegated(clause1.getChild(i))){
					for(int j = 0; j < clause2.getChildCount(); j++){
						if(clause1.getChild(i).getChild(0).getName().equals(clause2.getChild(j).getName())){
							count++;
						}
					}				
				}else{
					for(int j = 0; j < clause2.getChildCount(); j++){
						if(isLiteralNegated(clause2.getChild(j)) && clause1.getChild(i).getName().equals(clause2.getChild(j).getChild(0).getName())){
							count++;
						}
					}	
				}
			}
			if(count == clause1.getChildCount()){
				if(isLiteralNegated(clause1.getChild(0))){
					return clause1.getChild(0).getChild(0);
				}
				return clause1.getChild(0);
			}
		}
		while(index < clause1.getChildCount()){
			int check = 0;
			for(int i = 0; i < clause2.getChildCount(); i++){
				if(clause1.getChild(index).getName().equals("not")){
					if(clause1.getChild(index).getChild(0).getName().equals(clause2.getChild(i).getName())){
						check++;
						clause1.removeChild(clause1.getChild(index));
						clause2.removeChild(clause2.getChild(i));
						changed++;
						break;
					}
				}else{
					if(clause2.getChild(i).getName().equals("not")){
						if(clause1.getChild(index).getName().equals(clause2.getChild(i).getChild(0).getName())){
							check++;
							clause1.removeChild(clause1.getChild(index));
							clause2.removeChild(clause2.getChild(i));
							changed++;
							break;
						}
					}				
				}
			}
			if(check == 0){
				index++;
			}
		}
		if(changed == 0){
			return null;
		}else{
			//build new XML
			for(int k = 0; k < clause2.getChildCount(); k++){
				clause1.addChild(clause2.getChild(k));
			}
			return clause1;
		}				
	}	
	
	// REQUIRED HELPERS: may be helpful to implement these before collapse(), applyResolution(), and resolve()
	// Some terminology reminders regarding the following methods:
	// atom: a single named proposition with no children independent of whether it is negated
	// literal: either an atom-node containing a name, or a not-node with that atom as a child
	// clause: an or-node, all the children of which are literals
	// set: an and-node, all the children of which are clauses (disjunctions)
		
	public boolean isLiteralNegated(XML literal) 
	{ 
		// TODO - Implement to return true when this literal is negated and false otherwise.
		if(literal.getName().equals("not")){
			return true;
		}else{
			return false; 
		}		
	}

	public String getAtomFromLiteral(XML literal) 
	{ 
		// TODO - Implement to return the name of the atom in this literal as a string.
		return literal.getName();
	}	
	
	public boolean clauseContainsLiteral(XML clause, String atom, boolean isNegated)
	{
		// TODO - Implement to return true when the provided clause contains a literal
		// with the atomic name and negation (isNegated).  Otherwise, return false.	
		int count = 0;
		for(int i = 0; i < clause.getChildCount(); i++){
			if(!isNegated && clause.getChild(i).getName().equals(atom)){
				count++;
			}
			if(isLiteralNegated(clause.getChild(i)) && isNegated){
				if(clause.getChild(i).getChild(0).getName().equals(atom)){
					count++;
				}
			}
		}
		if(count > 1){
			return true;
		}
		return false;
	}
	
	public boolean setContainsClause(XML set, XML clause)
	{
		// TODO - Implement to return true when the set contains a clause with the
		// same set of literals as the clause parameter.  Otherwise, return false.
		if(isClause(clause) && isSet(set)){
			int count = 0;
			for(int i = 0; i < set.getChildCount(); i++){	
				int isInside = 0;
				if(set.getChild(i).getChildCount() == clause.getChildCount()){
					for(int j = 0; j < set.getChild(i).getChildCount(); j++){
						for(int k = 0; k < clause.getChildCount(); k++){		
							if(isLiteralNegated(set.getChild(i).getChild(j))){
								if(isLiteralNegated(clause.getChild(k)) && set.getChild(i).getChild(j).getChild(0).getName().equals(clause.getChild(k).getChild(0).getName())){
									isInside ++;
								}
							}else{
								if(set.getChild(i).getChild(j).getName().equals(clause.getChild(k).getName())){
									isInside ++;
								}
							}					
						}
					}
					if(isInside == clause.getChildCount()){
						count ++;
					}
				}
			}
			if(count > 1){
				return true;
			}
		}		
		return false;
	}
	
	public boolean clauseIsTautology(XML clause)
	{
		// TODO - Implement to return true when this clause contains a literal
		// along with the negated form of that same literal.  Otherwise, return false.
		if(isClause(clause)){
			for(int i = 0; i < clause.getChildCount(); i++){
				XML currN = clause.getChild(i);
				for(int j = 0; j < clause.getChildCount(); j++){
					if(isLiteralNegated(currN)){
						if(currN.getChild(0).getName().equals(clause.getChild(j).getName())){
							return true;
						}
					}else{
						if(clause.getChild(j).hasChildren()){
							if(currN.getName().equals(clause.getChild(j).getChild(0).getName())){
								return true;
							}
						}
					}
				}
			}
			
		}		
		return false;		
	}	
	
	//check if isClause
	public boolean isClause(XML clause){
		if(clause.getName().equals("or")){
			for(int i = 0; i < clause.getChildCount(); i++){
				if(clause.getChild(i).getName().equals("not")){
					if(clause.getChild(i).getChild(0).hasChildren()){
						return false;					
					}
				}else{
					if(clause.getChild(i).hasChildren()){
						return false;					
					}
				}
			}
			return true;
		}
		return false;
	}	
	//check if isSet
	public boolean isSet(XML set){
		if(!set.getName().equals("and")){
			return false;
		}else{
			for(int i = 0; i < set.getChildCount(); i++){
				if(!isClause(set.getChild(i))){
					return false;
				}
			}
			return true;
		}
	}
	//get depth of tree
	public int depthOfTree(XML tree){
		if(tree.getChildCount() == 0){
			return 0;
		}else if(tree.getChildCount() == 1){
			return depthOfTree(tree.getChild(0)) + 1;
		}else{
			if(depthOfTree(tree.getChild(0)) > depthOfTree(tree.getChild(1))){
				return depthOfTree(tree.getChild(0))+1;
			}else{
				return depthOfTree(tree.getChild(1))+1;
			}
		}
	}
}
