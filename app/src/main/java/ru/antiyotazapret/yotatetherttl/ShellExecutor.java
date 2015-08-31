package ru.antiyotazapret.yotatetherttl;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

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
        return execute("su -c " + command);
    }

    /**
     * Запуск команды.
     *
     * @param command команда.
     * @return результат выполнения.
     */
    public Result execute(String command) throws IOException, InterruptedException {

        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        Result result = new Result();

        InputStream inputStream = process.getInputStream();
        try {
            result.output = IOUtils.toString(inputStream);
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