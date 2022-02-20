package com.challenge.pokemon.model.dto;

import com.challenge.pokemon.model.Ability;
import com.challenge.pokemon.model.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PokemonBasicDto {

    private String name;
    private String photo;
    private List<Type> types;
    private Integer weight;
    private List<Ability> abilities;
}
