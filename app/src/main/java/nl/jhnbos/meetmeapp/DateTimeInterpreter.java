package nl.jhnbos.meetmeapp;

import java.util.Calendar;

/**
 * Created by Raquib on 1/6/2015.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
    String interpretTime(int hour, int minute);
}
