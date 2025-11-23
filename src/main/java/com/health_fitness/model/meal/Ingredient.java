package com.health_fitness.model.meal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Ingredient name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @PositiveOrZero
    @Column
    private Float calories;

    @PositiveOrZero
    @Column
    private Float protein;

    @PositiveOrZero
    @Column
    private Float carbs;

    @PositiveOrZero
    @Column
    private Float fat;

    @Column
    private String imageUrl;

    @Column
    private String imageId;

    @Transient
    @JsonIgnore
    MultipartFile image;
}
