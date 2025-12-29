package com.sfane.sfaneapi.security;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;


    public CloudinaryService(Cloudinary cloudinary){
        this.cloudinary = cloudinary;
    }

    public Map upload(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "products")
        );
    }

    public void delete(String publicId){
        try{
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e){
            throw new RuntimeException("Image delete failed");
        }
    }
}
