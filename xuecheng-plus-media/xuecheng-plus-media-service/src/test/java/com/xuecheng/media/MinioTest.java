package com.xuecheng.media;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import io.minio.messages.Upload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MinioTest {

   static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("D:\\picture\\a4beb967-60e4-4b07-9578-5c6f85305146.jpg")//指定本地文件
                    .object("1.picture").build();//对象名
            minioClient.uploadObject(uploadObjectArgs);
            log.info("上传成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("上传失败");
        }

    }


}
