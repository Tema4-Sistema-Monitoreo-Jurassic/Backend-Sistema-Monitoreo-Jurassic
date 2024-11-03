package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.omnivoro;

import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class OmnivoroDTO extends DinosaurioDTO {
}
