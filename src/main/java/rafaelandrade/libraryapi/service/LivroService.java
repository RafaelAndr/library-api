package rafaelandrade.libraryapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import rafaelandrade.libraryapi.model.Genero;
import rafaelandrade.libraryapi.model.Livro;
import rafaelandrade.libraryapi.repository.LivroRepository;
import rafaelandrade.libraryapi.repository.specs.LivroSpecs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static rafaelandrade.libraryapi.repository.specs.LivroSpecs.*;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository repository;

    public Livro salvar(Livro livro){
        return repository.save(livro);
    }

    public Optional<Livro> obterPorId(UUID id){
        return repository.findById(id);
    }

    public void deletar(Livro livro){
        repository.delete(livro);
    }

    public void atualizar(Livro livro) {
        if(livro.getId() == null){
            throw new IllegalArgumentException("Para atualizar, é necessário que o livro já esteja salvo na base.");
        }

        repository.save(livro);
    }
}
