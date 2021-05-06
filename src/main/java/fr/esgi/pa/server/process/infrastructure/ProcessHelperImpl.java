package fr.esgi.pa.server.process.infrastructure;

import fr.esgi.pa.server.process.core.ProcessHelper;
import fr.esgi.pa.server.process.core.ProcessResult;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProcessHelperImpl implements ProcessHelper {
    @Override
    public Process createCommandProcess(String[] command) throws IOException {
        return new ProcessBuilder(command).start();
    }

    @Override
    public ProcessResult launchCommandProcess(String[] command) throws IOException, InterruptedException {
        var process = createCommandProcess(command);
        var status = process.waitFor();
        var processStream = status == 0
                ? process.getInputStream()
                : process.getErrorStream();
        return new ProcessResult()
                .setStatus(status)
                .setOut(new String(processStream.readAllBytes()));
    }
}
