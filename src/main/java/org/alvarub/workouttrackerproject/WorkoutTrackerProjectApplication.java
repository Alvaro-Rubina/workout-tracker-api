package org.alvarub.workouttrackerproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WorkoutTrackerProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkoutTrackerProjectApplication.class, args);
    }

}
