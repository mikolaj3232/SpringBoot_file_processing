package com.rarToZip.RarToZip.service;

import com.rarToZip.RarToZip.myDictinary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {
    private Logger log = LoggerFactory.getLogger(ZipService.class);
    private Set<String> file_handlers = new HashSet<>();
   //@Async("asyncExecutor")
    public void putFiles(Dictionary<String,InputStream> args, String id) throws IOException {
        log.info("Method thread id : "+Thread.currentThread().getId());
       //log.info("Map length: "+args.size());
        final String filename = id + ".zip";
        Set<String> fname = args.getKeySet();
        FileOutputStream fos = new FileOutputStream(filename);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        //int length=0;
        for (String key : fname) {
            log.info("foreach loop");
            ZipEntry zipEntry = new ZipEntry(new File(key).getName());//(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            InputStream x = (InputStream)args.getItemByKey(key);
            byte[] bytes = x.readAllBytes();
            zipOut.write(bytes, 0, bytes.length);
            zipOut.closeEntry();
        }
        zipOut.close();
        fos.close();
        file_handlers.add(filename);
        log.info("Put zip file, set size :"+file_handlers.size());
    }
    public InputStream getZipFile(String id) throws Exception {
        final String filename = id + ".zip";
        if(!(file_handlers.contains(filename))){
            log.info("Get zip file, set size :"+file_handlers.size());
            throw new Exception("There is no file with this id");
        }
        InputStream zip = new FileInputStream(filename);
        InputStream zz = new ByteArrayInputStream(zip.readAllBytes());
        zip.close();
        return zz;
    }
    public void removeTemporaryFile(String id){
       new File((id+".zip")).delete();
    }
}
