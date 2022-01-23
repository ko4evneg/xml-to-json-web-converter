import com.github.ko4evneg.jaxb.Envelope;
import com.github.ko4evneg.jaxb.SendPayment;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.File;

public class XmlTest {
    public static void main(String[] args) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Envelope.class);

        SendPayment e = ((Envelope) context.createUnmarshaller().unmarshal(new File("test.xml"))).body.sendPayment;
        System.out.println(e);
    }
}
