package com.atguigu.ggkt.vod.mapper;

import com.atguigu.ggkt.model.vod.VideoVisitor;
import com.atguigu.ggkt.vo.vod.VideoVisitorCountVo;
import com.atguigu.ggkt.vo.vod.VideoVisitorVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 视频来访者记录表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-05-10
 */
public interface VideoVisitorMapper extends BaseMapper<VideoVisitor> {

    // 统计在开始时间-完成时间范围内，观看人数
    // startDate 和 endDate可能是null,利用<where> + <if test=""> 来拼接

    List<VideoVisitorCountVo> findCount(@Param("courseId")Long courseId,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate);
}
