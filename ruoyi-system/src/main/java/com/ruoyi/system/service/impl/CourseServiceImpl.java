package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.mapper.CourseMapper;
import com.ruoyi.system.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> selectCourseList(Course course) {
        return courseMapper.selectCourseList(course);
    }

    @Override
    public Course selectCourseById(Long id) {
        return courseMapper.selectCourseById(id);
    }

    @Override
    @Transactional
    public int insertCourse(Course course) {
        course.setCreateTime(DateUtils.getNowDate());
        course.setUpdateTime(DateUtils.getNowDate());
        return courseMapper.insertCourse(course);
    }

    @Override
    @Transactional
    public int updateCourse(Course course) {
        course.setUpdateTime(DateUtils.getNowDate());
        return courseMapper.updateCourse(course);
    }

    @Override
    @Transactional
    public int deleteCourseById(Long id) {
        return courseMapper.deleteCourseById(id);
    }
}