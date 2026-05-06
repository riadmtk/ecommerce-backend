package com.ecommerce.product.infrastructure.adapter.out.storage;

import com.ecommerce.product.domain.port.out.StoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalStorageAdapter implements StoragePort {

    @Value("${server.port:8082}")
    private String serverPort;

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // 1. Utiliser le répertoire de travail de l'application (racine du projet)
        String rootPath = System.getProperty("user.dir");
        Path directory = Paths.get(rootPath, "uploads", "products");

        // 2. Créer le dossier s'il n'existe pas
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Impossible de créer le dossier d'upload", e);
            }
        }

        // 3. Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // 4. Sauvegarder le fichier
        Path filePath = directory.resolve(newFilename);
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier", e);
        }

        // 5. Retourner l'URL (absolue pour l'affichage)
        return "http://localhost:" + serverPort + "/api/v1/products/images/" + newFilename;
    }
}