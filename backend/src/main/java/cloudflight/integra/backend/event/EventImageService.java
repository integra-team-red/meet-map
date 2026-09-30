package cloudflight.integra.backend.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventImageService {
    private static final long MAX_IMAGE_SIZE = 20L * 1024 * 1024;
    private final Path imageStorageDirectory;

    public EventImageService(@Value("${app.uploads.event-images:uploads/event-images}") String imageStorageDirectory) {
        this.imageStorageDirectory = Paths.get(imageStorageDirectory).toAbsolutePath().normalize();
    }

    public UUID save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No picture was provided.");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "The picture must be at most 20MB.");
        }
        try {
            if (detectContentType(file.getBytes()) == null) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Pictures must be .png or .jpeg");
            }
            UUID imageId = UUID.randomUUID();
            Files.createDirectories(imageStorageDirectory);
            Files.write(imageStorageDirectory.resolve(imageId.toString()), file.getBytes());
            return imageId;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store the picture.", e);
        }
    }

    public Optional<byte[]> load(UUID imageId) {
        Path imagePath = imageStorageDirectory.resolve(imageId.toString());
        if (!Files.exists(imagePath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllBytes(imagePath));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read the picture.", e);
        }
    }

    public void delete(UUID imageId) {
        try {
            Files.deleteIfExists(imageStorageDirectory.resolve(imageId.toString()));
        } catch (Exception e) {
        }
    }

    public MediaType detectContentType(byte[] bytes) {
        if (bytes.length >= 4 && (bytes[0] & 0xFF) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
            return MediaType.IMAGE_PNG;
        }
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
            return MediaType.IMAGE_JPEG;
        }
        return null;
    }
}
