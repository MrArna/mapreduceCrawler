package utils;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.hadoop.mapreduce.Mapper.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 29/09/16.
 */
public class NameFinder
{

    String tokenModelPAth;
    String engNameModelPAth;
    String esNameModelPAth;
    String nlNameModelPAth;
    String sentenceModelPAth;

    public NameFinder(URI[] uris)
    {
        super();

        for(URI uri : uris)
        {
            if(uri.getPath().contains("en-token.bin"))
            {
                tokenModelPAth = uri.getPath();
            }
            if(uri.getPath().contains("en-ner-person.bin"))
            {
                engNameModelPAth = uri.getPath();
            }
            if(uri.getPath().contains("es-ner-person.bin"))
            {
                esNameModelPAth = uri.getPath();
            }
            if(uri.getPath().contains("nl-ner-person.bin"))
            {
                nlNameModelPAth = uri.getPath();
            }
            if(uri.getPath().contains("en-sent.bin"))
            {
                sentenceModelPAth = uri.getPath();
            }
        }
    }

    public NameFinder()
    {
        super();
        try {
            tokenModelPAth = getClass().getResource("/en-token.bin").toURI().getPath();
            engNameModelPAth = getClass().getResource("/en-ner-person.bin").toURI().getPath();
            esNameModelPAth = getClass().getResource("/es-ner-person.bin").toURI().getPath();
            nlNameModelPAth = getClass().getResource("/nl-ner-person.bin").toURI().getPath();
            sentenceModelPAth = getClass().getResource("/en-sent.bin").toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    public List<String> findNamesIn(String text)
    {
        List<String> nameList = new ArrayList<String>();


        InputStream modelInToken = null;
        InputStream modelIn = null;
        InputStream modelInSentence = null;

        try {


            //0. convert text into sentence
            modelInSentence = new FileInputStream(sentenceModelPAth);
            SentenceModel modelSentence = new SentenceModel(modelInSentence);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(modelSentence);
            String sentences[] = sentenceDetector.sentDetect(text);



            for (String sentence : sentences)
            {
                //System.out.println(sentence);
                //1. convert sentence into tokens
                modelInToken = new FileInputStream(tokenModelPAth);
                TokenizerModel modelToken = new TokenizerModel(modelInToken);
                Tokenizer tokenizer = new TokenizerME(modelToken);
                String tokens[] = tokenizer.tokenize(sentence);


                //2. find names
                modelIn = new FileInputStream(nlNameModelPAth);
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME nameFinder = new NameFinderME(model);

                Span nameSpans[] = nameFinder.find(tokens);

                //find probabilities for names
                double[] spanProbs = nameFinder.probs(nameSpans);

                //3. print names
                for( int i = 0; i<nameSpans.length; i++)
                {
                    //System.out.println("Span: "+nameSpans[i].toString());
                    //System.out.println("Covered text is: "+tokens[nameSpans[i].getStart()] + " " + tokens[nameSpans[i].getStart()+1]);
                    //System.out.println("Probability is: "+spanProbs[i]);
                    nameList.add((tokens[nameSpans[i].getStart()] + " " + tokens[nameSpans[i].getStart()+1]).replace("+",""));
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println(tokenModelPAth);
        }
        finally
        {
            try {
                if (modelInToken != null) modelInToken.close();
            } catch (IOException e)
            {
                System.out.println("Unable to load token model");
            };
            try {
                if (modelIn != null) modelIn.close();
            } catch (IOException e)
            {
                System.out.println("Unable to load sentence model");

            }
        }

        return nameList;
    }
}
