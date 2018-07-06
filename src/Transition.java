import java.util.HashMap;
import java.util.Map;

public class Transition {

    //Transition HashMap model

    Double probablity;
    double frekans;

    //set transition elements probablities

    public HashMap<String, Transition> transitionProbablity(HashMap<String, Transition> transitionHashMap, HashMap<String, Integer> unigramHashMap){
        HashMap<String, Transition> temp = new HashMap<>();

        for (Map.Entry<String, Transition> entry : transitionHashMap.entrySet()) {
            int count=0;
            Transition temp2 = new Transition();
            String [] tokens = entry.getKey().split(" ");
            for(Map.Entry<String, Integer> entry2 : unigramHashMap.entrySet()){
                if(entry2.getKey().equals(tokens[0])){
                    count=entry2.getValue();
                    break;
                }
            }
            temp2.frekans=entry.getValue().frekans;
            temp2.probablity=entry.getValue().frekans/count;
            temp.put(entry.getKey(),temp2);

        }
        transitionHashMap=temp;
        return transitionHashMap;
    }
}