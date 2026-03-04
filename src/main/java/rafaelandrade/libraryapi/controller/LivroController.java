package rafaelandrade.libraryapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafaelandrade.libraryapi.controller.dto.CadastroLivroDto;
import rafaelandrade.libraryapi.controller.dto.ErroResposta;
import rafaelandrade.libraryapi.controller.dto.ResultadoPesquisaLivroDto;
import rafaelandrade.libraryapi.controller.mappers.LivroMapper;
import rafaelandrade.libraryapi.exceptions.RegistroDuplicadoExceptions;
import rafaelandrade.libraryapi.model.Genero;
import rafaelandrade.libraryapi.model.Livro;
import rafaelandrade.libraryapi.service.LivroService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/livros")
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDto dto) {
        Livro livro = mapper.toEntity(dto);
        service.salvar(livro);
        var url = gerarHeaderLocation(livro.getId());
        return ResponseEntity.created(url).build();

    }

    @GetMapping("{id}")
    public ResponseEntity<ResultadoPesquisaLivroDto> obterDetalhes(@PathVariable("id") String id) {
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var dto = mapper.toDto(livro);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(
            @PathVariable("id") String id, @RequestBody @Valid CadastroLivroDto dto){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    Livro entidadeAux = mapper.toEntity(dto);

                    livro.setDataPublicacao(entidadeAux.getDataPublicacao());
                    livro.setIsbn(entidadeAux.getIsbn());
                    livro.setPreco(entidadeAux.getPreco());
                    livro.setGenero(entidadeAux.getGenero());
                    livro.setTitulo(entidadeAux.getTitulo());
                    livro.setAutor(entidadeAux.getAutor());

                    service.atualizar(livro);

                    return ResponseEntity.noContent().build();

                }).orElseGet( () -> ResponseEntity.notFound().build() );
    }
}
