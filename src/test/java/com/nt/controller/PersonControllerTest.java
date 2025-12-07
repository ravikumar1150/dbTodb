package com.nt.controller;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private PersonController controller;

    @Test
    void startJob_returnsStatusWhenJobSucceeds() throws Exception {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenReturn(jobExecution);

        String result = controller.startJob();

        assertEquals("Job Status: COMPLETED", result);
    }


}
