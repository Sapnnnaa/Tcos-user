package com.telco.telco_co_app.controller.useridgenerator;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {

    public String generateNextUserId(String userId,int companyId) {
        String result="";
    	if(companyId==101) {
    		result="E0100"+userId;
    	} else if(companyId==102) {
    		result="ETCN"+userId;
    	} else if(companyId==103) {
    		result="ETTL"+userId;
    	} else if(companyId==104) {
    		result="E0223"+userId;
    	} else {
    		result="E0213"+userId;
    	}

        return result;
    }

}
