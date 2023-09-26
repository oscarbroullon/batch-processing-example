package com.example.batchprocessing.batch.helloWorldJob;

import com.example.batchprocessing.batch.personJob.PersonItemProcessor;
import com.example.batchprocessing.batch.personJob.model.Person;
import com.example.batchprocessing.batch.personJob.notification.JobCompletionNotificationListener;
import lombok.NonNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.JobStep;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class helloWorldJobConfiguration {

        @NonNull
        private TaskExecutor jobThreadPoolExecutor1;

        @Bean
        public PersonItemProcessor processor() {
                return new PersonItemProcessor();
        }

        @Bean
        public Job helloWorldJob(JobRepository jobRepository,
                                 JobCompletionNotificationListener listener, Step writeHelloWorldStep, JobStep importUserJob) {
                return new JobBuilder("helloWorldJob")
                        .repository(jobRepository)
                        .incrementer(new RunIdIncrementer())
                        .listener(listener)
                        .flow(writeHelloWorldStep)
                        .next(importUserJob)
                        .end()
                        .build();
        }

        @Bean
        public Step writeHelloWorldStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Person> writer) {
                return new StepBuilder("step1")
                        .repository(jobRepository)
                        .transactionManager(transactionManager)
                        .<Person, Person> chunk(10)
                        .reader(reader())
                        .processor(processor())
                        .writer(writer)
                        .taskExecutor(jobThreadPoolExecutor1)
                        .build();
        }

}
