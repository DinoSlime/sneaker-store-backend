package com.sneakerstore.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity { // Kế thừa BaseEntity
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
}