package com.health_fitness.model.nutrition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_plan_id")
    private MenuPlan menuPlan;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PositiveOrZero
    @Column
    private Float actualTotalCalories;

    @PositiveOrZero
    @Column
    private Float actualTotalProtein;

    @PositiveOrZero
    @Column
    private Float actualTotalCarb;

    @PositiveOrZero
    @Column
    private Float actualTotalFat;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Meal> meals = new ArrayList<>();

    @Enumerated
    private MenuStatus status;

    public void addTotalCalories(float delta){
        if(this.actualTotalCalories==null) this.actualTotalCalories = 0F;
        this.actualTotalCalories += delta;
    }

    public void addTotalCarbs(float delta){
        if(this.actualTotalCarb==null) this.actualTotalCarb = 0F;
        this.actualTotalCarb += delta;
    }

    public void addTotalProtein(float delta){
        if(this.actualTotalProtein==null) this.actualTotalProtein = 0F;
        this.actualTotalProtein += delta;
    }

    public void addTotalFat(float delta){
        if(this.actualTotalFat==null) this.actualTotalFat = 0F;
        this.actualTotalFat += delta;
    }

    public enum MenuStatus {
        DONE, IN_PROGRESS
    }
}