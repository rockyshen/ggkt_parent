package com.atguigu.ggkt.vod.controller;


import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vo.vod.TeacherQueryVo;
import com.atguigu.ggkt.vod.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-03
 */

@Api(tags = "讲师管理接口")
@RestController
@RequestMapping("/admin/vod/teacher")
//@CrossOrigin
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    //1 查询所有讲师
    @ApiOperation(value = "查询所有讲师")
    @GetMapping("findAll")
    public Result findAllTeacher() {
        List<Teacher> teachers = teacherService.list();
        return Result.ok(teachers);
    }

    //2 逻辑删除讲师
    @ApiOperation(value = "逻辑删除讲师")
    @DeleteMapping("remove/{id}")
    public Result removeTeacher(@PathVariable Long id){
        boolean isSuccess = teacherService.removeById(id);
        if(isSuccess){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }

    //3 条件查询分页
    @ApiOperation(value = "条件查询分页")
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findPage(@PathVariable Long current,
                           @PathVariable Long limit,
                           @RequestBody(required = false) TeacherQueryVo teacherQueryVo){

        Page<Teacher> pageParam = new Page<>(current,limit);

        if(teacherQueryVo == null){
            //查询全部
            IPage<Teacher> pageModel = teacherService.page(pageParam,null);
            return Result.ok(pageModel);
        }else{
            //获取条件值，进行非空判断，条件封装
            String name = teacherQueryVo.getName();
            Integer level = teacherQueryVo.getLevel();
            String joinDateBegin = teacherQueryVo.getJoinDateBegin();
            String joinDateEnd = teacherQueryVo.getJoinDateEnd();

            QueryWrapper<Teacher> wrapper = new QueryWrapper<>();

            if(!StringUtils.isEmpty(name)){
                wrapper.like("name",name);
            }

            if(!StringUtils.isEmpty(level)){
                wrapper.eq("level",level);
            }

            if(!StringUtils.isEmpty(joinDateBegin)){
                wrapper.ge("join_date",joinDateBegin);
            }

            if(!StringUtils.isEmpty(joinDateEnd)){
                wrapper.le("join_date",joinDateEnd);
            }

            IPage<Teacher> pageModel = teacherService.page(pageParam,wrapper);

            return Result.ok(pageModel);
        }
    }

    //4 添加讲师
    @ApiOperation(value = "添加讲师")
    @PostMapping("saveTeacher")
    public Result saveTeacher(@RequestBody Teacher teacher){
        boolean isSuccess = teacherService.save(teacher);

        if(isSuccess){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }

    //5 修改讲师
    @ApiOperation("根据id查询")
    @GetMapping("getTeacher/{id}")
    public Result getTeacher(@PathVariable Long id){
        Teacher teacher = teacherService.getById(id);
        //Todo 这里有问题啊，万一id是错的，难道也return ok？
        return Result.ok(teacher);
    }

    @ApiOperation("修改最终实现")
    @PostMapping("updateTeacher")
    public Result updateTeacher(@RequestBody Teacher teacher){
        boolean isSuccess = teacherService.updateById(teacher);
        if(isSuccess){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }

    //6 批量删除讲师
    @ApiOperation("批量删除讲师")
    @DeleteMapping("removeBatch")
    public Result removeBatch(@RequestBody List<Long> idList){
        boolean isSuccess = teacherService.removeByIds(idList);

        if(isSuccess){
            return Result.ok(null);
        }else{
            return Result.fail(null);
        }
    }
}

