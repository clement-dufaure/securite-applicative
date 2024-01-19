package re.dufau.securiteapplicative.securitynightmare.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/files")
public class FilesController {

    private static final String FILE_UPLOAD_DIR = "src/main/webapp/uploads/";

    @PostConstruct
    public void init() {
        // Création du dossier d'uploads s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(FILE_UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création du dossier d'uploads.", e);
        }
    }

    @GetMapping
    public String getFiles(Model model) {
        List<String> fileNames = listUploadedFiles();
        model.addAttribute("files", fileNames);
        return "files";
    }

    @PostMapping
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(FILE_UPLOAD_DIR + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/files";
    }

    @GetMapping("/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Path file = Paths.get(FILE_UPLOAD_DIR).resolve(filename);
        Resource resource;
        try {
            resource = new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier.");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private List<String> listUploadedFiles() {
        try (Stream<Path> walk = Files.walk(Paths.get(FILE_UPLOAD_DIR))) {
            return walk.filter(Files::isRegularFile)
                    .map(x -> x.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
