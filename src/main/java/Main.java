/**
 * Created by Marco on 21/09/16.
 */
import mapReduce.inputFormats.PDFInputFormat;
import mapReduce.mappers.nameRefMapper;
import mapReduce.reducers.nameRefReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Main {

    public static void main(String[] args) throws Exception {


        Configuration conf = new Configuration();

        GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
        String[] remainingArgs = optionParser.getRemainingArgs();
        if (remainingArgs.length != 2)
        {
            System.err.println("Usage: jar <in> <out>");
            System.exit(2);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        Job job = Job.getInstance(conf, "Name-Reference finder");

        job.addCacheFile(Main.class.getResource("en-token.bin").toURI());
        job.addCacheFile(Main.class.getResource("en-ner-person.bin").toURI());
        job.addCacheFile(Main.class.getResource("es-ner-person.bin").toURI());
        job.addCacheFile(Main.class.getResource("en-sent.bin").toURI());
        job.addCacheFile(Main.class.getResource("nl-ner-person.bin").toURI());



        job.setJarByClass(Main.class);
        job.setInputFormatClass(PDFInputFormat.class);
        //job.setMapperClass(nameRefMapper.class);
        job.setCombinerClass(nameRefReducer.class);
        job.setReducerClass(nameRefReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);


        Path inPath = new Path(inputPath);
        FileSystem fs = FileSystem.get(conf);
        FileStatus status = fs.getFileStatus(inPath);
        if(status.isDirectory())
        {
            FileStatus[] files = fs.listStatus(status.getPath());
            for(FileStatus file: files)
            {
                MultipleInputs.addInputPath
                        (
                            job,
                            file.getPath(),
                            PDFInputFormat.class,
                            nameRefMapper.class
                        );
            }
        }
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}