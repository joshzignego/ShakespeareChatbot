package CharacterGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/* Generate the list of characters in the plays using each play's
 * dramatis personae so chatbot knows what names to capitalize
 */
public class CharacterGenerator {

	public static void main(String[] args) {
		String outputfilename = "./characters.txt";
		Set<String> characters = new HashSet<String>();
		List<String> charactersSorted;

		//37 plays
		for(int i = 1; i <= 37; i++) {

			String inputFileName = "./Dramatis_Personae/" + i + ".txt";

			//For each character file: read in list of characters
			try {

				File inputFile = new File(inputFileName);

				BufferedReader reader = new BufferedReader(new FileReader(inputFile));

				String currentChar;

				while((currentChar = reader.readLine()) != null) {
					// trim word, remove special chars
					currentChar = currentChar.trim();
					currentChar = currentChar.replaceAll("[^\\w'.-]+", "");
					currentChar = currentChar.toLowerCase();
					characters.add(currentChar);			    
				}

				reader.close();

			} catch(FileNotFoundException ex){
				System.out.println("Input file Not Found.");
			} catch(IOException ex){
				System.out.println("IOException");
			}
		}

		//The words I and O are always capitalized, and for some reason these three
		//names weren't being added, so these words were all added manually
		characters.add("i");
		characters.add("i.");
		characters.add("i!");
		characters.add("i'");
		characters.add("i'd");
		characters.add("i'faith");
		characters.add("i'll");
		characters.add("i'm");
		characters.add("i'the");
		characters.add("i've");
		characters.add("o");
		characters.add("o'");
		characters.add("o.");
		characters.add("o!");
		characters.add("antony");
		characters.add("cassius");
		characters.add("aaron");

		//Sort list of characters
		charactersSorted = new ArrayList<String>(characters);
		java.util.Collections.sort(charactersSorted);

		//Write to output file
		try {
			File outputFile = new File(outputfilename);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));

			//Write each character to file
			for(int j = 0; j < charactersSorted.size(); j++) {
				String word = charactersSorted.get(j);
				writer.write(word);
				writer.write(System.lineSeparator());
			}

			writer.close(); 

		} catch(FileNotFoundException ex){
			System.out.println("Output file Not Found.");
		} catch(IOException ex){
			System.out.println("IOException");
		}

		System.out.println("Finished creating characters list!");

	}
}
