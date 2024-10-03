//package com.boot.ksis.controller.sse;
//
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Sinks;
//
//@RestController
//@RequestMapping("/sse")
//public class SseEncodingController {
//
//    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
//
//    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> streamEvents() {
//        return sink.asFlux();
//    }
//
//    public void sendEvent(String message) {
//        sink.tryEmitNext(message);
//    }
//}
