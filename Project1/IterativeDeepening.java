import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class IterativeDeepening {
	
	static int n;

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
		
		//parse inputs
		String[] first = splitLine(input);
		target = Integer.parseInt(first[0]);
		flag = first[1];
		
		//verbose output
		boolean verbose = false;
		if (flag.toUpperCase().equals("V")) verbose = true;
		
			
		System.out.println("Target: " + target + " Verbose Output: " + verbose);
				
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
		
		n = vertices.size();
		Search search = new Search(target,g , values, verbose);
		
		List<String> s = new ArrayList<String>();
		s.add(" ");

		State state = new State(s);
	
		if (search.ids(state)) {
			System.exit(1);
		}
		else {
			System.out.println("No Solution found");
			System.exit(1);
		}

	}
	
public static String[] splitLine(String line) {
	
	if (line == null) return null;
	line = line.trim().replaceAll("\\s+", " ");
	
	String[] entries = null;
	entries = line.split(" ");
	
	return entries;
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
	List<State> successors;
	static Graph<String> g;
	
	public State(List<String> letters) {
		this.set = new ArrayList<>(letters);
			
	}
	
	public List<State> getSuccessors(Graph<String> g){
		List<State> successors = new ArrayList<>();
		
		if (set.isEmpty() || set == null || set.get(0).equals(" ")) {
			
			for (String vertex : g.getVertices()) {
				List<String> temp = new ArrayList<>();
				temp.add(vertex);
				
				State st = new State(temp);
				successors.add(st);
				temp.clear();
			}
		}
		else {
			String end = this.set.get(this.set.size()-1);
			for (String vertex : g.getSuccessors(end)) {
				List<String> temp = new ArrayList<>();
				temp.addAll(this.set);
				temp.add(vertex);
				
				
				State st = new State(temp);					
				successors.add(st);
				temp.clear();
				}
			Collections.reverse(successors);
		}
		
		return successors;
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
	
	public boolean isSolution(int target, Map<String, Integer> values) {
		
		return this.getValue(values) >= target;
	}
	
}



public static class Search{
	
	private static int target;
	private static Map<String, Integer> values;
	static Graph<String> g;
	static boolean flag;
	int depth;
	int maxDepth;
	Stack<String> state = new Stack<>(); //path of the search
	
	public Search(int target, Graph<String> g, Map<String, Integer> values, boolean flag) {
		this.target = target;
		this.values = values;
		this.g = g;
		this.flag = flag;
	}
	

static boolean depthLimitedSearch(State state, int depth, Set<State> visited) {
	if (depth == 0 && state.isSolution(target, values)) {
		System.out.println("Solution found:" + state.set + " Value="+ state.getValue(values));
		return true;
	}
	
	if (depth > 0) {
		List<State> successors = state.getSuccessors(g);
		
		for (State successor : successors) {
			if (visited.contains(successor)) {
				continue;
			}
			if(flag) System.out.println(successor.set + " Value=" + successor.getValue(values));
			visited.add(state);
			if (depthLimitedSearch(successor, depth - 1, visited)) {
				return true;
			}
		}
	}
	
	return false;
}

public static boolean ids(State start) {
	
	int max = n;
	for (int i = 0; i < max; i++) {
		
		Set<State> visited = new HashSet<>();
		if(i!=0 && flag) System.out.println("Depth="+ i);
		if (depthLimitedSearch(start, i, visited)) {
			return true;
		}
	}
	return false;
}


}
}






