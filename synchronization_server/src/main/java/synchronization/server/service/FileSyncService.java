package synchronization.server.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class FileSyncService {
    public void deleteFile(String deletePath) {
        File serverFile = new File(deletePath);
        // 해당 경로에 디렉토리가 있으면
        if (serverFile.exists() && serverFile.isDirectory()){
            File[] files = serverFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()){
                        file.delete(); // 파일 삭제
                        log.info("{}경로의 파일이 삭제되었습니다.",deletePath);
                    }else {
                        deleteFile(file.getAbsolutePath());
                    }
                }
            }
            serverFile.delete();
        }
    }
}
