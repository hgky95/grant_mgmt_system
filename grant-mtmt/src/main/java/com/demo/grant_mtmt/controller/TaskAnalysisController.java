package com.demo.grant_mtmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.grant_mtmt.record.TaskRequest;
import com.demo.grant_mtmt.service.TaskAnalysisService;

@RestController
@RequestMapping("/task-analyzer")
public class TaskAnalysisController {

    private final TaskAnalysisService taskAnalysisService;

    @Autowired
    public TaskAnalysisController(TaskAnalysisService taskAnalysisService) {
        this.taskAnalysisService = taskAnalysisService;
    }

    @PostMapping("/analyze")
    public int analyzeTask(@RequestBody TaskRequest taskRequest) throws Exception {
        return taskAnalysisService.analyzeTask(taskRequest);
    }
}