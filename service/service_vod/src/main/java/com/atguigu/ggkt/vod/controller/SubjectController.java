package com.atguigu.ggkt.vod.controller;


import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vod.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-06
 */
@Api(tags = "课程分类接口")
@RestController
@RequestMapping("/admin/vod/subject")
//@CrossOrigin
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    //1 课程分类列表
    /*懒加载，先查第一层 select * from subject where parent_id=?
    传递一个id，找到parent_id等于这个值的子课程
     */

    @ApiOperation("课程分类列表")
    @GetMapping("getChildSubject/{id}")
    public Result getChildSubject(@PathVariable Long id){
        List<Subject> list = subjectService.selectSubjectList(id);
        return Result.ok(list);
    }

    @ApiOperation("课程分类导出")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response){
        subjectService.exportData(response);
    }

    @ApiOperation("课程分类导入")
    @PostMapping("importData")
    public Result<Object> importData(MultipartFile file){
        subjectService.importData(file);
        return Result.ok(null);
    }
}

