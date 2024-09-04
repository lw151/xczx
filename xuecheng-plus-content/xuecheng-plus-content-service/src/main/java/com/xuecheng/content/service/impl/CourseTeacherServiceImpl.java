package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    @Transactional
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        if(id==null){
            CourseTeacher teacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher,teacher);
            teacher.setCreateDate(LocalDateTime.now());
            int flag = courseTeacherMapper.insert(teacher);
            if(flag<=0){
                XueChengPlusException.cast("修改失败");
            }
            return getCourseTeacher(teacher);
        }else {
            CourseTeacher teacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(courseTeacher,teacher);
            int flag = courseTeacherMapper.updateById(teacher);
            if(flag<=0){
                XueChengPlusException.cast("修改失败");
            }
            return getCourseTeacher(teacher);
        }
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId,teacherId)
                .eq(CourseTeacher::getCourseId,courseId);
        int flag = courseTeacherMapper.delete(queryWrapper);
        if(flag<0){
            XueChengPlusException.cast("删除失败");
        }

    }

    public CourseTeacher getCourseTeacher(CourseTeacher courseTeacher) {
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }
}
