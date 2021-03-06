package ca.uwaterloo.redynisdaemon.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageMetric
{
    private Integer totalAccessCount;
    private Set<String> hosts;
    private Map<String, Integer> hostAccesses;
    private Date lastAccessedDate;
}
