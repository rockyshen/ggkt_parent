package com.atguigu.ggkt.vod.controller;


import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vo.vod.CourseFormVo;
import com.atguigu.ggkt.vo.vod.CoursePublishVo;
import com.atguigu.ggkt.vo.vod.CourseQueryVo;
import com.atguigu.ggkt.vod.service.CourseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-08
 */
@Api(tags = "课程管理接口")
@RestController
@RequestMapping("/admin/vod/course")
@CrossOrigin
public class CourseController {
    @Autowired
    private CourseService courseService;

    //1 课程列表
    @ApiOperation("点播课程列表")
    @GetMapping("{current}/{limit}")
    public Result courseList(@PathVariable Long current,
                             @PathVariable Long limit,
                             CourseQueryVo courseQueryVo) {
        Page<Course> pageParam = new Page<>(current,limit);
        // 当不确定返回值类型时，选Map,取值放值都方便
        Map<String, Object> map = courseService.findPageCourse(pageParam,courseQueryVo);
        return Result.ok(map);
    }

    //2 添加课程基本信息
    @ApiOperation("添加课程基本信息")
    @PostMapping("save")
    public Result save(@RequestBody CourseFormVo courseFormVo){
        Long courseId = courseService.saveCourseInfo(courseFormVo);  // 因为后续课程描述等都需要关联课程ID，所以返回这个比较好
        return Result.ok(courseId);
    }

    //3 根据ID获取课程信息
    @ApiOperation("根据Id获取课程信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        CourseFormVo courseFormVo = courseService.getCourseInfoById(id);
        return Result.ok(courseFormVo);
    }

    //4 修改课程信息
    @ApiOperation("修改课程信息")
    @PostMapping("update")
    public Result update(@RequestBody CourseFormVo courseFormVo){
        courseService.updateCourseId(courseFormVo);
        //返回course_id，前端要用
        return Result.ok(courseFormVo.getId());
    }

    //5 根据课程id，查询课程发布信息（包括：讲师、分类，用sql写）
    @ApiOperation("根据id查询发布课程信息")
    @GetMapping("getCoursePublishVo/{id}")
    public Result getCoursePublishVo(@PathVariable Long id) {
        CoursePublishVo coursePublishVo = courseService.getCoursePublishVo(id);
        return Result.ok(coursePublishVo);
    }

    //6 课程最终发布，更新状态：status
    @ApiOperation("课程最终发布")
    @PutMapping("publishCourse/{id}")
    public Result publishCourse(@PathVariable Long id){
        courseService.publishCourse(id);
        return Result.ok(null);
    }

    //7 删除课程
    @ApiOperation("删除课程")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        courseService.removeCourseId(id);
        return Result.ok(null);
    }

}

