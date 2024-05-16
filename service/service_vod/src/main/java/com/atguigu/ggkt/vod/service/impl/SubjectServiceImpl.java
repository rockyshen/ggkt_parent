package com.atguigu.ggkt.vod.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.ggkt.exception.GgktException;
import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.vo.vod.SubjectEeVo;
import com.atguigu.ggkt.vod.listener.SubjectListener;
import com.atguigu.ggkt.vod.mapper.SubjectMapper;
import com.atguigu.ggkt.vod.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-06
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Autowired
    private SubjectListener subjectListener;

    @Override
    public List<Subject> selectSubjectList(Long id) {
        // 查询id分类下的全部数据
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Subject> subjectList = baseMapper.selectList(wrapper);

        //遍历list，如果有下一层对象，把has_child值改为true
        for(Subject subject:subjectList){
            Long subjectId = subject.getId();
            boolean isChild = isChildren(subjectId);
            subject.setHasChildren(isChild);
        }
        return subjectList;
    }

    //判断当前id的对象，是否有下一层数据
    private boolean isChildren(Long subjectId) {
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",subjectId);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        // 响应头中写入mime
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("课程分类", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
            //查询课程分类所有数据
            List<Subject> subjectList = baseMapper.selectList(null);

//            System.out.println(subjectList);

            // List<Subject>  --->  List<SubjectEeVo>
            List<SubjectEeVo> subjectEeVoList = new ArrayList<>();
            for(Subject subject:subjectList){
                SubjectEeVo subjectEeVo = new SubjectEeVo();
                BeanUtils.copyProperties(subject,subjectEeVo);
                subjectEeVoList.add(subjectEeVo);
            }

//            System.out.println(subjectEeVoList);

            /* 流式写法
            List<SubjectEeVo> subjectEeVoList = subjectList.stream().map(
                subject -> {
                    SubjectEeVo subjectEeVo = new SubjectEeVo();
                    BeanUtils.copyProperties(subject, subjectEeVo);
                    return subjectEeVo;
                }).collect(Collector.toList());
             */

            //EasyExcel写操作
            EasyExcel.write(response.getOutputStream(),SubjectEeVo.class)
                    .sheet("课程分类")
                    .doWrite(subjectEeVoList);

        } catch (Exception e) {
            throw new GgktException(20001,"导出数据失败");
        }
    }

    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),SubjectEeVo.class,subjectListener).sheet().doRead();
        } catch (IOException e) {
            throw new GgktException(20001,"导入数据失败");
        }
    }
}
