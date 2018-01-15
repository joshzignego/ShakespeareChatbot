# The node will contain a list of words following
# 1 given word (bigram) or 2 given words (trigram)
# and the counts of the frequencies of each of the following words
class NgramNode:
    def __init__(self):
        self.wordsAfter = list()
        self.wordsAfterCounts = list()

    def addToWordsAfter(self, word):
        #If word already in wordsAfter, increments its count & return
        for i in range(0, len(self.wordsAfter)):
            if self.wordsAfter[i] == word:
                self.wordsAfterCounts[i] = self.wordsAfterCounts[i] + 1
                return

        #If new word, add to words after & initialize its count to 1
        self.wordsAfter.append(word)
        self.wordsAfterCounts.append(1)

    def getWordsAfter(self):
        return self.wordsAfter

    def getWordsAfterCounts(self):
        return self.wordsAfterCounts