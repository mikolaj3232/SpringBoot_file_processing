package com.rarToZip.RarToZip.Controllers;


import com.rarToZip.RarToZip.Component.Tools;
import com.rarToZip.RarToZip.myDictinary.Dictionary;
import com.rarToZip.RarToZip.service.ZipService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
public class FileController {
    @Autowired
    ZipService service_zip;
    @Autowired
    Tools tools;
    private Logger log =  LoggerFactory.getLogger(FileController.class);

    @PostMapping(value = "/pack", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void manyToOneZipFile(HttpServletResponse response,
                                 @RequestParam(value = "file") MultipartFile[] file,
                                 @RequestParam(name = "zipname",required = false,defaultValue = "unnamed_file.zip") String zipname) throws IOException, ExecutionException, InterruptedException {
        //Map<String,InputStream> args = new HashMap<>();
        Dictionary<String,InputStream> dictionary = new Dictionary<>();
        for(int i=0;i<file.length;i++){
            //log.info("File number : "+(i+1));
            //args.put(file[i].getOriginalFilename(),file[i].getInputStream());
            try {
                dictionary.insertItem(file[i].getOriginalFilename(),file[i].getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //service_zip.filesToZip(args);
        log.info("Endpoint thread id : "+Thread.currentThread().getId());
        final String key = tools.generateKey();
        //CompletableFuture<InputStream> task = service_zip.filesToZip(args,key);
        service_zip.putFiles(dictionary,key);
        InputStream is = null;//task.get();//service_zip.filesToZip(args);
        try {
            is = service_zip.getZipFile(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-disposition", "attachment; filename="+zipname);
        IOUtils.copy(is, response.getOutputStream());
        is.close();
        response.flushBuffer();
        service_zip.removeTemporaryFile(key);
    }
    @PostMapping(value = "/put_file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity inputFiles(@RequestParam(value = "file") MultipartFile[] file) throws IOException {
        String key_to_file = tools.generateKey();
        Dictionary<String,InputStream> dictionary = new Dictionary<>();
        //Map<String,InputStream> args = new HashMap<>();
        log.info("Endpoint file recesive : "+file.length);
        for(int i=0;i<file.length;i++){
            log.info("File number : "+(i+1));
            //args.put(file[i].getOriginalFilename(),file[i].getInputStream());
            try {
                dictionary.insertItem(file[i].getOriginalFilename(),file[i].getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //log.info("Endpoint map args size : "+args.size());
        log.info("Endpoint thread id : "+Thread.currentThread().getId());
        CompletableFuture.runAsync(()->{
            try {
                service_zip.putFiles(dictionary,key_to_file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok().body(key_to_file);
    }
    @RequestMapping(value = "/get_zip", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @RequestParam(name = "key") String key) throws IOException {
        log.info("Download Method call ok");
        //InputStream inputStream = new FileInputStream(new File(service.getUnrarFile(key))); //load the file
        //InputStream is = new ByteArrayInputStream(service.getUnrarFile(key).toByteArray());
        //UnrarData data = service.getUnrarFile(key);
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(service_zip.getZipFile(key).readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(404);
            return;
        }
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-disposition", "attachment; filename="+key+".zip");
        IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
        is.close();
    }
}
