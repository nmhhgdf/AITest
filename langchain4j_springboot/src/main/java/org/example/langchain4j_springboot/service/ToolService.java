package org.example.langchain4j_springboot.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ToolService {

    @Tool("有多少名字叫")
    public Integer nameCount(@P("地区") String area,
                             @P("姓名") String name) {
        // 调用三方接口，查询DB
        return 10;
    }

    @Tool("退票业务")
    public String cancelBooking(@P("预定号") String number,
                                @P("姓名") String name) {
        log.info("执行退票业务...");
        log.info("预定号 = {}, 姓名 = {}", number, name);

        return "退票成功";
    }

}
