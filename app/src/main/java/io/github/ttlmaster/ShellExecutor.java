package io.github.ttlmaster;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Класс для запуска консольных команд.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class ShellExecutor {

    /**
     * Запуск команды с привелегиями суперпользователя.
     *
     * @param command команда.
     * @return результат выполнения.
     */
    public Result executeAsRoot(String command) throws IOException, InterruptedException {
        return execute(new String[]{"su", "-c", command}, null);
    }

    /**
     * Запуск команды.
     *
     * @param command команда.
     * @return результат выполнения.
     */
    public Result execute(String command) throws  IOException, InterruptedException {
        return execute(new String[]{"sh", "-c", command}, null);
    }

    /**
     * Запуск команды.
     *
     * @param command команда.
     * @param input stdin
     * @return результат выполнения.
     */
    public Result executeAsRootWithInput(String command, String input) throws  IOException, InterruptedException {
        return execute(new String[]{"su", "-c", command}, input);
    }

    private Result execute(String[] command, String input) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process  = processBuilder.start();
        if (input != null) {
            OutputStream outputStream = process.getOutputStream();
            try {
                outputStream.write(input.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                TtlApplication.Loge(e.toString());
            } finally {
                outputStream.close();
            }
        }

        process.waitFor();

        Result result = new Result();

        InputStream inputStream = process.getInputStream();
        try {
            result.output = IOUtils.toString(inputStream);
            TtlApplication.Logi(Arrays.asList(command).toString() + " returned: [" + result.output.replaceAll("\n", "") + "]");
        } finally {
            inputStream.close();
        }

        result.exitCode = process.exitValue();

        return result;
    }

    /**
     * Модель с результатами выполнения команды.
     */
    public static class Result {

        private int exitCode;
        private String output;

        public int getExitCode() {
            return exitCode;
        }

        public String getOutput() {
            return output;
        }

    }

}