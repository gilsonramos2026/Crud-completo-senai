package com.crud.fullstack.controller;

import com.crud.fullstack.dto.request.ProdutoRequest;
import com.crud.fullstack.dto.response.ProdutoResponse;
import com.crud.fullstack.exception.ApiError;
import com.crud.fullstack.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento do catálogo de produtos")
public class ProdutoController {

    private final ProductService service;

    // ---- ENDPOINT PÚBLICO ----
    @GetMapping
    @Operation(
            summary = "Listar produtos paginados",
            description = "Retorna uma lista paginada de produtos. Permite filtro opcional por nome (parcial e case-insensitive)."
    )
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @Parameter(description = "Nome do produto para filtro (opcional)")
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<ProdutoResponse> page = service.listar(name, pageable).map(ProdutoResponse::from);
        return ResponseEntity.ok(page);
    }

    // ---- ENDPOINT PÚBLICO ----
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um único produto do catálogo.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ProdutoResponse.from(service.buscarPorId(id)));
    }

    // ---- ENDPOINTS ADMIN (Protegidos por Token JWT) ----
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Criar um novo produto",
            description = "Cadastra um novo produto no catálogo. Requer autenticação com a role ROLE_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação nos dados enviados",
            content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    @ApiResponse(responseCode = "403", description = "Acesso negado: requer privilégios de Administrador")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        var criado = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.from(criado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar um produto existente",
            description = "Atualiza os dados de um produto pelo ID. Requer autenticação com a role ROLE_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(ProdutoResponse.from(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Excluir um produto",
            description = "Remove o produto do banco de dados e apaga sua imagem física do disco. Requer ROLE_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Produto removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ---- UPLOAD DE IMAGEM ----
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Fazer upload da imagem do produto",
            description = "Envia um arquivo de imagem (PNG, JPEG, WEBP de até 5MB) para vincular ao produto. Substitui a imagem anterior se existir.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Imagem enviada e vinculada com sucesso")
    @ApiResponse(responseCode = "400", description = "Arquivo inválido, vazio ou maior que o limite de 5MB")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public ResponseEntity<ProdutoResponse> enviarImagem(
            @PathVariable Long id,
            @Parameter(description = "Arquivo binário da imagem")
            @RequestParam("file") MultipartFile arquivo) {
        var atualizado = service.atualizarImagem(id, arquivo);
        return ResponseEntity.ok(ProdutoResponse.from(atualizado));
    }
}
