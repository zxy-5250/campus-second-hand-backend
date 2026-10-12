//package com.secondhand.config;
//
//import com.secondhand.interceptor.LoginInterceptor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web 配置：CORS、静态资源映射、登录拦截器
// */
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Value("${upload.dir:./upload/}")
//    private String uploadDir;
//
//    /** 跨域配置，方便前端 5173 端口直连调试 */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }
//
//    /** 上传目录映射为 /upload/** 静态访问 */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/upload/**")
//                .addResourceLocations("file:" + uploadDir);
//    }
//
//    /** 需要登录的接口统一走拦截器 */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/api/**")
//                .excludePathPatterns(
//                        "/api/auth/login",
//                        "/api/auth/register",
//                        "/api/product/list",
//                        "/api/product/detail/**",
//                        "/api/category/list",
//                        "/api/banner/list",
//                        "/api/comment/list/**",
//                        "/upload/**"
//                );
//    }
//}
package com.secondhand.config;

import com.secondhand.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web 配置：CORS、静态资源映射、登录拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 跨域配置，方便前端 5173 端口直连调试 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /** 上传目录映射为 /upload/** 静态访问 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadAbsPath = System.getProperty("user.dir") + File.separator + "upload" + File.separator;
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadAbsPath);

        registry.addResourceHandler("/pictures/**")
                .addResourceLocations("classpath:/pictures/");
    }

    /** 需要登录的接口统一走拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/product/list",
                        "/api/product/detail/**",
                        "/api/category/list",
                        "/api/banner/list",
                        "/api/comment/list/**",
                        "/upload/**",
                        "/pictures/**",
                        "/api/order/admin/list",  //新增管理员订单列表放行
                        "/api/order/detail/**"    //订单详情放行
                );
    }
}
