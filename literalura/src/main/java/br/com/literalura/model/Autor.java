package br.com.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String ano;
    private String anoDaMorte;
    @OneToMany(mappedBy = "autor", fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();

    public Autor() {}

    public Autor(DadosAutor dadosAutor) {
        this.nome = dadosAutor.nome();
        this.ano = dadosAutor.ano();
        this.anoDaMorte = dadosAutor.anoDaMorte();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getAnoDaMorte() {
        return anoDaMorte;
    }

    public void setAnoDaMorte(String anoDaMorte) {
        this.anoDaMorte = anoDaMorte;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = new ArrayList<>();
        livros.forEach(b -> {
            b.setAutor(this);
            this.livros.add(b);
        });
    }

    @Override
    public String toString() {
        List<String> livros = this.getLivros().stream().map(Livro::getTitulo).toList();
        return "\n---------------------" +
                "\nNome: " + nome +
                "\nAno: " + ano +
                "\nAno da Morte: " + anoDaMorte +
                "\nLivros: " + livros +
                "\n---------------------";
    }
}
