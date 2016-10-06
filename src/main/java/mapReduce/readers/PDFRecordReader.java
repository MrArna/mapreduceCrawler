package mapReduce.readers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Created by Marco on 04/10/16.
 */
public class PDFRecordReader extends RecordReader
{

    private String[] lines = null;
    private LongWritable key = null;
    private Text value = null;
    private Text title = null;

    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
            throws IOException, InterruptedException
    {
        FileSplit split = (FileSplit) inputSplit;
        Configuration job = taskAttemptContext.getConfiguration();
        final Path file = split.getPath();

        FileSystem fs = file.getFileSystem(job);
        FSDataInputStream fileIn = fs.open(split.getPath());

        ParseContext pcontext = new ParseContext();

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        //parsing the document using models.PDF parser
        PDFParser pdfparser = new PDFParser();
        try {
            pdfparser.parse(fileIn, handler, metadata,pcontext);
            this.lines = handler.toString().split("\n");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException
    {
        if (key == null) {
            key = new LongWritable();
            key.set(1);
            value = new Text();
            value.set(lines[0]);
        } else {
            int temp = (int) key.get();
            if (temp < (lines.length - 1)) {
                int count = (int) key.get();
                value = new Text();
                value.set(lines[count]);
                count = count + 1;
                key = new LongWritable(count);
            } else {
                return false;
            }

        }
        if (key == null || value == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object getCurrentKey() throws IOException, InterruptedException
    {
        return key;
    }

    @Override
    public Object getCurrentValue() throws IOException, InterruptedException
    {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException
    {
        return 0;
    }

    @Override
    public void close() throws IOException
    {

    }
}