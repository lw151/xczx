package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

public interface CoursePublishService {

   CoursePreviewDto getCoursePreviewInfo(Long courseId);
    void publish(Long companyId,Long courseId);

     File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    void  uploadCourseHtml(Long courseId,File file);

    CoursePublish getCoursePunlish(Long courseId);
}
