import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class textClassifier {
	
	static String fileName;
	static int N;
	
	public static class Biography {
		
		String name;
		String category;
		String bio; // TURN INTO ARRAY LIST, STORE ALL WORDS THAT ARE NOT STOPWORDS INDIVIDUALLY
		//THEN RUN NAIVE BAYES 
		
		public Biography(String name, String cat, String bio) {
			this.name = name;
			this.category = cat;
			this.bio = bio;
		}
		
	}


	public static void main(String[] args) throws IOException{
		
		fileName = args[0];
		N = Integer.parseInt(args[1]);
		
//		System.out.println(args[0]);
//		System.out.println(args[1]);
					
		//HARDCODE STOP WORDS
		String[] s = new String[] 
				{"about","all","along","also","although",
				"among","and","any","anyone","anything","are","around","because",
				"been","before","being","both","but","came","come","coming","could",
				"did","each","else","every","for","from","get","getting","going","got",
				"gotten","had","has","have","having","her","here","hers","him","his","how",
				"however","into","its","like","may","most","next","now","only","our","out",
				"particular","same","she","should","some","take","taken","taking","than",
				"that","the","then","there","these","they","this","those","throughout","too",
				"took","very","was","went","what","when","which","while","who","why","will",
				"with","without","would","yes","yet","you","your","com","doc","edu","encyclopedia",
				"fact","facts","free","home","htm","html","http","information","internet","net",
				"new","news","official","page","pages","resource","resources","pdf","site","sites",
				"usa","web","wikipedia","www","one","ones","two","three","four","five","six","seven",
				"eight","nine","ten","tens","eleven","twelve","dozen","dozens","thirteen","fourteen",
				"fifteen","sixteen","seventeen","eighteen","nineteen","twenty","thirty","forty",
				"fifty","sixty","seventy","eighty","ninety","hundred","hundreds","thousand",
				"thousands","million","millions"};
		
		HashSet<String> stopWords = new HashSet<>(); //Constant time access
		
		for (String word : s) {
			stopWords.add(word);
		}
		
		//File file = new File("./src/" + fileName);		

		ArrayList<Biography> biographies = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader("./" + fileName));
		
//		String name = "";
//        String category = "";
//        String bio = "";
//        int trainingCount = 0;
//        int it = 0;
		
        String line;
        
        //Get biographies and store in Memory
        while ((line = reader.readLine()) != null) {
        	if ( !(line.trim().isBlank()) ){
        		String name = line.trim();
        		String category = reader.readLine().trim();
        		StringBuilder bioBuilder = new StringBuilder();
        		while ((line = reader.readLine()) != null && !line.isBlank()) {
        			bioBuilder.append(line + " ");
        		}
        		
        		String bio = bioBuilder.toString();
        		bio = bio.replace(",", "").replace(".", "");
        		bio.trim();
        		
        		//remove duplicate words
        		String[] words = bio.split(" ");
        		ArrayList<String> unique = new ArrayList<String>();
        		
        		for (String word : words) {
        			if (!unique.contains(word)) {
        				unique.add(word);
        			}
        		}
        		String result = String.join(" " , unique);
        		biographies.add(new Biography(name, category, result));
        		
        	}
        }
        
        reader.close();
        
        //Print Bios
//        for (Biography b : biographies) {
//        	System.out.println("Name: " + b.name);
//        	System.out.println("Category: " + b.category);
//        	System.out.println("Bio: " + b.bio);
//        	System.out.println();
//        }
//        
        //LEARNING PHASE
        
		//The number of biographies with category C
		HashMap<String, Integer> categoryCount = new HashMap<>();
		//Number of Category - Word pairs
		HashMap<String, Integer> categoryWordCount = new HashMap<>(); 
		
		// This is for the application phase, we want to skip words not in training set
		HashSet<String> wordSet = new HashSet<>(); 
				
		//Store negative logs -log(P(C) and -log(P(W|C)
		HashMap<String, Double> P_C = new HashMap<>();
		HashMap<String, Double> P_WC = new HashMap<>();
		
		//Here
		
		//Learn from the first N biographies
		for (int i = 0; i < N; i++) {
			
			Biography b = biographies.get(i);
			String category = b.category;
			String[] bioWords = b.bio.split(" ");
			
			//Keep track of Category Counts
			if (!categoryCount.containsKey(category)) {
				categoryCount.put(category, 1);
			} else {
				int newCount = categoryCount.get(category) + 1;
				categoryCount.put(category, newCount);
			}
			
			String categoryWordPair;
			
			for (String word : bioWords) {
				String norm = word.toLowerCase();
				
				//Check if stop word or 2 letters
				if (norm.length() <= 2 || stopWords.contains(norm)) continue;
				
				wordSet.add(norm);
				
				//Category Word Pair Format: Word|Category (W|C)
				categoryWordPair = norm + "|" + category;
				
				if (!categoryWordCount.containsKey(categoryWordPair)) {
					categoryWordCount.put(categoryWordPair, 1);
				}else {
					int newCount = categoryWordCount.get(categoryWordPair) + 1;
					categoryWordCount.put(categoryWordPair, newCount);
				}
				
			}
					
		}
		
		//Compute probabilities
		
		//P(C)
		double T = N;
		int numCategories = categoryCount.size();
		//System.out.println("# cat: " + numCategories);
		for (Map.Entry<String, Integer> element : categoryCount.entrySet()) {
			
			double freqC = element.getValue()/T;
			
			
			//System.out.println("Frequency " + element.getKey() + " " + freqC);
			
			double prob = (freqC + 0.1) / (1 + (numCategories * 0.1) );
			double L = -Math.log(prob) / Math.log(2);
			
			P_C.put(element.getKey(), L);
			
			for (String word : wordSet) {
						
				String entry = word + "|" + element.getKey();
				
				double freqWC;
				if (!categoryWordCount.containsKey(entry)) {
					freqWC = 0;
				}else {
					
					freqWC = ( (double) categoryWordCount.get(entry) ) / ( (double) element.getValue());
				}
				prob = (freqWC + 0.1) / (1 + 2*0.1);
				L = -Math.log(prob) / Math.log(2);
				
				P_WC.put(entry, L);
			}
			
		}
		
		//PREDICTION PHASE
	    
	    //for the rest of the biographies::
	    /*
	     * 1. normalize data like part 1 and skip any words that didnt appear in training set
	     * 2. FOR EACH CATEGORY: Compute the L(C|B) 
	     * 3. Prediction is the category C with smallest L(C|B) value
	     * 4. Recover actual probabilities
	     * 5. Print output --> DONE
	     */
		
//		for (String w : wordSet) {
//			System.out.println(w);
//		}
//		
//		System.out.println();
		
		double correctCount = 0;
		
		for (int i = N; i < biographies.size(); i++) {

			Biography b = biographies.get(i);
			String category = b.category;
			String[] bioWords = b.bio.split(" ");
			
			//remove words that didnt appear in training data
			ArrayList<String> relevantWords = new ArrayList<String>();
    		
    		for (String word : bioWords) {
    			String norm = word.toLowerCase();
    			
    			if (wordSet.contains(norm)) {
    				relevantWords.add(norm);
    			}
    		}
    		
//			for (String r : relevantWords) {
//				System.out.print(r + " ");
//			}
//			System.out.println();
    		
    		//compute L(C|B)
    		double smallest = Double.MAX_VALUE;
    		String currentGuess = "";
    		
    		ArrayList<Double> c = new ArrayList<>();
    		ArrayList<String> catList = new ArrayList<>();
    		
    		for (Map.Entry<String, Integer> element : categoryCount.entrySet()) {
    			
    			//System.out.println(P_C.get(element.getKey()));
    			double L_CB = P_C.get(element.getKey());
    			for (String word : relevantWords) {
    				String entry = word + "|" + element.getKey();
    			
    				L_CB = L_CB + P_WC.get(entry);		   				
    			}
    			
    			if (L_CB < smallest) {
    				smallest = L_CB;
					currentGuess = element.getKey();
				}
    			
    			c.add(L_CB);
    			catList.add(element.getKey());
    			    			
    		}
    		
    		String status = "Wrong";
    		
    		if (currentGuess.equals(b.category)) {
    			correctCount++;
    			status = "Right";
    		}
    		
    		System.out.println(b.name + ".  Prediction: " + currentGuess + ". " + status);
    		
    		//PRINT OUT ACTUAL PROBABILITES
    		int k = numCategories;
    		double m = smallest;
    		double S = 0;
    		ArrayList<Double> X = new ArrayList<>();
    		
    		for (int j = 0; j < k; j++) {
    			double x;
    			if (c.get(j) - m < 7) {
    				x = Math.pow(2, m-c.get(j));
    			}else {
    				x = 0;
    			}
    			X.add(x);
    			S += x;   			
    		}
    		
    		for (int j = 0; j < k; j++) {
    			double p = X.get(j) / S;
    			
    			//Round the number
    			DecimalFormat df = new DecimalFormat("#.##");
    			double rounded = Double.parseDouble(df.format(p));
    			
    			System.out.print(catList.get(j) + ": " + rounded + "  ");
    			
    		}
    		
    		System.out.println();
    		System.out.println();
			
		}
		DecimalFormat df = new DecimalFormat("#.##");
		
		//Accuracy Analysis
		int total = biographies.size() - N;
		double accuracy = correctCount / total;
		accuracy = Double.parseDouble(df.format(accuracy));
		System.out.println("Overall accuracy: " + (int) correctCount + " out of " + 
				total + " = " + accuracy);
		
		//OUTPUT TESTING
		
//		for (Map.Entry<String, Integer> element : categoryCount.entrySet()) {
//	    	String key = element.getKey();
//	    	
//	    	System.out.println(key + " : " + element.getValue());
//	    }
//		
//		System.out.println();
//		
//		for (Map.Entry<String, Double> element : P_C.entrySet()) {
//	    	String key = element.getKey();
//	    	
//	    	System.out.println(key + " : " + element.getValue());
//	    }
//	    
//		System.out.println();
//		
//	    for (Map.Entry<String, Integer> element : categoryWordCount.entrySet()) {
//	    	String key = element.getKey();
//	    	
//	    	System.out.println(key + " : " + element.getValue());
//	    }
//	    
//	    System.out.println();
//	    
//	    for (Map.Entry<String, Double> element : P_WC.entrySet()) {
//	    	String key = element.getKey();
//	    	
//	    	System.out.println(key + " : " + element.getValue());
//	    }
	    
        
	}

}
