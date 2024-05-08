package com.atguigu.ggkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.ggkt.vo.vod.CourseQueryVo;
import com.atguigu.ggkt.vod.mapper.CourseMapper;
import com.atguigu.ggkt.vod.service.CourseService;
import com.atguigu.ggkt.vod.service.SubjectService;
import com.atguigu.ggkt.vod.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-08
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SubjectService subjectService;

    // 点播课程列表，条件查询+分页
    @Override
    public Map<String, Object> findPageCourse(Page<Course> pageParam, CourseQueryVo courseQueryVo) {
        //从条件对象中取值
        Long teacherId = courseQueryVo.getTeacherId();
        Long subjectId = courseQueryVo.getSubjectId();   // 二层分类
        Long subjectParentId = courseQueryVo.getSubjectParentId();   // 一层分类
        String title = courseQueryVo.getTitle();

        // 封装queryWrapper
        QueryWrapper<Course> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(teacherId)){
            wrapper.eq("teacher_id",teacherId);
        }

        if(!StringUtils.isEmpty(subjectId)){
            wrapper.eq("subject_id",subjectId);
        }

        if(!StringUtils.isEmpty(subjectParentId)){
            wrapper.eq("subject_parent_id",subjectParentId);
        }

        if(!StringUtils.isEmpty(title)){
            wrapper.like("title",title);
        }

        Page<Course> pageModel = courseMapper.selectPage(pageParam, wrapper);

        long totalCount = pageModel.getTotal();
        long totalPage = pageModel.getPages();
        List<Course> records = pageModel.getRecords();

        //course中的teacher_id subject_id subject_parent_id查出内容
        //1 遍历courseList，针对每个course，查到上述几个值，封装到BaseEntity
        records.stream().forEach(item -> {
            this.getNameById(item);
        });

        //封装到Map中
        HashMap<String, Object> map = new HashMap<>();
        map.put("records",records);
        map.put("totalCount",totalCount);
        map.put("totalPage",totalPage);

        return map;
    }

    private Course getNameById(Course course) {
        //1 根据讲师id,获取讲师名称
        Teacher teacher = teacherService.getById(course.getTeacherId());
        if(teacher != null) {
            String teacherName = teacher.getName();
            course.getParam().put("teacherName",teacherName);
        }

        //2 根据分类id，获取课程分类名称（一层、二层）
        Subject subject1 = subjectService.getById(course.getSubjectParentId());
        if(subject1 != null) {
            String subjectParentName = subject1.getTitle();
            course.getParam().put("subjectParentTitle",subjectParentName);
        }

        Subject subject2 = subjectService.getById(course.getSubjectId());
        if(subject2 != null) {
            String subjectName = subject2.getTitle();
            course.getParam().put("subjectTitle",subjectName);
        }
        return course;
    }
}
