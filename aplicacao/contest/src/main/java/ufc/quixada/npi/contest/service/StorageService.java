package ufc.quixada.npi.contest.service;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import ufc.quixada.npi.contest.model.Trabalho;

public interface StorageService {

    void store(MultipartFile file, String path, Trabalho trabalho);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
    
    File getFile(String path);

}
