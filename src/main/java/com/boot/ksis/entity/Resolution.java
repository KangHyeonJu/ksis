package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resolution")
@Getter
@Setter
public class Resolution {
    //해상도 id
    @Id
    @Column(name = "resolution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resolutionId;

    //해상도 이름
    private String name;

    //가로(너비) 픽셀 수
    private int width;

    //세로(높이) 픽셀 수
    private int height;
}
