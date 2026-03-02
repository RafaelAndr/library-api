package rafaelandrade.libraryapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rafaelandrade.libraryapi.exceptions.OperacaoNaoPermitidaExceptions;
import rafaelandrade.libraryapi.model.Autor;
import rafaelandrade.libraryapi.repository.AutorRepository;
import rafaelandrade.libraryapi.repository.LivroRepository;
import rafaelandrade.libraryapi.validator.AutorValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor  //Cria construtores a partir de atributos com tipo final
public class AutorService {

    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;

    public Autor salvar(Autor autor){
        validator.validar(autor);
        return repository.save(autor);
    }

    public void atualizar(Autor autor){
        if(autor.getId() == null){
            throw new IllegalArgumentException("Autor não encontrado");
        }
        validator.validar(autor);
        repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id){
        return repository.findById(id);
    }

    public void deletar(Autor autor){
        if (possuiLivro(autor)){
            throw new OperacaoNaoPermitidaExceptions("Autor possui livros cadastrados");
        }
        repository.delete(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade){
        if (nome != null && nacionalidade != null){
            return repository.findByNomeAndNacionalidade(nome, nacionalidade);
        }

        if (nome != null){
            return repository.findByNome(nome);
        }

        if (nacionalidade != null){
            return repository.findByNacionalidade(nacionalidade);
        }

        return repository.findAll();
    }

    public boolean possuiLivro(Autor autor){
        return livroRepository.existsByAutor(autor);
    }
}
