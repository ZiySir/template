package me.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * demo1.
 * created on 2025-01
 * @author ziy
 */
@SpringBootApplication
@Controller
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * demo api.
     */
    @RequestMapping("/demo1")
    public ResponseEntity<String> demo1() {
        return ResponseEntity.ok("demo1");
    }
}
