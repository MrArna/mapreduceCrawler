/**
 * Created by Marco on 21/09/16.
 */
import mapReduce.inputFormats.PDFInputFormat;
import mapReduce.mappers.nameRefMapper;
import mapReduce.reducers.nameRefReducer;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

    public static void main(String[] args) throws Exception {


        CommandLineParser parser = new DefaultParser();


        Options options = new Options();
        options.addOption("i", "input-folder", true, "Input Folder");
        options.addOption("o", "output-file", true, "Output file");


        String inputPath = "";
        String outputPath = "";

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // validate that block-size has been set
            if (line.hasOption("i") && line.hasOption("o"))
            {
                inputPath = line.getOptionValue("i");
                outputPath = line.getOptionValue("o");
            }
            else
            {
                System.out.println("Invalid arguments");
                System.exit(0);
            }
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            System.exit(0);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Name-Reference finder");

        job.setJarByClass(Main.class);
        job.setInputFormatClass(PDFInputFormat.class);
        job.setMapperClass(nameRefMapper.class);
        job.setCombinerClass(nameRefReducer.class);
        job.setReducerClass(nameRefReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputDirRecursive(job, true);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}