package Chatbot;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/* Generates Shakespearean response based on text input
 * into stdin by the user
 */
public class Chatbot {
	//the corpus, the vocabulary, and the list of characters
	public static List<String> vocab;
	public static int vocabSize;
	public static List<String> corpus;
	public static int corpusSize;
	public static List<String> characters;
	
	//First & second words of a sentence generated via unigram & bigram
	public static String firstWord;
	public static String secondWord;
	
	//Counts of each uni/bi/trigram sequence of words
	public static List<Integer> unigramCounts;
	public static List<NgramNode> bigramCounts;
	public static List<List<NgramNode>> trigramCounts;

	//Generate first word of sentence via unigram
	static public String unigram(double random) {
		double prob = 0;
		//For each word type 
		for(int i = 0; i < vocabSize; i++) {	
			prob += unigramCounts.get(i)/(double)corpusSize;
			
			//Return word if prob larger than random now
			if (random <= prob  && unigramCounts.get(i)>0)
				return vocab.get(i);
		}
		
		//Error message- should never happen
		return "Shakespeare bot doesn't know what to say UNIGRAM";
	}

	//Generate first and second words of sentence via bigram
	static public String bigram(String firstWord, double random) {
		double prob = 0;
		double count_firstWord = 0;

		//Get vocab index of firstWord
		int i = 0;
		for(i = 0; i < vocabSize; i++)
			if(firstWord.equals(vocab.get(i)))
				break;
		
		//If not in vocab: return null
		if(i == vocabSize) 
			return null;

		//Get count of firstWord in corpus and words after it & their counts
		count_firstWord = unigramCounts.get(i);
		NgramNode node = bigramCounts.get(i);
		List<String> wordsAfter = node.getWordsAfter();
		List<Integer> wordsAfterCounts = node.getWordsAfterCounts();

		//For each word in words after the first word
		for(int j = 0; j < wordsAfter.size(); j++) {		
			int count = wordsAfterCounts.get(j);
			prob += count/count_firstWord;
			
			//Return word if prob larger than random now
			if (random <= prob) {
				String word = wordsAfter.get(j);
				
				//Capitalize word if a character name
				if(isCharacter(word.trim().replaceAll("[^\\w]+", "").toLowerCase()))
					return capitalize(word);
				return word;
			}
		}
		
		//Error message- should never happen
		return "Shakespeare bot doesn't know what to say BIGRAM";
	}
	
	//Generate new word based on previous two via trigram
	static public String trigram(String firstWord, String secondWord, double random) {
		double prob = 0;
		double countOfFirstWordThenSecondWord = 0;
		
		//Get total count of first 2 words together
		for(int i = 0; i < corpusSize-1; i++) 
			if(firstWord.equals(corpus.get(i)) && secondWord.equals(corpus.get(i+1)))
				countOfFirstWordThenSecondWord++;

		//Get index of first word in vocab
		int firstWordIndex = 0;
		for(int i = 0; i < vocabSize; i++) {
			if(firstWord.equals(vocab.get(i))) {
				firstWordIndex = i;
				break;
			}
		}

		//Find index of second word in wordsAfter for first word
		NgramNode bigramNode = bigramCounts.get(firstWordIndex);
		List<String> wordsAfterBigram = bigramNode.getWordsAfter();
		
		int secondWordIndex = 0;
		for(int i = 0; i < wordsAfterBigram.size(); i++) 
			if(secondWord.equals(wordsAfterBigram.get(i))) 
				secondWordIndex = i;

		//Get count of first 2 words together in corpus and words after them & their counts
		NgramNode node = trigramCounts.get(firstWordIndex).get(secondWordIndex);
		List<String> wordsAfter = node.getWordsAfter();
		List<Integer> wordsAfterCounts = node.getWordsAfterCounts();

		//For each word in words after the first and second words together
		for(int i = 0; i < wordsAfter.size(); i++) {		
			int count = wordsAfterCounts.get(i);
			prob += count/countOfFirstWordThenSecondWord;
			
			//Return word if prob larger than random now
			if (random <= prob) {
				String word = wordsAfter.get(i);
				
				//Capitalize word if a character name
				if(isCharacter(word.trim().replaceAll("[^\\w]+", "").toLowerCase()))
					return capitalize(word);
				return word;
			}
		}
		
		//Error message- should never happen
		return "Shakespeare bot doesn't know what to say TRIGRAM";
	}

	//Remove all extra characters from a word, make lower case
	public static String stripper(String string) {
		return string.trim().replaceAll("[^\\w'!.-]+", "").toLowerCase();
	}

	//Returns true if word is a character name
	public static boolean isCharacter(String word)  {
		for(String string : characters) 
			if(string.equals(word))
				return true;

		return false;
	}

	//Returns capitalized version of a word
	public static String capitalize(String word) {
		if (word.charAt(0) == '\'') {
			if (word.charAt(1) == '\'') {
				return word.substring(0, 2) + word.substring(2, 3).toUpperCase() + word.substring(3);
			} else {
				return word.substring(0, 1) + word.substring(1, 2).toUpperCase() + word.substring(2);
			}
		} else {
			return word.substring(0, 1).toUpperCase() + word.substring(1);
		}
	}

	//Returns true if sentence starts with one of following question words
	public static boolean isQuestion(String word) {
		word = stripper(word);
		if (word.contains("who") || word.contains("what") || word.equals("why") ||
				word.contains("where") || word.contains("when") || word.equals("how") || 
				word.equals("how's") || word.equals("has") || word.equals("hast") || word.equals("will") ||
				word.equals("is") || word.equals("are") || word.equals("which") || word.equals("dost") ||
				word.equals("have") || word.contains("had") || word.equals("hadst") || word.equals("didst") ||
				word.equals("did") || word.equals("does") || word.equals("was") || word.equals("were"))
			return true;
		return false;
	}

	//Generates corpus list based on corpus.txt
	public static void generateCorpus(){
		corpus = new ArrayList<String>();
		
		try{
			String filename = "./corpus.txt";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String word;
			
			while ((word = br.readLine()) != null) 
				corpus.add(stripper(word));
			
			br.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("File Not Found.");
		} catch (IOException e) {
			System.out.println("IOException.");
		}
		
		//Set global var b/c used so often it is faster as own var
		corpusSize = corpus.size();

		//Write out list as serializable object
		writeList("corpus");
	}

	//Generates vocab list based on corpus
	public static void generateVocab() {
		Set<String> vocabSet = new HashSet<String>();
		for(int i = 0; i < corpus.size(); i++) 
			vocabSet.add(corpus.get(i));			    

		vocab = new ArrayList<String>(vocabSet);
		java.util.Collections.sort(vocab);
		
		//Set global var b/c used so often it is faster as own var
		vocabSize = vocab.size();

		//Write out list as serializable object
		writeList("vocab");
	}

	//Generates characters list based on characters.txt
	public static void generateCharacters() {
		characters = new ArrayList<String>();
		
		try{
			String filename = "./characters.txt";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String word;

			while ((word = br.readLine()) != null) 
				characters.add(stripper(word));
			
			br.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("File Not Found.");
		} catch (IOException e) {
			System.out.println("IOException.");
		}

		//Write out list as serializable object
		writeList("characters");
	}

	//Generates unigram list based on corpus
	public static void generateUnigramCounts() {
		unigramCounts = new ArrayList<Integer>(vocabSize);
		
		//Get count of each word in vocab in the corpus
		for(int i = 0; i < vocabSize; i++) {
			//Prints progress
			if(i%100==0)
				System.out.println(i + " " + (double)i/vocabSize*100 + "% done");

			int count = 0;
			for(int j = 0; j < corpusSize; j++) 	//For each token in corpus
				//Increment count if found in corpus
				if(vocab.get(i).equals(corpus.get(j)))
					count++;

			unigramCounts.add(i, count);
		}

		//Write out list as serializable object
		writeList("unigramCounts");
	}

	//Generates bigram list based on corpus
	public static void generateBigramCounts() {
		bigramCounts = new ArrayList<NgramNode>(vocabSize);
		
		//Get words following each word in vocab in the corpus
		for(int i = 0; i < vocabSize; i++) {
			//Prints progress
			if(i%100==0)
				System.out.println(i + " " + (double)i/vocabSize*100 + "% done");

			//Create node for each word in vocab
			NgramNode node = new NgramNode();
			String vocabWord = vocab.get(i);
			
			for(int j = 0; j < corpusSize - 1; j++) {	//For each token in corpus
				
				//If found vocab word, add following word to its wordsAfter
				if(vocabWord.equals(corpus.get(j))) {
					node.addToWordsAfter(corpus.get(j+1));
				}
			}
			
			bigramCounts.add(i, node);
		}

		//Write out list as serializable object
		writeList("bigramCounts");
	}

	//Generates trigram list based on corpus
	public static void generateTrigramCounts() {
		trigramCounts = new ArrayList<List<NgramNode>>();
		
		//Get words following each pair of words in vocab in the corpus
		for(int i = 0; i < vocabSize; i++) {
			//Print progress
			if(i%100==0)
				System.out.println(i + " " + (double)i/vocabSize*100 + "% done");

			//Get words after vocab word
			List<String> wordsAfter = bigramCounts.get(i).getWordsAfter();
			//Each location in trigramCounts is a list of bigram Nodes
			List<NgramNode> list = new ArrayList<NgramNode>();
			String vocabWord = vocab.get(i);

			//Initialize node for each word after firstWord
			for (int j = 0; j < wordsAfter.size(); j++) 
				list.add(new NgramNode());
			
			for(int j = 0; j < corpusSize - 2; j++) {	//For each token in corpus
				
				//If found vocab word in corpus
				if(vocabWord.equals(corpus.get(j))) {
					
					int k = 0;
					//Get index of second word in wordsAfter
					for (k = 0; k < wordsAfter.size(); k++)  {
						String word2 = wordsAfter.get(k);
						if (word2.equals(corpus.get(j + 1)))
							break;
					}
					
					//Add word after first two words to trigram node 
					NgramNode wordsAfterFirstTwoWordsNode = list.get(k);
					wordsAfterFirstTwoWordsNode.addToWordsAfter(corpus.get(j+2));
				}
			}
			
			trigramCounts.add(list);
		}
		
		//Write out list as serializable object
		writeList("trigramCounts");
	}
	
	//Write out list as serializable object
	public static void writeList(String type) {
		String filename = "./" + type + ".list";
		
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			if (type.equals("corpus"))
				objectOutputStream.writeObject(corpus);
			else if (type.equals("vocab"))
				objectOutputStream.writeObject(vocab);
			else if (type.equals("characters"))
				objectOutputStream.writeObject(characters);
			else if (type.equals("unigramCounts"))
				objectOutputStream.writeObject(unigramCounts);
			else if (type.equals("bigramCounts")) 
				objectOutputStream.writeObject(bigramCounts);
			else if (type.equals("trigramCounts")) 
				objectOutputStream.writeObject(trigramCounts);

			objectOutputStream.close();
			
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		} catch(IOException e) {
			System.out.println("IO exception");
		}
	}

	@SuppressWarnings("unchecked")
	//Gets desired list based on type
	public static void getList(String type) {
		String filename = "./" + type + ".list";
		
		try {
			FileInputStream fileInputStream  = new FileInputStream(filename);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);

			if (type.equals("corpus")) {
				corpus = (ArrayList<String>) objectInputStream.readObject();
				corpusSize = corpus.size();
			} else if (type.equals("vocab")) {
				vocab = (ArrayList<String>) objectInputStream.readObject();
				vocabSize = vocab.size();
			} else if (type.equals("characters"))
				characters = (ArrayList<String>) objectInputStream.readObject();
			else if (type.equals("unigramCounts"))
				unigramCounts = (ArrayList<Integer>) objectInputStream.readObject();
			else if (type.equals("bigramCounts")) 
				bigramCounts = (ArrayList<NgramNode>) objectInputStream.readObject();
			else if (type.equals("trigramCounts")) 
				trigramCounts = (ArrayList<List<NgramNode>>) objectInputStream.readObject();

			objectInputStream.close();
			
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		} catch(IOException e) {	
			System.out.println("IO exception");
		} catch(ClassNotFoundException e) {
			System.out.println("Class not found excpetion");
		}
	}

	//Adds newline to response if preceded by punctuation
	public static String newLine(String word) {
		if (word.contains("!") || word.contains("."))
			return "\n";
		return "";
	}
	
	//Get first 2 words of entire response.
	//Returns true if next word after these two words should be capitalized
	public static boolean getAndPrintFirstAndSecondWords(String line) {
		
		//Split user input into array of strings/words
		String[] words = line.split("\\s+");
		for (int i = 0; i < words.length; i++) 
			words[i] = words[i].replaceAll("[^\\w]", "");
		
		Random rng = new Random();
		double r = rng.nextDouble();
		int index = words.length - 1;
		
		//If not valid text input, just call unigram
		if(index < 0)
			firstWord = unigram(r);
		else 
			firstWord = stripper(words[index]);
		
		//Call bigram with first word as last user input word
		r = rng.nextDouble();
		secondWord = bigram(firstWord, r);

		//Call bigram going from last word input to first as the first word
		//until bigram is not null
		while(secondWord == null && index>=1) {
			index--;
			r = rng.nextDouble();
			firstWord = words[index].toLowerCase();
			secondWord = bigram(firstWord, r);
		}
		
		//If still null, call unigram
		if (secondWord == null  || firstWord.equals("")) {
			firstWord = unigram(r);
			r = rng.nextDouble();
			secondWord = bigram(firstWord, r);
		}

		//Print first word as long as neither word has a period
		if(!firstWord.contains(".") && !secondWord.contains(".")) 
			System.out.print(capitalize(firstWord) + " " + newLine(firstWord));
		

		//Return true if first word has punctuation
		if(firstWord.contains(".") || firstWord.contains("!")) {
			return true;
		} else {
			//Normal case: no punctuation in either word
			if (!secondWord.contains(".")) 
				System.out.print(secondWord + " " + newLine(secondWord));

			//Return true if second word has punctuation
			if(secondWord.contains(".") || secondWord.contains("!")) 
				return true;
		}
		
		//Default/usual case: return false
		return false;
	}
	
	//Get first 2 words of sentence (not of response)
	//Returns true if next word after these two words should be capitalized
	public static boolean getAndPrintFirstAndSecondWordsOfSentence() {
		Random rng = new Random();
		Double r = rng.nextDouble();
		
		//Randomly generate start of sentence
		firstWord = unigram(r);
		r = rng.nextDouble();
		
		//Return true if ! in first word
		if(firstWord.contains("!")) {
			System.out.print(capitalize(firstWord) + '\n');
			r = rng.nextDouble();
			return true;
		} else if (firstWord.contains(".")) {
			//Just return true if . in it
			r = rng.nextDouble();
			return true;
		}

		//Generate second word based on first word
		secondWord = bigram(firstWord, r);
		
		//Return true is ! in second word
		if(secondWord.contains("!")) {
			System.out.print(capitalize(firstWord) + " " + secondWord + '\n');
			r = rng.nextDouble();
			return true;
		} else if (secondWord.contains(".")) {
			//Just return true if . in second word
			r = rng.nextDouble();
			return true;
		}

		//Normal.default case-return false & print first two words that have no
		//puncatuation in them
		System.out.print(capitalize(firstWord) + " " + secondWord + " ");
		return false;
	}
	
	public static void main(String[] args) {
		//Toggle whether want to generate lists or load them
		//Generating each (esp. trigram and bigram) takes about 1 hour
		//Loading each takes about 4 seconds!
		//Each time you hit enter/ after everything loaded: less than 1 second!
		
		generateCorpus();
		//getList("corpus");
		System.out.println("corpus list made");
		generateVocab();
		//getList("vocab");
		System.out.println("vocab list made");
		generateCharacters();
		//getList("characters");
		System.out.println("character list made");
		generateUnigramCounts();
		//getList("unigramCounts");
		System.out.println("unigramCounts list made");
		generateBigramCounts();
		//getList("bigramCounts");
		System.out.println("bigramCounts list made");
		generateTrigramCounts();
		//getList("trigramCounts");
		System.out.println("trigramCounts list made");

		Random rng = new Random();
		Scanner in = new Scanner(System.in);

		System.out.println("\nEnter your message for ShakespeareChatbot: ");
		
		//When user hits enter, their inputted text is input for chatbot
		while (in.hasNextLine()) {
			String line = in.nextLine();
			
			//Get first 2 words
			boolean capitalizeNextWord = getAndPrintFirstAndSecondWords(line);
			boolean question = isQuestion(firstWord);
			
			double r = rng.nextDouble();
			String word;
			boolean end = false;
			int i = 0;
			
			//Loop until at least 20 characters and current sentence ends
			while(!end){
				
				//Loop until first 2 words dont have punctuation
				while (capitalizeNextWord) {
					capitalizeNextWord = getAndPrintFirstAndSecondWordsOfSentence();
					question = isQuestion(firstWord);
				}
				
				r = rng.nextDouble();
				//Get next word via trigram
				word = trigram(firstWord.toLowerCase(), secondWord.toLowerCase(), r);

				//Add ? if punctuation and sentence is a question
				if(word.contains(".") || word.contains("!")) {
					if(question) {
						if(word.contains("."))
							word = word.replace(".", "?");
						if(word.contains("!"))
							word = word.replace("!", "?");
					}
					question = false;
					capitalizeNextWord = true;
				} else {
					capitalizeNextWord = false;
				}
				
				//Set up next loop
				firstWord = secondWord;
				secondWord = word;
				
				//Print word
				System.out.print(word + " ");
				if(capitalizeNextWord) 
					System.out.print('\n');
				
				//End loop if at least 20 words and sentence just ended
				i++;
				if (capitalizeNextWord && i > 20)
					end = true;
			}
			
			System.out.println("\nEnter your message for ShakespeareChatbot: ");
		}
		
		//Free resource (even tho will never be reached)
		in.close();
	}
}

