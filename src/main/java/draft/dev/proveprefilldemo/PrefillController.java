/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package draft.dev.proveprefilldemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/review")
    public String reviewPage(Model model) {

        model.addAttribute("formdetails", new Form());

        return "ReviewPage";

    }

    @GetMapping("/smswaiting")
    public String smsWaitingPage(Model model) {

        model.addAttribute("phoneNumber", "076 314 2419");

        return "SMSWaitingPage";

    }

    @GetMapping("/result")
    public String resultPage() {

        return "ResultPage";

    }

}
