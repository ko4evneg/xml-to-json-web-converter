import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ko4evneg.jaxb.Envelope;
import com.github.ko4evneg.jaxb.SendPayment;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

//todo: run note
public class XmlTest {
    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Envelope.class);

        Envelope envelope = (Envelope) context.createUnmarshaller().unmarshal(new File("test.xml"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(System.out, envelope);
    }
}
