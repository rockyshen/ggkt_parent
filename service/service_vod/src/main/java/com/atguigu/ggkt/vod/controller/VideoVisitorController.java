package com.atguigu.ggkt.vod.controller;


import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vod.service.VideoService;
import com.atguigu.ggkt.vod.service.VideoVisitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 视频来访者记录表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-10
 */
@Api(tags = "课程统计接口")
@RestController
@RequestMapping("/admin/vod/videoVisitor")
@CrossOrigin
public class VideoVisitorController {
    @Autowired
    private VideoVisitorService videoVisitorService;

    // 根据传递的开始、结束时间，查video_vistor表，统计时间段内人数
    @ApiOperation("课程统计")
    @GetMapping("findCount/{courseId}/{startDate}/{endDate}")
    public Result findCount(@PathVariable Long courseId,
                            @PathVariable String startDate,
                            @PathVariable String endDate
                            ) {

        Map<String,Object> map = videoVisitorService.findCount(courseId,startDate,endDate);
        return Result.ok(map);

    }
}

