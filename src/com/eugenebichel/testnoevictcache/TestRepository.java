package com.eugenebichel.testnoevictcache.config;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;

@Repository
public class TestRepository {

    public Collection<Object> get() {

        throw new RuntimeException("test");

        return Collections.EMPTY_LIST;
    }

}
