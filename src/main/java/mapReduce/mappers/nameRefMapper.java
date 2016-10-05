package mapReduce.mappers;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Marco on 04/10/16.
 */
public class nameRefMapper extends Mapper<Object,Text,Text,Text>
{
    private Text one = new Text();
    private Text word = new Text();

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException
    {
        StringTokenizer itr = new StringTokenizer(value.toString());
        while (itr.hasMoreTokens())
        {
            word.set(itr.nextToken());
            context.write(word, one);
        }
    }
}
