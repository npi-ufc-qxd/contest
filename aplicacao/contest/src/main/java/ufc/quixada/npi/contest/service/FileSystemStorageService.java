package ufc.quixada.npi.contest.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import ufc.quixada.npi.contest.model.StorageProperties;
import ufc.quixada.npi.contest.validator.StorageException;

@Service
public class FileSystemStorageService implements StorageService{
	private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

	@Override
	public void init() {
		
	}

	@Override
	public void store(MultipartFile file) {
		 try {
	            if (file.isEmpty()) {
	                throw new StorageException("Arquivo vazio " + file.getOriginalFilename());
	            }
	            Files.copy(file.getInputStream(), Paths.get("src/main/resources/static/arquivos", file.getOriginalFilename()));
	        } catch (IOException e) {
	        }
	}

	@Override
	public Stream<Path> loadAll() {
		try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Falha ao ler o arquivo", e);
        }
	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                return null;

            }
        } catch (MalformedURLException e) {
        	 return null;
        }
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
		
	}

}