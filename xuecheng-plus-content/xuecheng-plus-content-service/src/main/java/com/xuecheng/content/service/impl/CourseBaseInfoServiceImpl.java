package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        //创建分页查询对象 参数：当前页码，每页记录数
        Page<CourseBase> courseBasePage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(courseBasePage, queryWrapper);
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<>();
        courseBasePageResult.setItems(records);//数据列表
        courseBasePageResult.setCounts(total);//总记录数
        courseBasePageResult.setPage(pageParams.getPageNo());//当前页码
        courseBasePageResult.setPageSize(pageParams.getPageSize());//每页记录数
        return courseBasePageResult;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //向课程基本信息表插入数据
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto, courseBaseNew);
        //设置当前时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //设置机构id
        courseBaseNew.setCompanyId(companyId);
        /*
        * setPic
        *
        * */
//        courseBaseNew.setPic(dto.getPic());
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            XueChengPlusException.cast("添加课程失败");
        }

        //向课程营销表插入数据
        CourseMarket courseMarketNew = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto, courseMarketNew);

        courseMarketNew.setId(courseId); //主键的课程id
        int i = saveCourseMarket(courseMarketNew);
        if (i <= 0) {
            XueChengPlusException.cast("保存课程营销信息失败");
        }
        return getCourseBaseInfo(courseId);
    }

    private CourseBaseInfoDto getCourseBaseInfo(long courseId) {

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setStName(courseCategory.getName());
        CourseCategory courseCategory1 = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        courseBaseInfoDto.setMtName(courseCategory1.getName());
        return courseBaseInfoDto;
    }

    //单独写一个方法，保存营销信息, 存在则更新，不存在则添加
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        //参数的合法性检验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isBlank(charge)) {
            XueChengPlusException.cast("收费规则为空");
        }
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0 || courseMarketNew.getOriginalPrice() <= 0) {
                XueChengPlusException.cast("课程价格不能为空且必须大于0");
            }
        }

        //从数据库查询营销信息，存在则更新，不存在则添加
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket == null) {
            return courseMarketMapper.insert(courseMarketNew);
        } else {
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarket);
        }
    }

    @Override
    public CourseBaseInfoDto getCourseBaseById(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setStName(courseCategory.getName());
        CourseCategory courseCategory1 = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        courseBaseInfoDto.setMtName(courseCategory1.getName());
        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto modifyCourseBase(Long companyId, EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }

        if (!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("本机构只能修改本机构课程");
        }

        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
//        courseBase.setPic(editCourseDto.getPic());
        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0) {
            XueChengPlusException.cast("修改课程失败");
        }
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        saveCourseMarket(courseMarket);
        CourseBaseInfoDto courseBaseById = getCourseBaseById(courseId);
        return courseBaseById;
    }


    @Transactional
    @Override
    public void delectCourse(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBase.getCompanyId()))
            XueChengPlusException.cast("只允许删除本机构的课程");
        // 删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(teacherLambdaQueryWrapper);
        // 删除课程计划
        LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(teachplanLambdaQueryWrapper);
        // 删除营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程基本信息
        courseBaseMapper.deleteById(courseId);

    }
}