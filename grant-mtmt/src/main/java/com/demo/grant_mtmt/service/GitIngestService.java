package com.demo.grant_mtmt.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GitIngestService {
    
    public void runGitIngest(String path) throws Exception {
        try {
            String projectName = path.split("/")[path.split("/").length - 1];
            ProcessBuilder processBuilder = new ProcessBuilder("gitingest", path, "-o", "gitingest-output/" + projectName + ".txt", "-e", "/LICENSE");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new Exception("GitIngest process failed");
            }
            log.info("GitIngest process completed successfully");
        } catch (Exception e) {
            log.error("GitIngest process failed", e);
            throw e;
        }
        
    }
}
