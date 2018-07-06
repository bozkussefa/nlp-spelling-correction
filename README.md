# Spelling Correction
  
We take an input text of type correction and return a corrected form which is very important in Natural Language Processing (NLP) tasks. For example, "access" is converted into "actres". We are trying to correct typographical errors using Hidden Markov Models (HMMs).
## Getting Started  
The java programming language was used for this project. No additional library was used because this language contained the necessary libraries for this assignment.  

### Command Line Arguments  
To compile the code : javac Main.java  
To run the program : java Main datasetOriginal.txt results.txt  
  
First compile the code. Then run with the arguments given in the example. The first argument requested is the path to the data set's dataset (dataset.txt). The second argument is the name of the file in which the computed values are to be output. (result.txt).
  
- args[0]: the data set file name(datasetOriginal.txt)  
- args[1]: the output file name(result.txt)

## Details  
### Part 1 : Introduction

I read the file in this section. I split it up to the desired level and filled in the LinkedHashMaps as I needed to calculate the probabilities in the Build HMM section. I will describe LinkedHashMaps in more detail in the second part.

### Part 2 : HMMs Models  

We will use HMMs to correct the spelling in this assignment. There are hidden and observed situations in the HMM. In this experiment, the hidden situations will be misspelled words and the observed situations will be the correct forms.     
-In this section, I had to calculate 3 probabilities (Iinitial, Transition, Emission). I used 4 LinkedHashMaps (BigramForIntial,BigramForTransition,BigramWord, wrCrWord) to do these calculations.       
-BigramForIntitial Map I hold the first words and calculating initial probabilities.        
-I keep the words in BigramForTransition Map's teats and calculating the transition probabilities.  
-I keep the BigramWord Map words together with the frequencies one by one.  
-wrcrWord Map 'te words and correct cases; I keep it with the possibilities of emission.
  
### Part 2 : Viterbi

Our viterbi algorithm will search for all possible cues and search for the best possible form of the misspelled word and create a new sentence.

  
### Part 3 : Evaluation
I use smoothed and unsmoothed unigram,bigram and trigram to create texts.  In trigram more time and memory consuming but the sentences are more reasonable and logically correct. So in real test i saw and showed the reliability trigram > bigram > unigram  but in the performance view that is the opposite.  
  
### Output File Content

There are false words selected from false cues and new words created by correct states of these words. And finally the Evolution result (how many words are correctly guessed) is printed on the file.          

