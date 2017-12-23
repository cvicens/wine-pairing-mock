package com.redhat.chefapp.winepairing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class DBConfig extends AbstractMongoConfiguration{
    private static final String MONGO_DBNAME = "MONGO_DBNAME";
    private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    private static final String MONGO_PORT = "MONGO_PORT";
    
    @Override
    @Bean
    public Mongo mongo() throws Exception {
        String hostname = System.getenv(MONGO_HOSTNAME) != null ? System.getenv(MONGO_HOSTNAME) : "localhost";
        String port = System.getenv(MONGO_PORT) != null ? System.getenv(MONGO_PORT) : "27017";

        return new MongoClient(hostname + ":" + port);
    }

    @Override
    protected String getDatabaseName() {
        return System.getenv(MONGO_DBNAME) != null ? System.getenv(MONGO_DBNAME) : "mydb";
    }
}

