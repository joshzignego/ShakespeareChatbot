# ShakespeareChatbot
A chatbot that takes text input and responds as The Bard would.
Available as a java app or via Facebook Messenger through the following page: https://www.facebook.com/Shakespeare-Chatbot-183605505568422/

# Process
1. Download Shakespeare's plays from http://lexically.net/wordsmith/support/shakespeare.html and his poems (incl. the sonnets) from http://www.gutenberg.org/browse/authors/s 
2. Rename these text files as 1.txt 2.txt ... 40.txt
3. Run PlayParser on the files
4. scp these files into Linux and run the following tokenizer command on the directory that breaks up the corpus into words: gawk '{for (i=1;i<=NF;i++) print $i}' \*.txt > corpus.txt
5. Download corpus.txt
6. Put all dramatis personae into a directory, rename 1.txt ... 37.txt for each play
7. Pass these into CharacterListMaker to make characters.txt

# To run as java application
8. Simply load corpus.txt and characters.txt into same directory as Chatbot. Run generatelist()'s the first time to create .list files and getlist()'s subsequent times to load them. generatelist()'s stores the trained data structures into ".list" files that are serializable so that the getlist()'s simply retieve these serializable java data structures from these files in future uses.

    The app takes ~1 hour to train the chatbot when using generatelist()'s.
    
    The app takes ~4 seconds when using getlist()'s.
    
    The app takes <1 second for every message after the first message!
# To make Facebook messenger chatbot
8. Create AWS account
9. Create AWS API Gateway
10. Create AWS Lambda function
11. Create lambda_handler function and download Python's Request API into project
12. Upload zip of code to Lambda function
13. Create app on Facebook and connect to Lambda webhook
14. Get app reviewed successfully for public use!

The lambda_function.py code skeleton as well as a guide to connecting a Messnger Bot through AWS was provided by Adam Spannbauer at https://adamspannbauer.github.io/worklifebalance.github.io/post/2017-04-09-tutorial-facebook-messenger-chatbot-with-aws-lambda-python/
The entire requests folder in the python code is from Python's Requests API and is not my own!
