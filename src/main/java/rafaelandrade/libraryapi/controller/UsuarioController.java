package rafaelandrade.libraryapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import rafaelandrade.libraryapi.dto.UsuarioDto;
import rafaelandrade.libraryapi.mappers.UsuarioMapper;
import rafaelandrade.libraryapi.model.Usuario;
import rafaelandrade.libraryapi.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioMapper mapper;

    @PostMapping
    public void salvar(@RequestBody @Valid UsuarioDto dto){
        var usuario = mapper.toEntity(dto);
        service.salvar(usuario);
    }

    @DeleteMapping("{id}")
    public void deletar(@PathVariable String id){
        service.delete(id);
    }
}
