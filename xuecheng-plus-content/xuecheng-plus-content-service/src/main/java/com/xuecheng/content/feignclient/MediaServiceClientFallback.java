package com.xuecheng.content.feignclient;

import com.xuecheng.base.exception.XueChengPlusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class MediaServiceClientFallback implements MediaServiceClient{

    @Override
    public String uploadFile(MultipartFile upload, String objectName) {
        return null;
    }
}
