package pl.kuba.azureaitextanalythicsaproject;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final Analyzer analyzerService;

    @PostMapping("/summarize")
    public ResponseEntity<Response> retrieveSummary(
            @RequestParam("keywords") List<String> keyWords,
            @RequestParam("length") String length,
            @RequestParam("file") MultipartFile file) {
        Request request = Request.builder()
                .keyWords(keyWords)
                .length(length)
                .file(file)
                .build();
        return ResponseEntity.ok(this.analyzerService.summarizationExample(request));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("WORKS GOOD");
    }
}
