package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private ICourseService courseService;

    @GetMapping("/list")
    public R getCourseList(Course course) {
        List<Course> courses = courseService.selectCourseList(course);
        return R.ok(courses);
    }

    @GetMapping("/{id}")
    public R getCourseById(@PathVariable Long id) {
        Course course = courseService.selectCourseById(id);
        return R.ok(course);
    }

    @PostMapping("/add")
    public R addCourse(@RequestBody Course course) {
        courseService.insertCourse(course);
        return R.ok("添加成功");
    }

    @PutMapping("/update")
    public R updateCourse(@RequestBody Course course) {
        courseService.updateCourse(course);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public R deleteCourse(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return R.ok("删除成功");
    }
}