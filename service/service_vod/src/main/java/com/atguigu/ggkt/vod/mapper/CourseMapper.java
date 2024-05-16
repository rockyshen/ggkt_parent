package com.atguigu.ggkt.vod.mapper;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.vo.vod.CoursePublishVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-05-08
 */
public interface CourseMapper extends BaseMapper<Course> {

    CoursePublishVo selectCoursePublishVoById(Long id);  //传递给xml时，如果有多值的话，要用@Param注解指定
}
