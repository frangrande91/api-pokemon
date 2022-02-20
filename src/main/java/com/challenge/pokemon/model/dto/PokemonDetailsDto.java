package com.challenge.pokemon.model.dto;

import com.challenge.pokemon.model.Ability;
import com.challenge.pokemon.model.Characteristic;
import com.challenge.pokemon.model.Move;
import com.challenge.pokemon.model.Type;
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
public class PokemonDetailsDto {

    private String name;
    private String photo;
    private List<Type> types;
    private Integer weight;
    private List<Ability> abilities;
    private Set<Characteristic> descriptions;
    private List<Move> moves;
}
