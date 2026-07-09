package com.crud.fullstack.service;

import com.crud.fullstack.dto.request.ProdutoRequest;
import com.crud.fullstack.exception.ResourceNotFoundException;
import com.crud.fullstack.model.Product;
import com.crud.fullstack.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService { // Nome alterado para ProductService para seguir o padrão

    private final ProductRepository repository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<Product> listar(String name, Pageable pageable) { // Retorna Page<Product> e usa name
        if (name != null && !name.isBlank()) {
            // Método do repositório corrigido para o inglês
            return repository.findByNameContainingIgnoreCase(name, pageable);
        }
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Product buscarPorId(Long id) { // Retorna Product
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    @Transactional
    public Product criar(ProdutoRequest req) { // Recebe o DTO em inglês
        Product product = Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .quantity(req.quantity())
                .build();
        return repository.save(product);
    }

    @Transactional
    public Product atualizar(Long id, ProdutoRequest req) {
        Product product = buscarPorId(id);
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setQuantity(req.quantity());
        return repository.save(product);
    }

    @Transactional
    public void excluir(Long id) {
        Product product = buscarPorId(id);
        fileStorageService.excluir(product.getImageUrl()); // getImagemUrl() -> getImageUrl()
        repository.delete(product);
    }

    @Transactional
    public Product atualizarImagem(Long id, MultipartFile arquivo) {
        Product product = buscarPorId(id);

        String urlAntiga = product.getImageUrl();
        // Ajustado de .salvar() para .create() para bater com a sua interface FileStorageService
        String novaUrl = fileStorageService.create(arquivo, "products");
        product.setImageUrl(novaUrl);

        Product salvo = repository.save(product);

        if (urlAntiga != null) {
            fileStorageService.excluir(urlAntiga);
        }

        return salvo;
    }
}

