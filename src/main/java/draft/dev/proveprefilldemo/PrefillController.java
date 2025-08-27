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

        String authToken = prefillService.initialize(challenge);
        if (authToken.isBlank()) {
            model.addAttribute("challengedetails", challenge);
            model.addAttribute("statusmessage", "We could not verify your identity. Please try again or click on \"I don't have a mobile phone\".");
            return "ChallengePage";
        }

        model.addAttribute("authToken", authToken);
        return "SMSWaitingPage";
    }

    @GetMapping("/review")
    public String reviewPage(Model model) {

        model.addAttribute("formdetails", new Form());
        model.addAttribute("statusmessage", "Please fill in your details.");
        return "ReviewPage";

    }

    @PostMapping("/validate")
    public String complete(@ModelAttribute Challenge challenge,
            @RequestHeader("X-Correlation-ID") String correlationID,
            @RequestHeader("X-SSN") String ssn,
            Model model) {

        Form form;
        if (prefillService.validate(correlationID)) {
            form = prefillService.preFill(correlationID, ssn);
            if (form != null) {                
                model.addAttribute("formdetails", form);
            } else {
                model.addAttribute("challengedetails", challenge);
                model.addAttribute("statusmessage", "Could not find details for the given SSN. Please try again or click on \"I don't have a mobile phone\".");
                return "ChallengePage";
            }

        } else {
            model.addAttribute("challengedetails", challenge);
            model.addAttribute("statusmessage", "Verfication failed. Please try again or click on \"I don't have a mobile phone\".");
            return "ChallengePage";
        }
        model.addAttribute("statusmessage", "Please review your details.");
        return "ReviewPage";
    }
    
    @PostMapping("/complete")
    public String reviewPage(@ModelAttribute Form form, Model model) {
        if (prefillService.completeValidation(form)) {
            model.addAttribute("heading","Awesome!");
            model.addAttribute("statusmessage","Your details have been entered successfully");
        } else {            
            model.addAttribute("heading","Error!");
            model.addAttribute("statusmessage","We couldn't insert your details. Please review them and try again.");
        }
        return "ResultPage";
    }
}
