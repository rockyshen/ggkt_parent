package com.atguigu.ggkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.model.vod.CourseDescription;
import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.ggkt.vo.vod.CourseFormVo;
import com.atguigu.ggkt.vo.vod.CoursePublishVo;
import com.atguigu.ggkt.vo.vod.CourseQueryVo;
import com.atguigu.ggkt.vod.mapper.CourseDescriptionMapper;
import com.atguigu.ggkt.vod.mapper.CourseMapper;
import com.atguigu.ggkt.vod.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
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

    @Autowired
    private CourseDescriptionService courseDescriptionService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private ChapterService chapterService;


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
        //最好用Ipage<Course>

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

    // 添加课程信息 = 基本信息 + 描述信息
    @Override
    public Long saveCourseInfo(CourseFormVo courseFormVo) {
        // 基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseFormVo,course);
        baseMapper.insert(course);

        // 描述信息
        CourseDescription description = new CourseDescription();
        description.setDescription(courseFormVo.getDescription());

        /*返回上一部修改时，课程描述没有回显，是空的？
        这里有个bug，老师讲的时，课程描述id和课程id相等，但是到我的数据库表中就不想等
        问题出在下面的代码，设置id，应该用course.getId，而不是courseFormVo的id
         */
        description.setId(course.getId());  // 课程描述表的主键策略是Input,需要自己输入
        description.setCourseId(course.getId());  // 让课程描述表中id和course_id相同
        courseDescriptionService.save(description);
        return course.getId();
    }

    //根据Id获取课程信息
    @Override
    public CourseFormVo getCourseInfoById(Long id) {
        Course course = baseMapper.selectById(id);
        if (course == null) {
            return null;
        }
        //基于course查询描述信息
        CourseDescription courseDescription = courseDescriptionService.getById(id);

        //封装到CourseFormVo
        CourseFormVo courseFormVo = new CourseFormVo();
        BeanUtils.copyProperties(course,courseFormVo);

        if(courseDescription != null){
            courseFormVo.setDescription(courseDescription.getDescription());
        }
        return courseFormVo;
    }

    //修改课程信息
    @Override
    public void updateCourseId(CourseFormVo courseFormVo) {
        //修改基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseFormVo,course);
        baseMapper.updateById(course);

        //修改课程描述信息
        CourseDescription description = new CourseDescription();
        description.setDescription(courseFormVo.getDescription());
        //需要设置descripti_id，否则怎么updateById呢？
        description.setId(course.getId());
        courseDescriptionService.updateById(description);
    }

    //根据id查询课程发布信息（包括：讲师、分类，用sql）
    @Override
    public CoursePublishVo getCoursePublishVo(Long id) {
        CoursePublishVo coursePublishVo = baseMapper.selectCoursePublishVoById(id);
        return coursePublishVo;
    }

    //课程最终发布，更新状态：status
    @Override
    public void publishCourse(Long id) {
        Course course = baseMapper.selectById(id);
        course.setStatus(1);  //0表示未发布，1表示已发布
        course.setPublishTime(new Date());
        baseMapper.updateById(course);
    }

    // 删除课程
    @Override
    public void removeCourseId(Long id) {

        //Todo 涉及删除多表的情况，要加事务啊！

        // 根据课程id,删除小节表  获取video对象
        videoService.removeVideoByCourseId(id);
        // 根据课程id,删除章节
        chapterService.removeChapterByCourseId(id);
        // 根据课程id,删除描述
        courseDescriptionService.removeById(id);   //课程描述id 等于 课程id（注意：前面的不是，从26号 redis开始修复了bug
        // 根据课程id,删除课程基本信息
        baseMapper.deleteById(id);
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
