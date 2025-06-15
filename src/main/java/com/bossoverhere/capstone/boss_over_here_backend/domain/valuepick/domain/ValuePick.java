package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Getter
@Table(name = "value_pick")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValuePick {
    @Id
    @Column(name = "value_pick_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name= "value_type")
    private ValueType valueType;

    @Column(nullable = false)
    private String question;




    @Type(JsonType.class)
    @Column(name = "answers", columnDefinition = "longtext", nullable = false)
    private Map<Integer, Object> answers;



}
