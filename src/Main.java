import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        ReadDataset rd = new ReadDataset();
        Processes processes = new Processes();
        Writer wr = new Writer();

        // create map and list

        ArrayList<String> correctDataset = new ArrayList<>();
        ArrayList<String> incorrectDataset = new ArrayList<>();
        ArrayList<String> accurrencySentencesDataset = new ArrayList<>();
        HashMap<String, Integer> sentenceHashMap = new HashMap<>();
        HashMap<String, Integer> bigramHashMap = new HashMap<>();
        HashMap<String, Integer> unigramHashMap = new HashMap<>();
        HashMap<String, Initial> initialHashMap = new HashMap<>();
        HashMap<String, Transition> transitionHashMap = new HashMap<>();
        HashMap<String, HashMap<String, Distance>> errorWordHashMap = new HashMap<>();
        HashMap<String, Integer> emissionMap = new HashMap<>();

        rd.reader(correctDataset, sentenceHashMap, errorWordHashMap, incorrectDataset, args);
        transitionHashMap = processes.createBiGromModel(sentenceHashMap, bigramHashMap, initialHashMap, transitionHashMap, unigramHashMap);
        processes.createEmissionMap(errorWordHashMap, emissionMap);


        // calculate viterbi sentences and calculate max prob

        for (int i = 0; i < incorrectDataset.size(); i++) {

            if (incorrectDataset.get(i).contains("</ERR>")) {
                HashMap<String, Integer> viterbiCombinesMap = new HashMap<>();
                viterbiCombinesMap = processes.calculateViterbi(errorWordHashMap, incorrectDataset.get(i), viterbiCombinesMap);
                String[] A = processes.calculateAccurrency(viterbiCombinesMap, initialHashMap, transitionHashMap, emissionMap, errorWordHashMap, correctDataset, unigramHashMap);
                accurrencySentencesDataset.add(A[0]);
            } else {
                accurrencySentencesDataset.add(incorrectDataset.get(i));
            }
        }


        // calculate Evaluation
        int result = processes.evaluation(incorrectDataset, accurrencySentencesDataset);

        //write output
        wr.writeResult(accurrencySentencesDataset, result, args);
    }
}