package io.Rafa_lol.full_Project.form;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsForm {

    @NotNull(message = "Enable cannot be null or empty")
    private Boolean enabled;
    @NotNull(message = "Not Locked cannot be null or empty")
    private Boolean notLocked;


}
