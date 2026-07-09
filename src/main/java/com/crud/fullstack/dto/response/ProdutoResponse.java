package com.crud.fullstack.dto.response;

import com.crud.fullstack.model.Product;
import java.math.BigDecimal;
import java.time.Instant;

public record ProdutoResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {
    // Corrigido de 'frm' para 'from' para seguir o padrão de nomenclatura
    public static ProdutoResponse from(Product p) {
        return new ProdutoResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getQuantity(),
                p.getImageUrl(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
