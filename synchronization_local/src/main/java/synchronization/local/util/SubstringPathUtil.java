package synchronization.local.util;

public class SubstringPathUtil {
    public static String substringPath(String filePath, String originPath) {
        try {
            if (filePath.startsWith(originPath)) {
                // 루트 부분 길이만큼 잘라내고 반환
                return filePath.substring(originPath.length());
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}
