package synchronization.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import synchronization.server.service.FileSyncService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@Slf4j
public class FileUploadController {

    // 파일을 저장할 디렉토리
    @Value("${target.save-directory}")
    private String saveDirectory;

    @Autowired
    private FileSyncService fileSyncService;

    @PostMapping("/upload/file")
    public ResponseEntity<String> uploadSavedFile(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("filePath") String filePath) {
        try {
            // 업로드할 파일의 경로 설정
            Path savefilePath = new File(saveDirectory + filePath).toPath();

            // 파일 저장
            Files.copy(file.getInputStream(), savefilePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("{}경로로 파일이 저장되었습니다.",filePath);
            return ResponseEntity.ok("File save successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("File save failed.");
        }
    }
    @PostMapping("/upload/dir")
    public ResponseEntity<String> uploadSavedDir(@RequestBody String dirPath) {
        try {
            File serverFile = new File(saveDirectory + dirPath);
            if (!serverFile.exists()){
                serverFile.mkdir();
            }

            log.info("{}경로로 디렉토리가 저장되었습니다.",dirPath);
            return ResponseEntity.ok("Directory save successfully.");
        }catch (SecurityException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Directory save failed.");
        }
    }

    @DeleteMapping("/upload/file")
    public ResponseEntity<String> uploadDeletedFile(@RequestBody String deletePath) {
        try {
            File serverFile = new File(saveDirectory + deletePath);
            if (serverFile.exists()){
                serverFile.delete();
            }

            log.info("{}경로의 파일이 삭제되었습니다.",deletePath);
            return ResponseEntity.ok("File delete successfully.");
        }catch (SecurityException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("File delete failed.");
        }
    }
    @DeleteMapping("/upload/dir")
    public ResponseEntity<String> uploadDeletedDir(@RequestBody String deletePath) {
        try {
            log.info("{}경로의 디렉토리 내부 삭제를 시작합니다.",deletePath);
            fileSyncService.deleteFile(saveDirectory + deletePath);

            log.info("{}경로의 디렉토리가 삭제되었습니다.",deletePath);
            return ResponseEntity.ok("Directory delete successfully.");
        }catch (SecurityException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Directory delete failed.");
        }
    }
}
