package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.carnivoro;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class CarnivoroDTO extends DinosaurioDTO {
}
