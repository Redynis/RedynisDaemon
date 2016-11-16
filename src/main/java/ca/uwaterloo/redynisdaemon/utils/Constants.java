package ca.uwaterloo.redynisdaemon.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants
{
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Double ACCESS_THRESHOLD_FACTOR = 0.4;
}
