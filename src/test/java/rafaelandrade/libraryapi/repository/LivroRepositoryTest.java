package rafaelandrade.libraryapi.repository;

import org.springframework.transaction.annotation.Transactional;
import rafaelandrade.libraryapi.model.Autor;
import rafaelandrade.libraryapi.model.Genero;
import rafaelandrade.libraryapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class LivroRepositoryTest {

    @Autowired
    LivroRepository repository;

    @Autowired
    AutorRepository autorRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Test
    void salvarTest(){
        Livro livro = new Livro();
        livro.setIsbn("96787-87774");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(Genero.FICCAO);
        livro.setTitulo("Outro Livro");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = autorRepository
                .findById(UUID.fromString("0faa331a-86d5-408d-a6fd-beca289ce912"))
                .orElse(null);

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void salvarAutorELivroTest(){
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(Genero.FICCAO);
        livro.setTitulo("Terceiro Livro");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = new Autor();
        autor.setNome("José");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951, 1, 31));

        autorRepository.save(autor);

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void salvarCascadeTest(){
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(Genero.FICCAO);
        livro.setTitulo("Outro Livro");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = new Autor();
        autor.setNome("João");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951, 1, 31));

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void atualizarAutorDoLivro(){
        UUID id = UUID.fromString("cfbc87ce-5932-4792-bff0-78ef5973861b");
        var livroParaAtualizar = repository.findById(id).orElse(null);

        UUID idAutor = UUID.fromString("76e7c418-ccf9-4e2a-af20-c28b9e50ab55");
        Autor maria = autorRepository.findById(idAutor).orElse(null);

        livroParaAtualizar.setAutor(maria);

        repository.save(livroParaAtualizar);
    }

    @Test
    void deletar(){
        UUID id = UUID.fromString("cfbc87ce-5932-4792-bff0-78ef5973861b");
        repository.deleteById(id);
    }

    @Test
    void deletarCascade(){
        UUID id = UUID.fromString("22238c02-8118-45ba-a9f0-202dfc3acc67");
        repository.deleteById(id);
    }

    @Test
    @Transactional
    void buscarLivroTest(){
        UUID id = UUID.fromString("75395f2d-625d-4094-a6b5-b9d6ca1b1045");
        Livro livro = repository.findById(id).orElse(null);
    }

    @Test
    void listarPorTitulo(){
        List<Livro> lista = livroRepository.findByTitulo("Terceiro Livro");
        lista.forEach(System.out::println);
    }

    @Test
    void listarPorIsbn(){
        Optional<Livro> livro = livroRepository.findByIsbn("90887-84874");
        livro.ifPresent(System.out::println);
    }

    @Test
    void pesquisaPorTituloEPrecoTest(){
        var preco = BigDecimal.valueOf(100.00);
        var tituloPesquisa = "Terceiro Livro";

        List<Livro> lista = repository.findByTituloAndPreco(tituloPesquisa, preco);
        lista.forEach(System.out::println);
    }

    @Test
    void listarPorTituloOuPreco(){
        var titulo = "Terceiro Livro";
        var isbn = "96787-87774";

        List<Livro> lista = repository.findByTituloOrIsbn(titulo, isbn);
        lista.forEach(System.out::println);
    }
}