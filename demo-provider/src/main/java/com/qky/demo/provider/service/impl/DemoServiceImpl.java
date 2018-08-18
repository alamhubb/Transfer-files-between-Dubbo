package com.qky.demo.provider.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qky.demo.api.service.DemoService;

import java.io.*;

@Service(
        version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }

    @Override
    public String convertFile(byte[] bytes) {
        System.out.println(bytes);
        System.out.println(123);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            String filePath = "d:/file";
            File dir = new File(filePath);
            if (!dir.exists() || !dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "/a.jpg");
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            System.out.println(file.getName());
            System.out.println(file.length());
            System.out.println(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return "失败";
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return "成功";
    }

}
