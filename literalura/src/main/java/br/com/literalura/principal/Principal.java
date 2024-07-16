package br.com.literalura.principal;

import br.com.literalura.model.*;
import br.com.literalura.repository.AutorRepository;
import br.com.literalura.repository.LivroRepository;
import br.com.literalura.service.ConsumoApi;
import br.com.literalura.service.ConverteDados;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class Principal {
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    \n
                    ======================================
                                  LiterAlura
                    ======================================
                    \n
                    --- Selecione uma opção ---
                    
                    1 - Buscar livro pelo titulo
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    6 - Listar top 10 downloads de livros
                    7 - Mostrar estatísticas do banco de dados de livros
                                        
                    0 - Sair
                    """;

            System.out.println(menu);

            if (leitura.hasNextInt()) {
                opcao = leitura.nextInt();
                leitura.nextLine();

                switch (opcao) {
                    case 1:
                        procurarLivroPorTitulo();
                        break;
                    case 2:
                        listarLivrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosNoAno();
                        break;
                    case 5:
                        listarLivrosPeloIdioma();
                        break;
                    case 6:
                        listarTop10();
                        break;
                    case 7:
                        estatisticaDoBancoDeDados();
                        break;
                    case 0:
                        System.out.println("\nSaindo...");
                        break;
                    default:
                        System.out.println("\nOpção inválida");
                }

            } else {
                System.out.println("Opção inválida!");
                leitura.next();
            }
        }
    }

    @Transactional
    private void procurarLivroPorTitulo() {
        String ENDERECO = "https://gutendex.com/books/?search=";
        System.out.println("\nEscreva o nome do livro que deseja procurar:");
        var titulo = leitura.nextLine();

        if (!titulo.isBlank() && !numero(titulo)) {

            var json = consumo.obterDados(ENDERECO + titulo.replace(" ", "%20"));
            var dados = conversor.obterDados(json, Dados.class);
            Optional<DadosLivro> procurarLivro = dados.resultado()
                    .stream()
                    .filter(l -> l.titulo().toLowerCase().contains(titulo.toLowerCase()))
                    .findFirst();

            if (procurarLivro.isPresent()) {
                DadosLivro dadosLivro = procurarLivro.get();

                if (!verifificarExistenciaDoLivro(dadosLivro)) {
                    Livro livro = new Livro(dadosLivro);
                    DadosAutor dadosAutor = dadosLivro.autor().get(0);
                    Optional<Autor> optionalAutor = autorRepository.acharPeloNome(dadosAutor.nome());

                    if (optionalAutor.isPresent()) {
                        Autor autorExistente = optionalAutor.get();
                        livro.setAutor(autorExistente);
                        autorExistente.getLivros().add(livro);
                        autorRepository.save(autorExistente);
                    } else {
                        Autor newAutor = new Autor(dadosAutor);
                        livro.setAutor(newAutor);
                        newAutor.getLivros().add(livro);
                        autorRepository.save(newAutor);
                    }

                    livroRepository.save(livro);

                } else {
                    System.out.println("\nO Livro já está registrado no banco de dados");
                }

            } else {
                System.out.println("\nLivro não encontrado");
            }

        } else {
            System.out.println("\nOpção inválida!");
        }

    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = livroRepository.findAll();

        if(!livros.isEmpty()) {
            System.out.println("\n----- Livros registrados -----");
            livros.forEach(System.out::println);
        } else {
            System.out.println("\nNenhum livro foi registrado ainda");
        }

    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if(!autores.isEmpty()) {
            System.out.println("\n----- Autores registrados -----");
            autores.forEach(System.out::println);
        } else {
            System.out.println("\nNenhum autor foi registrado ainda");
        }

    }

    private boolean verifificarExistenciaDoLivro(DadosLivro dadosLivro) {
        Livro livro = new Livro(dadosLivro);
        return livroRepository.verificarExistencia(livro.getTitulo());
    }

    private void listarAutoresVivosNoAno() {
        System.out.println("\nDigite o ano para consulta: ");

        if (leitura.hasNextInt()) {
            var ano = leitura.nextInt();
            List<Autor> autores = autorRepository.acharAutoresVivosNoAno(ano);

            if (!autores.isEmpty()) {
                System.out.println("\n----- Autores registrados vivos no ano de " + ano + " -----");
                autores.forEach(System.out::println);
            } else {
                System.out.println("\nSem resultados, tente outro ano");
            }

        } else {
            System.out.println("\nOpção inválida!");
            leitura.next();
        }

    }

    private void listarLivrosPeloIdioma() {
        var opcao = -1;
        String idioma = "";

        System.out.println("\nSelecione o idioma que deseja consultar");
        var languagesMenu = """
               \n
               1 - Espanhol
               2 - Inglês
               3 - Francês
               4 - Português
               """;

        System.out.println(languagesMenu);

        if (leitura.hasNextInt()) {
            opcao = leitura.nextInt();

            switch (opcao) {
                case 1:
                    idioma = "es";
                    break;
                case 2:
                    idioma = "en";
                    break;
                case 3:
                    idioma = "fr";
                    break;
                case 4:
                    idioma = "pt";
                    break;
                default:
                    System.out.println("\nOpção inválida");
            }

            System.out.println("\nLivros registrados:");
            List<Livro> livros = livroRepository.acharLivrosPeloIdioma(idioma);

            if (!livros.isEmpty()) {
                livros.forEach(System.out::println);
            } else {
                System.out.println("\nSem resultados, selecione outro idioma");
            }

        } else {
            System.out.println("\nOpção inválida!");
            leitura.next();
        }

    }

    private boolean numero(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void listarTop10() {
        List<Livro> livros = livroRepository.acharTop10();

        if (!livros.isEmpty()) {
            System.out.println("\n----- Top 10 downloads de livros -----");
            livros.forEach(l -> System.out.println(l.getTitulo()));
        } else {
            System.out.println("\nNenhum livro foi registrado ainda!");
        }

    }

    private void estatisticaDoBancoDeDados() {
        List<Livro> livros = livroRepository.findAll();

        if (!livros.isEmpty()) {
            IntSummaryStatistics sta = livros.stream()
                    .filter(l -> l.getDownloads() > 0)
                    .collect(Collectors.summarizingInt(Livro::getDownloads));

            System.out.println("\n----- Estatistica do Banco de Dados -----");
            System.out.println("Média de Downloads: " + sta.getAverage());
            System.out.println("Máximo de downloads: " + sta.getMax());
            System.out.println("Minimo de downloads: " + sta.getMin());
            System.out.println("Livros Registrados: " + sta.getCount());
        } else {
            System.out.println("\nNada foi registrado ainda!");
        }

    }
}
