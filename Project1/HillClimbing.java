import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HillClimbing {
	
		static boolean verbose = false;
	
		public static void main(String[] args){
		
		File file = new File("./input.txt");
		Scanner in = null;
		
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			System.err.println("The file " + file.getAbsolutePath()+ " cannot be read\n");
			System.exit(1);
		}
		
		String input = "";
		input = in.nextLine();
			
		String flag = "";
		int target = 0;
		int restartsRandom = 0;
		
		//parse inputs
		String[] first = splitLine(input);
		target = Integer.parseInt(first[0]);
		flag = first[1];
		restartsRandom = Integer.parseInt(first[2]);
		
		//verbose output
		
		if (flag.toUpperCase().equals("V")) verbose = true;
		
			
		System.out.println("Target: " + target + " Verbose Output: " + verbose + "\nRandom Restarts: "+ restartsRandom);
		System.out.println();
				
		boolean getValues = true;
		boolean getEdges = false;
		

		Graph<String> g = new Graph<String>();
		List<String> vertices = new ArrayList<String>(); 
		
		Map<String, Integer> values = new HashMap<String, Integer>();
		
		while(in.hasNextLine() && getValues) {
			
			input = in.nextLine();
			
			if (input.isEmpty()) {
				getValues = false;
				getEdges = true;
				break;
			}
			
			String[] v = splitLine(input);
			String label = v[0];
			int val = Integer.parseInt(v[1]);
			
			g.addVertex(label);
			vertices.add(label);
			values.put(label, val);

		}		
			
		while(in.hasNextLine() && getEdges) {
			//store and define edges
			input = in.nextLine();
			String[] e = splitLine(input);
			String v = e[0];
			String u = e[1];
			g.addEdge(v, u);
			
		}		
		
		State state;
		
		int restarts = 0;
		while (restarts < restartsRandom) {
			state = generateRandomState(g);
			if (verbose) System.out.println("Randomly chosen start state " + state.set);
			
			solve(state,g,values,target);
			
			restarts++;
		}
		
		System.out.println("No solution found");
	
	}
	
public static String[] splitLine(String line) {
	
	if (line == null) return null;
	line = line.trim().replaceAll("\\s+", " ");
	
	String[] entries = null;
	entries = line.split(" ");
	
	return entries;
}

public static void solve(State state, Graph<String> g, Map<String, Integer> values, int target) {
	
	int iteration = 0;
	while (iteration < 100) { //random restart triggered after 100 iterations
		
		if(iteration != 0 && verbose) {
			System.out.println("\nMove to: " + state.set + " Value: " + state.getValue(values) + " Error=" + state.error(g, values, target));
		}
		
		if (verbose) {
			
			System.out.println(state.set + " Value=" +state.getValue(values) + " Error=" + state.error(g, values, target));
			
			System.out.println("Neighbors:");
			for (State s : state.getNeighbors(g)) {
				int e = s.error(g, values, target);
				int v= s.getValue(values);
				System.out.println(s.set + " Value=" +v + " Error=" + e);
				
				if(e == 0) {
					System.out.println("Found solution " + s.set + " Value="+v);
					System.exit(1);
				}
				
			}
						
		}
		else {
			for (State s : state.getNeighbors(g)) {
				int e = s.error(g, values, target);
				int v= s.getValue(values);
				
				if(e == 0) {
					System.out.println("Found solution " + s.set + " Value="+v);
					System.exit(1);
				}
				
			}
		}
		
		State bestNeighbor = state.getBestNeighbor(g, values, target);
		state=bestNeighbor;
		iteration++;
		
	}
	if (verbose) System.out.println("Search Failed\n");
	
}

public static State generateRandomState(Graph<String> g) {
	
	List<String> letters = new ArrayList<>();
	
	for (String vertex : g.getVertices()) {
		Random rand = new Random();
		int toss = rand.nextInt(2);
		
		if (toss == 1) {
			letters.add(vertex);
		}
	}
	
	State s = new State(letters);
	
	return s;
}

/**
 * Basic graph implementation
 *
 * @param <T> Object type of vertex
 */
static public class Graph<T>{
	
	private Map<String, Set<String>> adj;
	
	public Graph() {
		this.adj = new HashMap<>();
	}
	
	public void addVertex(String v) {
		this.adj.put(v, new HashSet<String>());
	}
	
	public void addEdge(String v, String u) {
		this.adj.get(v).add(u);
		this.adj.get(u).add(v);
	}
	
	public boolean isAdjacent(String v, String u) {
		return this.adj.get(v).contains(u);
	}
	
	public Iterable<String> getVertices(){
		return this.adj.keySet();
	}
	
	public Iterable<String> getSuccessors(String v){
		
		if (v == null || v.equals(" ")) {
			return this.adj.keySet();
		}
				
		List<String> vertices = new ArrayList<String>();
		
		for (String vertex : this.getVertices()) {
			
			if (v.compareTo(vertex) < 0) //comes after, alphabetically 
			vertices.add(vertex);
		}
		
		vertices.removeAll(this.adj.get(v)); //remove all vertices that have edges connecting
		Collections.reverse(vertices);
		return vertices;
	}
	

}

public static class State{
	
	private List<String> set; //the letters
	int value;
	static Graph<String> g;
	
	public State(List<String> letters) {
		this.set = new ArrayList<>(letters);
			
	}
	
	public List<State> getNeighbors(Graph<String> g){
		
		List<State> neighbors = new ArrayList<>();
		
		if (set.isEmpty() || set == null || set.get(0).equals(" ")) {
			for (String vertex : g.getVertices()) {
				List<String> temp = new ArrayList<>();
				temp.add(vertex);
			
				State st = new State(temp);
				neighbors.add(st);
				temp.clear();
			}
		}
		else {
			
			List<String> addList = new ArrayList<>();
			List<String> remList = new ArrayList<>(this.set);
			for (String vertex : g.getVertices()) {
				//get list of all vertices not in the set
				if(!this.set.contains(vertex)) {
					addList.add(vertex);
				}
			}
			
			for (String vertex : remList) {
				List<String> temp = new ArrayList<>();

				temp.addAll(this.set);
				temp.remove(vertex);
				
				
				State st = new State(temp);					
				neighbors.add(st);
				temp.clear();
				
			}
			
			for (String vertex : addList) {
				List<String> temp = new ArrayList<>();

				temp.addAll(this.set);
				temp.add(vertex);
				
				
				State st = new State(temp);					
				neighbors.add(st);
				temp.clear();
			}
			
		}
		
		return neighbors;
	}
	
	public State getBestNeighbor(Graph<String> g, Map<String, Integer> values, int target) {
		
		State best = null;
		int lowest = Integer.MAX_VALUE;
		
		for (State s : this.getNeighbors(g)) {
			int error = s.error(g, values, target);
			
			if (error < lowest) {
				lowest = error;
				best = s;
			}
		}
		
		return best;
	}
	
	public int error(Graph<String> g, Map<String, Integer> values, int target) {
		
		if (this.set == null || this.set.isEmpty() || this.set.get(0).equals(" ")) {
			return target;
		}
		
		if (this.set.size() == 1) {
			return Math.max(0, target - this.getValue(values));
		}
		
		int sumEdges = 0;
		int edgeValue;
		
		List<String> vertices = new ArrayList<>(this.set);
				
		int d = 0;
		for (String vertex : this.set) {
			//check if vertex is adjacent to any in "vertices" from index i to size.vertices - 1
			//if there is an edge, edge value is the minimum of the values of each vertex
			//add this edgevalue to the sum of edges
			for (int i = d; i < vertices.size(); i++) {
				if (g.isAdjacent(vertex, vertices.get(i))) {
					edgeValue = Math.min(values.get(vertex), values.get(vertices.get(i)));
					sumEdges+= edgeValue;
				}
			}
		d++;
			
		}
		
		return Math.max(0, target - this.getValue(values)) + sumEdges;
	}
	
	public int getValue(Map<String, Integer> values) {
		int sum = 0;
		
		if (set == null || set.isEmpty() || set.get(0).equals(" ")) {
			return 0;
		}
		
		for (String letter : set) {
			sum += values.get(letter);
		}
		
		return sum;
	}
	
}
}