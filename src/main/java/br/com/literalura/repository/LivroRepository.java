package br.com.literalura.repository;

import br.com.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    @Query("SELECT COUNT(l) > 0 FROM Livro l WHERE l.titulo LIKE %:titulo%")
    Boolean verificarExistencia(@Param("titulo") String titulo);

    @Query(value = "SELECT * FROM livros WHERE :idioma = ANY (livros.idioma)", nativeQuery = true)
    List<Livro> acharLivrosPeloIdioma(@Param("idioma") String idioma);

    @Query("SELECT l FROM Livro l ORDER BY l.downloads DESC LIMIT 10")
    List<Livro> acharTop10();
}
