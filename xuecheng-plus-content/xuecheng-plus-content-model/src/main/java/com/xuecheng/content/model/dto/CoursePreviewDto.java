package com.xuecheng.content.model.dto;


import lombok.Data;

import java.util.List;

/*
*
* 课程预览模型类
*
* */
@Data
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息暂时不加...

}
