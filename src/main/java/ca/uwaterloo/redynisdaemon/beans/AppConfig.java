package ca.uwaterloo.redynisdaemon.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppConfig
{
    private String appName;
    private Double accessThreshold;
    private Set<String> dataHosts;
    private Integer dataPort;
    private String metadataHost;
    private Integer metadataPort;
    private Integer analysisThreadPoolSize;
    private Integer placementThreadPoolSize;
    private Integer minutesBetweenAnalysis;
    private Integer keyExpirySeconds;
}
