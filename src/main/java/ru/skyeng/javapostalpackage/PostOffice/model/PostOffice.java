package ru.skyeng.javapostalpackage.PostOffice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_office")
public class PostOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "postal_index", nullable = false)
    private String postalIndex;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;
}

