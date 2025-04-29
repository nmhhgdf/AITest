package org.example.langchain4j_springboot.controller;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.example.langchain4j_springboot.config.AiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private QwenChatModel qwenChatModel;

    @Autowired
    private QwenStreamingChatModel qwenStreamingChatModel;

    @Autowired
    private AiConfig.Assistant assistant;

    @Autowired
    private AiConfig.AssistantUnique assistantUnique;

    @Autowired
    private AiConfig.AssistantUnique assistantUniquePersistent;

    @RequestMapping("/chat")
    public String test(@RequestParam(defaultValue = "你是谁") String message) {
        return qwenChatModel.chat(message);
    }

    @RequestMapping(value = "/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam(defaultValue = "你是谁") String message) {

        Flux<String> flux = Flux.create(fluxSink -> {
            qwenStreamingChatModel.chat(message, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String s) {
                    fluxSink.next(s);
                }

                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    fluxSink.complete();
                }

                @Override
                public void onError(Throwable throwable) {
                    fluxSink.error(throwable);
                }
            });
        });

        return flux;
    }

    @RequestMapping(value = "/mermory_chat")
    public String memoryChat(@RequestParam(defaultValue = "你是谁") String message) {
        return assistant.chat(message);
    }

    @RequestMapping(value = "/mermory_stream_chat")
    public Flux<String> memoryStreamChat(@RequestParam(defaultValue = "你是谁") String message) {
//        TokenStream stream = assistant.stream(message);
        TokenStream stream = assistant.stream(message, LocalDate.now().toString());

        return Flux.create(fluxSink -> {
            stream.onPartialResponse(s -> fluxSink.next(s))
                    .onCompleteResponse(s -> fluxSink.complete())
                    .onError(e -> fluxSink.error(e))
                    .start();
        });
    }

    @RequestMapping(value = "/mermoryId_chat")
    public String memoryIdChat(@RequestParam(defaultValue = "你是谁") String message,
                               @RequestParam Integer memoryId) {
        return assistantUnique.chat(memoryId, message);
    }

    @RequestMapping(value = "/mermoryId_stream_chat")
    public Flux<String> memoryIdStreamChat(@RequestParam(defaultValue = "你是谁") String message,
                                           @RequestParam Integer memoryId) {
        TokenStream stream = assistantUnique.stream(memoryId, message);

        return Flux.create(fluxSink -> {
            stream.onPartialResponse(fluxSink::next)
                    .onCompleteResponse(s -> fluxSink.complete())
                    .onError(fluxSink::error)
                    .start();
        });
    }

    @RequestMapping(value = "/persistent_mermoryId_chat")
    public String persistentMemoryIdChat(@RequestParam String message,
                               @RequestParam Integer memoryId) {
        return assistantUniquePersistent.chat(memoryId, message);
    }

    @RequestMapping(value = "/persistent_mermoryId_stream_chat")
    public Flux<String> persistentMemoryIdStreamChat(@RequestParam String message,
                                           @RequestParam Integer memoryId) {
        TokenStream stream = assistantUniquePersistent.stream(memoryId, message);

        return Flux.create(fluxSink -> {
            stream.onPartialResponse(fluxSink::next)
                    .onCompleteResponse(s -> fluxSink.complete())
                    .onError(fluxSink::error)
                    .start();
        });
    }

}
