package com.smartbiz.document;

import com.smartbiz.invoice.InvoiceEntity;
import com.smartbiz.invoice.InvoiceRepository;
import com.smartbiz.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    private final InvoiceRepository invoiceRepo;
    private final PdfService pdfService;

    public byte[] generate(Long invoiceId, UserEntity user) {

        InvoiceEntity inv = invoiceRepo
                .findByIdAndCreatedBy(invoiceId, user.getId())
                .orElseThrow();

        String html = """
                    <!DOCTYPE html>
                    <html>
                    <body>
                    <h2>SmartBiz Invoice</h2>
                    <p>Invoice ID: %d</p>
                    <p>Amount: %.2f</p>
                    <p>Tax: %.2f</p>
                    <p>Total: %.2f</p>
                    <p>Status: %s</p>
                    <p>Due: %s</p>
                    </body>
                    </html>
                """.formatted(
                inv.getId(),
                inv.getAmount(),
                inv.getTax(),
                inv.getTotal(),
                inv.getStatus(),
                inv.getDueDate()
        );
//        try {
//            validateXhtml(html);
//        } catch (SAXException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return pdfService.generate(html);
    }

    private void validateXhtml(String html) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(getClass().getResourceAsStream("/xhtml1-strict.xsd")));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new StringReader(html)));
    }
}
