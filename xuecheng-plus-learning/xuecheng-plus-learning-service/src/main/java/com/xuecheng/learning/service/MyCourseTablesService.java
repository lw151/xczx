package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;

public interface MyCourseTablesService {

    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    XcCourseTablesDto getLearningStatus(String userId, Long courseId);
}
