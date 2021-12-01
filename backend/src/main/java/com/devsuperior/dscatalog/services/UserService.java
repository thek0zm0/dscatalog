package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Dto.UserDto;
import com.devsuperior.dscatalog.Dto.UserInsertDto;
import com.devsuperior.dscatalog.Dto.UserUpdateDto;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService
{
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    // readOnly -> doesnt "lock" database
    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable)
    {
        /*
        List<User> list = repository.findAll();
        List<UserDto> listDto = new ArrayList<>();
        for(User cat : list)
        {
            listDto.add(new UserDto(cat));
        }
        */
        return repository
                .findAll(pageable)
                .map(UserDto::new);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id)
    {
        Optional<User> obj = repository.findById(id);
        // Throw my exception
        User entity = obj.orElseThrow( () -> new ResourceNotFoundException("Entity not found"));
        return new UserDto(entity);
    }

    @Transactional
    public UserDto insert(UserInsertDto productDto)
    {
        User entity = new User();
        copyDtoToEntity(productDto, entity);
        entity.setPassword(passwordEncoder.encode(productDto.getPassword()));
        entity = repository.save(entity);
        return new UserDto(entity);
    }

    @Transactional
    public UserDto update(Long id, UserUpdateDto userDto)
    {
        try
        {
            User entity = repository.getOne(id);
            copyDtoToEntity(userDto, entity);
            entity = repository.save(entity);

            return new UserDto(entity);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }

    public void delete(Long id)
    {
        try
        {
            repository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e)
        {
            throw new ResourceNotFoundException("Invalid id: "+id);
        }
        catch (DataIntegrityViolationException e)
        {
            throw new DatabaseException("Integrity Violation");
        }
    }

    private void copyDtoToEntity(UserDto userDto, User entity)
    {
        entity.setFirstName(userDto.getFirstName());
        entity.setLastName(userDto.getLastName());
        entity.setEmail(userDto.getEmail());

        entity.getRoles().clear();

        userDto.getRoles().forEach(roleDto ->
        {
            entity.getRoles().add(roleRepository.getOne(roleDto.getId()));
        });
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = repository.findByEmail(s);

        if (user==null) {
            logger.error("User not found " + s);
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }
        logger.info("User found " + s);
        return repository.findByEmail(s);
    }
}
