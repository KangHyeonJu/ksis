package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "visit")
@Getter
@Setter
public class Visit {
    //방문 id
    @Id
    @Column(name = "visit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitId;

    //날짜(방문일)
    private LocalDate visitDate;
}
