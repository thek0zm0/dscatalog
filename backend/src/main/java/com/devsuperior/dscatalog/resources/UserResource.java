package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Dto.UserDto;
import com.devsuperior.dscatalog.Dto.UserInsertDto;
import com.devsuperior.dscatalog.Dto.UserUpdateDto;
import com.devsuperior.dscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping(value = "/users")
public class UserResource
{
    @Autowired
    private UserService userService;

    /* Encapsulate Http request */
    @GetMapping
    public ResponseEntity<Page<UserDto>> findAll(Pageable pageable)
    {
        Page<UserDto> list = userService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id)
    {
        UserDto cat = userService.findById(id);
        return ResponseEntity.ok().body(cat);
    }

    @PostMapping
    public ResponseEntity<UserDto> insert(@Validated @RequestBody UserInsertDto userInsertDto)
    {
        UserDto newUserDto = userService.insert(userInsertDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUserDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newUserDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                              @Validated @RequestBody UserUpdateDto userDto)
    {
        UserDto newDto = userService.update(id, userDto);
        return ResponseEntity.ok().body(newDto);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserDto> delete(@PathVariable Long id)
    {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
