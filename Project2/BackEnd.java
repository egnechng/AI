import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class BackEnd {
	
	
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
		
		line = in.nextLine().trim();
		
		if (line.equals("0")) {
			
			try {
				PrintWriter out = new PrintWriter("./src/output.txt");
				
				out.print("NO SOLUTION");
				System.out.println("Back End: output redirected to output.txt");
				out.close();
				System.exit(1);
						
				}
			catch (FileNotFoundException e) {
				System.out.println("File not found: output.txt");
			}
				
		}
		
		String[] input = line.split(" ");
		
	
		
		HashMap<Integer, String> map = new HashMap<>();
		
		int atom = Integer.parseInt(input[0]);
		String value = input[1];
		map.put(atom, value);
		
		while (in.hasNextLine()) {
			line = in.nextLine().trim();
			
			if (line.equals("0")) {
				break;
			}
			
			input = line.split(" ");
			
			atom = Integer.parseInt(input[0]);
			value = input[1];
			map.put(atom, value);
			
		}
		
		ArrayList<String> path = new ArrayList<>();
		
		while (in.hasNextLine()) {
			line = in.nextLine().trim();
			
			input = line.split(" ");
			if (map.get(Integer.parseInt(input[0])).equals("T") && input[1].substring(0,1).equals("A")) {
				
				path.add(input[1]);
				
			}
			
		}
		
		path.sort(Comparator.comparingInt(BackEnd::getNumber));
		
		try {
			PrintWriter out = new PrintWriter("./output.txt");
			
			for (String step : path) {
				
				int start = step.indexOf("(");
				int end = step.indexOf(",");
				
				out.print(step.substring(start+1,end) + " ");
				
			}
			
			out.close();
			
		}catch (FileNotFoundException e) {
			System.out.println("File not found: output.txt");
		}
		

		System.out.println("Back End: output redirected to output.txt");
		System.exit(0);
				

	}
	
	private static int getNumber(String str) {
        // Find the index of the first non-digit character
        int index = 0;
        while (index < str.length() && !Character.isDigit(str.charAt(index))) {
            index++;
        }
        
        // Extract the first number from the string and parse it to an int
        StringBuilder number = new StringBuilder();
        while (index < str.length() && Character.isDigit(str.charAt(index))) {
            number.append(str.charAt(index));
            index++;
        }
        return Integer.parseInt(number.toString());
    }

}
