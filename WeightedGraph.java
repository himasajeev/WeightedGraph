/* This program was written
 * by Jessica Devlin
 * on 4/29/19
 * and last updated on 5/1/19 
 * This is a program written to take input line by line of the relationships between family members, until end of input
 * The main program then accepts pairs of members as queries and checks the distance between them
 * The program uses Dijikstra's Shortest Path algorithm implemented with a MinPQ to return the shortest path between two vertices
 * This program uses a weightedGraph structure in which the graph stores all vertices and vertices store all relationships to that vertex
 * Finally, the program contains a method specific to the family problem that converts a string storing the relationship into an edge weight
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class WeightedGraph {

	ArrayList<Vertex> vertices;			// list of all vertices	
    HashMap<String,Vertex> vertexMap;	// map of vertex 
    int count;

    private class Edge {	// edge class stores neighbors and the weight of their relationship
        Vertex destination;		// neighbor
        int weight;		// aka distance

        public Edge(Vertex destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }
    
    public class Vertex implements Comparable<Vertex> {		// vertex class to store member
    	String name;
    	int priority;	// shortest distance used in SP
    	Vertex parent = null;		// used in SP
    	ArrayList<Edge> neighbors;	// all relationships of a vertex
    	ArrayList<Vertex> marked = new ArrayList<Vertex>();		// used for bookkeeping in SP
    	ArrayList<Vertex> inFringe = new ArrayList<Vertex>();

    	public Vertex(String name) {
	    	vertices.add(this);		// add vertex to list of current vertices
	    	this.name = name;
	    	priority = Integer.MAX_VALUE; // MAX_VALUE assumed to be infinity
	    	neighbors = new ArrayList<Edge>();	// create list of neighbors
		}
    	
    	public void reset() {
    		priority = Integer.MAX_VALUE; // MAX_VALUE assumed to be infinity
    		parent = null;
    		clearMarked();		// empty both lists used in SP
    		clearInFringe();
    	}

		public String toString() { return name; }
		
		public void setPriority(int x) {	// used in SP
			this.priority = x;
		}
		
		public int getPriority() {	// retrieves priority
			return this.priority;
		}
		
		public void addNeighbor(Edge e) {	// add new edge to relationships of vertex
		    neighbors.add(e);
		}
		
		public ArrayList<Edge> getNeighbors() {	// get all relationship edges
			return this.neighbors;
		}
		
		public void mark() {	// mark as visited
			marked.add(this);
		}
		
		public boolean isMarked() {	     // check if vertex has been visited
			if (marked.contains(this)) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public void markInFringe() {	// mark as in the fringe for SP use
			inFringe.add(this);
		}
		
		public void markNotInFringe() {		// remove from fringe if it exists there
			if(inFringe.contains(this)) {
				inFringe.remove(this);
			}
		}
		
		public void clearMarked() {
			marked.clear();
		}
		
		public void clearInFringe() {
			inFringe.clear();
		}
		
		public boolean isInFringe() {		// check if in fringe
			if(inFringe.contains(this)) {
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		public int compareTo(Vertex v) {	// makes vertex comparable
			return Integer.compare(this.priority, v.priority);
		}
		
		public void clearAll() {
			for (Vertex v : vertices) {
				v.reset();
			}
		}
		
		// findSP and visit methods adapted from code provided on COSC 300 by Dr.Simon
		
		public void findSP(Vertex s) {
	    	IndexedMinPQ<Vertex> pq = new IndexedMinPQ<Vertex>();	// create minPQ of vertices
	    	clearAll();
	    	s.setPriority(0);	// set dest priority to 0
	    	//System.out.println("Setting distance to 0");
	    	visit(s, 0, pq);	// visit dest
	    		while(!pq.isEmpty()) {
	    			Vertex v = pq.delMin(); 
	    			int d = v.getPriority();
	    			count++;
	    			v.markNotInFringe();
	    			//System.out.println(v.name + " is not in fringe.");
	    			visit(v, d, pq);
	    		}
	     }

	     private void visit(Vertex v, int d, IndexedMinPQ<Vertex> pq) {
	    	v.mark();	// mark dest as visited
	    	//System.out.println(v.name + " is marked");
	    		for(Edge e : v.getNeighbors()) { // Edge is pair (v, w). For each edge of the dest
	    			Vertex x = e.destination;	// set x equal to the neighbor of the dest that was marked
	    			//System.out.println("Checking " + e.destination);
	    			if(x.isMarked()) { // if x is also marked
	    				//System.out.println(e.destination + " is marked");
	    				continue;		// check the next neighbor
	    			}
	    			if(!x.isInFringe()) {	// if neighbor is not yet in fringe
	    				//System.out.println(e.destination + " is not in fringe.");
	    				x.setPriority(d + e.weight);
	    				pq.insert(x);
	    				x.parent = v;
	    				x.markInFringe();
	    			}
	    			else if(x.getPriority() > d + e.weight) {
	    				//System.out.println(e.destination + " is in fringe.");
	    				x.setPriority(d + e.weight);
	    				pq.change(x);
	    				x.parent = v;
	    			}
	    		}
	      }
		
    }
    
    public WeightedGraph() {	// graph constructor
    	
    	vertices = new ArrayList<Vertex>();
    	vertexMap = new HashMap<String, Vertex>();
    }
    
    // print method written for testing that things are stored properly
    public void printGraph() {
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.get(i).neighbors.size(); j++) {
				System.out.println(vertices.get(i).name + " is related to " + vertices.get(i).neighbors.get(j).destination + " with a distance of " + vertices.get(i).neighbors.get(j).weight);
			}
		}
	}
    
    public int getWeight(String relative) {		// returns a weight value based on the string declaring relationship between 2 vertices 
    	int n;
    	int m;
    	if (relative.contains(" ")) {	// if relationship contains space
    		if (relative.indexOf(" ") == relative.lastIndexOf(" ")) {	// if relationship only contains 1 space
    			n = Integer.parseInt(relative.substring(0, relative.indexOf(" ")));
    			return (n * 2) + 1;	// distance of child from parent n * 2 branches (each cousin) + 1 (distance of parent to aunt/uncle)
    		}
    		else {	// if relationship contains more than 1 space, ex. "1 cousin 2 removed"
    			n = Integer.parseInt(relative.substring(0, relative.indexOf(" ")));		// n = # cousin
    			m = Integer.parseInt(relative.substring(relative.lastIndexOf(" ") - 1, relative.lastIndexOf(" ")));	// m = # removed
    			return (n * 2) + 1 + m; // same as not removed, but m denotes distance from parent to aunt/uncle
    		}
    	}
    	
    	else {
    		switch(relative) {		// if not in cousin case, simply switch for the string given
    	
    			case "self": return 0;
    			case "parent": return 1;
    			case "sibling": return 1;
    			case "grandparent": return 2;
    	
    			default: return -1;
    		}
    	}
    }
    
    // findVertex used from Bridges.java code provided on the website for COSC 300 by Dr. Simon
    Vertex findVertex(String name) {	
    	Vertex v = vertexMap.get(name);
    		if(v == null)	// if v does not exist in map,
    			vertexMap.put(name, v = new Vertex(name));	// store new vertex
    		return v;
    }
    
    public void addEdge(String sourceName, String destName, String relation) {	// method to add edge from main program
        Vertex v = findVertex(sourceName);
        Vertex w = findVertex(destName);	// check if vertex exists, return or create
        int value = getWeight(relation);	// get value of relation to store in edge
        Edge y = new Edge(w, value);
        Edge z = new Edge(v, value);
        v.addNeighbor(y);	// add each to relationships of eachother
        w.addNeighbor(z);
    }
    
      public static void main(String[] args) {

            WeightedGraph graph = new WeightedGraph();	// create graph
            ArrayList<String> queries = new ArrayList<String>();	// used to store query input to check after input has ended
            
            Scanner sc = new Scanner(System.in);
    		String in;
    		try {
    			System.out.println("Please enter your relationships, one per line:");
    			while (sc.hasNextLine()) {	// until end of input,
    				in = sc.nextLine();
    				if (in.equals("")) {	// check for the blank line
    					break;		// if blank line is found, move onto the next version of storing
    				}
    				else {
    					String name = in.substring(0, in.indexOf(","));
    					String relation = in.substring(in.indexOf(",")+ 1, in.lastIndexOf(","));	// split relationship input using substring
    					String name2 = in.substring(in.lastIndexOf(",") + 1, in.length());
    					graph.addEdge(name, name2, relation);	// add dissected information to graph
    				}
    			}
    			
    			//graph.printGraph();
    			
    			System.out.println("Now please enter your queries, one per line:");
    			while (sc.hasNextLine()) {	// until end of input,
    				in = sc.nextLine();
    				queries.add(in);	// store queries to be evaluated after input
    			}
    		}
    		finally {
    			sc.close();
    		}
    		
    		System.out.println("The distances of your queries are as follows: ");
    		String query;
    		for (int i = 0; i < queries.size(); i++) {	// for each query that was entered
    			query = queries.get(i);
    			String q1 = query.substring(0, query.indexOf(","));
				String q2 = query.substring(query.indexOf(",")+ 1, query.length());	// split first and second names in query
				Vertex first = graph.findVertex(q1);
				Vertex second = graph.findVertex(q2);	// retrieve the vertex relating to the string
				first.findSP(second);	// perform algorithm on the vertices
    			System.out.println(query + ": " + first.priority);	// output
    		}
    	
        }
    }