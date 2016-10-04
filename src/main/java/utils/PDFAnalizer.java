package utils;

import models.PDF;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Marco on 27/09/16.
 */
public class PDFAnalizer
{

    public PDFAnalizer()
    {
        super();
    }

    public PDF analyzePDF(String path) throws TikaException, SAXException, IOException
    {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File(path));
        ParseContext pcontext = new ParseContext();

        //parsing the document using models.PDF parser
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(inputstream, handler, metadata,pcontext);

        return  new PDF(metadata,handler);
    }

}
