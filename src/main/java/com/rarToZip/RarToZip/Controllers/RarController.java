package com.rarToZip.RarToZip.Controllers;

import com.github.junrar.exception.RarException;
import com.rarToZip.RarToZip.Component.Tools;
import com.rarToZip.RarToZip.DataClasses.UnrarData;
import com.rarToZip.RarToZip.service.RarService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class RarController {
    @Autowired
    RarService service;
    @Autowired
    Tools tools;
    private Logger log =  LoggerFactory.getLogger(RarController.class);
    @PostMapping(value = "/rar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity inputRar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String key_to_file = tools.generateKey();
        if(file != null) {
            log.info("Method call ok");
            log.info(file.getOriginalFilename());
            try {
                if(!file.getOriginalFilename().contains(".rar")){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("This endpoint get only rar file");
                }
                service.unrar(file.getInputStream(),key_to_file,file.getOriginalFilename());
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
        //InputStream is = new ByteArrayInputStream(service.getUnrarFile(key).toByteArray());
        UnrarData data = service.getUnrarFile(key);
        InputStream is = new ByteArrayInputStream(data.getOutputStream().toByteArray());
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-disposition", "attachment; filename="+data.getOrginal_name());
        IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
    }
}
