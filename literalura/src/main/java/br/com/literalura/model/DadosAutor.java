package br.com.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosAutor (@JsonAlias("name") String nome,
                          @JsonAlias("birth_year") String ano,
                          @JsonAlias("death_year") String anoDaMorte) {
}
