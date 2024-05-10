package com.atguigu.ggkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.VideoVisitor;
import com.atguigu.ggkt.vo.vod.VideoVisitorCountVo;
import com.atguigu.ggkt.vo.vod.VideoVisitorVo;
import com.atguigu.ggkt.vod.mapper.VideoVisitorMapper;
import com.atguigu.ggkt.vod.service.VideoVisitorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频来访者记录表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-10
 */
@Service
public class VideoVisitorServiceImpl extends ServiceImpl<VideoVisitorMapper, VideoVisitor> implements VideoVisitorService {

    // 根据传递的开始、结束时间，查video_vistor表，统计时间段内人数,用xml中写sql
    @Override
    public Map<String, Object> findCount(Long courseId, String startDate, String endDate) {
        List<VideoVisitorCountVo> videoVisitorCountVoList = baseMapper.findCount(courseId,startDate,endDate);

        //封装数据
        Map<String, Object> map = new HashMap<>();
        //创建两个list,一个代表日期x轴，一个代表观看人数y轴
//        List<String> dateList = new ArrayList<>();
//        List<Integer> countList = new ArrayList<>();

        List<String> dateList = videoVisitorCountVoList.stream().map(item -> {
            String date = item.getJoinTime();
            return date;
        }).collect(Collectors.toList());

        List<Integer> countList = videoVisitorCountVoList.stream().map(item -> {
            Integer count = item.getUserCount();
            return count;
        }).collect(Collectors.toList());

        map.put("xData",dateList);
        map.put("yData",countList);

        return map;
    }
}
