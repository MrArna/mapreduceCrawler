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

        URI[] localPath = context.getCacheFiles();



        InputSplit split = context.getInputSplit();
        Class<? extends InputSplit> splitClass = split.getClass();

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



        title.set(fileSplit.getPath().getName());
        NameFinder nf = new NameFinder(localPath);

        for(String n : nf.findNamesIn(value.toString()))
        {
            System.out.println(" mapper output -> <" + name.toString() + "," + title.toString() + ">");
            name.set(n);
            context.write(name, title);
        }
    }
}
