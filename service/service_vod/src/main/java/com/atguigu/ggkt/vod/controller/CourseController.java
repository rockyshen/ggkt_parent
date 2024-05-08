package com.atguigu.ggkt.vod.controller;


import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.result.Result;
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

    //1 点播课程列表
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
}

