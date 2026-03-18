package rafaelandrade.libraryapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rafaelandrade.libraryapi.model.Usuario;
import rafaelandrade.libraryapi.repository.UsuarioRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public void salvar(Usuario usuario){
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        repository.save(usuario);
    }

    public void delete(String id){
        UUID userId = UUID.fromString(id);
        repository.deleteById(userId);
    }

    public Usuario obterPorLogin(String login){
        return repository.findByLogin(login);
    }

    public Usuario getByEmail(String email){
        return repository.findByEmail(email);
    }
}
