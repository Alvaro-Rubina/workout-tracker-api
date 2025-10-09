package org.alvarub.workouttrackerproject.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alvarub.workouttrackerproject.exception.CloudinaryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String folder;

    public UploadResult upload(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) return null;
        try {
            String folderPath = (subfolder == null || subfolder.isBlank()) ? folder : folder + "/" + subfolder;
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folderPath,
                    "resource_type", "image"
            ));
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            log.info("Imagen subida a Cloudinary correctamente. Link: {}", url);
            return new UploadResult(url, publicId);
        } catch (IOException e) {
            log.error("Error subiendo imagen a Cloudinary");
            throw new RuntimeException("Error subiendo imagen a Cloudinary", e);
        }
    }

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Imagen eliminada de Cloudinary correctamente");
        } catch (IOException e) {
            log.error("Error al eliminar la imagen en Cloudinary");
            throw new CloudinaryException("Error al eliminar la imagen en Cloudinary: ", e);
        }
    }

    public record UploadResult(String url, String publicId) {}
}