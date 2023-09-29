package synchronization.local.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

@Slf4j
public class HashUtil {
    public static String hashAlgorithm(String filePath) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 파일 경로를 바이트 배열로 변환
            byte[] filePathBytes = filePath.getBytes("UTF-8");
            // 파일 내용을 읽어와 해시값 계산
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            digest.update(filePathBytes);
            digest.update(fileBytes);

            byte[] hash = digest.digest();
            StringBuffer hexString = new StringBuffer();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
