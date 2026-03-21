package rafaelandrade.libraryapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rafaelandrade.libraryapi.dto.AutorDto;
import rafaelandrade.libraryapi.dto.ErroResposta;
import rafaelandrade.libraryapi.mappers.AutorMapper;
import rafaelandrade.libraryapi.exceptions.RegistroDuplicadoExceptions;
import rafaelandrade.libraryapi.model.Autor;
import rafaelandrade.libraryapi.model.Usuario;
import rafaelandrade.libraryapi.service.AutorService;
import rafaelandrade.libraryapi.service.UsuarioService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/autores")
@Tag(name = "Autores")
@Slf4j
public class AutorController implements GenericController {

    private final AutorService service;
    private final AutorMapper mapper;
    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Save", description = "Register new author")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author successfully registered."),
            @ApiResponse(responseCode = "422", description = "Validation Error"),
            @ApiResponse(responseCode = "409", description = "Author already registered"),
    })
    public ResponseEntity<Object> salvar(@RequestBody @Valid AutorDto dto) {

        log.info("Cadastrando novo autor {}", dto.nome());
        Autor autor = mapper.toEntity(dto);

        service.salvar(autor);

        URI location = gerarHeaderLocation(autor.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Get Details", description = "Get Author Details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author founded."),
            @ApiResponse(responseCode = "404", description = "Author not founded"),
    })
    public ResponseEntity<AutorDto> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
//      Optional<Autor> autorOptional = service.obterPorId(idAutor);

        return service
                .obterPorId(idAutor)
                .map(autor -> {
                    AutorDto dto = mapper.toDto(autor);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping({"{id}"})
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Delete", description = "Delete an author")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Author successfully deleted."),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "400", description = "Author has registered books"),
    })
    public ResponseEntity<Object> deletar(@PathVariable("id") String id) {

        log.info("Deletando autor de id {}", id);
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);

        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.deletar(autorOptional.get());
        return ResponseEntity.noContent().build();

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Search", description = "Search an author by parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success.")
    })
    public ResponseEntity<List<AutorDto>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade
    ) {
        List<Autor> resultado = service.pesquisaByExample(nome, nacionalidade);

        List<AutorDto> lista = resultado
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Update", description = "Update an existing author")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully updated."),
            @ApiResponse(responseCode = "404", description = "Author not found."),
    })
    public ResponseEntity<Object> atualizar(
            @PathVariable String id,
            @RequestBody @Valid AutorDto dto) {
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.obterPorId(idAutor);

            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var autor = autorOptional.get();
            autor.setNome(dto.nome());
            autor.setNacionalidade(dto.nacionalidade());
            autor.setDataNascimento(dto.dataNascimento());

            service.atualizar(autor);

            return ResponseEntity.noContent().build();
        } catch (RegistroDuplicadoExceptions e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }


//    @PostMapping
//    @PreAuthorize("hasRole('GERENTE')")
//    public ResponseEntity<Object> salvar(@RequestBody @Valid AutorDto dto,
//                                         Authentication authentication) {
//        UserDetails usuarioLogado = (UserDetails) authentication.getPrincipal();
//
//        Usuario usuario = usuarioService.obterPorLogin(usuarioLogado.getUsername());
//
//
//        System.out.println(authentication);
//        Autor autor = mapper.toEntity(dto);
//        autor.setUsuario(usuario);
//
//        service.salvar(autor);
//
//        //http://localhost:8080/autores/id
//        URI location = gerarHeaderLocation(autor.getId());
//        return ResponseEntity.created(location).build();
//    }
}

