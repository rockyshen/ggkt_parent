package com.atguigu.ggkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.ggkt.vod.mapper.VideoMapper;
import com.atguigu.ggkt.vod.service.VideoService;
import com.atguigu.ggkt.vod.service.VodService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-08
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private VodService vodService;


    //根据课程id删除小节，并要删除课程包含所有小节里的视频
    @Override
    public void removeVideoByCourseId(Long id) {
        //根据课程id查询所有小节
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",id);
        List<Video> videoList = baseMapper.selectList(wrapper);

        //遍历所有小节的集合，得到每个小节，获取每个小节的视频id
        videoList.stream().forEach(video -> {
            String videoSourceId = video.getVideoSourceId();
            //判断视频id是否为空，不为空，删除腾讯云中的视频
            if (!StringUtils.isEmpty(videoSourceId)){
                vodService.removeVideo(videoSourceId);
            }
        });
        //删除课程中的所有小节
        baseMapper.delete(wrapper);
    }

    // 根据小节id，删除小节，同步删除小节里的视频
    @Override
    public void removeVideoById(Long id) {
        Video video = baseMapper.selectById(id);
        String videoSourceId = video.getVideoSourceId();
        if (!StringUtils.isEmpty(videoSourceId)){
            vodService.removeVideo(videoSourceId);  // 去腾讯云的管理后台删除视频
        }
        baseMapper.deleteById(id);   //最后删除video对象的一整行记录
    }
}
