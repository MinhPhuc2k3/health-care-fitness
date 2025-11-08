package com.health_fitness.model.recipe.meal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "recipe")
@Getter
@Setter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Float calories;
    private Float protein;
    private Float carbs;
    private Float fat;
    private LocalDate createdDate;
    private LocalDate lastUpdate;
    private String imageId;
    @Enumerated(EnumType.STRING)
    private List<MealType> mealType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Transient
    private Boolean isCreatedByAdmin;

    @Transient
    @JsonIgnore
    private MultipartFile imageFile;

    public Boolean getIsCreatedByAdmin() {
        if(isCreatedByAdmin==null){
            isCreatedByAdmin = !(this.getCreatedBy().getRoles().stream().filter(role->role.getRole().equals("ADMIN"))).collect(Collectors.toSet()).isEmpty();
        }
        return isCreatedByAdmin;
    }
}

