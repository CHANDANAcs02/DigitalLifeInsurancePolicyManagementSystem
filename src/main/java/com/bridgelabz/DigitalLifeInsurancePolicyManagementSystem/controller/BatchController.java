package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.controller;

import java.io.File;
import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job customerImportJob;

    @PostMapping("/import")
    public String importCustomers(
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Excel file must not be empty"
            );
        }

        String filename = file.getOriginalFilename();
        if (filename == null
                || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only .xlsx files are supported"
            );
        }

        File tempFile = File.createTempFile("customers-", ".xlsx");

        try {
            file.transferTo(tempFile);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("file", tempFile.getAbsolutePath())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(customerImportJob, jobParameters);

            return "Customer import completed";
        } finally {
            deleteQuietly(tempFile);
        }
    }

    private void deleteQuietly(File file) {
        try {
            java.nio.file.Files.deleteIfExists(file.toPath());
        } catch (IOException ignored) {
            // Temporary files are best-effort cleanup only.
        }
    }
}
