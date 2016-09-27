import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;

/**
 * Created by Marco on 27/09/16.
 */
public class PDF
{
    private Metadata metadata;
    private BodyContentHandler content;

    public PDF()
    {
        super();
        metadata = new Metadata();
        content = new BodyContentHandler();
    }

    public  PDF(Metadata metadata, BodyContentHandler content)
    {
        super();
        this.metadata = metadata;
        this.content = content;
    }


    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public BodyContentHandler getContent() {
        return content;
    }
}
