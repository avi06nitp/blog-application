package com.BlogApp.services.Impl;

import com.BlogApp.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File Name
        String name = file.getOriginalFilename(); // abc.png

        // Random name generate file
        String randomId = UUID.randomUUID().toString();
        String fileName1 = randomId.concat(name.substring(name.indexOf(".")));

        // Full path
        String filePath = path + File.separator + fileName1;

        // Create folder if not created
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }

        //  File copy
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName1;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        InputStream is = new FileInputStream(fullPath);

        // db logic to return inputstream
        return is;
    }
}
