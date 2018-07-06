import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processes {

    //create initial , transation, and unigram probablity hashmap

    public HashMap<String, Transition> createBiGromModel(HashMap<String, Integer> sentenceHashMap, HashMap<String, Integer> bigramHashMap, HashMap<String, Initial> initialHashMap, HashMap<String, Transition> transitionHashMap, HashMap<String, Integer> unigramHashMap) {


        for (Map.Entry<String, Integer> entry : sentenceHashMap.entrySet()) {
            String[] tokens = entry.getKey().split(" ");
            for (int i = 0; i < tokens.length; i++) {
                if (unigramHashMap.containsKey(tokens[i])) {
                    unigramHashMap.put(tokens[i], unigramHashMap.get(tokens[i]) + 1);
                } else
                    unigramHashMap.put(tokens[i], 1);
            }

        }

        for (Map.Entry<String, Integer> entry : sentenceHashMap.entrySet()) {
            String[] tokens = entry.getKey().split(" ");
            for (int i = 0; i < entry.getValue(); i++) {
                for (int j = 0; j < tokens.length - 1; j++) {
                    if (!bigramHashMap.containsKey(tokens[j] + " " + tokens[j + 1])) {  // first time we've seen this string
                        bigramHashMap.put(tokens[j] + " " + tokens[j + 1], 1);
                    } else {
                        int count = bigramHashMap.get(tokens[j] + " " + tokens[j + 1]);
                        bigramHashMap.put(tokens[j] + " " + tokens[j + 1], count + 1);
                    }
                }
            }
        }


        for (Map.Entry<String, Integer> entry : sentenceHashMap.entrySet()) {
            Initial initial = new Initial();
            String[] tokens = entry.getKey().split(" ");
            for (int i = 0; i < entry.getValue(); i++) {
                for (int j = 0; j < 1; j++) {
                    if (!initialHashMap.containsKey(tokens[j] + " " + tokens[j + 1])) {  // first time we've seen this string
                        initial.frekans = 1;
                        initial.probablity = 0.00;
                        initialHashMap.put(tokens[j] + " " + tokens[j + 1], initial);
                    } else {
                        initial.frekans = initialHashMap.get(tokens[j] + " " + tokens[j + 1]).frekans + 1;
                        initial.probablity = 0.00;
                        initialHashMap.put(tokens[j] + " " + tokens[j + 1], initial);
                    }
                }
            }
        }

        Initial initial = new Initial();
        initialHashMap = initial.initialProbablity(initialHashMap);


        for (Map.Entry<String, Integer> entry : sentenceHashMap.entrySet()) {
            Transition transition = new Transition();
            String[] tokens = entry.getKey().split(" ");
            for (int i = 0; i < entry.getValue(); i++) {
                for (int j = 2; j < tokens.length - 1; j++) {
                    if (!transitionHashMap.containsKey(tokens[j] + " " + tokens[j + 1])) {  // first time we've seen this string
                        transition.frekans = 1;
                        transition.probablity = 0.0;
                        transitionHashMap.put(tokens[j] + " " + tokens[j + 1], transition);
                    } else {

                        transition.frekans = transitionHashMap.get(tokens[j] + " " + tokens[j + 1]).frekans + 1;
                        transition.probablity = 0.0;
                        transitionHashMap.put(tokens[j] + " " + tokens[j + 1], transition);
                    }
                }
            }
        }

        Transition transation = new Transition();
        transitionHashMap = transation.transitionProbablity(transitionHashMap, unigramHashMap);

        System.out.println("");

        return transitionHashMap;


    }

    //calculate emission prabablities for each viterbi sentences

    public void createEmissionMap(HashMap<String, HashMap<String, Distance>> errorWordHashMap, HashMap<String, Integer> emissionMap) {


        for (Map.Entry<String, HashMap<String, Distance>> entry : errorWordHashMap.entrySet()) {
            HashMap<String, Distance> innerMap = entry.getValue();
            for (Map.Entry<String, Distance> entry2 : innerMap.entrySet()) {
                String errorLetter = String.valueOf(entry2.getValue().errorLetter);
                String correctLetter = String.valueOf(entry2.getValue().correctLetter);
                if (!emissionMap.containsKey(correctLetter + "->" + errorLetter)) {  // first time we've seen this string
                    emissionMap.put(correctLetter + "->" + errorLetter, 1);
                } else {
                    int count = emissionMap.get(correctLetter + "->" + errorLetter);
                    emissionMap.put(correctLetter + "->" + errorLetter, count + 1);
                }

            }
        }
    }

    // calcualte viterbi algorithm will result in sentences

    public HashMap<String, Integer> calculateViterbi(HashMap<String, HashMap<String, Distance>> errorWordHashMap, String s, HashMap<String, Integer> viterbiCombinesMap) {

        Pattern pattern = Pattern.compile("<ERR targ=(.*?)</ERR>");
        Matcher matcher = pattern.matcher(s);

        int abc = -1;


        while (matcher.find()) {
            ArrayList<String> temp = new ArrayList<>();
            String[] words = matcher.group(1).split("> ");
            ArrayList frekansList = calcualteFrekans(words[1], errorWordHashMap);

            if (frekansList.size() == 0 && abc == -1) {
                viterbiCombinesMap.put(s.replaceFirst("<ERR targ=(.*?)>", "<ERR1 targ=" + words[1] + ">"), 1);
            }

            if (frekansList.size() == 0 && abc != -1) {
                HashMap<String, Integer> viterbiCombinesMap2 = new HashMap<>(viterbiCombinesMap);
                for (Map.Entry<String, Integer> entry : viterbiCombinesMap2.entrySet()) {
                    temp.add(entry.getKey());
                    viterbiCombinesMap.put(entry.getKey().replaceFirst("<ERR targ=(.*?)>", "<ERR1 targ=" + words[1] + ">"), 1);

                }
            }


            for (int i = 0; i < frekansList.size(); i++) {
                if (abc == -1) {
                    viterbiCombinesMap.put(s.replaceFirst("<ERR targ=(.*?)>", "<ERR1 targ=" + frekansList.get(i) + ">"), 1);
                } else {
                    HashMap<String, Integer> viterbiCombinesMap2 = new HashMap<>(viterbiCombinesMap);
                    for (Map.Entry<String, Integer> entry : viterbiCombinesMap2.entrySet()) {
                        temp.add(entry.getKey());
                        viterbiCombinesMap.put(entry.getKey().replaceFirst("<ERR targ=(.*?)>", "<ERR1 targ=" + frekansList.get(i) + ">"), 1);
                    }
                }
            }
            abc++;
            HashMap<String, Integer> viterbiCombinesMap2 = new HashMap<>(viterbiCombinesMap);

            for (int i = 0; i < temp.size(); i++) {
                for (Map.Entry<String, Integer> entry : viterbiCombinesMap.entrySet()) {
                    if (entry.getKey().equals(temp.get(i))) {
                        viterbiCombinesMap2.remove(temp.get(i));
                    }
                }
            }
            viterbiCombinesMap = viterbiCombinesMap2;

        }


        return viterbiCombinesMap;
    }

    // calculate which word as errorWord > correctWord

    public ArrayList<String> calcualteFrekans(String word, HashMap<String, HashMap<String, Distance>> errorWordHashMap) {
        ArrayList<String> frekansList = new ArrayList<>();


        for (Map.Entry<String, HashMap<String, Distance>> entry : errorWordHashMap.entrySet()) {
            HashMap<String, Distance> innerMap = entry.getValue();
            for (Map.Entry<String, Distance> entry2 : innerMap.entrySet()) {
                if (entry2.getValue().errorWord.equals(word.trim()))
                    frekansList.add(entry.getKey());

            }
        }

        return frekansList;
    }

    //calculate accurrency for each viterbi sentences , use initial probablity and transation probablity

    public String[] calculateAccurrency(HashMap<String, Integer> viterbiCombinesMap, HashMap<String, Initial> initialHashMap, HashMap<String, Transition> transitionHashMap, HashMap<String, Integer> emissionMap, HashMap<String, HashMap<String, Distance>> errorWordHashMap, ArrayList<String> correctDataset, HashMap<String, Integer> unigramHashMap) {

        HashMap<String, Double> tempMap = new HashMap<>();

        Pattern pattern = Pattern.compile("<ERR1 targ=(.*?)</ERR>");

        for (Map.Entry<String, Integer> entry : viterbiCombinesMap.entrySet()) {
            double tempEmission = 1.0;
            String temp = entry.getKey();
            Matcher matcher = pattern.matcher(entry.getKey());
            while (matcher.find()) {

                String[] words = matcher.group(1).split("> ");
                temp = temp.replaceFirst("<ERR1 targ=(.*?)</ERR>", "<SEFA targ=" + words[0] + "</SEFA>");

                tempEmission *= calculateEmission(words[0], words[1].trim(), errorWordHashMap, emissionMap, correctDataset);


            }
            tempMap.put(temp, tempEmission);


        }
        HashMap<String, Double> lastAccMap = new HashMap<>(tempMap);


        for (Map.Entry<String, Double> entry : lastAccMap.entrySet()) {
            if (entry.getKey().contains("<ERR>")) {
                tempMap.remove(entry.getKey());
            }

        }


        tempMap = calculateFinalAccur(tempMap, initialHashMap, transitionHashMap, unigramHashMap);

        String lastSentence = null;
        Double maxAcc = 0.0;

        for (Map.Entry<String, Double> last : tempMap.entrySet()) {
            if (last.getValue() >= maxAcc) {
                lastSentence = last.getKey();
                maxAcc = last.getValue();
            }

        }

        String[] A = new String[]{lastSentence, String.valueOf(maxAcc)};

        return A;
    }

    //calculate emiission probablity

    public double calculateEmission(String correct, String incorrect, HashMap<String, HashMap<String, Distance>> errorWordHashMap, HashMap<String, Integer> emissionMap, ArrayList<String> correctDataset) {

        String temp = null;
        double tempFrekans = 0.0;
        for (Map.Entry<String, HashMap<String, Distance>> entry : errorWordHashMap.entrySet()) {
            if (entry.getKey().equals(correct)) {
                HashMap<String, Distance> innerMap = entry.getValue();
                for (Map.Entry<String, Distance> entry2 : innerMap.entrySet()) {
                    if (entry2.getKey().equals(incorrect)) {
                        temp = String.valueOf(entry2.getValue().correctLetter) + "->" + String.valueOf(entry2.getValue().errorLetter);
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : emissionMap.entrySet()) {
            if (entry.getKey().equals(temp)) {
                tempFrekans = entry.getValue();
                break;
            }
        }


        /**
         * TODO:is-Zi kaç kez hesapla ona böl
         */

        double s = (double) computeTotalEmission(correctDataset, temp);

        if (s == 0)
            return 1;
        else
            return tempFrekans / s;

    }

    // caclculate how many "xx" repeats in the whole dataset

    public int computeTotalEmission(ArrayList<String> correctData, String Wi) {
        String[] token = String.valueOf(Wi).split("->");
        int start = 0;
        int finish = 0;
        int result;

        if (token[0] == "null") {
            return 0;
        }

        for (int i = 0; i < correctData.size(); i++) {
            start += correctData.get(i).toCharArray().length;
            String temop = correctData.get(i).replaceAll(token[0], "");
            finish += temop.toCharArray().length;
        }


        result = (start - finish) / token[0].toCharArray().length;

        return result;
    }


    public HashMap<String, Double> calculateFinalAccur(HashMap<String, Double> tempMap, HashMap<String, Initial> initialHashMap, HashMap<String, Transition> transitionHashMap, HashMap<String, Integer> unigramHashMap) {

        double temp;
        HashMap<String, Double> lastMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : tempMap.entrySet()) {

            Pattern pattern = Pattern.compile("<SEFA targ=(.*?)</SEFA>");
            Matcher matcher = pattern.matcher(entry.getKey());
            String temp1 = entry.getKey();
            while (matcher.find()) {
                String[] words = matcher.group(1).split(" ");
                temp1 = temp1.replaceFirst("<SEFA targ=(.*?)</SEFA>", words[0]);
            }


            String[] tokens = temp1.split(" ");
            temp = entry.getValue();
            for (Map.Entry<String, Initial> entry2 : initialHashMap.entrySet()) {
                Initial innerMap = entry2.getValue();
                if (entry2.getKey().equals("<s> " + tokens[0])) {
                    temp *= innerMap.probablity;
                    break;
                }

            }

            for (Map.Entry<String, Transition> entry3 : transitionHashMap.entrySet()) {
                Transition innerMap = entry3.getValue();
                for (int i = 0; i < tokens.length - 1; i++) {
                    if (entry3.getKey().equals(tokens[i] + " " + tokens[i + 1])) {
                        temp *= innerMap.probablity;
                    }
                }
            }

            lastMap.put(entry.getKey(), temp);

        }

        return lastMap;
    }

    //calculate evaluation result

    public int evaluation(ArrayList<String> incorrectDataset, ArrayList<String> accurrencySentencesDataset) {

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        for (int i = 0; i < incorrectDataset.size(); i++) {
            Pattern pattern = Pattern.compile("<ERR targ=(.*?)</ERR>");
            Matcher matcher = pattern.matcher(incorrectDataset.get(i));
            while (matcher.find()) {
                String[] words = matcher.group(1).split("> ");
                list1.add(words[0].trim());
            }

        }

        for (int i = 0; i < accurrencySentencesDataset.size(); i++) {
            Pattern pattern2 = Pattern.compile("<SEFA targ=(.*?)</SEFA>");
            Matcher matcher2 = pattern2.matcher(accurrencySentencesDataset.get(i));
            while (matcher2.find()) {
                //System.out.println(i);
                String[] words = matcher2.group(1).split(" ");
                list2.add(words[0].trim());
            }

        }

        int temp = 0;
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).equals(list2.get(i)))
                temp++;
        }

        //System.out.println(list1);
        //System.out.println(list2);

        temp = 100 * temp / list1.size();

        return temp;
    }

}