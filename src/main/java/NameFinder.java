import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 29/09/16.
 */
public class NameFinder
{
    public NameFinder()
    {
        super();
    }

    public List<String> findNamesIn(String text)
    {
        List<String> nameList = new ArrayList<String>();


        InputStream modelInToken = null;
        InputStream modelIn = null;

        try {

            String tokenModelPAth = this.getClass().getResource("en-token.bin").toURI().getPath();
            String nameModelPAth = this.getClass().getResource("en-ner-person.bin").toURI().getPath();


            //1. convert sentence into tokens
            modelInToken = new FileInputStream(tokenModelPAth);
            TokenizerModel modelToken = new TokenizerModel(modelInToken);
            Tokenizer tokenizer = new TokenizerME(modelToken);
            String tokens[] = tokenizer.tokenize(text);





            //2. find names
            modelIn = new FileInputStream(nameModelPAth);
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinder = new NameFinderME(model);

            Span nameSpans[] = nameFinder.find(tokens);

            //find probabilities for names
            double[] spanProbs = nameFinder.probs(nameSpans);

            //3. print names
            for( int i = 0; i<nameSpans.length; i++) {
                System.out.println("Span: "+nameSpans[i].toString());
                System.out.println("Covered text is: "+tokens[nameSpans[i].getStart()] + " " + tokens[nameSpans[i].getStart()+1]);
                System.out.println("Probability is: "+spanProbs[i]);
            }
            //Span: [0..2) person
            //Covered text is: Jack London
            //Probability is: 0.7081556539712883
        }
        catch (Exception ex) {}
        finally {
            try { if (modelInToken != null) modelInToken.close(); } catch (IOException e){};
            try { if (modelIn != null) modelIn.close(); } catch (IOException e){};
        }

        return nameList;
    }
}
