package synchronization.local.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Slf4j
@Service
public class UploadService {

    public static void uploadSavedFile(String serverUrl, String filePath, String savePath){
        File targetDirectory = new File(filePath); // 업로드할 파일 경로

        RestTemplate restTemplate = new RestTemplate();

        // 파일을 MultiPart로 전송하기 위한 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //전송할 내용
        FileSystemResource fileSystemResource = new FileSystemResource(targetDirectory);
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", fileSystemResource); // 파일
        parts.add("filePath", savePath);  //저장 경로

        //전송
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("File uploaded successfully.");
        } else {
            log.info("File upload failed.");
        }
    }
    public static void uploadSavedDir(String serverUrl, String dirPath){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(dirPath, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Diretory uploaded successfully.");
        } else {
            log.info("Diretory uploaded failed.");
        }
    }

    public static void uploadDeletedFile(String serverUrl, String deletePath){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(deletePath, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Delete Info upload successfully.");
        } else {
            log.info("Delete Info upload failed.");
        }
    }
}
