package fr.esgi.pa.server.process.core;

import java.io.IOException;

public interface ProcessHelper {
    Process createCommandProcess(String[] command) throws IOException;

    ProcessResult launchCommandProcess(String[] command) throws IOException, InterruptedException;
}
