package com.rarToZip.RarToZip.service;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Setter
@Getter
public class RarService {
    private Logger log = LoggerFactory.getLogger(RarService.class);
    private Random random = new Random();
    private Map<String,ByteArrayOutputStream> buffer = new HashMap<>();

    public void unrar(InputStream in_rar , String key) throws IOException, RarException {
        final Archive archive = new Archive(in_rar);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (true) {
            log.info("before file heder");
            FileHeader fileHeader = archive.nextFileHeader();
            log.info("after file heder");
            //log.info(fileHeader.);
            if (fileHeader == null) {
                break;
            }
            log.info(fileHeader.getFileName());
            archive.extractFile(fileHeader, outputStream);
        }
        buffer.put(key,outputStream);
    }
    public String fileKey(){
        String result = "";
        for(int i=0;i<25;i++){
            result+=random.nextInt(10);
        }
        return result;
    }
    public ByteArrayOutputStream getUnrarFile(String key){
        return buffer.get(key);
    }
}
