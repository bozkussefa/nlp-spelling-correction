import java.util.HashMap;
import java.util.Map;

public class Initial {

    //initial HashMap model

    Double probablity;
    double frekans;

    //set transition probablities

    public HashMap<String, Initial> initialProbablity(HashMap<String, Initial> initialHashMap) {

        int initialCount = 0;
        for (Map.Entry<String, Initial> entry : initialHashMap.entrySet()) {
            initialCount += entry.getValue().frekans;
        }

        for (Map.Entry<String, Initial> entry : initialHashMap.entrySet()) {
            entry.getValue().probablity = entry.getValue().frekans / initialCount;
        }

        return initialHashMap;
    }
}
