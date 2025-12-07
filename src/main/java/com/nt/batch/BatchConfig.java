package com.nt.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.nt.person.Person;

@Configuration
public class BatchConfig {

    @Autowired
    @Qualifier("dataSource")
    private DataSource sourceDataSource;

    @Autowired
    @Qualifier("targetDataSource")
    private DataSource targetDataSource;

    @Bean
    public JdbcCursorItemReader<Person> reader() {
        return new JdbcCursorItemReaderBuilder<Person>()
                .name("personItemReader")
                .dataSource(sourceDataSource)
                 // this reads from your source database (db1)
                .sql("SELECT first_name, last_name FROM people") 
                 .fetchSize(100)// query
                .rowMapper(new DataClassRowMapper<>(Person.class)) // maps to Person class
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Person> writer() {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(targetDataSource)
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      JdbcCursorItemReader<Person> reader,
                      PersonItemProcessor processor,
                      JdbcBatchItemWriter<Person> writer) {

        return new StepBuilder("step1", jobRepository)
                .<Person, Person>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
