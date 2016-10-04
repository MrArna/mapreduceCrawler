import models.PDF;
import org.junit.Test;
import utils.PDFAnalizer;

import static org.junit.Assert.*;

/**
 * Created by Marco on 27/09/16.
 */
public class PDFAnalizerTest {

    @Test
    public void analyzePDF() throws Exception
    {
        PDFAnalizer analizer = new PDFAnalizer();

        //File dirs = new File("./../../");
        //String path = dirs.getCanonicalPath() + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "/test.pdf";
        String path = PDFAnalizerTest.class.getResource("test.pdf").toURI().getPath();
        PDF pdf = analizer.analyzePDF(path);

        //getting the content of the document
        System.out.println("Contents of the models.PDF :" + pdf.getContent().toString());

        //getting metadata of the document
        System.out.println("Metadata of the models.PDF:");
        String[] metadataNames = pdf.getMetadata().names();

        for(String name : metadataNames) {
            System.out.println(name+ " : " + pdf.getMetadata().get(name));
        }


        assertNotNull(pdf);
    }

}