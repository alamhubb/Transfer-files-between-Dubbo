# dubbo服务之间传输File Transfer files between Dubbo 

#### 项目介绍

dubbo之间传输File文件，将File转成byte数组传输，附上代码

项目没有使用zokkeeper

先启动服务端代码，之后才能启动消费端，否则的话是无法访问服务端的，

启动项目后输入http://localhost:8080/1 

点击页面中的小相机按钮上传图片，然后点击上传按钮

图片则会上传到电脑的 D:\file\a.jpg


### 参考


dubbo-spring-boot-starter使用的是阿里官方整合的springboot框架

https://github.com/alibaba/dubbo-spring-boot-starter/blob/master/README_zh.md

dubbo-spring-boot-starter使用方式参考了以下文章

https://blog.csdn.net/qq_36890499/article/details/80858663

Java 文件和byte数组转换 参考：

https://www.cnblogs.com/kgdxpr/p/3595518.html



### 代码部分：

前端部分有很多没有用的代码，不需要关注，

重要代码

前端：

使用了vue和mint-ui

### 首页代码


### html：


相机按钮

        <label for="img" class="icon iconfont icon-add" style="font-size: 80px;"></label>
大加号上传图片样式

        <input id="img" type="file" accept="image/*" style="opacity: 0;width: 0;height: 0" @change="upload">
上传按钮

        <mt-button style="margin-left: 50px" @click="postImgs">上传</mt-button>

### js:


    data: {     imgs: [],//图片文件数组
            talkImgs: [],//图片名数组
            index: 0 //图片数量 好像没用到大写尴尬
        },
    methods: {
            
            //上传图片后出发的方法
            upload(obj) {
                const reader = new FileReader()
                reader.readAsDataURL(obj.target.files[0]);
                reader.onload = function () {
                    app.imgs.push({img: obj.target.files[0], src: this.result})
                    app.talkImgs.push({imgSrc: obj.target.files[0].name})
                    app.index++
                }
            },
            //点击上传按钮执行的方法
            postImgs() {
                let formData = new FormData();
                let imgs = []
                for (let img of this.imgs) {
                    imgs.push(img.img)
                    formData.append("files", img.img)
                }
                axios.post('/upload', formData)
                    .then(response => {
                        console.log(response.data);
                    })
                    .catch(function (error) {
                        alert("上传失败");
                        console.log(error);
                    });
            }

### java api:


    public interface DemoService {

    String sayHello(String name);

    String convertFile(byte[] bytes);
    }


### java 消费端


    @Controller
    public class IndexController {
    @RequestMapping("1")
    public String index() {
        System.out.println("123");
        return "index";
    }
    }

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

### java服务端：


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

### pom文件


```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.qky</groupId>
    <artifactId>dubbo-starter-demo</artifactId>
    <packaging>pom</packaging>
    <version>0.0.1-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <modules>
        <module>demo-api</module>
        <module>demo-consumer</module>
        <module>demo-provider</module>
    </modules>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>0.2.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```




