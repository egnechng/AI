import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


public class FrontEnd {
	
	static int steps;
	static String[] labels;
	static String[] treasures;
		
	public static void main(String[] args) {
		
		File file = new File("./input.txt");
		Scanner in = null;

		
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			System.err.println("The file " + file.getAbsolutePath()+ " cannot be read\n");
			System.exit(1);
		}
		
		String line = "";
		
		//get nodes
		try {
			line = in.nextLine();
		}
		catch (NoSuchElementException e) {
			System.out.println("Error: Empty input");
			System.exit(0);
		}
	
		labels = line.split(" ");
		
		//get treasures
		line = in.nextLine();
		treasures = line.split(" ");
		
		//number of steps
		line = in.nextLine();
		steps = Integer.parseInt(line.trim());
		
		List<Node> nodes = new ArrayList<>();
		HashSet<Set<String>> clauses = new HashSet<>();
		ArrayList<String> atoms = new ArrayList<>();
		
		//collect information about nodes
		while (in.hasNextLine()) {
			line = in.nextLine();
			
			String[] inputs = line.split(" ");
			Node node = new Node(inputs[0]);
			
			int i = 2;
			String token = inputs[i];
			
			//get treasures
			while (!token.equals("NEXT")) {
				node.addTreasure(token);
				i++;
				token = inputs[i];	
			}
			
			i++;
			
			//get next nodes
			for (int k = i; k < inputs.length; k++) {
				token = inputs[k];
				node.addNext(token);
			}
			//add Node to list	
			nodes.add(node);	
	
		}
		in.close();
		
		clauses = createClauses(nodes);
		if (clauses != null) atoms = getAtoms(clauses);
		
		HashMap<String, Integer> conv = new HashMap<>();
		
		int atm = 1;
		for (String atom : atoms) {
			conv.put(atom, atm);
			atm++;
		}

		try {
			PrintWriter out = new PrintWriter("./output.txt");
			
			if (clauses == null) {
				out.println("0");
				out.close();
			}else {
				for (Set<String> clause : clauses) {
					
					for (String atom : clause) {
						if (atom.substring(0,1).equals("~")) {
							out.print(-conv.get(atom.substring(1)) + " ");
						}else {
							out.print(conv.get(atom) + " ");
						}
					}
					out.print("\n");
				}
				out.println("0");
			}
			
			ArrayList<String> output = new ArrayList<>();
			for (String atom : conv.keySet()) {
				String temp;
				temp = conv.get(atom) + " " + atom;
				output.add(temp);
			}
			output.sort(Comparator.comparingInt(FrontEnd::getNumber));
			for (String s : output) {
				
				if (s.equals(output.get(output.size() -1))) {
					out.print(s);
				}else {
					out.println(s);
				}
				
			}
			
			out.close();
			
		}catch (FileNotFoundException e) {
			System.out.println("File not found: output.txt");
		}
		
		System.out.println("Front End: output redirected to output.txt");
		System.exit(0);
				
		
		// TESTING INPUT HANDLING
		/*
		for (Node n : nodes) {
			System.out.println("NODE " + n.label);
			System.out.println("Treaures:");
			for (String treasure : n.treasures) {
				System.out.println(treasure);
			}
			System.out.println("Next Nodes:");
			for (String next : n.nextNodes) {
				System.out.println(next);
			}
			
			System.out.println();
		}*/
			
	}
	
	private static int getNumber(String str) {
		
		int endIndex = 0;
	    while (endIndex < str.length() && Character.isDigit(str.charAt(endIndex))) {
	        endIndex++;
	    }
	   
	    return Integer.parseInt(str.substring(0, endIndex));
		
	}
	
	public static ArrayList<String> getAtoms(HashSet<Set<String>> clauses){
		HashSet<String> atoms = new HashSet<>();
		
		for (Set<String> clause : clauses) {
			for (String atom : clause) {
				
				if (atom.substring(0,1).equals("~")) {
					atoms.add(atom.substring(1));
				}else {
					atoms.add(atom);
				}
			}
		}
		
		ArrayList<String> sortedAtoms = new ArrayList<>(atoms);
		Collections.sort(sortedAtoms);
		
		return sortedAtoms;
	}
	
	public static HashSet<Set<String>> createClauses(List<Node> nodes){
		
		HashSet<Set<String>> clauses = new HashSet<>();
		
		for (int i = 0; i <= steps; i++) {	
			//Category 1. Player is at one place at a time
			
			for (String node : labels) {
				for (String node2 : labels) {
					HashSet<String> clause = new HashSet<>();
					
					if (node.equals(node2)) continue;
					clause.add("~At("+node+","+i+")");
					clause.add("~At("+node2+","+i+")");		
					clauses.add(clause);		
				}
			}
			
			//Category 3 Suppose that treasure T is located at node N. Then if the player is at N at time I, then at time I the player has T.
			for (Node node : nodes) {
				
				if (!node.treasures.isEmpty()) {
					for (String treasure : node.treasures) {
						HashSet<String> cat3 = new HashSet<>();
						cat3.add("~At(" + node.label + "," + i +")");
						cat3.add("Has(" + treasure + "," + i + ")");
						clauses.add(cat3);
					}
				}
			}
			
		}
		
		/*Category 2.The player must move on edges. Suppose that node N is connected to M1 ... Mq. For any time I, if the player is at node N at time I, 
		*then the player moves to M1 or to M2 ... or to Mq at time I+1.
		*/
		
		for (int i = 0; i < steps; i++) {
			for (Node node : nodes) {
				if (node.nextNodes.isEmpty()) continue;
				String l = node.label;
				HashSet<String> cat2 = new HashSet<>();
				
				cat2.add("~At(" + l + "," + i + ")");
				for (String next : node.nextNodes) {
					cat2.add("At(" + next + "," + (i+1) + ")");
				}
				
				clauses.add(cat2);
			}
		}	
		
		
		//Category 4. If the player has treasure T at time I-1, then the player has T at time I. (I=1..K)
		
		for (int i = 1; i <= steps; i++) {
			
			for (String treasure : treasures) {
				HashSet<String> clause = new HashSet<>();
				clause.add("~Has(" + treasure + "," + (i-1) + ")");
				clause.add("Has(" + treasure + "," + i + ")");
				clauses.add(clause);

				/*Category 5.
				 *  Let M1 ... Mq be the nodes that supply treasure T. If the player does not have treasure T at time I-1 and has T at time I
		 then at time I they must be at one of the nodes M1 ... Mq.
				 */
				HashSet<String> cat5 = new HashSet<>();
				cat5.add("Has(" + treasure + "," + (i-1) + ")");
				cat5.add("~Has(" + treasure + "," + i + ")");
						
				for (Node node : nodes) {
					if (node.treasures.contains(treasure)) {				
						cat5.add("At(" + node.label + "," + i + ")");
					}
				}
				clauses.add(cat5);
				
			}
			
		}	
		
		//Category 6. The player is at START at time 0. At(START,0).
		
		HashSet<String> cat6 = new HashSet<>();
		cat6.add("At(START,0)");
		clauses.add(cat6);
		
		//Category 7 - At time 0, the player has none of the treasures.
		for (String treasure : treasures) {
			HashSet<String> clause = new HashSet<>();
			clause.add("~Has(" + treasure + ",0)");
			clauses.add(clause);
		}
		
		//Category 8. At time K, the player has all the treasures.
		for (String treasure : treasures) {
			HashSet<String> clause = new HashSet<>();
			clause.add("Has(" + treasure + "," + steps + ")");
			clauses.add(clause);
		}
		
		return clauses;
		
	}
	
	public static class Node{
		
		public String label;
		public ArrayList<String> treasures = new ArrayList<>();
		public ArrayList<String> nextNodes = new ArrayList<>();;
		
		public Node(String label) {
			this.label = label;
		}
		
		private void addTreasure(String t) {
			this.treasures.add(t);
		}
		
		private void addNext(String next) {
			this.nextNodes.add(next);
		}
	}

}


