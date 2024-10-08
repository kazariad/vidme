package dev.dkaz.vidme.thumbnail;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.Optional;

@Controller
public class ThumbnailWebController {
    private final ThumbnailService thumbnailService;
    private final MediaType mediaType = new MediaType("image", "jpeg");

    public ThumbnailWebController(ThumbnailService thumbnailService) {
        this.thumbnailService = thumbnailService;
    }

    @GetMapping(path = "/thumbnail/{fileId}")
    public ResponseEntity<FileSystemResource> getThumbnailImage(@PathVariable String fileId) {
        Optional<Path> pathOpt = thumbnailService.findThumbnailFileByFileId(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(mediaType).body(fsr);
    }
}
