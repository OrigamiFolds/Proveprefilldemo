/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package draft.dev.proveprefilldemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//Prove Imports Begin
import com.prove.proveapi.Proveapi;
import com.prove.proveapi.models.components.Security;
import com.prove.proveapi.models.components.V3ChallengeAddressEntryRequest;
import com.prove.proveapi.models.components.V3ChallengeIndividualRequest;
import com.prove.proveapi.models.components.V3ChallengeRequest;
import com.prove.proveapi.models.components.V3ChallengeResponse;
import com.prove.proveapi.models.components.V3CompleteIndividualRequest;
import com.prove.proveapi.models.components.V3CompleteRequest;
import com.prove.proveapi.models.components.V3CompleteResponse;
import com.prove.proveapi.models.components.V3StartRequest;
import com.prove.proveapi.models.components.V3StartResponse;
import com.prove.proveapi.models.components.V3ValidateRequest;
import com.prove.proveapi.models.components.V3ValidateResponse;
import com.prove.proveapi.models.operations.V3ChallengeRequestResponse;
import com.prove.proveapi.models.operations.V3CompleteRequestResponse;
import com.prove.proveapi.models.operations.V3StartRequestResponse;
import com.prove.proveapi.models.operations.V3ValidateRequestResponse;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
//Prove Imports End

/**
 *
 * @author beart
 */
@Service
public class PrefillService {

    @Value("${spring.application.client.id}")
    private String clientId;
    @Value("${spring.application.client.secret}")
    private String clientSecret;
    @Value("${spring.application.token.url}")
    private String tokenURL;

    private Proveapi sdk;

    private void initializeProveSDK() {
        sdk = Proveapi.builder()
                .security(Security.builder()
                        .tokenURL(tokenURL)
                        .clientID(clientId)
                        .clientSecret(clientSecret)
                        .build())
                .build();

    }

    public String initialize(Challenge challenge) {
        // Create client for Prove API.
        if (sdk == null) {
            initializeProveSDK();
        }

        // Send the start request.                              
        V3StartRequest req = V3StartRequest.builder()
                .phoneNumber(challenge.getPhoneNumber())
                .flowType("desktop")
                .finalTargetUrl("https://www.example.com")
                .build();

        /*
        * Note: Flow type is hard coded here.
        * However, it should be created and verified on the client side 
        * and then sent to the server.
         */
        try {
            V3StartRequestResponse res = sdk.v3().v3StartRequest()
                    .request(req)
                    .call();
      
            V3StartResponse startResponse = res.v3StartResponse().get();
      
            return startResponse.authToken();

        } catch (Exception ex) {
            System.getLogger(PrefillService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        return new String();

    }

    public boolean validate(String coId) {

        if (sdk == null) {
            initializeProveSDK();
        }

        V3ValidateRequest req = V3ValidateRequest.builder()
                .correlationId(coId)
                .build();

        try {
            V3ValidateRequestResponse res = sdk.v3().v3ValidateRequest()
                    .request(req)
                    .call();

            V3ValidateResponse validateResponse = res.v3ValidateResponse().get();
                        
            return validateResponse.success();
            

        } catch (Exception ex) {
            System.getLogger(PrefillService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return false;
    }

    public Form preFill(String coId, String ssn) {
        if (sdk == null) {
            initializeProveSDK();
        }

        System.out.println("Social Security Number:" + ssn);
        V3ChallengeRequest creq = V3ChallengeRequest.builder()
                .correlationId(coId)
                .ssn(ssn)
                .build();
                
        try {
            V3ChallengeRequestResponse qres = sdk.v3().v3ChallengeRequest()
                    .request(creq)
                    .call();
            
            V3ChallengeResponse challengeResponse = qres.v3ChallengeResponse().get();
            
            
            Form form = new Form();
            V3ChallengeIndividualRequest individual = challengeResponse.individual().orElse(new V3ChallengeIndividualRequest());
            form.setFirstName(individual.firstName().orElse(new String()));
            form.setLastName(individual.lastName().orElse(new String()));
            form.setDob(individual.dob().orElse(new String()));
            form.setSsn(individual.ssn().orElse(new String()));
            
            
            /*
            * Add Code to fetch addresses and email addresses 
            */
           
            return form;
            
        } catch (Exception ex) {
            Logger.getLogger(PrefillService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    

}
