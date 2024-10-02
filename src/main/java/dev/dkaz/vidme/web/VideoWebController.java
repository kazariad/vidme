package dev.dkaz.vidme.web;

import dev.dkaz.vidme.service.VideoService;
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
public class VideoWebController {
    private final VideoService videoService;
    private final MediaType mediaType = new MediaType("video", "webm");

    public VideoWebController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(path = "/video/stream/{fileId}")
    public ResponseEntity<FileSystemResource> getVideoStream(@PathVariable String fileId) {
        Optional<Path> pathOpt = videoService.findVideoFileByFileId(fileId);
        if (pathOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        FileSystemResource fsr = new FileSystemResource(pathOpt.get());
        return ResponseEntity.ok().contentType(mediaType).body(fsr);
    }
}
