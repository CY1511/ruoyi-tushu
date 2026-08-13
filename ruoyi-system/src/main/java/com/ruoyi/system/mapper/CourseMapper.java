package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Course;

@Mapper
public interface CourseMapper {
    List<Course> selectCourseList(Course course);
    Course selectCourseById(Long id);
    int insertCourse(Course course);
    int updateCourse(Course course);
    int deleteCourseById(Long id);
    List<String> selectUniversities();
    List<String> selectMajors(String university);
}