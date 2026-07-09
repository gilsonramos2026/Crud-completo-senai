package com.crud.fullstack.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products") // Ajustado para corresponder à tabela em inglês
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product { // Nome da classe alterado para Product

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name; // nome -> name

    @Column(length = 500)
    private String description; // descricao -> description

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // preco -> price

    @Column(nullable = false)
    private Integer quantity; // quantidade -> quantity

    @Column(name = "image_url", length = 500) // imagem_url -> image_url
    private String imageUrl;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
