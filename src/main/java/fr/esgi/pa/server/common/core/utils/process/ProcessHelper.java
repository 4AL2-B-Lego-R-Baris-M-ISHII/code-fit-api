package fr.esgi.pa.server.common.core.utils.process;

import java.io.IOException;

public interface ProcessHelper {
    Process createCommandProcess(String[] command) throws IOException;

    ProcessResult launchCommandProcess(String[] command) throws IOException, InterruptedException;
}
