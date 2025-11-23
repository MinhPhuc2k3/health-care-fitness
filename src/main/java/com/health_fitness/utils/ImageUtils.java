package com.health_fitness.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ImageUtils {

    private final Cloudinary cloudinary;

    public ImageUtils(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public List<Object> uploadImage(MultipartFile file, ImageType type) throws IOException {
        return this.uploadImage(file,type,null);
    }

    public List<Object> uploadImage(MultipartFile file, ImageType type, Long public_id) throws IOException {
        Map<String, Object> uploadOptions = getOptionsByType(type, public_id);
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        return List.of(uploadResult.get("public_id"), (String) uploadResult.get("secure_url"));
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private Map<String, Object> getOptionsByType(ImageType type, Long public_id) {
        String folder;
        Transformation<?> transformation = switch (type) {
            case AVATAR -> {
                folder = "users";
                yield new Transformation<>()
                        .width(200).height(200)
                        .crop("fill").gravity("face")
                        .rawTransformation("webp").quality("auto:eco");
            }
            case RECIPE -> {
                folder = "recipes";
                yield new Transformation<>()
                        .width(800).height(600)
                        .crop("fill").quality("auto:good");
            }
            case INGREDIENT -> {
                folder = "ingredient";
                yield new Transformation<>()
                        .width(800).height(600)
                        .crop("fill").quality("auto:good");
            }
            default -> {
                folder = "others";
                yield new Transformation<>()
                        .width(600).height(400)
                        .crop("fill").quality("auto");
            }
        };

        return ObjectUtils.asMap(
                "folder", folder,
                "transformation", transformation,
                "public_id", public_id
        );
    }

    public static enum ImageType{
        AVATAR, RECIPE, MEAL, INGREDIENT, EXERCISE
    }
}
