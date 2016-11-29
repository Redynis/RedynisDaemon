package ca.uwaterloo.redynisdaemon.instructions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletionInstruction
{
    private String redisKey;
    private Set<String> deleteFromHosts;
}
