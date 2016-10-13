package ufc.quixada.npi.contest.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.CopyOption;
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
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.validator.StorageException;
import ufc.quixada.npi.contest.validator.StorageFileNotFoundException;

@Service
public class FileSystemStorageService implements StorageService{
	
	private final Path rootLocation;
	
	@Autowired
	private MessageService messsagemService;
	
    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }
    
   
	@Override
	public void store(MultipartFile arquivoUpload, String filepath) {
		 try {
			 	String caminho = Constants.CAMINHO_TRABALHOS;
	            if (arquivoUpload.isEmpty()) {
	                throw new StorageException(messsagemService.getMessage("ARQUIVO_VAZIO"));
	            }
	            String pastaDeDestino = new StringBuilder(caminho)
	            		.append(filepath.substring(0, filepath.lastIndexOf('.'))).append("/").toString();
	            
	            File pasta = new File(pastaDeDestino);
	            if(!pasta.exists()){
	            	if(!pasta.mkdirs()){
	            		throw new RuntimeException("Não foi possível criar pasta de destino");
	            	};	            	
	            }
	            String string = pastaDeDestino+filepath;
				Files.copy(arquivoUpload.getInputStream(), Paths.get(string));
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}

	@Override
	public Stream<Path> loadAll() {
		try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException(messsagemService.getMessage("FALHA_AO_LER_ARQUIVO"));
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
            	throw new StorageFileNotFoundException(messsagemService.getMessage("FALHA_AO_LER_ARQUIVO"));

            }
        } catch (MalformedURLException e) {
        	throw new StorageFileNotFoundException(messsagemService.getMessage("FALHA_AO_LER_ARQUIVO"));
        }
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
		
	}

	@Override
	public File getFile(String path) {
		File file = new File(path);
		return file;
	}

}