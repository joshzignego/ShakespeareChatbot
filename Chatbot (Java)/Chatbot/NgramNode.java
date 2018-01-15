package Chatbot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* The node will contain a list of words following
 * 1 given word (bigram) or 2 given words (trigram)
 * and the counts of the frequencies of each of the following words
 */
public class NgramNode implements Serializable {
	private List<String> wordsAfter;
	private List<Integer> wordsAfterCounts;
	
	public NgramNode() {
		wordsAfter = new ArrayList<String>();
		wordsAfterCounts = new ArrayList<Integer>();
	}
	
	public void addToWordsAfter(String word) {
		//If word already in wordsAfter, increments its count & return
		for(int i=0; i<wordsAfter.size(); i++) {
			if (wordsAfter.get(i).equals(word)) {
				wordsAfterCounts.set(i, wordsAfterCounts.get(i) + 1);
				return;
			}
		}
		
		//If new word, add to words after & initialize its count to 1
		wordsAfter.add(word);
		wordsAfterCounts.add(1);
	}
	
	public List<String> getWordsAfter() {
		return wordsAfter;
	}
	
	public List<Integer> getWordsAfterCounts() {
		return wordsAfterCounts;
	}
}