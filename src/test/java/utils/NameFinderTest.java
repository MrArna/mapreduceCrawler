package utils;

import models.PDF;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Marco on 29/09/16.
 */
public class NameFinderTest {

    String text = "Jack London is the author of what novel?";
    PDF pdf;


    @Test
    public void findNamesIn() throws Exception
    {

        NameFinder nf = new NameFinder();
        assertNotNull(nf.findNamesIn(text));
        assertNotEquals(nf.findNamesIn(text),0);

    }

    @Before
    public void initPDFText() throws Exception
    {
        PDFAnalizer pdfAnalizer = new PDFAnalizer();
        String path = NameFinderTest.class.getResource("/test.pdf").toURI().getPath();
        pdf = pdfAnalizer.analyzePDF(path);
    }

    @Test
    public void findNamesInPDF() throws Exception
    {
        NameFinder nf = new NameFinder();
        System.out.println(nf.findNamesIn(pdf.getContent().toString()));
        assertNotEquals(nf.findNamesIn(pdf.getContent().toString()).size(),0);

    }

}