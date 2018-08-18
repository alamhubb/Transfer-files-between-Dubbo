package com.qky.demo.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qky.demo.api.service.DemoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
public class UploadController {
    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            url = "dubbo://localhost:12345")
    private DemoService demoService;

    @PostMapping("upload")
    public String upload(MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (Objects.isNull(file) || file.isEmpty()) {
                return "文件为空，请重新上传";
            }
            try {
                System.out.println(file.getName());
                System.out.println(file.getSize());
                byte[] bytes = file.getBytes();
                return demoService.convertFile(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return "失败";
            }
        }
        return "失败";
    }
}
