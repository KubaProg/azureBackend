package pl.kuba.azureaitextanalythicsaproject;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final Analyzer analyzerService;

    @PostMapping("/summarize")
    public ResponseEntity<Response> retrieveSummary(@RequestBody Request request){
        return ResponseEntity.ok(this.analyzerService.summarizationExample(request));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("WORKS GOOD");
    }
}
