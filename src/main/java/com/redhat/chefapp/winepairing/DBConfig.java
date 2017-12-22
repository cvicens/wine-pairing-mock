package com.redhat.chefapp.winepairing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class DBConfig extends AbstractMongoConfiguration{
    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient("localhost:27017");
    }

    @Override
    protected String getDatabaseName() {
        return "mydb";
    }
}

