package com.crud.fullstack.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
    /**
     * Salva o arquivo e devolve a URL pública para acessá-lo.
     */

    String create(MultipartFile file, String subpasta);
    void excluir(String url);
}
