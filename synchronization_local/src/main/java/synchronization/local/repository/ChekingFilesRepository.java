package synchronization.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import synchronization.local.entity.ChekingFiles;


public interface ChekingFilesRepository extends JpaRepository<ChekingFiles, Long> {
    ChekingFiles findByNameAndPathAndType(String name,String path, String type);
    boolean existsByFileHash(@Param("fileHash") String fileHash);
}
