package com.atguigu.ggkt.client.course;

import com.atguigu.ggkt.model.vod.Course;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("service-vod")
public interface CourseFeignClient {
    @GetMapping("/api/vod/course/inner/findByKeyword/{keyword}")
    List<Course> findByKeyword(@PathVariable String keyword);

}
