package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter  
@NoArgsConstructor
@Entity
@Table(
    name = "interaction_rule",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_interaction_tags", columnNames = {"tag_a", "tag_b"})
    }
)
public class InteractionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_a", nullable = false, length = 100)
    private String tagA;

    @Column(name = "tag_b", nullable = false, length = 100)
    private String tagB;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    private InteractionSeverity severity;

    @Column(nullable = false, length = 1000)
    private String message;

}
