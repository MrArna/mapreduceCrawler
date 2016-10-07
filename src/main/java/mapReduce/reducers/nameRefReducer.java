package mapReduce.reducers;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by Marco on 04/10/16.
 */
public class nameRefReducer extends Reducer<Text,Text,Text,Text>
{
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
    ) throws IOException, InterruptedException {
        String append = "";
        //concatenate titles of pdfs
        for (Text val : values)
        {
            append = append + val.toString() + ", ";
        }
        result.set(append);
        context.write(key, result);
    }
}
