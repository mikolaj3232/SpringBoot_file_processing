package com.rarToZip.RarToZip.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Tools {
    private Logger log = LoggerFactory.getLogger(Tools.class);
    private Random random = new Random();
    public String generateKey(){
        String result = "";
        for(int i=0;i<25;i++){
            result+=random.nextInt(10);
        }
        return result;
    }
}
