package com.crud.fullstack.service.impl;

import com.crud.fullstack.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorageService {

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/png", "image/jpeg", "image/webp");
    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024; // 5MB

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.public-base-url}")
    private String publicBaseUrl;

    @Override
    public String create(MultipartFile file, String subpasta) {
        validar(file);

        try {
            Path pasta = Paths.get(uploadDir, subpasta);
            Files.createDirectories(pasta);

            String extensao = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String nomeArquivo = UUID.randomUUID() + "." + extensao;
            Path destino = pasta.resolve(nomeArquivo);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            return publicBaseUrl + "/" + subpasta + "/" + nomeArquivo;
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar arquivo", e);
        }
    }

    @Override
    public void excluir(String url) {
        if (url == null) return;
        try {
            String caminhoRelativo = url.replace(publicBaseUrl + "/", "");
            Files.deleteIfExists(Paths.get(uploadDir, caminhoRelativo));
        } catch (IOException ignored) {
            // exclusão best-effort — não deve quebrar o fluxo principal
        }
    }

    private void validar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
        if (file.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("Arquivo maior que 5MB");
        }
        if (!TIPOS_PERMITIDOS.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Use PNG, JPEG ou WEBP");
        }
    }
}
