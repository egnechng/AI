import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

//followed Pseudocode in Brightspace
public class DavisPutnam {
	
	public static HashMap<Integer, Boolean> dp(Set<HashSet<Integer>> clauses) {
		
		HashSet<Integer> atoms = new HashSet<>();
		for (HashSet<Integer> clause : clauses) {
			for (Integer literal : clause) {
				atoms.add(Math.abs(literal));
			}
		}
		
		//System.out.println("ATOMS: " + atoms);
		
		//new HashMap of valuations (Integer --> Boolean)
		HashMap<Integer, Boolean> V = new HashMap<>();
		
		//assignments defaulted null = unbound
		for (int atom : atoms) {
			//System.out.println(atom);
			V.put(atom, null);
			//System.out.println(V);
		}
		
		return dp1(clauses, atoms, V);
		
	}
	
	private static HashMap<Integer, Boolean> dp1(Set<HashSet<Integer>> clauses, 
			HashSet<Integer> atoms, HashMap<Integer, Boolean> V){
		
		//loop
		while(true) {
			
			//BASE CASE:
			if (clauses.isEmpty()) {
				for (int atom : atoms) {
					if (V.get(atom) == null) {
						V.put(atom, true); //arbitrarily assign T
					}
				}
				return V;
			}else {
				for (HashSet<Integer> clause : clauses) {
					if (clause.isEmpty()) {
						return null; // FAIL --> Backtrack
					}
				}
			}
			
			//EASY CASES; pure literals and singletons

			//PURE LITERAL
			for (int atom : atoms) {
				boolean pos = false;
				boolean neg = false;
				
				if (V.get(atom) == null) {
					
					for (HashSet<Integer> clause : clauses) {
						if (clause.contains(atom)) {
							pos = true;
						}
						if (clause.contains(-atom)) {
							neg = true;
						}
					}
					if (pos && !neg) {
						V.put(atom, true);
						
						Set<HashSet<Integer>> S1 = new HashSet<>();
				
						for (HashSet<Integer> clause : clauses) {
							if (!clause.contains(atom)) {
								S1.add(clause);
							}
						}
						
						clauses = S1;
						continue;
					}else if (!pos && neg) {
						V.put(atom, false);
						
						Set<HashSet<Integer>> S1 = new HashSet<>();
						for (HashSet<Integer> clause : clauses) {
							if (!clause.contains(-atom)) {
								S1.add(clause);
							}
						}
												
						clauses = S1;
						continue;
					}	
				}
			}
			
			
			//SINGLETON
			for (HashSet<Integer> clause : clauses) {
				if (clause.size() == 1) {
					boolean isPositive = false;
					int literal = clause.iterator().next();
					
					if (literal > 0) {
						isPositive = true;
					}
					int A = Math.abs(literal);
					
					V.put(A, isPositive);
					Set<HashSet<Integer>> S1 = propogate(A, clauses, V);
					clauses = S1;
					
					continue;
				}
			}		
			
			break;	//end loop if no easy cases			
		}	
		
		
		//HARD CASE: PICK SOME ATOM AND TRY EACH ASSIGNMENT IN TURN
		int A = 0; //Atom we want to try
		for (int atom : atoms) {
			if (V.get(atom) == null) {
				A = atom;
				break;
			}
		}
		
		HashMap<Integer, Boolean> V1 = new HashMap<>(V);
		
		//TRY TRUE
		V.put(A, true);
		Set<HashSet<Integer>> S1 = propogate(A, new HashSet<>(clauses), V);
		HashMap<Integer, Boolean> v_new = dp1(S1, atoms, V);
		if (v_new != null) {
			return v_new;
		}
		
		//TRY FALSE
		V1.put(A, false);
		Set<HashSet<Integer>> S2 = propogate(A, new HashSet<>(clauses), V1);
		return dp1(S2, atoms, V1);
			

	}
	
	private static Set<HashSet<Integer>> propogate(int A, Set<HashSet<Integer>> S, 
			HashMap<Integer, Boolean> V){
		
		Set<HashSet<Integer>> result = new HashSet<>();
		
		for (HashSet<Integer> clause : S) {
			if ( (clause.contains(A) && V.get(A) == true) || (clause.contains(-A) && V.get(A) == false) ) {
				//A is true in the clause so delete it
				continue;
			}else if (clause.contains(A) && V.get(A) == false) {
				//A is false in the clause so delete the literal
				HashSet<Integer> newClause = new HashSet<>(clause);
				newClause.remove(A);
				result.add(newClause);
			}else if (clause.contains(-A) && V.get(A) == true) {
				//A is false in the clause so delete the literal
				HashSet<Integer> newClause = new HashSet<>(clause);
				newClause.remove(-A);
				result.add(newClause);
			}
			else {
				//A does not appear in the clause
				result.add(clause);
			}
		}
		
		return result;
		
	}


	public static void main(String[] args) {
		
		File file = new File("./input.txt");
		Scanner in = null;
		Set<HashSet<Integer>> clauses = new HashSet<>();
		
		try {
			in = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			System.err.println("The file " + file.getAbsolutePath()+ " cannot be read\n");
			System.exit(1);
		}
		
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			
			if (line.equals("0")) {
				break;
			}
			
			String[] literals = line.split(" ");
			HashSet<Integer> clause = new HashSet<>();
			
			for (String word : literals) {
				clause.add(Integer.parseInt(word));
			}
			clauses.add(clause);
					
		}
		
		//execution of program
		HashMap<Integer, Boolean> truthValues = dp(clauses);
		
		try {
			truthValues.remove(0);
		}
		catch (NullPointerException e) {
		}
		
		//System.out.println(truthValues);
		//write output to file called "output.txt"
		try {
			PrintWriter out = new PrintWriter("./output.txt");
			
			if (truthValues == null) {
				out.println("0");
				out.close();
			}else {
				for (int atom : truthValues.keySet()) {
					out.println(atom + " " + (truthValues.get(atom) ? "T" : "F"));
				}
				out.println("0");
			}
			
			while(in.hasNextLine()) {
				String line = in.nextLine();
				if (!in.hasNextLine()) {
					out.print(line);
					break;
				}
				out.println(line);
			}
			
			out.close();
			
		}catch (FileNotFoundException e) {
			System.out.println("File not found: output.txt");
		}
		
		
		in.close();
		System.out.println("Davis Putnam Algorithm: Output redirected to output.txt");
	}

}
