package com.ecommerce.product.infrastructure.adapter.out.storage;

import com.ecommerce.product.domain.port.out.StoragePort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalStorageAdapter implements StoragePort {

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

        // 5. Retourner UNIQUEMENT le nom du fichier ! (ex: b8b32b74-f441-4fd5-a1e4.webp)
        return newFilename;
    }
}