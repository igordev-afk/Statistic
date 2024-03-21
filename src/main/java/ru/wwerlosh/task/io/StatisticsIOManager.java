package ru.wwerlosh.task.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import ru.wwerlosh.task.DocGenerator;

public class StatisticsIOManager extends IOManager {

    protected String group;
    protected String name;
    protected String option;

    private final ArrayList<Double> selection = new ArrayList<>();

    public StatisticsIOManager(String... args) {
        group = args[0];
        name = args[1];
        option = args[2];
    }

    @Override
    public void read(String option) {
        try{
            File file = new File("./data/" + option + ".txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()){
                double element = scanner.nextDouble();
                selection.add(element);
            }
            System.out.println("Данные успешно прочитаны.");
            scanner.close();
        } catch (FileNotFoundException e){
            System.err.println("File not found: " + e.getMessage());
        }
    }

    @Override
    public void write(Object o) {
        DocGenerator complete = (DocGenerator) o;
        complete.write("Отчет статистика " + name + " " + group + ".docx");
    }

    public ArrayList<Double> getSelection() {
        return selection;
    }

}
