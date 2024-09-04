package com.xuecheng.content;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URL;


public class testPath {

    @Test
    public void t(){
        URL resource = this.getClass().getResource("/");
        System.out.println(resource);
        String path=this.getClass().getResource("/").getPath();
        System.out.println(path);
    }
}
