package com.atguigu.ggkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Chapter;
import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.ggkt.vo.vod.ChapterVo;
import com.atguigu.ggkt.vo.vod.VideoVo;
import com.atguigu.ggkt.vod.mapper.ChapterMapper;
import com.atguigu.ggkt.vod.service.ChapterService;
import com.atguigu.ggkt.vod.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-08
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoService videoService;

    //1 大纲列表（章节和小节）
    @Override
    public List<ChapterVo> getTreeList(Long courseId) {
        //
        List<ChapterVo> finalChapterList = new ArrayList<>();

        //根据courseId获取所有Chapter对象
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id",courseId);
        List<Chapter> chapterList = baseMapper.selectList(chapterQueryWrapper);

        System.out.println(chapterList);

        //根据courseId获取Video对象
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", courseId);
        List<Video> videoList = videoService.list(videoQueryWrapper);

        //封装章节
        //1.传统计数for循环
//        for (int i = 0; i < chapterList.size(); i++){
//            Chapter chapter = chapterList.get(i);
//            ChapterVo chapterVo = new ChapterVo();
//            BeanUtils.copyProperties(chapter,chapterVo);
//            finalChapterList.add(chapterVo);
//        }

        //2.map进行遍历，不合适！用forEach
//        chapterList.stream().map(chapter -> {
//            //得到课程每个章节
//            ChapterVo chapterVo = new ChapterVo();
//            BeanUtils.copyProperties(chapter,chapterVo);
//            finalChapterList.add(chapterVo);
//            return chapterVo;
//        }).collect(Collectors.toList());

        chapterList.stream().forEach(chapter -> {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter,chapterVo);
            finalChapterList.add(chapterVo);

            //封装章节内的小节
            List<VideoVo> videoVoList = new ArrayList<>();
            videoList.stream().forEach(video -> {
                //判断小节是哪个章节下的
                if (chapter.getId().equals(video.getChapterId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video,videoVo);
                    videoVoList.add(videoVo);
                }
            chapterVo.setChildren(videoVoList);
            });
        });
        return finalChapterList;
    }
}
