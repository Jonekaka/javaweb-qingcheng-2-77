package com.qingcheng.controller.file;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private HttpServletRequest request;
/*本地文件上传，取参数file*/
    @PostMapping("/native")
    public String nativeUpload(@RequestParam("file") MultipartFile file){
        System.out.println("native......");
        /*确定用户上传的路径，写入的路径，获得img的绝对路径*/
        String path=  request.getSession().getServletContext().getRealPath("img");
        /*存储路径，获得文件的原始文件名*/
        String filePath= path +"/"+file.getOriginalFilename();
        File desFile=new File(filePath);
        /*确保目录一定存在，不存在就创建*/
        if(!desFile.getParentFile().exists() ){
            desFile.mkdirs();
        }
        try {
            /*写文件的细节，已经封装好*/
            file.transferTo(desFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*最终返回上传后的文件名*/
        return "http://localhost:9101/img/"+file.getOriginalFilename();
    }

    @Autowired
    private OSSClient ossClient;

    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file,String folder){
        String bucketName="qcshangchenglilia";
        String fileName = folder+"/"+ UUID.randomUUID()+ file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName,fileName,file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "https://"+bucketName+".oss-cn-beijing.aliyuncs.com/"+fileName;
    }


}
