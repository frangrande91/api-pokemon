package com.challenge.pokemon.service;

import com.challenge.pokemon.model.Ability;
import com.challenge.pokemon.model.Pokemon;
import com.challenge.pokemon.model.Type;
import com.challenge.pokemon.model.dto.PokemonBasicDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PokemonService {

    private static final String ACCEPT = "accept";
    private static final String JSON = "application/json";

    @Autowired
    private ModelMapper modelMapper;

    public List<PokemonBasicDto> getAllPokemons(Integer limit) throws IOException, InterruptedException {
        List<PokemonBasicDto> pokemons = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=" + limit + "/" ))
                .header(ACCEPT, JSON)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray jsonPokemons = JsonParser.parseString(response.body()).getAsJsonObject().get("results").getAsJsonArray();
        jsonPokemons.forEach(p -> {
            try {
                pokemons.add(getBasicPokemon(p.getAsJsonObject().get("name").getAsString()));
            } catch (IOException | InterruptedException e) {
                log.error("Interrupted!", e);
            }
        });
        return pokemons;
    }

    private PokemonBasicDto getBasicPokemon(String idOrName) throws IOException, InterruptedException {
        return modelMapper.map(getPokemon(idOrName), PokemonBasicDto.class);
    }


    public Pokemon getPokemon(String idOrName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/" + idOrName + "/"))
                .header(ACCEPT, JSON)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return buildPokemon(response);
    }

    private Pokemon buildPokemon(HttpResponse<String> response) {
        List<Ability> abilities = new ArrayList<>();
        JsonArray jsonAbilities = JsonParser.parseString(response.body()).getAsJsonObject().get("abilities").getAsJsonArray();
        jsonAbilities.forEach(a -> abilities.add(new Gson().fromJson(a.getAsJsonObject().get("ability"), Ability.class)));

        List<Type> types = new ArrayList<>();
        JsonArray jsonTypes = JsonParser.parseString(response.body()).getAsJsonObject().get("types").getAsJsonArray();
        jsonTypes.forEach(t -> types.add(new Gson().fromJson(t.getAsJsonObject().get("type"), Type.class)));

        return Pokemon.builder()
                .name(JsonParser.parseString(response.body()).getAsJsonObject().get("name").getAsString())
                .photo(JsonParser.parseString(response.body()).getAsJsonObject().get("sprites").getAsJsonObject().get("front_default").getAsString())
                .types(types)
                .weight(JsonParser.parseString(response.body()).getAsJsonObject().get("weight").getAsInt())
                .abilities(abilities)
                .build();
    }

}
