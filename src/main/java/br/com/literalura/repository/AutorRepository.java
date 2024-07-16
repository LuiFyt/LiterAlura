package br.com.literalura.repository;

import br.com.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Autor a WHERE a.nome LIKE %:nome%")
    Optional<Autor> acharPeloNome(@Param("nome") String nome);

    @Query("SELECT a FROM Autor a WHERE :ano BETWEEN CAST(a.ano AS integer) AND CAST(a.anoDaMorte AS integer)")
    List<Autor> acharAutoresVivosNoAno(@Param("ano") int ano);
}