package com.sfane.sfaneapi.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dq2zh3kgt",
                        "api_key", "829856111387269",
                        "api_secret", "U3O3ZTmAWgPUipkAMPCEhWwBmrA"

        ));
    }
}
