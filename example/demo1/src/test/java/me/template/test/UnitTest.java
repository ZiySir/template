/*
 * (c) Copyright 2025 Palantir Technologies Inc. All rights reserved.
 */

package me.template.test;

import lombok.extern.slf4j.Slf4j;
import me.template.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * .
 * @author ziy
 * @date 2025-01
 */
@Slf4j
@SpringBootTest(classes = DemoApplication.class)
public class UnitTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void contextLoads() {
        log.info("application name:{}", context.getApplicationName());
    }
}
