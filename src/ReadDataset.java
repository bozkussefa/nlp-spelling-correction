import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadDataset {

    //read dataset and clean dataset use regex

    public void reader(ArrayList<String> correctDataset, HashMap<String, Integer> sentenceHashMap, HashMap<String, HashMap<String, Distance>> errorWordHashMap, ArrayList<String> incorrectDataset, String[] args) {

        String csvFile = args[0];
        BufferedReader br = null;
        String line;
        String cvsSplitBy = "//n";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] body = line.split(cvsSplitBy);
                if (line.trim().length() > 0) {
                    incorrectDataset.add(line);
                    Pattern pattern = Pattern.compile("<ERR targ=(.*?)</ERR>");
                    Matcher matcher = pattern.matcher(body[0]);
                    while (matcher.find()) {
                        String[] words = matcher.group(1).split("> ");
                        body[0] = body[0].replaceFirst("<ERR targ=(.*?)</ERR>", words[0]);
                        createErrorWordMap(words[0], words[1].trim(), errorWordHashMap);
                    }
                    correctDataset.add(body[0].replaceAll("( ?\\p{P}+ |\\p{P}+$|^\\p{P})", " ").replaceAll("\' ", "").trim());
                    String sentence = "<s> " + body[0].replaceAll("( ?\\p{P}+ |\\p{P}+$|^\\p{P})", " ").replaceAll("\' ", "").trim() + " </s>";
                    if (!sentenceHashMap.containsKey(sentence)) {
                        sentenceHashMap.put(sentence, 1);
                    } else {
                        int count = sentenceHashMap.get(sentence);
                        sentenceHashMap.put(sentence, count + 1);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // create errorwords map
    private void createErrorWordMap(String word1, String word2, HashMap<String, HashMap<String, Distance>> errorWordHashMap) {

        HashMap<String, Distance> innerMap = new HashMap<String, Distance>();
        Distance P = new Distance();
        int distance = P.distance(word1.toCharArray(), word2.toCharArray());
        String[] difference;
        if (distance == 1) {
            if (word2.length() > word1.length())
                P.distanceType = "insertion";
            if (word2.length() < word1.length())
                P.distanceType = "deletion";
            if (word2.length() == word1.length())
                P.distanceType = "replace";

            P.minimumEditDistance = distance;
            P.errorWord = word2;
            if (P.distanceType.equals("insertion")) {
                difference = difff(word1, word2, P.distanceType);
                P.correctLetter = difference[1].toCharArray();
                P.errorLetter = difference[0].toCharArray();
            }
            if (P.distanceType.equals("deletion")) {
                difff(word2, word1, P.distanceType);
                difference = difff(word2, word1, P.distanceType);
                P.correctLetter = difference[1].toCharArray();
                P.errorLetter = difference[0].toCharArray();
            }
            if (P.distanceType.equals("replace")) {
                difff(word2, word1, P.distanceType);
                difference = difff(word2, word1, P.distanceType);
                P.correctLetter = difference[1].toCharArray();
                P.errorLetter = difference[0].toCharArray();
            }

            if (!errorWordHashMap.containsKey(word1)) {
                P.frequency = 1;
                innerMap.put(word2, P);
                errorWordHashMap.put(word1, innerMap);
            } else {
                if (!errorWordHashMap.get(word1).containsKey(word2)) {
                    innerMap = errorWordHashMap.get(word1);
                    innerMap.put(word2, P);
                    errorWordHashMap.put(word1, innerMap);
                } else {
                    int count = errorWordHashMap.get(word1).get(word2).frequency;
                    innerMap = errorWordHashMap.get(word1);
                    P.frequency = count + 1;
                    innerMap.put(word2, P);
                    errorWordHashMap.put(word1, innerMap);
                }

            }

        }
    }

    // calculate error letter and correct letter

    private static String[] difff(String word1, String word2, String type) {

        if (type.equals("insertion")) {
            char[] word1_char = word1.toCharArray();
            char[] word2_char = word2.toCharArray();

            int count = 0;
            String[] returnTypes = type(word2, word1_char, word2_char, count);
            word1 = returnTypes[1];
            word2 = returnTypes[0];
        } else if (type.equals("deletion")) {
            char[] word1_char = word1.toCharArray();
            char[] word2_char = word2.toCharArray();

            int count = 0;
            String[] returnTypes = type(word2, word1_char, word2_char, count);
            word1 = returnTypes[0];
            word2 = returnTypes[1];
        } else {
            char[] word1_char = word1.toCharArray();
            char[] word2_char = word2.toCharArray();
            int count = 0;
            for (int i = 0; i < word2_char.length; i++) {
                for (int j = count; j < word1_char.length; j++) {
                    if (word2_char[i] == word1_char[j]) {
                        word2 = word2.replaceFirst(String.valueOf(word2_char[j]), "->");
                        word1 = word1.replaceFirst(String.valueOf(word1_char[i]), "->");

                    }
                    count++;
                    break;
                }
            }
            word1 = word1.replaceAll("->", "");
            word2 = word2.replaceAll("->", "");


        }


        return new String[]{word1, word2};
    }

    //calculate minimum edit distance type as insertion,deletion and replace

    private static String[] type(String word2, char[] word1_char, char[] word2_char, int count) {
        char[] word2Array, word22Array;
        word2Array = word2.toCharArray();
        word22Array = word2.toCharArray();
        for (int i = 0; i < word2_char.length; i++) {
            for (int j = count; j < count + 1; j++) {
                if (word2_char[i] == word1_char[j]) {
                    word22Array[i] = '-';
                    count++;
                    break;
                }
            }
            if (count >= word1_char.length)
                break;
        }
        int temp = 0;

        for (int i = 0; i < word2.toCharArray().length; i++) {
            if (word22Array[i] != '-') {
                temp = i;
                break;
            }

        }
        String word1;

        if (temp == 0) {
            word1 = String.valueOf(word1_char[0]);
            word2 = word2_char[0] + "" + word2_char[1];
            return new String[]{word1, word2};
        }


        word1 = String.valueOf(word2Array[temp - 1]);
        word2 = String.valueOf(word2Array[temp - 1]) + word2Array[temp];
        return new String[]{word1, word2};


    }
}