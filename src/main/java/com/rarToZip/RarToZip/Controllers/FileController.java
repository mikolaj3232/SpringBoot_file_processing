package com.rarToZip.RarToZip.Controllers;


import com.github.junrar.exception.RarException;
import com.rarToZip.RarToZip.service.RarService;
import com.rarToZip.RarToZip.service.ZipService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipOutputStream;


@RestController
public class FileController {
    @Autowired
    RarService service;
    @Autowired
    ZipService service_zip;
    private Logger log =  LoggerFactory.getLogger(Controller.class);
    @PostMapping(value = "/rar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity inputRar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String key_to_file = service.fileKey();
        if(file != null) {
            log.info("Method call ok");
            log.info(file.getOriginalFilename());
            try {
                if(!file.getOriginalFilename().contains(".rar")){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("This endpoint get only rar file");
                }
                 service.unrar(file.getInputStream(),key_to_file);
            } catch (RarException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
        return ResponseEntity.ok().body(key_to_file);
    }
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @RequestParam(name = "key") String key) throws IOException {
        log.info("Download Method call ok");
        //InputStream inputStream = new FileInputStream(new File(service.getUnrarFile(key))); //load the file
        InputStream is = new ByteArrayInputStream(service.getUnrarFile(key).toByteArray());
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-disposition", "attachment; filename="+"file.xls");
        IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
    }
    @PostMapping(value = "/pack", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void mantToOneZipFile(HttpServletResponse response,
                                 @RequestParam(value = "file") MultipartFile[] file,
                                 @RequestParam(name = "zipname",required = false,defaultValue = "unnamed_file.zip") String key) throws IOException, ExecutionException, InterruptedException {
        Map<String,InputStream> args = new HashMap<>();
        for(int i=0;i<file.length;i++){
            //log.info("File number : "+(i+1));
            args.put(file[i].getOriginalFilename(),file[i].getInputStream());
        }
        //service_zip.filesToZip(args);
        log.info("Endpoint thread id : "+Thread.currentThread().getId());
        CompletableFuture<InputStream> task = service_zip.filesToZip(args);
        InputStream is = task.get();//service_zip.filesToZip(args);
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-disposition", "attachment; filename="+"file.zip");
        IOUtils.copy(is, response.getOutputStream());
        is.close();
        response.flushBuffer();
    }
}
