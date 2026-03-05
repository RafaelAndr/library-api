package rafaelandrade.libraryapi.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rafaelandrade.libraryapi.exceptions.CampoInvalidoException;
import rafaelandrade.libraryapi.exceptions.RegistroDuplicadoExceptions;
import rafaelandrade.libraryapi.model.Livro;
import rafaelandrade.libraryapi.repository.LivroRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivroValidator {
    private final LivroRepository repository;
    private static final int ANO_EXIGENCIA_PRECO = 2020;

    public void validar(Livro livro){
        if (existeLivroComIsbn(livro)){
            throw new RegistroDuplicadoExceptions("ISBN já cadastrado");
        }

        if (isPrecoObrigatorioNulo(livro)) {
            throw new CampoInvalidoException("preco", "Preço obrigatório para ano > 2020");
        }
    }

    private boolean isPrecoObrigatorioNulo(Livro livro) {
        return livro.getPreco() == null && livro.getDataPublicacao().getYear() > ANO_EXIGENCIA_PRECO;
    }

    public boolean existeLivroComIsbn(Livro livro){
        Optional<Livro> livroEncontrado = repository.findByIsbn(livro.getIsbn());

        if (livro.getId() == null){
            return livroEncontrado.isPresent();
        }

        return livroEncontrado
                .map(Livro::getId)
                .stream()
                .anyMatch(id -> !id.equals(livro.getId()));
    }
}
