package com.challenge.pokemon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pokemon {

    private Integer id;
    private String name;
    private String photo;
    private List<Type> types;
    private Integer weight;
    private List<Ability> abilities;
    private Set<Characteristic> descriptions;
    private List<Move> moves;
}
