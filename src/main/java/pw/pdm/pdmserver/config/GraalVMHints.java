package pw.pdm.pdmserver.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import pw.pdm.pdmserver.model.User;
import pw.pdm.pdmserver.model.dto.UserDto;
import pw.pdm.pdmserver.repository.UserRepository;

public class GraalVMHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(User.class);
        hints.reflection().registerType(UserDto.class);
        hints.reflection().registerType(UserRepository.class);
    }
}