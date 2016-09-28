package ufc.quixada.npi.contest.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import ufc.quixada.npi.contest.model.Trabalho;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, Long idAutor);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
