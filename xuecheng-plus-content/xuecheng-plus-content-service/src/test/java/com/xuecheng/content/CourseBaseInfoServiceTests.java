package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseInfoServiceTests {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Test
    public void testCourseBaseInfoService(){
        QueryCourseParamsDto queryCourseParamsDto=new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        PageParams pageParams=new PageParams();
        pageParams.setPageSize(2L);
        pageParams.setPageNo(1L);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }

}
