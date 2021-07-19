package com.project.filestoragesystem.controller;

import com.project.filestoragesystem.entity.FileMetaData;
import com.project.filestoragesystem.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileActionController {

    private final FileRepository fileRepository;

    public FileActionController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<String> create(HttpServletRequest request) throws IOException, FileUploadException {
        return createOrUpdate(request);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<String> update(HttpServletRequest request) throws IOException, FileUploadException {
        return createOrUpdate(request);
    }

    private ResponseEntity<String> createOrUpdate(HttpServletRequest request) throws IOException, FileUploadException {

        ServletFileUpload upload = new ServletFileUpload();
        if (!ServletFileUpload.isMultipartContent(request)) {
            log.info("Not a multipart content.");
            return ResponseEntity.badRequest().
                    contentType(MediaType.APPLICATION_JSON).
                    body("Not a multipart content.");
        }

        FileItemIterator iterator = upload.getItemIterator(request);

        boolean saved = false;
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();

            InputStream stream = item.openStream();
            if (!item.isFormField()) {
                Integer latestVersion = fileRepository.getLatestVersion(item.getName());
                FileMetaData fileMetaData = new FileMetaData(item.getName(), latestVersion == null ? 1 : latestVersion + 1);
                saved = fileRepository.saveStreaming(fileMetaData, stream);
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(saved ? "OK." : "Failed.");
    }

    @DeleteMapping(value = "/delete", params = {"name"})
    public ResponseEntity<String> delete(@RequestParam("name") String name) {

        boolean deleted = fileRepository.delete(name);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleted ? "OK." : "Failed.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileMetaData>> list() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileRepository.getFileMetaDataList());
    }

    @GetMapping(value = "/download")
    public ResponseEntity<StreamingResponseBody> get(@RequestParam("name") String fileName,
                                                     @RequestParam("version") Optional<Integer> version) {

        Integer latestVersion = fileRepository.getLatestVersion(fileName);
        if (latestVersion == null) {
            return ResponseEntity.notFound().build();
        }

        if (version.isPresent()) {
            if (version.get() > latestVersion) {
                return ResponseEntity.notFound().build();
            }
        }

        byte[] fileData = fileRepository.getFileData(fileName, version.orElse(latestVersion));

        StreamingResponseBody streamingResponseBody = outputStream -> outputStream.write(fileData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", fileName))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(streamingResponseBody);
    }
}
