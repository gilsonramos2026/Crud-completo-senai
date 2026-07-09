package com.crud.fullstack.controller;

import com.crud.fullstack.dto.request.ProdutoRequest;
import com.crud.fullstack.dto.response.ProdutoResponse;
import com.crud.fullstack.service.ProductService; // Certifique-se de importar o nome correto do Service
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products") // Alterado para /api/products para manter o padrão em inglês das URLs
@RequiredArgsConstructor
public class ProdutoController {

    private final ProductService service; // Nome da classe de serviço ajustado

    // ---- ENDPOINT PÚBLICO (qualquer visitante pode listar) ----
    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @RequestParam(required = false) String name, // nome -> name para sincronizar com o Service
            Pageable pageable) {
        // Passando 'name' corrigido para o método listar do service
        Page<ProdutoResponse> page = service.listar(name, pageable).map(ProdutoResponse::from);
        return ResponseEntity.ok(page);
    }

    // ---- ENDPOINT PÚBLICO (detalhe de um produto) ----
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ProdutoResponse.from(service.buscarPorId(id)));
    }

    // ---- ENDPOINTS ADMIN (protegidos por role) ----
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        var criado = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.from(criado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(ProdutoResponse.from(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ---- UPLOAD DE IMAGEM (admin) ----
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data") // imagem -> image
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponse> enviarImagem(@PathVariable Long id,
                                                        @RequestParam("file") MultipartFile arquivo) { // arquivo -> file
        var atualizado = service.atualizarImagem(id, arquivo);
        return ResponseEntity.ok(ProdutoResponse.from(atualizado));
    }
}
