/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package draft.dev.proveprefilldemo;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author beart
 */
@Controller

public class PrefillController {

    @Autowired

    PrefillService prefillService;

    @GetMapping("/")
    public String challengePage(Model model) {

        model.addAttribute("challengedetails", new Challenge());
        

        return "ChallengePage";

    }
    
    
    @PostMapping("/verify-identity")
    public String challengeSubmit(@ModelAttribute Challenge challenge, Model model) {
        
        model.addAttribute("ssn", challenge.getSsn());
        model.addAttribute("phoneNumber", challenge.getPhoneNumber());
        model.addAttribute("authToken", prefillService.initialize(challenge));              
        
        
        return "SMSWaitingPage";
    }    
        
    @GetMapping("/review")
    public String reviewPage(Model model) {

        model.addAttribute("formdetails", new Form());

        return "ReviewPage";

    }


    @GetMapping("/result")
    public String resultPage() {

        return "ResultPage";

    }
    
    
    @PostMapping("/validate")
    public String complete(@ModelAttribute Challenge challenge,
            @RequestHeader("X-Correlation-ID") String correlationID,
            @RequestHeader("X-SSN") String ssn,
            Model model) {
        
        Form form;
        if (prefillService.validate(correlationID)) {
           form = prefillService.preFill(correlationID, ssn);
                      
        } else {
            model.addAttribute("statusmessage", "Verification failed");
            return "ChallengePage";
        }
        
        model.addAttribute("formdetails", form);
        
        return "ReviewPage";
    }

}
