package mapReduce.inputFormats;

import mapReduce.readers.PDFRecordReader;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

/**
 * Created by Marco on 04/10/16.
 */
public class PDFInputFormat extends FileInputFormat {

    @Override
    public RecordReader createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
            throws IOException, InterruptedException
    {
        //return a reader for the PDF
        return new PDFRecordReader();
    }
}
