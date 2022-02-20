package com.challenge.pokemon.controller;

import com.challenge.pokemon.model.dto.PokemonBasicDto;
import com.challenge.pokemon.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    private final PokemonService pokemonService;

    @Autowired
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public ResponseEntity<List<PokemonBasicDto>> getAllPokemons(@RequestParam(defaultValue = "20", value = "limit") Integer limit) throws IOException, InterruptedException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Total-Count", Long.toString(limit))
                .body(pokemonService.getAllPokemons(limit));
    }
}
