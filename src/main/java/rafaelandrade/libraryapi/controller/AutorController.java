package rafaelandrade.libraryapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rafaelandrade.libraryapi.controller.dto.AutorDto;
import rafaelandrade.libraryapi.controller.dto.ErroResposta;
import rafaelandrade.libraryapi.exceptions.OperacaoNaoPermitidaExceptions;
import rafaelandrade.libraryapi.exceptions.RegistroDuplicadoExceptions;
import rafaelandrade.libraryapi.model.Autor;
import rafaelandrade.libraryapi.service.AutorService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/autores")
public class AutorController {

    private final AutorService service;

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody AutorDto autor){
        try {
            Autor autorEntidade = autor.mapearParaAutor();
            service.salvar(autorEntidade);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(autorEntidade.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch(RegistroDuplicadoExceptions e){
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }

    }

    @GetMapping("{id}")
    public ResponseEntity<AutorDto> obterDetales(@PathVariable("id") String id){
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);

        if (autorOptional.isPresent()){
            Autor autor = autorOptional.get();
            AutorDto dto = new AutorDto(autor.getId(), autor.getNome(), autor.getDataNascimento(), autor.getNacionalidade());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.obterPorId(idAutor);

            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            service.deletar(autorOptional.get());
            return ResponseEntity.noContent().build();
        } catch (OperacaoNaoPermitidaExceptions e){
            var erroResposta = ErroResposta.repostaPadrao(e.getMessage());
            return ResponseEntity.status(erroResposta.status()).body(erroResposta);
        }
    }

    @GetMapping
    public ResponseEntity<List<AutorDto>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade
    ){
        List<Autor> resultado = service.pesquisa(nome, nacionalidade);
        List<AutorDto> lista = resultado.
                stream().
                map(autor -> new AutorDto(
                        autor.getId(),
                        autor.getNome(),
                        autor.getDataNascimento(),
                        autor.getNacionalidade())
                ).collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(
            @PathVariable String id,
            @RequestBody AutorDto dto){
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.obterPorId(idAutor);

            if(autorOptional.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            var autor = autorOptional.get();
            autor.setNome(dto.nome());
            autor.setNacionalidade(dto.nacionalidade());
            autor.setDataNascimento(dto.dataNascimento());

            service.atualizar(autor);

            return ResponseEntity.noContent().build();
        } catch (RegistroDuplicadoExceptions e){
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}
