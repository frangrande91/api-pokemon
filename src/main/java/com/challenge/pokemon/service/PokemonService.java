package com.challenge.pokemon.service;

import com.challenge.pokemon.exception.PokemonNotExistsException;
import com.challenge.pokemon.model.Ability;
import com.challenge.pokemon.model.Characteristic;
import com.challenge.pokemon.model.Move;
import com.challenge.pokemon.model.Pokemon;
import com.challenge.pokemon.model.Type;
import com.challenge.pokemon.model.dto.PokemonBasicDto;
import com.challenge.pokemon.model.dto.PokemonDetailsDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            } catch (IOException | InterruptedException | PokemonNotExistsException e) {
                log.error("Interrupted!", e);
            }
        });
        return pokemons;
    }

    private PokemonBasicDto getBasicPokemon(String idOrName) throws IOException, InterruptedException, PokemonNotExistsException {
        return modelMapper.map(getPokemon(idOrName, "basic"), PokemonBasicDto.class);
    }

    public PokemonDetailsDto getDetailsPokemon(String idOrName) throws IOException, InterruptedException, PokemonNotExistsException {
        return modelMapper.map(getPokemon(idOrName, "details"), PokemonDetailsDto.class);
    }

    public Pokemon getPokemon(String idOrName, String typeResponse) throws IOException, InterruptedException, PokemonNotExistsException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/" + idOrName + "/"))
                .header(ACCEPT, JSON)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if(response.body().equals("Not Found")) {
            throw new PokemonNotExistsException("Pokemon " +idOrName+ " not exists");
        }

        return buildPokemon(response, typeResponse);
    }

    private Pokemon buildPokemon(HttpResponse<String> response, String typeResponse) throws IOException, InterruptedException {
        //Get abilities
        List<Ability> abilities = new ArrayList<>();
        JsonArray jsonAbilities = JsonParser.parseString(response.body()).getAsJsonObject().get("abilities").getAsJsonArray();
        jsonAbilities.forEach(a -> abilities.add(new Gson().fromJson(a.getAsJsonObject().get("ability"), Ability.class)));

        //Get types
        List<Type> types = new ArrayList<>();
        JsonArray jsonTypes = JsonParser.parseString(response.body()).getAsJsonObject().get("types").getAsJsonArray();
        jsonTypes.forEach(t -> types.add(new Gson().fromJson(t.getAsJsonObject().get("type"), Type.class)));

        List<Move> moves = new ArrayList<>();
        Set<Characteristic> descriptions = new HashSet<>();
        if(typeResponse.equals("details")) {
            //Get moves
            JsonArray jsonMoves = JsonParser.parseString(response.body()).getAsJsonObject().get("moves").getAsJsonArray();
            jsonMoves.forEach(m -> moves.add(new Gson().fromJson(m.getAsJsonObject().get("move"), Move.class)));

            //Get descriptions
            List<String> urls = new ArrayList<>();
            JsonArray jsonStats = JsonParser.parseString(response.body()).getAsJsonObject().get("stats").getAsJsonArray();
            jsonStats.forEach(p -> urls.add(p.getAsJsonObject().get("stat").getAsJsonObject().get("url").getAsString()));
            for (String url : urls) {
                List<String> urlsCharacteristic = getUrlsCharacteristic(url);
                for (String urlChar : urlsCharacteristic) {
                    descriptions.add(getDescription(urlChar));
                }
            }
        }

        return Pokemon.builder()
                .name(JsonParser.parseString(response.body()).getAsJsonObject().get("name").getAsString())
                .photo(JsonParser.parseString(response.body()).getAsJsonObject().get("sprites").getAsJsonObject().get("front_default").getAsString())
                .types(types)
                .weight(JsonParser.parseString(response.body()).getAsJsonObject().get("weight").getAsInt())
                .abilities(abilities)
                .moves(moves)
                .descriptions(descriptions)
                .build();
    }

    public List<String> getUrlsCharacteristic(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(ACCEPT, JSON)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        List<String> urlsCharacteristics = new ArrayList<>();
        JsonArray jsonCharactetistic = JsonParser.parseString(response.body()).getAsJsonObject().get("characteristics").getAsJsonArray();
        jsonCharactetistic.forEach(u -> urlsCharacteristics.add(u.getAsJsonObject().get("url").getAsString()));
        return urlsCharacteristics;
    }

    public Characteristic getDescription(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(ACCEPT, JSON)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray jsonDescriptions = JsonParser.parseString(response.body()).getAsJsonObject().get("descriptions").getAsJsonArray();
        JsonElement aux = jsonDescriptions.get(5);
        return Characteristic.builder().description(aux.getAsJsonObject().get("description").getAsString()).build();
    }

}
