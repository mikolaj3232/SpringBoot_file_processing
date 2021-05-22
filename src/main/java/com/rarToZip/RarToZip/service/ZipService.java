package com.rarToZip.RarToZip.service;

import javafx.scene.shape.Path;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {
    private Logger log = LoggerFactory.getLogger(ZipService.class);

    @Async("asyncExecutor")
    //public InputStream filesToZip(Map<String, InputStream> args) throws IOException {
    public CompletableFuture<InputStream> filesToZip(Map<String, InputStream> args) throws IOException{
        log.info("Method thread id : "+Thread.currentThread().getId());
        Set<String> fname = args.keySet();
        FileOutputStream fos = new FileOutputStream("multiCompressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        //int length=0;
        for (String key : fname) {
            log.info("foreach loop");
            //File fileToZip = new File(key);
            //FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(new File(key).getName());//(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = args.get(key).readAllBytes();

            // while ((length = args.get(key).readAllBytes().length) >= 0) {
            zipOut.write(bytes, 0, bytes.length);
            //length+=args.get(key).readAllBytes().length;
            zipOut.closeEntry();
           /* while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }*/
            // fis.close();
            // }
        }
        zipOut.close();
        fos.close();
        InputStream zip = new FileInputStream("multiCompressed.zip");
        InputStream zz = new ByteArrayInputStream(zip.readAllBytes());
        zip.close();
        //return zz;
        return CompletableFuture.completedFuture(zz);
    }
}
/*
private void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException {
    File[] files = folder.listFiles();
    for (File file : files) {
        if (file.isDirectory()) {
            addFolderToZip(file, zip, baseName);
        } else {
            String name = file.getAbsolutePath().substring(baseName.length());
            ZipEntry zipEntry = new ZipEntry(name);
            zip.putNextEntry(zipEntry);
            IOUtils.copy(new FileInputStream(file), zip);
            zip.closeEntry();
        }
    }
}
 */
