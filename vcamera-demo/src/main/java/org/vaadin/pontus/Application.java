package org.vaadin.pontus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 *
 * @author mstahv
 */
@SpringBootApplication
public class Application extends WebMvcConfigurationSupport {

    static Path videos;

    static {
        try {
            // create a temp file for all updated videos
            videos = Files.createTempDirectory("videos");
            System.out.println("Videos will be stored in:" + videos.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure Spring MVC to serve resources from the videos folder
        // Spring MVC requires a magic trailing slash for some reason...
        Resource vids = new FileSystemResource(videos.toString() + "/");
        registry.addResourceHandler("/videos/**")
                .addResourceLocations(vids);
    }

}
