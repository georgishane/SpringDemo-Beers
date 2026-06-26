package com.telerikacademy.com.springdemo.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beers_ratings",
uniqueConstraints = {
@UniqueConstraint(columnNames = {"user_id", "beer_id"})
    }
)

public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @SequenceGenerator(name = "beers_ratings_seq", sequenceName = "beers_ratings_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "beer_id")
    private Beer beer;

    @Column(nullable = false)
    private double rating;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

}
