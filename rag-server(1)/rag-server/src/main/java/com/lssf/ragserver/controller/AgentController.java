package com.lssf.ragserver.controller;
import com.lssf.ragserver.agent.ReactAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {
    @Autowired
    private ReactAgent reactAgent;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return reactAgent.chat(message);
    }
}