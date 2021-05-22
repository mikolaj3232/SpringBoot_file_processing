package com.rarToZip.RarToZip.service;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.rarToZip.RarToZip.DataClasses.UnrarData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class RarService {
    private Logger log = LoggerFactory.getLogger(RarService.class);
    private Random random = new Random();
    private Map<String, UnrarData> buffer = new HashMap<>();

    public void unrar(InputStream in_rar , String key, String origin_name) throws IOException, RarException {
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
        buffer.put(key,new UnrarData(outputStream,origin_name));
    }

    public UnrarData getUnrarFile(String key){
        return buffer.get(key);
    }
}
