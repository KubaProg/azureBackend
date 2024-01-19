package pl.kuba.azureaitextanalythicsaproject;


import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.*;
import com.azure.ai.textanalytics.util.AnalyzeActionsResultPagedIterable;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Analyzer {


    private final PdfContentExtractor pdfContentExtractor;
    private static String languageKey = "6b979b4ac8fa446bb5bd5f9fc4986c69";
    private static String languageEndpoint = "https://azureai-languageservice.cognitiveservices.azure.com/";
    String pdfContent = "Test content";
    TextAnalyticsClient client = authenticateClient(languageKey, languageEndpoint);
    static TextAnalyticsClient authenticateClient(String key, String endpoint) {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }

    public static List<String> processSummary(List<String> summarySentences, List<String> importantKeywords) {
        List<String> prioritizedSentences = new ArrayList<>();

        List<String> otherSentences = new ArrayList<>(summarySentences);

        for (String sentence : summarySentences) {
            for (String keyword : importantKeywords) {
                // If the sentence contains an important keyword, add it to the prioritized list
                if (sentence.toLowerCase().contains(keyword.toLowerCase())) {
                    prioritizedSentences.add(sentence);
                    otherSentences.remove(sentence);
                    break; // Break to avoid adding the same sentence multiple times
                }
            }
        }

        List<String> modifiedSummary = new ArrayList<>();
        modifiedSummary.addAll(prioritizedSentences);
        modifiedSummary.addAll(otherSentences);

        return modifiedSummary;
    }

    public Response summarizationExample(Request request) {

        try {
            pdfContent = pdfContentExtractor.processPdfFile(request.getFile().getBytes());
        } catch (IOException e) {
            throw new RuntimeException();
        }

//        pdfContent = "Title: The Future of Renewable Energy Technologies\n" +
//                "In recent years, the global focus on renewable energy has significantly intensified due to growing environmental concerns and the urgent need to reduce carbon emissions. As the world grapples with the effects of climate change, renewable energy technologies are increasingly seen as vital to achieving a sustainable future.\n" +
//                "Solar Power Advancements\n" +
//                "Solar power has seen remarkable advancements. The efficiency of photovoltaic cells has dramatically improved, making solar energy more viable than ever. Innovations in solar panel design, including flexible and transparent panels, have enabled a wider range of applications. These advancements not only enhance the aesthetics but also increase the practical applications of solar energy in urban environments.\n" +
//                "Wind Energy Developments\n" +
//                "Wind energy has also made significant strides. The creation of larger, more efficient turbine designs has resulted in increased energy output, making wind farms more productive. Offshore wind farms, in particular, have gained popularity due to their ability to harness stronger and more consistent winds. However, the challenge remains in minimizing the impact on marine ecosystems and addressing the concerns of coastal communities.\n" +
//                "Hydroelectric Power and Environmental Impact\n" +
//                "Hydroelectric power, while not a new technology, continues to be a significant source of renewable energy. Recent trends focus on minimizing the ecological footprint of hydroelectric plants. This includes developing smaller, less invasive hydroelectric installations that do not disrupt river ecosystems as significantly as large-scale dams.\n" +
//                "Geothermal Energy Exploration\n" +
//                "Geothermal energy is another area witnessing rapid growth. Technological advancements have enabled deeper and more efficient extraction of geothermal energy. This has opened up new possibilities in regions previously considered unsuitable for geothermal energy exploitation.\n" +
//                "Energy Storage and Grid Integration\n" +
//                "A critical aspect of renewable energy is the development of efficient energy storage systems. Innovations in battery technology, such as solid-state batteries and improved lithium-ion batteries, offer greater storage capacity and faster charging times. Effective integration of renewable energy into the grid remains a challenge. Grid modernization and smart grid technologies are crucial for managing the intermittent nature of renewable sources like solar and wind.\n" +
//                "Economic and Policy Considerations\n" +
//                "The economic landscape of renewable energy has evolved. The costs of solar and wind energy have decreased significantly, making them competitive with traditional fossil fuels. Governments worldwide are providing incentives and subsidies to encourage the adoption of renewable energies. However, the transition to renewable energy also presents challenges, including the need for substantial investment in infrastructure and the potential impact on jobs in traditional energy sectors.\n" +
//                "Conclusion\n" +
//                "The future of renewable energy is promising but requires continued innovation and support. As technologies advance, the integration of renewable energy into our daily lives will become more seamless and efficient, playing a crucial role in combating climate change and leading us towards a more sustainable future.\n" +
//                "\n";

        List<String> documents = new ArrayList<>();
        documents.add(pdfContent);

        SyncPoller<AnalyzeActionsOperationDetail, AnalyzeActionsResultPagedIterable> syncPoller =
                this.client.beginAnalyzeActions(documents,
                        new TextAnalyticsActions().setDisplayName("{tasks_display_name}")
                                .setExtractiveSummaryActions(new ExtractiveSummaryAction().setMaxSentenceCount(Integer.valueOf(request.getLength())))
                                .setAnalyzeSentimentActions(new AnalyzeSentimentAction()
                                        .setIncludeOpinionMining(true)),
                        "pl",
                        new AnalyzeActionsOptions());

        syncPoller.waitForCompletion();

        List<String> importantKeywords = request.getKeyWords();

        // Here we will store the summary sentences
        List<String> summarySentences = new ArrayList<>();

        syncPoller.getFinalResult().forEach(actionsResult -> {
            for (ExtractiveSummaryActionResult actionResult : actionsResult.getExtractiveSummaryResults()) {
                if (!actionResult.isError()) {
                    for (ExtractiveSummaryResult documentResult : actionResult.getDocumentsResults()) {
                        if (!documentResult.isError()) {
                            documentResult.getSentences().stream()
                                    .map(ExtractiveSummarySentence::getText)
                                    .forEach(summarySentences::add);

                            List<String> processedSummary = processSummary(summarySentences, importantKeywords);

                            // Adding processed summary sentences to our list
                            summarySentences.clear();
                            summarySentences.addAll(processedSummary);
                        }
                    }
                }
            }
        });

        // Create a response object with the summary sentences
        Response response = new Response();
        response.setSummary(summarySentences.toString());
        return response;
    }
}
