package pl.kuba.azureaitextanalythicsaproject;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfContentExtractor {

    public String processPdfFile(byte[] fileContent) throws IOException {
        try {
            PdfReader reader = new PdfReader(fileContent);
            int pages = reader.getNumberOfPages();
            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= pages; i++) {
                text.append(PdfTextExtractor.getTextFromPage(reader, i));
            }
            reader.close();
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


}
