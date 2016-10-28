import java.util.ArrayList;
import java.util.Collections;

public class Yizhe_Qu_AStar 
{		
	public ArrayList<SearchPoint> frontier;
	public ArrayList<SearchPoint> explored;

	public Map.Point endPoint;
	public Map.Point startPoint;
	public int choiceH;
	// TODO - add any extra member fields that you would like here 
	
	public class SearchPoint implements Comparable<SearchPoint>
	{
		public Map.Point mapPoint;
		public ArrayList<Map.Point> neighbors;
		public ArrayList<Map.Point> path; //for keep track of the path lead to this point
		
		public SearchPoint(Map.Point p){
			this.neighbors = p.neighbors;
			this.mapPoint = p;
			this.path = new ArrayList<Map.Point>();
		}
		
		// TODO - add any extra member fields or methods that you would like here
		
		// TODO - implement this method to return the minimum cost
		// necessary to travel from the start point to here
		public float g() 
		{
			//in book: g(n) is the actual cost to reach n along the current path !
			float totalC = 0;
			if(path.size() > 0){
				for(int i = 1; i < path.size(); i++){
					SearchPoint currPoint = new SearchPoint(path.get(i));
					SearchPoint prevPoint = new SearchPoint(path.get(i-1));
					double distance = Math.pow((Math.pow((currPoint.mapPoint.x - prevPoint.mapPoint.x), 2) 
							+ Math.pow((currPoint.mapPoint.y - prevPoint.mapPoint.y), 2)), 0.5);
					totalC = (float) (totalC + distance);
				}
			}				
			return totalC;
			
		}	
		
		// TODO - implement this method to return the heuristic estimate
		// of the remaining cost, based on the H parameter passed from main:
		// 0: always estimate zero, 1: manhattan distance, 2: euclidean l2 distance
		public float h()
		{
			//zero heuristic which evaluates to zero for every state
			if(choiceH == 0){
				return 0;
			//manhattan distance heuristic (the sum of the x and y offesets between the point in question and the goal point)
			}else if(choiceH == 1){
				return (Math.abs(endPoint.x - mapPoint.x) + Math.abs(endPoint.y - mapPoint.y));
			//the standard Euclidean L2 (straight-line distance between the point in question and the goal point)
			}else if(choiceH == 2){
				double distance = Math.pow((Math.pow((endPoint.x - mapPoint.x), 2) + Math.pow((endPoint.y - mapPoint.y), 2)), 0.5);
				return (float) distance;
			}else{
				//should not happen
				return -1;
			}			
		}
		
		// TODO - implement this method to return to final priority for this
		// point, which include the cost spent to reach it and the heuristic 
		// estimate of the remaining cost
		public float f()
		{
			return (h() + g());
		}
		
		// TODO - override this compareTo method to help sort the points in 
		// your frontier from highest priority = lowest f(), and break ties
		// using whichever point has the lowest g()
		@Override
		public int compareTo(SearchPoint other)
		{
			if(f() < other.f()){
				return -1;
			}else if(f() > other.f()){
				return 1;
			}else{
				//break ties using g()
				if(g() < other.g()){
					return -1;
				}else{
					return 1;
				}
			}			
		}
		
		// TODO - override this equals to help you check whether your ArrayLists
		// already contain a SearchPoint referencing a given Map.Point
		@Override
		public boolean equals(Object other)
		{
			//comparing SearchPoint type
			SearchPoint curP = (SearchPoint)other;
			if(mapPoint == curP.mapPoint){
				return true;
			}else{
				return false;
			}
		}		
	}
	
	// TODO - implement this constructor to initialize your member variables
	// and search, by adding the start point to your frontier.  The parameter
	// H indicates which heuristic you should use while searching:
	// 0: always estimate zero, 1: manhattan distance, 2: euclidean l2 distance
	public Yizhe_Qu_AStar(Map map, int H)
	{
		//initialize two arraylist
		frontier = new ArrayList<SearchPoint>();
		explored = new ArrayList<SearchPoint>();
		//initialize the start/end point
		SearchPoint cPoint = new SearchPoint(map.start);
		endPoint = map.end;
		startPoint = map.start;
		frontier.add(cPoint);	
		choiceH = H;
	}
	
	// TODO - implement this method to explore the single highest priority
	// and lowest f() SearchPoint from your frontier.  This method will be 
	// called multiple times from Main to help you visualize the search.
	// This method should not do anything, if your search is complete.
	public void exploreNextNode() 
	{
		if(!isComplete()){
			//start point(special case)
			if(explored.isEmpty()){
				SearchPoint cPt = frontier.get(0);
				cPt.path.add(cPt.mapPoint);
				frontier.remove(0);
				explored.add(cPt);
				//frontier is empty at this point, so add first neighbor into it
				SearchPoint h = new SearchPoint(cPt.neighbors.get(0));
				h.path.addAll(cPt.path);
				h.path.add(h.mapPoint);
				frontier.add(h);
				//continue on other neighbors
				for(int i = 1; i < cPt.neighbors.size(); i++){
					SearchPoint cP = new SearchPoint(cPt.neighbors.get(i));
					//keep track of the path
					cP.path.addAll(cPt.path);
					cP.path.add(cP.mapPoint);
					//put points in order
					if(cP.compareTo(frontier.get(frontier.size()-1)) == 1){
						frontier.add(cP);
					}else{
						for(int j = 0; j < frontier.size(); j++){
							if(cP.compareTo(frontier.get(j)) == -1){
								frontier.add(j, cP);
								break;
							}
						}
					}
				}
			//normal steps
			}else{
				SearchPoint cPt = frontier.get(0);
				frontier.remove(0);
				explored.add(cPt);
				//adding neighbors into the frontier
				for(int i = 0; i < cPt.neighbors.size(); i++){
					SearchPoint cP = new SearchPoint(cPt.neighbors.get(i));
					//keep track of the path
					cP.path.addAll(cPt.path);
					cP.path.add(cP.mapPoint);
					//check if already explored(frontier&explored)
					int check = 0;
					for(int n = 0; n < explored.size(); n++){
						if(explored.get(n).equals(cP)){
							check++;
						}
					}
					for(int n = 0; n < frontier.size(); n++){
						if(frontier.get(n).equals(cP)){
							check++;
						}
					}
					//if need to add to frontier
					if(check == 0){					
						//if frontier is empty
						if(frontier.isEmpty()){
							frontier.add(cP);
						//if need to put at last position
						}else if(cP.compareTo(frontier.get(frontier.size()-1)) == 1){
							frontier.add(cP);
						}else{
							//put points in order
							for(int j = 0; j < frontier.size(); j++){
								if(cP.compareTo(frontier.get(j)) == -1){
									frontier.add(j, cP);
									break;
								}
							}
						}
					//skip the current point(already exist)
					}else{
						continue;
					}
				}
		
			}			
		}
	}

	// TODO - implement this method to return an ArrayList of Map.Points
	// that represents the SearchPoints in your frontier.
	public ArrayList<Map.Point> getFrontier()
	{
		ArrayList<Map.Point> frontierP = new ArrayList<Map.Point>();
		int fSize = frontier.size();
		for(int i = 0; i < fSize; i++){
			frontierP.add(frontier.get(i).mapPoint);
		}
		return frontierP;
	}
	
	// TODO - implement this method to return an ArrayList of Map.Points
	// that represents the SearchPoints that you have explored.
	public ArrayList<Map.Point> getExplored()
	{
		ArrayList<Map.Point> exploredP = new ArrayList<Map.Point>();
		int fSize = explored.size();
		for(int i = 0; i < fSize; i++){
			exploredP.add(explored.get(i).mapPoint);
		}
		return exploredP;
	}

	// TODO - implement this method to return true only after a solution
	// has been found, or you have determined that no solution is possible.
	public boolean isComplete()
	{
		//if no solution
		if(frontier.isEmpty()){
			return true;
		//if we reach the goal
		}else if(frontier.get(0).mapPoint == endPoint){
			return true;
		}else{
			return false;
		}		
	}

	// TODO - implement this method to return an ArrayList of the Map.Points
	// that are along the path that you have found from the start to end.  
	// These points must be in the ArrayList in the order that they are 
	// traversed while moving along the path that you have found.
	public ArrayList<Map.Point> getSolution()
	{
		//if no solution
		if(frontier.isEmpty()){
			return new ArrayList<Map.Point>();
		//else return the solution
		}else{
			return frontier.get(0).path;
		}		
	}	
}
