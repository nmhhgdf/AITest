//package com.ai.stdio.server;
//
//import org.springframework.ai.tool.ToolCallbackProvider;
//import org.springframework.ai.tool.method.MethodToolCallbackProvider;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class McpConfiguration {
//
//    @Bean
//    public ToolCallbackProvider weatherTools(OpenMeteoService openMeteoService) {
//        return MethodToolCallbackProvider.builder().toolObjects(openMeteoService).build();
//    }
//
//}
