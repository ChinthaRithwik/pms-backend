package com.example.ProjectManagementSystem.entity;

import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@NoArgsConstructor
@Setter
@Getter
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatusTypes status;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Task> tasks =new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User owner;

    public void addTask(Task task){
        tasks.add(task);
        task.setProject(this);
    }
    public void removeTask(Task task){
        tasks.remove(task);
        task.setProject(null);
    }
}
