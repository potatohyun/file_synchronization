package synchronization.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
public class FileUploadController {

    // 파일을 저장할 디렉토리
    @Value("${target.save-directory}")
    private String saveDirectory;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSavedFile(@RequestParam("file") MultipartFile file, @RequestParam("filePath") String filePath) {
        System.out.println(filePath+"경로로 저장해야 할 파일이 업로드 되었습니다.");

        try {
            // 업로드할 파일의 경로 설정
            Path savefilePath = new File(saveDirectory + filePath).toPath();

            // 파일 저장
            Files.copy(file.getInputStream(), savefilePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("File upload failed.");
        }
    }

    @DeleteMapping("/upload")
    public ResponseEntity<String> uploadDeletedFile(@RequestBody String deletePath) {
        try {
            File serverFile = new File(saveDirectory + deletePath);
            if (serverFile.exists()){
                serverFile.delete();
            }

            return ResponseEntity.ok("File uploaded successfully.");
        }catch (SecurityException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("File upload failed.");
        }
    }
}
