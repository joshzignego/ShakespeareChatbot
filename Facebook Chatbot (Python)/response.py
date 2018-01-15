import re
import random
import pickle
from ngramnode import NgramNode

#the corpus, the vocabulary, and the list of characters
vocab = list()
vocabSize = 0
corpus = list()
corpusSize = 0
characters = list()

#First & second words of a sentence generated via unigram & bigram
firstWord = ""
secondWord = ""

#the response to be returned/sent to the user
response = ""

#Counts of each uni/bi/trigram sequence of words
unigramCounts = list()
bigramCounts = list()
trigramCounts = list()

#Generate first word of sentence via unigram
def unigram(randomUnigram):
    global unigramCounts, vocab, vocabSize, corpusSize

    prob = 0.0
    #For each word type
    for i in range(0, vocabSize):
        prob += unigramCounts[i] / float(corpusSize)

        #Return word if prob larger than random now
        if randomUnigram <= prob and unigramCounts[i] > 0:
            return vocab[i]

    #Error message- should never happen
    return "Shakespeare bot doesn't know what to say UNIGRAM"

#Generate first and second words of sentence via bigram
def bigram(firstWordBigram, randomBigram):
    global vocab, vocabSize, unigramCounts, bigramCounts
    prob = 0.0

    #Get vocab index of firstWord
    i = 0
    for l in range(0, vocabSize):
        if firstWordBigram == vocab[l]:
            break
        i += 1

    #If not in vocab: return null
    if i == vocabSize or "." in firstWordBigram or "!" in firstWordBigram:
        return None

    #Get count of firstWord in corpus and words after it & their counts
    count_firstWord = unigramCounts[i]
    node = bigramCounts[i]
    wordsAfter = node.getWordsAfter()
    wordsAfterCounts = node.getWordsAfterCounts()

    #For each word in words after the first word
    for j in range(0, len(wordsAfter)):
        count = wordsAfterCounts[j]
        prob += count/ float(count_firstWord)

        #Return word if prob larger than random now
        if randomBigram <= prob:
            word = wordsAfter[j]

            #Capitalize word if a character name
            if isCharacter(word.strip().replace("[^\\w]+", "").lower()):
                return capitalize(word)
            return word

    #Error message- should never happen
    return "Shakespeare bot doesn't know what to say BIGRAM"

#Generate new word based on previous two via trigram
def trigram(firstWordTrigram, secondWordTrigram, randomTrigram):
    global corpus, corpusSize, bigramCounts, trigramCounts

    prob = 0.0
    countOfFirstWordThenSecondWord = 0

    #Get total count of first 2 words together
    for i in range(0, corpusSize - 1):
        if firstWordTrigram == corpus[i] and secondWordTrigram == corpus[i + 1]:
            countOfFirstWordThenSecondWord += 1


    #Get index of first word in vocab
    firstWordIndex = 0
    for i in range(0, vocabSize):
        if firstWordTrigram == vocab[i]:
            firstWordIndex = i
            break

    #Find index of second word in wordsAfter for first word
    bigramNode = bigramCounts[firstWordIndex]
    wordsAfterBigram = bigramNode.getWordsAfter()

    secondWordIndex = 0
    for i in range(0, len(wordsAfterBigram)):
        if secondWordTrigram == wordsAfterBigram[i]:
            secondWordIndex = i

    #Get count of first 2 words together in corpus and words after them & their counts
    node = trigramCounts[firstWordIndex][secondWordIndex]
    wordsAfter = node.getWordsAfter()
    wordsAfterCounts = node.getWordsAfterCounts()

    #For each word in words after the first and second words together
    for i in range(0, len(wordsAfter)):
        count = wordsAfterCounts[i]
        prob += count/ float(countOfFirstWordThenSecondWord)

        #Return word if prob larger than random now
        if randomTrigram <= prob:
            word = wordsAfter[i]

            #Capitalize word if a character name
            if isCharacter(word.strip().replace("[^\\w]+", "").lower()):
                return capitalize(word)
            return word

    #Error message- should never happen
    return "Shakespeare bot doesn't know what to say TRIGRAM"

#Remove all extra characters from a word, make lower case
def stripper(string):
    charsToKeep= re.compile('[^-a-zA-Z0-9.!\']|_')
    return re.sub(charsToKeep, '', string.strip().replace("\"", "").replace('\x00', '').lower())

#Returns true if word is a character name
def isCharacter(word):
    for string in characters:
        if string == word:
            return True
    return False

#Returns capitalized version of a word
def capitalize(word):
    if word[0] == '\'':
        if len(word) > 1:
            if word[1] == '\'':
                if len(word) > 2:
                    return word[:2] + word[2].upper() + word[3:]
                return word[:2] + word[2].upper()
        if len(word) > 2:
            return word[0] + word[1].upper() + word[2:]
        return word[0] + word[1].upper()
    else:
        if len(word) >= 2:
            return word[0].upper() + word[1:]
        return word[0].upper()

#Returns true if sentence starts with one of following question words
def isQuestion(word):
    word = stripper(word)
    if "who" in word or "what" in word or "why" == word or "where" in word or "when" in word or \
            "how" == word or "how's" == word or "has" == word or "hast" == word or "will" == word or \
            "is" == word or "are" == word or "which" == word or "dost" == word or "have" == word or\
            "had" in word or "hadst" == word or "didst" == word or "did" == word or "does" == word or \
            "was" == word or "were" == word:
        return True
    return False

#Generates corpus list based on corpus.txt
def generateCorpus():
    global corpus, corpusSize

    corpus = list()

    try:
        filename = "./corpus.txt"
        with open(filename, "rt") as f:
            for word in f:
                corpus.append(stripper(word))
            f.close()

    except OSError:
        print("File Not Found." + '\n')
    except IOError:
        print("IOError." + '\n')

    #Set global var b/c used so often it is faster as own var
    corpusSize = len(corpus)

    #Write out list as pickled object
    writeList("corpus")

#Generates vocab list based on corpus
def generateVocab():
    global corpus, corpusSize, vocab, vocabSize

    vocabSet = set()

    for i in corpus:
        vocabSet.add(i)

    vocab = list(vocabSet)
    vocab.sort()

    #Set global var b/c used so often it is faster as own var
    vocabSize = len(vocab)

    # Write out list as pickled object
    writeList("vocab")

#Generates characters list based on characters.txt
def generateCharacters():
    global characters

    characters = list()

    try:
        filename = "./characters.txt"
        with open(filename, "rb") as f:
            for word in f:
                characters.append(stripper(word))
            f.close()
    except OSError:
        print("File Not Found." + '\n')
    except IOError:
        print("IOException." + '\n')

    # Write out list as pickled object
    writeList("characters")

#Generates unigram list based on corpus
def generateUnigramCounts():
    global unigramCounts, vocab, vocabSize, corpus, corpusSize

    unigramCounts = list()

    #Get count of each word in vocab in the corpus
    index = 0
    for vocabWord in vocab:

        # Prints progress
        index+=1.0
        if index % 100 == 0:
            percent = index / vocabSize * 100.0
            print(index, " ", percent, "% done")

        count = 0
        for corpusWord in corpus: #For each token in corpus

            #Increment count if found in corpus
            if vocabWord == corpusWord:
                count += 1

        unigramCounts.append(count)

    # Write out list as pickled object
    writeList("unigramCounts")

#Generates bigram list based on corpus
def generateBigramCounts():
    global bigramCounts, vocab, vocabSize, corpus, corpusSize

    bigramCounts = list()

    #Get words following each word in vocab in the corpus
    spot = 0.0
    for i in range(0, vocabSize):

        #Prints progress
        spot +=1
        if spot % 100 == 0:
            print(spot, " " , spot / vocabSize * 100.0 , "% done")

        #If punctuation in word, add none in its spot in bigramCounts
        vocabWord = vocab[i]
        if "." in vocabWord or "!" in vocabWord:
            bigramCounts.append(None)
            continue

        #Create node for each word in vocab
        node = NgramNode()
        for j in range(0, corpusSize -1): #For each token in corpus

            #If found vocab word, add following word to its wordsAfter
            if vocabWord == corpus[j]:
                node.addToWordsAfter(corpus[j+1])

        bigramCounts.append(node)

    # Write out list as pickled object
    writeList("bigramCounts")

#Generates trigram list based on corpus
def generateTrigramCounts():
    global trigramCounts, vocab, vocabSize, corpus, corpusSize

    trigramCounts = list()

    #Get words following each pair of words in vocab in the corpus
    vocabIndex = 0.0
    for i in range(0, vocabSize):

        #Prints progress
        vocabIndex += 1
        if vocabIndex % 100 == 0:
            print(vocabIndex, " ", vocabIndex / vocabSize * 100.0, "% done")

        vocabWord = vocab[i]
        #If punctutation in word, add None at its spot in trigramCounts
        if "." in vocabWord or "!" in vocabWord:
            trigramCounts.append(None)
            continue

        #Get words after vocab word
        wordsAfter = bigramCounts[i].getWordsAfter()
        #Each location in trigramCounts is a list of bigram Nodes
        bigramsList = list()

        #Initialize node for each word after firstWord
        for j in range(0, len(wordsAfter)):
            bigramsList.append(NgramNode())

        for j in range(0, corpusSize - 2): #For each token in corpus

            #If found vocab word in corpus
            if vocabWord == corpus[j]:
                index = 0

                #Get index of second word in wordsAfter
                for k in range(0, len(wordsAfter)):
                    word2 = wordsAfter[k]
                    if word2 == corpus[j + 1]:
                        break
                    index += 1

                #add none instead if punctuation in second word
                if "." in corpus[j + 1] or "!" in corpus[j + 1] or "?" in corpus[j + 1]:
                    continue

                #Add word after first two words to trigram node
                else:
                    wordsAfterFirstTwoWordsNode = bigramsList[index]
                    wordsAfterFirstTwoWordsNode.addToWordsAfter(corpus[j + 2])


        trigramCounts.append(bigramsList)

    # Write out list as pickled object
    writeList("trigramCounts")

#Write out list as pickled object
def writeList(category):
    filename = "./" + category + ".list"

    try:
        with open(filename, "w+") as f:  #Pickling
            if category == "corpus":
                pickle.dump(corpus, f)
            elif category == "vocab":
                pickle.dump(vocab, f)
            elif category == "characters":
                pickle.dump(characters, f)
            elif category == "unigramCounts":
                pickle.dump(unigramCounts, f)
            elif category == "bigramCounts":
                pickle.dump(bigramCounts, f)
            elif category == "trigramCounts":
                pickle.dump(trigramCounts, f)
            f.close()

    except OSError:
        print("File not found")
    except IOError:
        print("IO exception")

#Gets desired list based on type
def getList(category):
    global corpus, corpusSize, vocab, vocabSize, characters, unigramCounts, bigramCounts, trigramCounts

    filename = "./" + category + ".list"

    try:
        with open(filename, "rb") as f:  # Unpickling
            if category == "corpus":
                corpus = pickle.load(f)
                corpusSize = len(corpus)
            elif category == "vocab":
                vocab = pickle.load(f)
                vocabSize = len(vocab)
            elif category == "characters":
                characters =  pickle.load(f)
            elif category == "unigramCounts":
                unigramCounts  = pickle.load(f)
            elif category == "bigramCounts":
                bigramCounts = pickle.load(f)
            elif category == "trigramCounts":
                trigramCounts = pickle.load(f)
            f.close()

    except OSError:
        print("File not found")
    except IOError:
        print("IO exception")

#Adds newline to response if preceded by punctuation
def newLine(word):
    if "!" in word or "." in word:
        return "\n"
    return ""

#Get first 2 words of entire response.
#Returns true if next word after these two words should be capitalized
def getAndPrintFirstAndSecondWords(userMessage):
    global firstWord, secondWord, response

    #Split user input into array of strings/words
    words = re.sub("[^\w]", " ", userMessage).split()
    r = random.random()
    index = len(words) - 1

    #If not valid text input, just call unigram
    if index < 0:
        firstWord = unigram(r)
    else:
        firstWord = stripper(words[index])

    #Call bigram with first word as last user input word
    r = random.random()
    secondWord = bigram(firstWord, r)

    #Call bigram going from last word input to first as the first word
	#until bigram is not none
    while secondWord is None and index >= 1:
        index -= 1
        r = random.random()
        firstWord = words[index].lower()
        secondWord = bigram(firstWord, r)

    #If still none, call unigram
    if secondWord is None or firstWord == "":
        firstWord = unigram(r)
        r = random.random()
        secondWord = bigram(firstWord, r)

    #add first word to response as long as neither word has a period
    if "." not in firstWord and "." not in secondWord:
        response += capitalize(firstWord) + " " + newLine(firstWord)

    #Return true if first word has punctuation
    if "." in firstWord or "!" in firstWord:
        return True
    else:
        #Normal case: no punctuation in either word
        if "." not in secondWord:
            response += secondWord + " " + newLine(secondWord)

        #Return true if second word has punctuation
        if "." in secondWord or "!" in secondWord:
            return True

    #Default/usual case: return false
    return False

#Get first 2 words of sentence (not of response)
#Returns true if next word after these two words should be capitalized
def getAndPrintFirstAndSecondWordsOfSentence():
    global firstWord, secondWord, response

    #Randomly generate start of sentence
    r = random.random()
    firstWord = unigram(r)
    r = random.random()

    #Return true if ! in first word
    if "!" in firstWord:
        response += capitalize(firstWord) + '\n'
        return True

    #Just return true if . in it
    elif "." in firstWord:
        return True

    #Generate second word based on first word
    secondWord = bigram(firstWord, r)

    #Return true is ! in second word
    if "!" in secondWord:
        response += capitalize(firstWord) + " " + secondWord + '\n'
        return True
    #Just return true if . in second word
    elif "." in secondWord:
        return True

    #Normal.default case-return false & add first two words that have no
    #puncatuation in them to response
    response += capitalize(firstWord) + " " + secondWord + " "
    return False

def generateResponse(message):
    global firstWord, secondWord, corpusSize, response, corpus

    #generateCorpus()
    getList("corpus")
    print("corpus list made")
    #generateVocab()
    getList("vocab")
    print("vocab list made")
    #generateCharacters()
    getList("characters")
    print("character list made")
    #generateUnigramCounts()
    getList("unigramCounts")
    print("unigramCounts list made")
    #generateBigramCounts()
    getList("bigramCounts")
    print("bigramCounts list made")
    #generateTrigramCounts()
    getList("trigramCounts")
    print("trigramCounts list made")

    #Get first 2 words
    response = ""
    capitalizeNextWord = getAndPrintFirstAndSecondWords(message)
    question = isQuestion(firstWord)

    end = False
    i = 0

    #/Loop until at least 20 characters and current sentence ends
    while not end:

        #Loop until first 2 words dont have punctuation
        while capitalizeNextWord:
            capitalizeNextWord = getAndPrintFirstAndSecondWordsOfSentence()
            question = isQuestion(firstWord)

        #Get next word via trigram
        r = random.random()
        word = trigram(firstWord.lower(), secondWord.lower(), r)

        #Add ? if punctuation and sentence is a question
        if "." in word or "!" in word:
            if question:
                if "." in word:
                    word = word.replace(".", "?")
                if "!" in word:
                    word = word.replace("!", "?")

            question = False
            capitalizeNextWord = True
        else:
            capitalizeNextWord = False

        #Set up next loop
        firstWord = secondWord
        secondWord = word

        #Add word to repsonse string
        response += word + " "
        if capitalizeNextWord:
            response += '\n'

        #End loop if at least 20 words and sentence just ended
        i += 1
        if capitalizeNextWord and i > 10:
            end = True

    return response
