package me.template;

import com.palantir.logsafe.exceptions.SafeRuntimeException;
import java.util.ArrayList;
import java.util.List;
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
        List<Object> objects = new ArrayList<>();
        if (objects.isEmpty()) {
            throw new SafeRuntimeException("");
        }
        return ResponseEntity.ok("demo1");
    }
}
