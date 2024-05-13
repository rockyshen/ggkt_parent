package com.atguigu.ggkt.vod.api;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.vod.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "课程对外暴露接口")
@RestController
@RequestMapping("/api/vod/course")
public class CourseApiController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("根据关键字查询课程")
    @GetMapping("inner/findByKeyword/{keyword}")
    public List<Course> findByKeyword(@PathVariable String keyword) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.like("title",keyword);
        List<Course> courseList = courseService.list(wrapper);
        return courseList;
    }

    //根据课程ID，查询课程信息
    /*
    注意：service_vod模块的controller里面也有相同方法，但是注意，是提供给后台管理系统的。
    两者有差异！此处方法是对接微信公众号！所以放在api下！
     */
    @ApiOperation("根据ID查询课程")
    @GetMapping("inner/getById/{courseId}")
    public Course getById(@PathVariable Long courseId) {
        Course course = courseService.getById(courseId);
        return course;
    }
}
