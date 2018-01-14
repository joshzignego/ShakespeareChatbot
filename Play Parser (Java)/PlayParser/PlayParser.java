package PlayParser;
import java.util.*;
import java.io.*;
import java.nio.charset.*;

/* Input: 	raw Shakespeare plays from http://lexically.net/wordsmith/support/shakespeare.html,
 * 			and poems from http://www.gutenberg.org/browse/authors/s
 * Output: 	files that are the pure text from Shakespeare's works, ie no character names/scene titles
 */
public class PlayParser {
	public static void main(String[] args) {
		
		//40 plays & poems
    	for(int i = 1; i <= 40; i++) {
    		
    		String inputFileName = "./Raw_Corpus/" + i + ".txt";
    		String outputFileName = "./Edited_Corpus/" + i + ".txt";
    		
    		//For each file in corpus: read in & write out
    		try {
    			
    			File inputFile = new File(inputFileName);
    			File tempFile = new File(outputFileName);

    			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
    			
    			String currentLine;

    			while((currentLine = reader.readLine()) != null) {
    				
    				// trim word in case special chars
    			    String trimmedLine = currentLine.trim();
    			    
    			    //If line has < or $ or is empty- don't add it to the corpus
    			    if(trimmedLine.contains("<") || trimmedLine.contains("$")) {
    			    	;
    			    } else if(trimmedLine.isEmpty()) {
    			    	;
    				} else {
    					//Otherwise write the input line out to corpus
    			    	writer.write(trimmedLine + " ");
    			    	writer.write(System.lineSeparator());
    			    }
    			    
    			}
    			
    			//Free resources
    			writer.close(); 
    			reader.close(); 
	            
    		} catch(FileNotFoundException ex){
                System.out.println("File Not Found.");
            } catch(IOException ex){
            	System.out.println("IOException");
            }
    	}
    	System.out.println("Finished processing corpus!");
    }
}
