package org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.herbivoro;

import org.main_java.sistema_monitoreo_jurassic.model.dinosauriosDTO.DinosaurioDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class HerbivoroDTO extends DinosaurioDTO {
}
