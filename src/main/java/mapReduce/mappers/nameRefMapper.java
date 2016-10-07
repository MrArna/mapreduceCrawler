package mapReduce.mappers;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import utils.NameFinder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Created by Marco on 04/10/16.
 */
public class nameRefMapper extends Mapper<Object,Text,Text,Text>
{
    private Text title = new Text();
    private Text name = new Text();

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException
    {

        //retrieving the cached NLP models
        URI[] localPath = context.getCacheFiles();


        //retrieving the file name
        InputSplit split = context.getInputSplit();
        Class<? extends InputSplit> splitClass = split.getClass();

        //need to use reflection since the returned type is not file split in case of MultiInputFiles
        //since it returns TaggedInputSplits
        //this is a bug in hadoop...
        FileSplit fileSplit = null;
        if (splitClass.equals(FileSplit.class)) {
            fileSplit = (FileSplit) split;
        } else if (splitClass.getName().equals(
                "org.apache.hadoop.mapreduce.lib.input.TaggedInputSplit")) {
            // begin reflection hackery...

            try {
                Method getInputSplitMethod = splitClass
                        .getDeclaredMethod("getInputSplit");
                getInputSplitMethod.setAccessible(true);
                fileSplit = (FileSplit) getInputSplitMethod.invoke(split);
            } catch (Exception e) {
                // wrap and re-throw error
                throw new IOException(e);
            }

            // end reflection hackery
        }

        //set the title
        title.set(fileSplit.getPath().getName());
        //instantiate the parser with the retrieved NLP models
        NameFinder nf = new NameFinder(localPath);

        //create <key,value> pairs and then send them
        for(String n : nf.findNamesIn(value.toString()))
        {
            //System.out.println(" mapper output -> <" + name.toString() + "," + title.toString() + ">");
            if(!n.equals(" ") && !n.equals(""))
            {
                name.set(n);
                context.write(name, title);
            }
        }
    }
}
