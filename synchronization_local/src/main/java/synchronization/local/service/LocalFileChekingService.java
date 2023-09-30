package synchronization.local.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import synchronization.local.entity.ChekingFiles;
import synchronization.local.repository.ChekingFilesRepository;
import synchronization.local.util.HashUtil;
import synchronization.local.util.SubstringPathUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;



@Slf4j
@Service
public class LocalFileChekingService {

    @Value("${target.directory}")
    private String targetDirectory;

    @Value("${server.url}")
    private String serverUrl;

    @Autowired
    private ChekingFilesRepository chekingFilesRepository;

    public void chekingFile(File directory) {
        File[] localFiles = directory.listFiles();

        for (File file : localFiles) {

            String fileHash = HashUtil.hashAlgorithm(file.getAbsolutePath(), file.isFile());

            if (!chekingFilesRepository.existsByFileHash(fileHash)) {
                // 같은 경로에 동일한 파일이 존재하는 경우(변경) -> 해쉬값, 업데이트 날짜 수정
                ChekingFiles update = chekingFilesRepository.findByNameAndPathAndType(
                        file.getName(),
                        file.getPath(),
                        file.isDirectory() ? "dir" : "file"
                );
                if (update != null && !fileHash.equals(update.getFileHash())) {
                    update.setFileHash(fileHash);
                    update.setUptDt(LocalDateTime.now());
                    chekingFilesRepository.save(update);
                    log.info("변경된 파일 업데이트");
                }

                // 새롭게 생성된 경우
                else {
                    ChekingFiles chekingFiles = new ChekingFiles();

                    chekingFiles.setName(file.getName());
                    chekingFiles.setType(file.isDirectory() ? "dir" : "file");
                    chekingFiles.setPath(file.getAbsolutePath());
                    chekingFiles.setFileHash(fileHash);
                    chekingFiles.setRegDt(LocalDateTime.now());
                    chekingFiles.setUptDt(LocalDateTime.now());

                    chekingFilesRepository.save(chekingFiles);
                    log.info("새로운 파일 생성됨");

                }
                String savePath = SubstringPathUtil.substringPath(file.getAbsolutePath(), targetDirectory);
                //서버로 전송
                if (file.isFile()){
                    UploadService.uploadSavedFile(serverUrl + "/file", file.getAbsolutePath(), savePath);
                }else {
                    UploadService.uploadSavedDir(serverUrl + "/dir", savePath);
                }
            }
            // 디렉토리라면 하위 디렉토리도 탐색(재귀)
            if (file.isDirectory()) {
                chekingFile(file);
            }
        }
    };

    private void deleteFile() {
        List<ChekingFiles> dbFileList = chekingFilesRepository.findAll();
        for (ChekingFiles file : dbFileList){
            File localFile = new File(file.getPath());
            // DB에 있는데 로컬에 없으면 해당 데이터를 DB에서 삭제
            if (!localFile.exists()) {
                chekingFilesRepository.delete(file);
                log.info("존재하지 않는 파일 삭제");

                //서버로 삭제 경로 전송
                String deletePath = SubstringPathUtil.substringPath(file.getPath(), targetDirectory);
                if (file.getType().equals("file")){
                    UploadService.uploadDeletedFile(serverUrl + "/file", deletePath);
                }else {
                    UploadService.uploadDeletedFile(serverUrl + "/dir", deletePath);
                }

            }
        }
    }


    // 5초마다 스케줄러 실행
    @Scheduled(fixedRate = 5000)
    public void runScheduled() {
        log.info("Running Scheduled");
        //파일 변화 체크
        chekingFile(new File(targetDirectory));
        //변경사항, 삭제에 대한 파일 삭제
        deleteFile();

    }
}
