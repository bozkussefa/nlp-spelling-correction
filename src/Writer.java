import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Writer {

    public void writeResult(ArrayList<String> accurrencySentencesDataset, double result, String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        PrintWriter writer = new PrintWriter(args[1], "UTF-8");

        String temp;

        for (int i = 0; i < accurrencySentencesDataset.size(); i++) {
            Pattern pattern2 = Pattern.compile("<SEFA targ=(.*?)</SEFA>");
            Matcher matcher2 = pattern2.matcher(accurrencySentencesDataset.get(i));
            temp = accurrencySentencesDataset.get(i);
            while (matcher2.find()) {
                String[] tokens = matcher2.group(1).split(" ");
                temp = temp.replaceFirst("<SEFA targ=(.*?)</SEFA>", tokens[0].trim());

            }
            writer.println(temp.trim());
        }

        writer.println("----------------------");
        writer.println("Evaluation Result: %" + result);

        writer.close();
    }

}
