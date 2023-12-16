package org.edumss.domain.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank
    private String name;

    @NotNull
    private String password;

    @NotNull
    private UserRole role;

    @NotNull
    private Boolean active;
}
