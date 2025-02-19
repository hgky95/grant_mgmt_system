package com.demo.grant_mtmt.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.demo.grant_mtmt.record.TaskAnalysisResponse;
import com.demo.grant_mtmt.record.TaskRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskAnalysisService {
    
    @Value("classpath:/prompts/task_analysis_prompt.txt")
    private Resource taskAnalysisPrompt;

    private final ChatModel chatModel;
    private final GitIngestService gitIngestService;

    @Autowired
    public TaskAnalysisService(GitIngestService gitIngestService, ChatModel chatModel) {
        this.gitIngestService = gitIngestService;
        this.chatModel = chatModel;
    }

    public int analyzeTask(TaskRequest taskRequest) {
        try {
            String repoUrl = taskRequest.repositoryUrl();
            gitIngestService.runGitIngest(repoUrl);
            
            String projectName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get("gitingest-output", projectName + ".txt");
            String repoContent = Files.readString(filePath);

            var outputConverter = new BeanOutputConverter<>(TaskAnalysisResponse.class);
            PromptTemplate promptTemplate = new PromptTemplate(taskAnalysisPrompt,
                Map.of(
                        "taskRequirement", taskRequest.taskRequirement(),
                        "repoContent", repoContent,
                        "format", outputConverter.getFormat()
                )
            );
            Prompt prompt = promptTemplate.create();
            Generation generation = chatModel.call(prompt).getResult();
            TaskAnalysisResponse response = outputConverter.convert(generation.getOutput().getText());
            log.info("Task analysis response: {}", response);
            return response.score();
        } catch (Exception e) {
            log.error("Error analyzing task", e);
            throw new RuntimeException("Error analyzing task", e);
            //TODO handle error
        }
    }
}
