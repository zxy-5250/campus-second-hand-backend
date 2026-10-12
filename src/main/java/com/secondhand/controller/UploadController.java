//package com.secondhand.controller;
//
//import com.secondhand.common.Result;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * 图片上传接口
// * 图片保存在 backend 项目根目录的 upload/ 下，通过 /upload/** 静态访问
// */
//@RestController
//@RequestMapping("/api/upload")
//public class UploadController {
//
//    @Value("${upload.dir:./upload/}")
//    private String uploadDir;
//
//    @PostMapping("/image")
//    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            return Result.error("请选择要上传的文件");
//        }
//        String original = file.getOriginalFilename();
//        String ext = "";
//        if (original != null && original.contains(".")) {
//            ext = original.substring(original.lastIndexOf("."));
//        }
//        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
//        try {
//            File dir = new File(uploadDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            File dest = new File(dir, filename);
//            file.transferTo(dest);
//            Map<String, String> data = new HashMap<>();
//            data.put("url", "/upload/" + filename);
//            return Result.ok("上传成功", data);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("上传失败：" + e.getMessage());
//        }
//    }
//}
package com.secondhand.controller;

import com.secondhand.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传接口
 * 图片保存在 backend 项目根目录的 upload/ 下，通过 /upload/** 静态访问
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        // 项目根目录绝对路径
        String projectRoot = System.getProperty("user.dir");
        String uploadDir = projectRoot + File.separator + "upload" + File.separator;

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir, filename);
            file.transferTo(dest);
            Map<String, String> data = new HashMap<>();
            data.put("url", "/upload/" + filename);
            return Result.ok("上传成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
