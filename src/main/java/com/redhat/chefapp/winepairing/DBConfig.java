package com.redhat.chefapp.winepairing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class DBConfig extends AbstractMongoConfiguration{
    private static final String MONGODB_USER = "MONGODB_USER";
    private static final String MONGODB_PASSWORD = "MONGODB_PASSWORD";
    private static final String MONGODB_DATABASE = "MONGODB_DATABASE";
    private static final String MONGODB_HOSTNAME = "MONGODB_HOSTNAME";
    private static final String MONGODB_PORT = "MONGODB_PORT";
    
    @Override
    @Bean
    public Mongo mongo() throws Exception {
        String hostname = System.getenv(MONGODB_HOSTNAME) != null ? System.getenv(MONGODB_HOSTNAME) : "localhost";
        String port = System.getenv(MONGODB_PORT) != null ? System.getenv(MONGODB_PORT) : "27017";
        String user = System.getenv(MONGODB_USER);
        String pass = System.getenv(MONGODB_PASSWORD);
        
        String uri = String.format("mongodb://%s/%s", hostname, port);
        if (user != null && pass != null) {
            uri = String.format("mongodb://%s:%s@%s/%s", user, pass, hostname, port);
        }

        System.out.println("uri: " + uri);

        return new MongoClient(new MongoClientURI(uri)
);
    }

    @Override
    protected String getDatabaseName() {
        return System.getenv(MONGODB_DATABASE) != null ? System.getenv(MONGODB_DATABASE) : "mydb";
    }
}

